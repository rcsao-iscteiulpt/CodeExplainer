

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.roles.IVariableRole;

public interface IMostWantedHolder extends IVariableRole {

	Operation getOperation();

	default String getName() {
		return "MostWantedHolder";
	}

	enum Operation {
		GREATER, SMALLER;
	}

	enum VarPosition {
		RIGHT, LEFT, NONE;
	}

	class Visitor implements IBlock.IVisitor {
		final IVariable targetVar;
		IVariable arrayVar;

		/**
		 * Checks if the visited If is inside a while
		 */
		boolean isIfInsideWhile = false;

		/**
		 * Checks if the While's guard has the arrayVar in it.
		 */
		boolean isArrayVarInWhileGuard = false;

		/**
		 * Checks if the visited If has an Assignment of the type TargetVar =
		 * ArrayVar[Variable].
		 */
		boolean isAssignmentCorrect = false;
		Operation RelOperator = null;
		boolean first = true;

		Visitor(IVariable var) {
			this.targetVar = var;
			this.arrayVar = null;
		}

		@Override
		public boolean visit(ISelection expression) {
			IBinaryExpression guard = (IBinaryExpression) expression.getGuard();
			VarPosition varPos = VarPosition.NONE;
			IVariable aVar = null;
			if (guard.getLeftOperand().equals(targetVar)) {
				varPos = VarPosition.LEFT;
				aVar = (IVariable) guard.getRightOperand();
			}
			if (guard.getRightOperand().equals(targetVar)) {
				varPos = VarPosition.RIGHT;
				aVar = (IVariable) guard.getLeftOperand();
			}

			if (!varPos.equals(VarPosition.NONE)) {
				Operation op = getRelationalOperator(guard, varPos);
				if (op != null && op != RelOperator) {
					RelOperator = op;
					arrayVar = aVar;

					IProgramElement parent = expression.getParent().getParent();
					CheckWhileConditions(parent, aVar);
					CheckStatementConditions(expression);
				}

			}

			return false;
		}

		/**
		 * Checks if certain conditions are true to discover if the target Variable is a
		 * MostWantedHolder Condition 1: If the visited If is inside a While. Condition
		 * 2: If the While's guard has a array variable.
		 * 
		 * @param parent
		 * @param aVar
		 */
		void CheckWhileConditions(IProgramElement parent, IVariable aVar) {
			if (parent instanceof ILoop) {
				isIfInsideWhile = true;
				IExpression parentGuard = ((ILoop) parent).getGuard();
				if (parentGuard.getParts().get(0).equals(aVar) || parentGuard.getParts().get(1).equals(aVar))
					isArrayVarInWhileGuard = true;
				arrayVar = aVar;
			}
		}

		/**
		 * Checks if certain conditions are true to discover if the target Variable is a
		 * MostWantedHolder Condition 1: Checks if the visited If has a
		 * VariableAssignment in which the target is the targetVariable and if the right
		 * of the operator is the array Variable.
		 * 
		 * @param parent
		 * @param aVar
		 */
		void CheckStatementConditions(ISelection expression) {
			for (IBlockElement i : expression.getBlock().getChildren()) {
				if (i instanceof IVariableAssignment) {
					IVariableAssignment assignment = (IVariableAssignment) i;
					IVariable target = assignment.getTarget();

					if (target.equals(targetVar) && assignment.getExpression().equals(arrayVar)) {
						isAssignmentCorrect = true;
					}
				}
			}
		}

	}

	static boolean isMostWantedHolder(IVariable var) {
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return v.isIfInsideWhile && v.isArrayVarInWhileGuard && v.isAssignmentCorrect && v.RelOperator != null;
	}

	static class MostWantedHolder implements IMostWantedHolder {
		private final Operation operator;

		public MostWantedHolder(Operation operator) {
			this.operator = operator;
		}

		@Override
		public Operation getOperation() {
			return operator;
		}

		public String toString() {
			return getName() + "(" + getOperation() + ")";
		}
	}

	static IVariableRole createMostWantedHolder(IVariable var) {
		assert isMostWantedHolder(var);
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return new MostWantedHolder(v.RelOperator);
	}

	static Operation getRelationalOperator(IBinaryExpression assignment, VarPosition varPos) {
		if (assignment.getOperator().equals(IBinaryOperator.GREATER)
				|| assignment.getOperator().equals(IBinaryOperator.SMALLER)) {
			if (varPos.equals(VarPosition.RIGHT))
				return match(assignment.getOperator(), false);
			else
				return match(assignment.getOperator(), true);
		}
		return null;
	}
	

	static Operation match(IBinaryOperator op, boolean inverse) {
		if (!inverse)
			if (op == IOperator.GREATER)
				return Operation.GREATER;
		if (op == IOperator.SMALLER)
			return Operation.SMALLER;
		else if (op == IOperator.GREATER)
			return Operation.SMALLER;
		if (op == IOperator.SMALLER)
			return Operation.GREATER;

		return null;
	}
}

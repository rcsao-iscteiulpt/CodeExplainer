package pt.iscte.paddle.model.demo2;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;

public class MostWantedHolder implements IMostWantedHolder {

	final Operation operator;
    final IVariable targetVar;
	IVariable arrayVar;
	IArrayElement arrayEl;

	public MostWantedHolder(IVariable targetVar) {
		assert isMostWantedHolder(targetVar);
		Visitor v = new Visitor(targetVar);
		targetVar.getOwnerProcedure().accept(v);
		this.operator = v.RelOperator;
		this.targetVar = targetVar;
		this.arrayVar = v.arrayVar;
		this.arrayEl = v.arrayEl;
	}

	private static class Visitor implements IBlock.IVisitor {
		final IVariable targetVar;
		IVariable arrayVar;
		IArrayElement arrayEl;

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
		Operation RelOperator = Operation.UNDEFINED;

		Visitor(IVariable var) {
			this.targetVar = var;
			this.arrayVar = null;
		}

		@Override
		public boolean visit(ISelection expression) {
			if (expression.getGuard() instanceof IBinaryExpression) {
				IBinaryExpression guard = (IBinaryExpression) expression.getGuard();
				VarPosition varPos = VarPosition.NONE;
				IArrayElement expressionVar = null;
				IVariable aVar = null;

				try {
					if (guard.getLeftOperand().equals(targetVar)) {
						varPos = VarPosition.LEFT;
						expressionVar = (IArrayElement) guard.getRightOperand();
						arrayEl = expressionVar;
						aVar = (IVariable) expressionVar.getTarget();
					}
					if (guard.getRightOperand().equals(targetVar)) {
						varPos = VarPosition.RIGHT;
						expressionVar = (IArrayElement) guard.getLeftOperand();
						arrayEl = expressionVar;
						aVar = (IVariable) expressionVar.getTarget();
					}

					if (!varPos.equals(VarPosition.NONE)) {
						Operation op = getRelationalOperator(guard, varPos);
						if (!op.equals(Operation.UNDEFINED) && op != RelOperator) {

							RelOperator = op;
							arrayVar = aVar;

							IProgramElement parent = expression.getParent().getParent();
							CheckWhileConditions(parent, aVar);
							CheckStatementConditions(expression);
						}
					}
				} catch (ClassCastException e) {
					return false;
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
				IBinaryExpression parentGuard = (IBinaryExpression) ((ILoop) parent).getGuard();
				// TODO Double Condition and Unary Condition
				if (parentGuard.getLeftOperand().equals(aVar))
					isArrayVarInWhileGuard = true;
				if (parentGuard.getRightOperand().equals(aVar))
					isArrayVarInWhileGuard = true;
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

					if (assignment.getExpression() instanceof IArrayElement) {
						IArrayElement ex = (IArrayElement) assignment.getExpression();

						if (target.equals(targetVar) && ex.getTarget().equals(arrayVar)) {
							isAssignmentCorrect = true;
						}
					}
				}
			}
		}
	}

	public static boolean isMostWantedHolder(IVariable var) {
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return v.isIfInsideWhile && v.isArrayVarInWhileGuard && v.isAssignmentCorrect && v.RelOperator != null;
	}

	@Override
	public Operation getOperation() {
		return operator;
	}

	public String toString() {
		return getName() + "(" + getOperation() + ")";
	}

	@Override
	public IVariable getTargetArray() {
		return arrayVar;
	}

	static Operation getRelationalOperator(IBinaryExpression assignment, VarPosition varPos) {
		if (assignment.getOperator().equals(IBinaryOperator.GREATER)
				|| assignment.getOperator().equals(IBinaryOperator.SMALLER)) {
			if (varPos.equals(VarPosition.RIGHT))
				return match(assignment.getOperator(), false);
			else
				return match(assignment.getOperator(), true);
		}
		return Operation.UNDEFINED;
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

		return Operation.UNDEFINED;
	}
}
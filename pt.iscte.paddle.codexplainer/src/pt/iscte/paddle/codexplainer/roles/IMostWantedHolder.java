package pt.iscte.paddle.codexplainer.roles;


import java.util.List;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IVariableRole;


public interface IMostWantedHolder extends IVariableRole {

	Operation getOperation();
	
	IVariableExpression getTargetArray();
	
	
	/**
	 * returns a list containing expressions used to determine whether the variable is or not a MostWantedHolder.
	 * Useful for expression marking on Javardise.
	 * Order in List: 1- Selection Guard, 2- Loop Guard, 3- Max/Min value assignment. 
	 * @return
	 */
	List<IProgramElement> getExpressions();

	default String getName() {
		return "MostWantedHolder";
	}

	public enum Operation {
		GREATER, SMALLER, UNDEFINED;
	}

	enum VarPosition {
		RIGHT, LEFT, NONE;
	}


//	class Visitor implements IBlock.IVisitor {
//		final IVariable targetVar;
//		IVariable arrayVar;
//		IArrayElement arrayEl;
//		IVariable iterator;
//
//		/**
//		 * Checks if the visited If is inside a while
//		 */
//		boolean isIfInsideWhile = false;
//
//		/**
//		 * Checks if the While's guard has the arrayVar in it.
//		 */
//		boolean isArrayVarInWhileGuard = false;
//
//		/**
//		 * Checks if the visited If has an Assignment of the type TargetVar =
//		 * ArrayVar[Variable].
//		 */
//		boolean isAssignmentCorrect = false;
//		Operation RelOperator = null;
//		boolean first = true;
//
//		Visitor(IVariable var) {
//			this.targetVar = var;
//			this.arrayVar = null;
//		}
//
//		@Override
//		public boolean visit(ISelection expression) {
//			//Unary Expression
//			IBinaryExpression guard = (IBinaryExpression) expression.getGuard();
//			VarPosition varPos = VarPosition.NONE;
//			IArrayElement expressionVar = null;
//			IVariable aVar = null;
//			
//			try {
//				if (guard.getLeftOperand().equals(targetVar)) {
//					varPos = VarPosition.LEFT;
//					expressionVar = (IArrayElement) guard.getRightOperand();
//					arrayEl = expressionVar;
//					aVar = (IVariable) expressionVar.getTarget();
//				}
//				if (guard.getRightOperand().equals(targetVar)) {
//					varPos = VarPosition.RIGHT;
//					expressionVar = (IArrayElement) guard.getLeftOperand();
//					arrayEl = expressionVar;
//					aVar = (IVariable) expressionVar.getTarget();
//				}
//				
//				
//
//				if (!varPos.equals(VarPosition.NONE)) {
//					Operation op = getRelationalOperator(guard, varPos);
//					if (op != null && op != RelOperator) {
//						
//						RelOperator = op;
//						arrayVar = aVar;
//
//						IProgramElement parent = expression.getParent().getParent();
//						CheckWhileConditions(parent, aVar);
//						CheckStatementConditions(expression);
//					}
//				}
//			} catch (ClassCastException e) {
//				return false;
//			}
//
//			return false;
//		}
//
//		/**
//		 * Checks if certain conditions are true to discover if the target Variable is a
//		 * MostWantedHolder Condition 1: If the visited If is inside a While. Condition
//		 * 2: If the While's guard has a array variable.
//		 * 
//		 * @param parent
//		 * @param aVar
//		 */
//		void CheckWhileConditions(IProgramElement parent, IVariable aVar) {
//			if (parent instanceof ILoop) {
//				isIfInsideWhile = true;
//				IBinaryExpression parentGuard = (IBinaryExpression) ((ILoop) parent).getGuard();
//				//TODO Double Condition and Unary Condition
//				if (parentGuard.getLeftOperand().equals(aVar))
//					isArrayVarInWhileGuard = true;
//					iterator = (IVariable) parentGuard.getRightOperand();
//				if (parentGuard.getRightOperand().equals(aVar))
//					isArrayVarInWhileGuard = true;
//					iterator = (IVariable) parentGuard.getLeftOperand();
//			}
//		}
//
//		/**
//		 * Checks if certain conditions are true to discover if the target Variable is a
//		 * MostWantedHolder Condition 1: Checks if the visited If has a
//		 * VariableAssignment in which the target is the targetVariable and if the right
//		 * of the operator is the array Variable.
//		 * 
//		 * @param parent
//		 * @param aVar
//		 */
//		void CheckStatementConditions(ISelection expression) {
//			for (IBlockElement i : expression.getBlock().getChildren()) {
//				if (i instanceof IVariableAssignment) {
//					IVariableAssignment assignment = (IVariableAssignment) i;
//					IVariable target = assignment.getTarget();
//
//					if (assignment.getExpression() instanceof IArrayElement) {
//						IArrayElement ex = (IArrayElement) assignment.getExpression();
//
//						if (target.equals(targetVar) && ex.getTarget().equals(arrayVar)) {
//							isAssignmentCorrect = true;
//						}
//					}
//				}
//			}
//		}
//	}
//
//	static boolean isMostWantedHolder(IVariable var) {
//		Visitor v = new Visitor(var);
//		var.getOwnerProcedure().accept(v);
//		return v.isIfInsideWhile && v.isArrayVarInWhileGuard && v.isAssignmentCorrect && v.RelOperator != null;
//	}
//
//	static class MostWantedHolder implements IMostWantedHolder {
//	    final Operation operator;
//		final IVariable targetVar;
//		IVariable arrayVar;
//		IArrayElement arrayEl;
//		
//
//		public MostWantedHolder(Operation operator, IVariable targetVar, IVariable arrayVar, IArrayElement arrayEl) {
//			this.operator = operator;
//			this.targetVar = targetVar;
//			this.arrayVar = arrayVar;
//			this.arrayEl = arrayEl;
//		}
//		
//		
//		@Override
//		public Operation getOperation() {
//			return operator;
//		}
//
//		public String toString() {
//			return getName() + "(" + getOperation() + ")";
//		}
//
//		@Override
//		public IVariable getTargetArray() {
//			return arrayVar; 
//		}
//	}
//
//	static IVariableRole createMostWantedHolder(IVariable var) {
//		assert isMostWantedHolder(var);
//		Visitor v = new Visitor(var);
//		var.getOwnerProcedure().accept(v);
//		return new MostWantedHolder(v.RelOperator, var, v.arrayVar, v.arrayEl);
//	}
//
//	static Operation getRelationalOperator(IBinaryExpression assignment, VarPosition varPos) {
//		if (assignment.getOperator().equals(IBinaryOperator.GREATER)
//				|| assignment.getOperator().equals(IBinaryOperator.SMALLER)) {
//			if (varPos.equals(VarPosition.RIGHT))
//				return match(assignment.getOperator(), false);
//			else
//				return match(assignment.getOperator(), true);
//		}
//		return null;
//	}
//	
//
//	static Operation match(IBinaryOperator op, boolean inverse) {
//		if (!inverse)
//			if (op == IOperator.GREATER)
//				return Operation.GREATER;
//		if (op == IOperator.SMALLER)
//			return Operation.SMALLER;
//		else if (op == IOperator.GREATER)
//			return Operation.SMALLER;
//		if (op == IOperator.SMALLER)
//			return Operation.GREATER;
//
//		return null;
//	}
	
}
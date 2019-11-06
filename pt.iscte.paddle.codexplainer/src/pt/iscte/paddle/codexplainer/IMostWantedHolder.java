package pt.iscte.paddle.codexplainer;


import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IOperator;
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
	
	class Visitor implements IBlock.IVisitor {
		final IVariable var;
		boolean allSameAcc = true;
		Operation operator = null;
		boolean first = true;

		Visitor(IVariable var) {
			this.var = var;
		}

		@Override
		public boolean visit(ISelection guard) {
			System.out.println(guard);
			if(( (IVariableAssignment) guard).getTarget().equals(var)) {
				if(first)
					first = false;
				else {
					Operation op = null;//getRelationalOperator(guard);
					if(op == null || operator != null && op != operator)
						allSameAcc = false;
					else
						operator = op;
				}
			}
			return false;
		}
		
		/*
		@Override
		public boolean visit(IVariableAssignment assignment) {
			if(assignment.getTarget().equals(var)) {
				if(first)
					first = false;
				else {
					Operation op = getAccumulationOperator(assignment);
					if(op == null || operator != null && op != operator)
						allSameAcc = false;
					else
						operator = op;
				}
			}
			return false;
		}
		*/
	}

	static boolean isMostWantedHolder(IVariable var) {
		Visitor v = new Visitor(var);
		var.getProcedure().accept(v);
		return v.allSameAcc && v.operator != null;
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
		var.getProcedure().accept(v);
		return new MostWantedHolder(v.operator);
	}

	/*
	static Operation getAccumulationOperator(IVariableAssignment var) {
		IExpression expression = var.getExpression();
		if(expression instanceof IBinaryExpression) {
			IBinaryExpression e = (IBinaryExpression) expression;
			IExpression left = e.getLeftOperand();
			IExpression right = e.getRightOperand();
			if(e.getOperator().isArithmetic() && 
					(
							left instanceof IVariable && ((IVariable) left).equals(var.getTarget()) && !(right instanceof ILiteral) ||
					)
				return match(e.getOperator());
		}
		return null;
	}
	*/
	
	/*
	static Operation getRelationalOperator(ISelection var) {
		IExpression expression = var.getExpression();
		if(expression instanceof IBinaryExpression) {
			IBinaryExpression e = (IBinaryExpression) expression;
			IExpression left = e.getLeftOperand();
			IExpression right = e.getRightOperand();
			if(e.getOperator().isArithmetic() && 
					(
							left instanceof IVariable && ((IVariable) left).equals(var.getTarget()) && !(right instanceof ILiteral) ||
					)
				return match(e.getOperator());
		}
		return null;
	}
	
	}
	*/
	static Operation match(IBinaryOperator op) {
		if(op == IOperator.GREATER)
			return Operation.GREATER;
		if(op == IOperator.SMALLER)
			return Operation.SMALLER;
		return null;
	}
}

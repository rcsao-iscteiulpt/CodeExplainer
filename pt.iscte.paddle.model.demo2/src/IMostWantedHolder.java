


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
		IExpression var2;
		boolean condition1 = false;
		boolean condition2 = false;
		Operation RelOperator = null;
		boolean first = true;

		Visitor(IVariable var) {
			this.var = var;
			this.var2 = null;
		}
		
		@Override
		public boolean visit(IBinaryExpression expression) {
			System.out.println(expression);
			//IBinaryExpression expression = (IBinaryExpression) condition.getGuard();
			String varPos = "";
			IExpression ex = null;
			if( expression.getLeftOperand().equals(var)) {
				varPos = "left";
				ex = expression.getRightOperand();
			}	
			if(expression.getRightOperand().equals(var)) {
				varPos = "right";
				ex = expression.getLeftOperand();
			}	
				
			if(!varPos.equals("")) {
				Operation op = getRelationalOperator(expression, varPos);
				if(op != null && op != RelOperator) {
					condition1 = true;
					RelOperator = op;
					var2 = ex; 
				}	
			} 
			
			return false;
		}

		@Override
		public boolean visit(IVariableAssignment assignment) {
			System.out.println(assignment);
			if (assignment.getTarget().equals(var)) {
				if (first) {
					first = false;
				} else {
					IExpression e = assignment.getExpression();
					if(e.equals(var2) && RelOperator != null) {
						condition2 = true;
					} else {
						condition2 = false;
					}
				}
			}

			return false;
		}

	}

	static boolean isMostWantedHolder(IVariable var) {
		Visitor v = new Visitor(var);
		var.getProcedure().accept(v);
		return v.condition1 && v.condition2 && v.RelOperator != null;
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
		return new MostWantedHolder(v.RelOperator);
	}

	static Operation getRelationalOperator(IBinaryExpression assignment, String varPos) { 
			if(assignment.getOperator().equals(IBinaryOperator.GREATER) || assignment.getOperator().equals(IBinaryOperator.SMALLER)) {
				if(varPos.equals("right"))
					return match(assignment.getOperator(), false);
				else
					return match(assignment.getOperator(), true);
		}
		return null;
	}
	
	static Operation match(IBinaryOperator op, boolean inverse) {
		if(!inverse)
			if(op == IOperator.GREATER)
				return Operation.GREATER;
			if(op == IOperator.SMALLER)
				return Operation.SMALLER;
		else
			if(op == IOperator.GREATER)
				return Operation.SMALLER;
			if(op == IOperator.SMALLER)
				return Operation.GREATER;
		
		return null;
	}
}

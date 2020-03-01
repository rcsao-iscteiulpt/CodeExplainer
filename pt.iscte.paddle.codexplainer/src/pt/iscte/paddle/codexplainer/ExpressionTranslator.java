package pt.iscte.paddle.codexplainer;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayLength;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IUnaryOperator;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IType;


public class ExpressionTranslator {

	
	static String TranslateDeclaration(IVariableAssignment assignment) {
		StringBuilder g = new StringBuilder();
		
		return g.toString();	
	}
	
	static String TranslateVariableAssignment(IVariableAssignment assignment) {
		StringBuilder g = new StringBuilder();
		
		
		return g.toString();	
	}
	
	static String translateBinaryExpression(IBinaryExpression ex) {
		StringBuilder g = new StringBuilder();
		IExpression leftEx = ex.getLeftOperand();
		IExpression rightEx = ex.getRightOperand();

		String specialCase = CheckBinaryExpressionSpecialCases(ex);
		if (!specialCase.equals("")) {
			g.append(specialCase);
		} else {

			if (leftEx instanceof IArrayElement) {
				IArrayElement leftExArray = (IArrayElement) leftEx;
				g.append(translateArrayElement(leftExArray));
			}	
			if (leftEx instanceof IArrayLength) {
				g.append("o valor de " + leftEx.toString() + " ");
			}
			if (leftEx instanceof IVariable) {
				g.append("o valor de " + leftEx.toString() + " ");
			}
			if (leftEx instanceof ILiteral) {
				g.append(leftEx.toString() + " ");
			}

			g.append(translateOperator(ex.getOperator()));

			if (rightEx instanceof IArrayElement) {
				IArrayElement rightExArray = (IArrayElement) rightEx;
				g.append(translateArrayElement(rightExArray));
			}	
			if (rightEx instanceof IArrayLength) {
				g.append("o valor de " + rightEx.toString() + " ");
			}
			if (rightEx instanceof IVariable) {
				g.append("o valor de " + rightEx.toString() + " ");
			}
			if (rightEx instanceof ILiteral) {
				g.append(rightEx.toString() + " ");
			}

		}

		return g.toString();
	}
	
	static String CheckBinaryExpressionSpecialCases(IBinaryExpression ex) {
		StringBuilder g = new StringBuilder();
		IExpression leftEx = ex.getLeftOperand();
		IExpression rightEx = ex.getRightOperand();
		System.out.println(rightEx.getType());
		
		
		//array positions
		ILiteral l = null;
		IArrayLength aLenght = null;
		if(leftEx instanceof IArrayLength && rightEx instanceof ILiteral && ex.getOperator().equals(IBinaryOperator.SUB)) {
			aLenght = (IArrayLength) leftEx; 
			l = (ILiteral) rightEx;
			
		}
		
		if(rightEx instanceof IArrayLength && leftEx instanceof ILiteral && ex.getOperator().equals(IBinaryOperator.SUB)) {
			aLenght = (IArrayLength) rightEx; 
			l = (ILiteral) leftEx;
			
		}
		
		if(l != null) {
			if(l.getStringValue().equals("1")) {
				g.append("a última posição do vetor " + aLenght.getVariable());
			}
			if(l.getStringValue().equals("2")) {
				g.append("a penúltima posição do vetor " + aLenght.getVariable());
			}
		}
		return g.toString();
	}
	
	static String CheckAssignmentsSpecialCases(IVariable ex) {
		
		//TODO
		return null;
		
		
	}
	
	
	public static String translateUnaryExpression(IUnaryExpression ex) {
		//TODO
		return null;	
	}
	
	
	public static String translateArrayElement(IArrayElement element) {
		StringBuilder g = new StringBuilder();
		IExpression e = element.getIndexes().get(0);
		
		if(element.getIndexes().get(0) instanceof IBinaryExpression) {
			IBinaryExpression ex = (IBinaryExpression) e;
			g.append(translateBinaryExpression(ex));
		} else {
			if(e.equals(IType.INT.literal(0))) {
				g.append("a primeira posição do vetor " + element.getTarget());
			} else {
				g.append("à posição " + element.getIndexes().get(0) + " do vetor " 
									+ element.getTarget() + "("+ element+ ")" + " ");
			}
		}
		
		
		return g.toString();
	}
	
	static String translateSimple(IExpression ex) {
		//TODO
		return ex.toString();
}
	
	static String translateOperator(IBinaryOperator op) {
		if (op.equals(IBinaryOperator.GREATER)) {
			return "maior que " ;	
		}
		if (op.equals(IBinaryOperator.GREATER_EQ)) {
			return "maior ou igual a "; 		
		}
		if (op.equals(IBinaryOperator.SMALLER)) {
			return "menor que "; 
		}
		if (op.equals(IBinaryOperator.SMALLER_EQ)) {
			return "menor ou igual a ";
		}
		if (op.equals(IBinaryOperator.EQUAL)) {
			return "igual a "; 
		}
		if (op.equals(IBinaryOperator.DIFFERENT)) {
			return "diferente de ";
		}
		if (op.equals(IBinaryOperator.ADD)) {
			return "somar com ";
		}
		if (op.equals(IBinaryOperator.DIV)) {
			return "dividir por ";
		}
		if (op.equals(IBinaryOperator.SUB)) {
			return "subtrair por ";
		}
		if (op.equals(IBinaryOperator.MUL)) {
			return "multiplicar por ";
		}
		if (op.equals(IBinaryOperator.AND)) {
			return "e ";
		}
		if (op.equals(IBinaryOperator.OR)) {
			return "ou ";
		}
		/*
		if (op.equals(IBinaryOperator.MOD)) {
			return "multiplicar por ";
		}
		*/
		return "";
	}
	
	static String translateUnaryOperator(IUnaryOperator op) {
		if (op.equals(IUnaryOperator.NOT)) {
			//TODO	
		}
	
		/*
		if (op.equals(IBinaryOperator.MOD)) {
			return "multiplicar por ";
		}
		*/
		return "";
	}
}

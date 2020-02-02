package pt.iscte.paddle.codexplainer;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IUnaryOperator;
import pt.iscte.paddle.model.IVariable;



public class ExpressionTranslator {

	
	static String translateBinaryExpression(IBinaryExpression ex) {
		String s = "";
		IExpression leftEx = ex.getLeftOperand();
		IExpression rightEx = ex.getRightOperand();
		
		if(leftEx instanceof IArrayElement) {
			IArrayElement leftExArray = (IArrayElement) leftEx;
			s += translateArrayElement(leftExArray);	
		} else {
			s += "o valor de " + leftEx.toString() + " ";	
		}
		
		s += translateOperator(ex.getOperator());
		
		if(rightEx instanceof IArrayElement) {
			IArrayElement rightExArray = (IArrayElement) rightEx;
			s += translateArrayElement(rightExArray);	
		} else {
			s += "o valor de " + rightEx.toString() + " ";	
		}

		return s;
	}
	
	static String translateUnaryExpression(IUnaryExpression ex) {
		//TODO
		return null;	
	}
	
	
	static String translateArrayElement(IArrayElement element) {
			return "à posição " + element.getIndexes().get(0) 
					+ " do vetor " + element.getTarget() + " ";
	}
	
	String translateVariable(IVariable variable) {
		return variable.toString();
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

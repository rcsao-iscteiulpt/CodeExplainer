package pt.iscte.paddle.model.demo2;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.role.impl.FunctionClassifier;
import pt.iscte.paddle.codexplainer.role.impl.MostWantedHolder;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier;
import pt.iscte.paddle.codexplainer.roles.IMostWantedHolder.Operation;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IVariableRole;

public class VariableRoleExplainer {

	
	public static String getMostWantedHolderExplanation(IVariableRole role) {
		MostWantedHolder mostWantedHolder = (MostWantedHolder) role;
		
		List<IProgramElement> expressionList = mostWantedHolder.getExpressions();
		
		Operation operator = mostWantedHolder.getOperation();
	    IVariableExpression targetVar = ((IVariableAssignment)expressionList.get(2)).getTarget().expression();
	    IVariableExpression arrayVar = mostWantedHolder.getTargetArray();
		IArrayElement arrayEl;
		
		IBinaryExpression e = ((IBinaryExpression)expressionList.get(0));
		if( e.getLeftOperand().isSame(targetVar)) {
			arrayEl = (IArrayElement) e.getRightOperand();
		} else {
			arrayEl = (IArrayElement) e.getLeftOperand();
		}
		
		String s1 = "";
		String s2 = "";
		if(operator.equals(Operation.GREATER)) {
			s1 = "alto";
			s2 = "maior que";
		} else {
			s1 = "baixo";
			s2 = "menor que";
		}
		return "\nFunção da Variável: A variável "+ targetVar + " é um MostWanteHolder cujo objetivo é guardar o valor mais "+ s1 +" de um certo conjunto de valores." + "\n" + "Neste caso, o vetor " + arrayVar 
				+ " vai ser iterado e cada vez que " + arrayEl + " for " + s2 + " " + targetVar+ ", " + targetVar+ " irá guardar o valor de "+ arrayEl + "\nSendo assim, após o while "+ targetVar +" irá conter o valor mais " + s1 + " do vetor " +arrayVar+".";		
	}
	
	public static List<List<TextComponent>> getMethodClassificationExplanation(FunctionClassifier func) {
		List<List<TextComponent>> line = new ArrayList<List<TextComponent>>();
		
		func.getAssignments();
		
		//TODO
		
		
		return line;
	}

	
	
}

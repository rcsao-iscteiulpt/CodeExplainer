package pt.iscte.paddle.model.demo2;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.demo2.IMostWantedHolder.Operation;
import pt.iscte.paddle.model.roles.IVariableRole;

public class VariableRoleExplainer {

	
	public static String getMostWantedHolderExplanation(IVariableRole role) {
		MostWantedHolder mostWantedHolder = (MostWantedHolder) role;
		Operation operator = mostWantedHolder.operator;
	    IVariableDeclaration targetVar = mostWantedHolder.targetVar;
	    IVariableDeclaration arrayVar = mostWantedHolder.arrayVar;
		IArrayElement arrayEl = mostWantedHolder.arrayEl;
		
		String s1 = "";
		String s2 = "";
		if(operator.equals(Operation.GREATER)) {
			s1 = "alto";
			s2 = "maior que";
		} else {
			s1 = "baixo";
			s2 = "menor que";
		}
		return "\nFun��o da Vari�vel: A vari�vel "+ targetVar + " � um MostWanteHolder cujo objetivo � guardar o valor mais "+ s1 +" de um certo conjunto de valores." + "\n" + "Neste caso, o vetor " + arrayVar 
				+ " vai ser iterado e cada vez que " + arrayEl + " for " + s2 + " " + targetVar+ ", " + targetVar+ " ir� guardar o valor de "+ arrayEl + "\nSendo assim, ap�s o while "+ targetVar +" ir� conter o valor mais " + s1 + " do vetor " +arrayVar+".";		
	}

	
	
}

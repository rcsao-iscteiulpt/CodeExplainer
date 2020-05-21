package pt.iscte.paddle.codexplainer.translator;



import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.*;



public class VariableRoleExplainer {

	
	public static List<TextComponent> getRoleExplanationPT(IVariableDeclaration var, IVariableRole varRole) {
		List<TextComponent> line = new ArrayList<TextComponent>();
		
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(line);
		
		if(varRole instanceof IMostWantedHolder) {
			IMostWantedHolder role = (IMostWantedHolder) varRole;
		
			line.add(new TextComponent("\nA vari�vel "+ var +" vai guardar o valor mais "
					+ t.translateMostWantedholderObjective(role.getObjective()) +" de um "));
					t.translateIType(role.getTargetArray().getType());
					line.add(new TextComponent(".\n"));	
		}
		if(varRole instanceof IGatherer) {
			IGatherer role = (IGatherer) varRole;
			line.add(new TextComponent("\nA vari�vel "+ var +" vai acumular a cada itera��o: "));  
			System.out.println(role.getAccumulationExpression());
			t.translateExpression(role.getAccumulationExpression());
			line.add(new TextComponent(".")); 
		}		
		if(varRole instanceof IFixedValue) {
			IFixedValue role = (IFixedValue) varRole;
			if(!role.isModified()) {
				line.add(new TextComponent("\nA vari�vel "+ var +" ap�s ser inicializada o seu valor nunca ir� ser alterado."));
			} else {
				line.add(new TextComponent("\nAp�s ser inicializado, os valores internos do array "));
				line.add(new TextComponent("ser�o alterados.", role.getExpressions()));
			}
		}	
		if(varRole instanceof IArrayIndexIterator)	{
			IArrayIndexIterator role = (IArrayIndexIterator) varRole;
			line.add(new TextComponent("\nA vari�vel "+ var +" � usada para iterar e "));
			line.add(new TextComponent("aceder"));
			for(int i = 0; i != role.getArrayVariables().size(); i++) {
				if(i == 0) {
					line.add(new TextComponent(" ao vetor " + role.getArrayVariables().get(i), 
							role.getArrayExpressionsMap().get(role.getArrayVariables().get(i))));
				} else 
				if(i != 0 && i == role.getArrayVariables().size() - 1) {
					line.add(new TextComponent(",ao vetor " + role.getArrayVariables().get(i), 
							role.getArrayExpressionsMap().get(role.getArrayVariables().get(i))));
				} else {
					line.add(new TextComponent("e ao vetor " + role.getArrayVariables().get(i), 
							role.getArrayExpressionsMap().get(role.getArrayVariables().get(i))));
				}
			}
			line.add(new TextComponent(" "));
			line.add(new TextComponent("a cada " + Math.abs(role.getStepSize()) + " posi��es ", role.getExpressions()));		
		}
		if(varRole instanceof IStepper && !(varRole instanceof IArrayIndexIterator)) {
			IStepper role = (IStepper) varRole;
			int stepSize = Math.abs(role.getStepSize());
			line.add(new TextComponent("\nA vari�vel "+ var +" vai ser incrementada " 
					+ stepSize +" a "+ stepSize+ " ao longo deste m�todo.")); 
		}
		return line; 
	}
	



	
	
}

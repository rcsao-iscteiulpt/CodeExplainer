package pt.iscte.paddle.codexplainer.translator;



import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.model.IArrayType;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IReferenceType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.*;



public class VariableRoleExplainer {

	
	public static List<TextComponent> getRoleExplanationPT(IVariableDeclaration var, IVariableRole varRole) {
		List<TextComponent> line = new ArrayList<TextComponent>();
		
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(line);
		
		if(varRole instanceof IMostWantedHolder) {
			IMostWantedHolder role = (IMostWantedHolder) varRole;
			if(!role.getObjective().equals(IMostWantedHolder.Objective.UNDEFINED)) {
				line.add(new TextComponent(", esta variável vai guardar o " + t.translateMostWantedholderObjective(role.getObjective()) + "valor encontrado durante a iteração a "));
						t.translateIType(role.getTargetArray().getType(), false);
						line.add(new TextComponent(" "));
						t.linkVariable(role.getTargetArray());
			}			
		}
		if(varRole instanceof IGatherer) {
			IGatherer role = (IGatherer) varRole;
			line.add(new TextComponent(", esta variável vai acumular a cada iteração: "));  
			System.out.println(role.getAccumulationExpression());
			t.translateExpression(role.getAccumulationExpression(), false);
		}		
		if(varRole instanceof IFixedValue) {
			IFixedValue role = (IFixedValue) varRole;
			if(!role.isModified()) {
				line.add(new TextComponent(", após receber o seu valor, o valor desta variável nunca será alterado"));
			} else
			if(var.getType() instanceof IReferenceType) {
				IReferenceType ref = (IReferenceType) var.getType();
				if(ref.getTarget() instanceof IArrayType) {
					IArrayType array = (IArrayType) ref.getTarget();
					if(array.getDimensions() == 1) {
						line.add(new TextComponent(", após a sua inicialização, os valores do vetor "));
						line.add(new TextComponent("serão alterados", role.getExpressions()));
					} else 
					if(array.getDimensions() == 2) {
						line.add(new TextComponent(", após a sua inicialização, os valores da matriz "));
						line.add(new TextComponent("serão alterados", role.getExpressions()));
					}
				} else 
				if(var.getType() instanceof IRecordType){
					line.add(new TextComponent(", após a sua inicialização, os valores dos atributos do objeto "));
					line.add(new TextComponent("serão alterados", role.getExpressions()));
				}		
			}
		}	
		if(varRole instanceof IArrayIndexIterator)	{
			IArrayIndexIterator role = (IArrayIndexIterator) varRole;
			System.out.println("InicializationValue == " + role.getInitializationValue());
			System.out.println("UpperLimit == " + role.getCycleLimit());
			line.add(new TextComponent(", esta variável é usada para iterar e "));
			line.add(new TextComponent("aceder "));
			line.add(new TextComponent("a cada " + Math.abs(role.getStepSize()) + " posições", role.getExpressions()));	
			line.add(new TextComponent(" "));
			
			for(int i = 0; i != role.getArrayVariables().size(); i++) {
				if(i == 0) {
					line.add(new TextComponent("d "));
					t.translateIType(role.getArrayVariables().get(i).getType(), false);
					line.add(new TextComponent(role.getArrayVariables().get(i).toString(),
							role.getArrayExpressionsMap().get(role.getArrayVariables().get(i))));
				} else 
				if(i != 0 && i != role.getArrayVariables().size() - 1) {
					line.add(new TextComponent(","));
					line.add(new TextComponent("de "));
					t.translateIType(role.getArrayVariables().get(i).getType(), false);
					line.add(new TextComponent(role.getArrayVariables().get(i).toString(),
							role.getArrayExpressionsMap().get(role.getArrayVariables().get(i))));
				} else {
					line.add(new TextComponent(" e "));
					line.add(new TextComponent("de "));
					t.translateIType(role.getArrayVariables().get(i).getType(), false);
					line.add(new TextComponent(role.getArrayVariables().get(i).toString(),
							role.getArrayExpressionsMap().get(role.getArrayVariables().get(i))));
				}
			}	
		}
		if(varRole instanceof IStepper && !(varRole instanceof IArrayIndexIterator)) {
			IStepper role = (IStepper) varRole;
			System.out.println("InicializationValue == " + role.getInitializationValue());
			System.out.println("UpperLimit == " + role.getCycleLimit());
			int stepSize = Math.abs(role.getStepSize());
			line.add(new TextComponent(", esta variável vai ser ")); 
			line.add(new TextComponent("incrementada " + stepSize +" a "+ stepSize, role.getExpressions().get(0))); 
		}
		
		if(varRole instanceof IOneWayFlag)	{
			IOneWayFlag role = (IOneWayFlag) varRole;
			
			line.add(new TextComponent(", se o valor desta variável é alterado após essa atribuição esse nunca mais irá mudar.")); 
		}
		return line; 
	}
	



	
	
}

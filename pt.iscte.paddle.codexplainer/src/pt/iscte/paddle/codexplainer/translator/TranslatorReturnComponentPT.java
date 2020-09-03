package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.model.roles.IFunctionClassifier.MethodType;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.roles.IFixedValue;
import pt.iscte.paddle.model.roles.IGatherer;
import pt.iscte.paddle.model.roles.IMostWantedHolder;
import pt.iscte.paddle.model.roles.IOneWayFlag;
import pt.iscte.paddle.model.roles.IStepper;
import pt.iscte.paddle.model.roles.IVariableRole;

public class TranslatorReturnComponentPT implements TranslatorPT {

	ReturnComponent comp;
	List<List<TextComponent>> explanationByComponents = new ArrayList<>();
	 List<ReturnComponent> returnList;
	private int depthLevel;

	public TranslatorReturnComponentPT(ReturnComponent comp, List<ReturnComponent> returnList, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
		this.returnList = returnList;
	}

	@Override
	public void translatePT() {
		List<TextComponent> line = new ArrayList<TextComponent>();
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(line, comp.getMethodComponent());
		addDepthLevel(line);

		String s1 = "função ";
		String s2 = "a ";
		if (comp.getMethodComponent().getFunctionClassifier().getClassification().equals(MethodType.PROCEDURE)) {
			s1 = "procedimento ";
			s2 = "o ";
		}

		if (comp.getVarReturnRole() instanceof IMostWantedHolder) {
			IMostWantedHolder m = (IMostWantedHolder) comp.getVarReturnRole();

			line.add(
					new TextComponent("Após a iteração de "));  
			
			t.translateIType(m.getTargetArray().getType(),false);
			line.add(new TextComponent(" "));  
			line.add(new TextComponent(s2 + s1 + "devolve "));
			t.linkVariable(comp.getReturnExpression());				
					line.add(new TextComponent(" que contém o " + 
							t.translateMostWantedholderObjective(m.getObjective()) + "valor encontrado "));
			//t.translateIType(m.getTargetArray().getType(), false);
		}
		if (comp.getVarReturnRole() instanceof IGatherer || comp.getVarReturnRole() instanceof IStepper) {
			line.add(new TextComponent(
					"Após a acumulação de valores " + s2 + s1 + "devolve o resultado dessa acumulação"));
			
		}
		if (comp.getVarReturnRole() instanceof IFixedValue) {
			IFixedValue m = (IFixedValue) comp.getVarReturnRole();
			if (m.isModified()) {
				line.add(new TextComponent(s2.toUpperCase() + s1 + "devolve o "));
				t.translateIType(comp.getReturnExpression().getType(), false);
				line.add(new TextComponent(" " + comp.getReturnExpressionParts().get(0)));
				line.add(new TextComponent(" cujos valores foram alterados."));
			} else {
				line.add(new TextComponent(s2.toUpperCase() + s1
						+ "devolve o valor da variável ")); 
				t.linkVariable(comp.getReturnExpressionParts().get(0));
			}
			
		}


		if (comp.getReturnExpressionParts().isEmpty() && comp.getReturnType().equals(IType.VOID)) {
			line.add(new TextComponent(s2.toUpperCase() + s1 + "pára aqui a sua execução"));
		}

		
		if (comp.getVarReturnRole().equals(IVariableRole.NONE) || comp.getVarReturnRole() instanceof IOneWayFlag) {
			IReturn lastReturn = (IReturn) returnList.get(returnList.size() - 1).getElement();
			if (comp.getReturnExpression() instanceof IProcedureCall) {
				t.translateExpression(comp.getReturnExpression(), false);
			} else if(returnList.size() >= 2 && lastReturn.getParent().getParent().isSame(lastReturn.getOwnerProcedure()) 
					&& lastReturn.isSame(comp.getElement())) {
				line.add(new TextComponent("Caso esta"));
				
				//conditionList
				List<IProgramElement> condList = new ArrayList<IProgramElement>();
				
				for(ReturnComponent c: returnList) {
					if(c.getCondition() != null)
						condList.add(c.getCondition());
				}
				
				if(condList.size() == 1) {
					line.add(new TextComponent(" ")); 
					line.add(new TextComponent("condição", condList)); 
					line.add(new TextComponent(" não seja verdadeira "));
				} else {
					line.add(new TextComponent("s ")); 
					line.add(new TextComponent("condições", condList)); 
					line.add(new TextComponent(" não sejam verdadeiras "));
				}
				
				line.add(new TextComponent(s2 + s1 + "acabará por devolver ")); 
				t.translateExpression(comp.getReturnExpression(), false); 
			} else 
			{
				line.add(new TextComponent(s2.toUpperCase() + s1 + "devolve "));
				t.translateExpression(comp.getReturnExpression(), false);
			}
		}
	

		// Recursive Extras
		if (comp.getMethodComponent().IsRecursive()) {
			String recursive = "(Caso Base)";
			for (IProgramElement e : comp.getMethodComponent().getRecursive().getExpressions()) {
				if (comp.getReturnExpressionParts().contains(e)) {
					line.add(new TextComponent(" "));
					recursive = "(Caso Recursivo)";		
				}
			}
			line.add(new TextComponent(recursive, ((IReturn) comp.getElement())));
			
		}
		
		explanationByComponents.add(line);

	}

	@Override
	public List<List<TextComponent>> getExplanationByComponents() {
		return explanationByComponents;
	}

//	@Override
//	public String getExplanationText() {
//		String explanationText = "";
//
//		for (TextComponent t : explanationByComponents) {
//			explanationText += t.getText();
//			if (t.getType().equals(TextType.NEWLINE))
//				explanationText += "\n";
//		}
//		return explanationText;
//	}

	public void addDepthLevel(List<TextComponent> line) {
		for (int i = 0; i < depthLevel; i++)
			line.add(new TextComponent("       "));
		if(depthLevel != 0) {
			line.add(new TextComponent("\u2022 "));
		}		
	}
}

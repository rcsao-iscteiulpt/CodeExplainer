package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier.MethodType;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.roles.IFixedValue;
import pt.iscte.paddle.model.roles.IGatherer;
import pt.iscte.paddle.model.roles.IMostWantedHolder;
import pt.iscte.paddle.model.roles.IStepper;
import pt.iscte.paddle.model.roles.IVariableRole;

public class TranslatorReturnComponentPT implements TranslatorPT {
	
	
	ReturnComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();
	private int depthLevel;

	public TranslatorReturnComponentPT(ReturnComponent comp, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents, comp.getMethodComponent());
		addDepthLevel();
		
		String s1 = "função ";
		String s2 = "a ";
		if(comp.getMethodComponent().getFunctionClassifier().getClassification().equals(MethodType.PROCEDURE)) {
			s1 = "procedimento ";
			s2 = "o ";
		}
		
		
		if(comp.getVarReturnRole() instanceof IMostWantedHolder) {
			IMostWantedHolder m = (IMostWantedHolder) comp.getVarReturnRole();

			explanationByComponents.add(new TextComponent("Após a iteração do array "+ s2 + s1 +"devolve a variável com o valor mais " 
			+ t.translateMostWantedholderObjective(m.getObjective()) + "do vetor"));
		}
		if(comp.getVarReturnRole() instanceof IGatherer || comp.getVarReturnRole() instanceof IStepper) {
			explanationByComponents.add(new TextComponent("Após a acumulação de valores "+ s2 + s1 +"devolve o resultado dessa acumulação")); 
		}
		if(comp.getVarReturnRole() instanceof IFixedValue) {
			IFixedValue m = (IFixedValue) comp.getVarReturnRole();
			if(m.isModified()) {
				explanationByComponents.add(new TextComponent( s2.toUpperCase() + s1 +"devolve o vetor " + comp.getReturnExpressionParts().get(0)));
				explanationByComponents.add(new TextComponent(" cujos valores foram alterados."));
			} else {
				explanationByComponents.add(new TextComponent(s2.toUpperCase() + s1 + "devolve o valor fixo da variável "+ comp.getReturnExpressionParts().get(0)));
			}	
		}	
//		if(comp.getVarReturnRole() instanceof IOneWayFlag) {
//			//TODO ONE Way Flag return
//		}
		
		if(comp.getReturnExpressionParts().isEmpty() && comp.getReturnType().equals(IType.VOID)) {
			explanationByComponents.add(new TextComponent(s2.toUpperCase() + s1 + "pára aqui a sua execução"));
		}
		
		if(comp.getVarReturnRole().equals(IVariableRole.NONE)) {
			explanationByComponents.add(new TextComponent(s2.toUpperCase() + s1 +"devolve "));
			t.translateExpression(comp.getReturnExpression());
		}
		
	}
	

	@Override
	public List<TextComponent> getExplanationByComponents() {
		return explanationByComponents;
	}

	@Override
	public String getExplanationText() {
		String explanationText = "";

		for (TextComponent t : explanationByComponents) {
			explanationText += t.getText();
			if (t.getType().equals(TextType.NEWLINE))
				explanationText += "\n";
		}
		return explanationText;
	}
	
	public void addDepthLevel() {
		for(int i = 0; i < depthLevel; i++)
			explanationByComponents.add(new TextComponent("--"));
	}
}	

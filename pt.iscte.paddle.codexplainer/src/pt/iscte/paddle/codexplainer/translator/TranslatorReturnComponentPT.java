package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.IGatherer;
import pt.iscte.paddle.model.roles.IMostWantedHolder;
import pt.iscte.paddle.model.roles.IStepper;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.model.roles.IMostWantedHolder.Objective;

public class TranslatorReturnComponentPT implements TranslatorPT {
	
	
	ReturnComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();

	public TranslatorReturnComponentPT(ReturnComponent comp) {
		this.comp = comp;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents);
		
		if(comp.getVarReturnRole() instanceof IMostWantedHolder) {
			String s1 = "baixo";
			
			if(((IMostWantedHolder)comp.getVarReturnRole()).getOperation().equals(Objective.GREATER)) {
				s1 = "alto";
			}
			explanationByComponents.add(new TextComponent("Após a iteração do array vai ser devolvido a variável com o valor mais " 
			+ s1 + " do vetor", TextType.NORMAL));
		}
		if(comp.getVarReturnRole() instanceof IGatherer || comp.getVarReturnRole() instanceof IStepper) {
			explanationByComponents.add(new TextComponent("Após a acumulação de valores vai ser devolvido o resultado dessa acumulação", TextType.NORMAL)); 
		}

//		if(comp.getVarReturnRole() instanceof IOneWayFlag) {
//			
//		}
		if(comp.getVarReturnRole().equals(IVariableRole.NONE)) {
			explanationByComponents.add(new TextComponent("Vai devolver o valor d", TextType.NORMAL));
			if(comp.getReturnType().equals(IType.INT)) 
				explanationByComponents.add(new TextComponent("o inteiro "+ comp.getReturnExpressionParts().get(0), TextType.NORMAL));
			if(comp.getReturnType().equals(IType.BOOLEAN)) 
				explanationByComponents.add(new TextComponent("o booleano "+ comp.getReturnExpressionParts().get(0), TextType.NORMAL));
			if(comp.getReturnType().equals(IType.DOUBLE)) 
				explanationByComponents.add(new TextComponent("o double "+ comp.getReturnExpressionParts().get(0), TextType.NORMAL));
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
}	

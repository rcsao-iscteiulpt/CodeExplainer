package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.LoopComponent;
import pt.iscte.paddle.codexplainer.components.ProcedureCallComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier.MethodType;
import pt.iscte.paddle.model.IProcedureCallExpression;

public class TranslatorProcedureCallComponentPT implements TranslatorPT {

	
	
	ProcedureCallComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();
	
	int depthLevel;

	public TranslatorProcedureCallComponentPT(ProcedureCallComponent comp, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
	}
	
	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents, comp.getMethodComponent());
		addDepthLevel();
		t.translateProcedureCall((IProcedureCallExpression) comp.getElement());
	}

	public List<TextComponent> getExplanationByComponents() {
		return explanationByComponents;
	}
	
	public void addDepthLevel() {
		for(int i = 0; i < depthLevel; i++)
			explanationByComponents.add(new TextComponent("--"));
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

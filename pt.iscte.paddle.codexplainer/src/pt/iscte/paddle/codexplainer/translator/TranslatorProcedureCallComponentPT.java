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
	List<List<TextComponent>> explanationByComponents = new ArrayList<>();
	
	int depthLevel;

	public TranslatorProcedureCallComponentPT(ProcedureCallComponent comp, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
	}
	
	@Override
	public void translatePT() {
		List<TextComponent> line = new ArrayList<TextComponent>();
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(line, comp.getMethodComponent());
		addDepthLevel(line);
		t.translateProcedureCall((IProcedureCallExpression) comp.getElement());
		
		explanationByComponents.add(line);
	}

	public List<List<TextComponent>> getExplanationByComponents() {
		return explanationByComponents;
	}
	
	public void addDepthLevel(List<TextComponent> line) {
		for (int i = 0; i < depthLevel; i++)
			line.add(new TextComponent("       "));
		if(depthLevel != 0) {
			line.add(new TextComponent("\u2022 "));
		}	
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
//
//		return explanationText;
//	}

}

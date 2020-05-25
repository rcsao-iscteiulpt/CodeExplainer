package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.LoopComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IProcedureCallExpression;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IStepper;

public class TranslatorLoopComponentPT implements TranslatorPT {

	LoopComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();
	
	int depthLevel;

	public TranslatorLoopComponentPT(LoopComponent comp, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents, comp.getMethodComponent());

		addDepthLevel();
		explanationByComponents.add(new TextComponent("Este "));
		explanationByComponents.add(new TextComponent("ciclo ", comp.getLoopBlock().getParent()));
		explanationByComponents.add(new TextComponent("repete-se enquanto "));
		explanationByComponents.add(new TextComponent());
		
		t.translateExpression(comp.getGuard());

		//ArrayIndexIterator
		if (comp.getIteratorComponent().getRole() instanceof IArrayIndexIterator) {
			IArrayIndexIterator it = (IArrayIndexIterator) comp.getIteratorComponent().getRole();

			//explanationByComponents.add(new TextComponent(TextType.NEWLINE));
			addDepthLevel();
			explanationByComponents.add(new TextComponent(
					"\nPercorre as posi��es do vetor " + it.getArrayVariables().get(0)
							+ " atrav�s da vari�vel " + comp.getIteratorComponent().getVar()));
		}

		//Stepper
		if (comp.getIteratorComponent().getRole() instanceof IStepper && !(comp.getIteratorComponent().getRole() instanceof IArrayIndexIterator)) {
			IStepper it = (IStepper) comp.getIteratorComponent().getRole();

			explanationByComponents.add(new TextComponent());
			addDepthLevel();
			explanationByComponents.add(new TextComponent("As itera��es deste ciclo "));
			explanationByComponents.add(new TextComponent("s�o controladas atrav�s da vari�vel "+ comp.getIteratorComponent().getVar(), comp.getIteratorCondition()));
			explanationByComponents.add(new TextComponent(" que � "));	
			explanationByComponents.add(new TextComponent(t.translateDirection(it.getDirection())+" por "+ it.getStepSize() + " em cada itera��o "));
		}

		explanationByComponents.add(new TextComponent());
		addDepthLevel();
		explanationByComponents.add(new TextComponent("Por cada itera��o vai:"));

	}

	public List<TextComponent> getExplanationByComponents() {
		return explanationByComponents;
	}

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

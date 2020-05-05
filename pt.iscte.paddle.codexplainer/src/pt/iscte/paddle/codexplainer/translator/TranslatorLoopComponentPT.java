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
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IStepper;

public class TranslatorLoopComponentPT implements TranslatorPT {

	LoopComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();

	public TranslatorLoopComponentPT(LoopComponent comp) {
		this.comp = comp;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents);

		explanationByComponents.add(new TextComponent("Este ", TextType.NORMAL));
		explanationByComponents.add(new TextComponent("ciclo ", TextType.LINK, comp.getLoopBlock()));
		explanationByComponents.add(new TextComponent("vai continuar a repetir-se enquanto ", TextType.NORMAL));
		explanationByComponents.add(new TextComponent(TextType.NEWLINE));

		for (IProgramElement e : comp.getGuardParts()) {

			if (e instanceof IBinaryExpression) {
				t.translateBinaryExpression((IBinaryExpression) e);
			}
			if (e instanceof IUnaryExpression) {
				t.translateUnaryExpression((IUnaryExpression) e);
			}
			if (e instanceof IOperator) {
				t.translateOperator((IBinaryOperator) e);
			}
			if (e instanceof IProcedureCall) {
				// TODO Procedure call
			}
		}

		//ArrayIndexIterator
		if (comp.getIteratorComponent().getRole() instanceof IArrayIndexIterator) {
			IArrayIndexIterator it = (IArrayIndexIterator) comp.getIteratorComponent().getRole();

			explanationByComponents.add(new TextComponent(TextType.NEWLINE));
			explanationByComponents.add(new TextComponent(
					"O ciclo vai percorrer as posições do vetor " + it.getArrayVariables().get(0)
							+ " através da variável " + comp.getIteratorComponent().getVar(),
					TextType.NORMAL));
		}

		//Stepper
		if (comp.getIteratorComponent().getRole() instanceof IStepper && !(comp.getIteratorComponent().getRole() instanceof IArrayIndexIterator)) {
			IArrayIndexIterator it = (IArrayIndexIterator) comp.getIteratorComponent().getRole();

			explanationByComponents.add(new TextComponent(TextType.NEWLINE));
			explanationByComponents
					.add(new TextComponent("O número de iterações deste ciclo vão ser controladas através da variável "
							+ comp.getIteratorComponent().getVar() + " que vai ser ", TextType.NORMAL));
			t.translateDirection(it.getDirection());
			explanationByComponents.add(new TextComponent(" no fim de cada iteração ", TextType.NORMAL));
		}

		explanationByComponents.add(new TextComponent(TextType.NEWLINE));
		explanationByComponents.add(new TextComponent("Por cada iteração vai:", TextType.NORMAL));

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
}

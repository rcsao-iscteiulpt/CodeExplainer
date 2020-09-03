package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Text;

import pt.iscte.paddle.codexplainer.components.LoopComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayLength;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IExpression;
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
	List<List<TextComponent>> explanationByComponents = new ArrayList<>();

	int depthLevel;

	public TranslatorLoopComponentPT(LoopComponent comp, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
	}

	@Override
	public void translatePT() {
		List<TextComponent> firstLine = new ArrayList<TextComponent>();
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(firstLine, comp.getMethodComponent());

		addDepthLevel(firstLine);
		firstLine.add(new TextComponent("Este "));
		firstLine.add(new TextComponent("ciclo", comp.getLoopBlock().getParent()));
		firstLine.add(new TextComponent(" repete-se enquanto "));

		t.translateExpression(comp.getGuard(), true);
		explanationByComponents.add(firstLine);

		List<TextComponent> secondLine = new ArrayList<TextComponent>();
		addDepthLevel(secondLine);
		t = new ExpressionTranslatorPT(secondLine, comp.getMethodComponent());
		// ArrayIndexIterator
		if (comp.getIteratorComponent().getRole() instanceof IArrayIndexIterator) {
			IArrayIndexIterator it = (IArrayIndexIterator) comp.getIteratorComponent().getRole();

			int iterationType = 0; // 0 - array position; 1 - matrix rows; 2 - positions of matrix row
			IVariableExpression rowIndex = null;
			for (IProgramElement e : it.getArrayExpressionsMap().get(it.getArrayVariables().get(0))) {
				if (e instanceof IArrayElement) {
					IArrayElement el = (IArrayElement) e;

					if (el.getIndexes().size() == 2) {
						IExpression ind1 = el.getIndexes().get(0);
						IExpression ind2 = el.getIndexes().get(1);
						rowIndex = (IVariableExpression) ind1;

						if (comp.getIteratorComponent().getVar().expression().isSame(ind1)) {
							iterationType = 1;
							break;
						} else if (comp.getIteratorComponent().getVar().expression().isSame(ind2)) {
							iterationType = 2;
							break;
						}
					}
				}
			}

			secondLine.add(new TextComponent("Através de "));
			t.linkVariable(comp.getIteratorComponent().getVar().expression());
			secondLine.add(new TextComponent(", o "));
			secondLine.add(new TextComponent("ciclo", comp.getElement()));
			secondLine.add(new TextComponent(" itera e acede "));

			System.out.println(it.isIteratingWholeArray());
			if (it.isIteratingWholeArray()) {
				secondLine.add(new TextComponent("a todas as "));
			} else {
				secondLine.add(new TextComponent("às "));
			}

			if (iterationType == 0) {
				secondLine.add(new TextComponent("posições "));
			} else if (iterationType == 1) {
				secondLine.add(new TextComponent("linhas "));
			} else if (iterationType == 2) {
				secondLine.add(new TextComponent("posições de cada linha "));
				t.linkVariable(rowIndex);
				secondLine.add(new TextComponent(" "));
			}

			secondLine.add(new TextComponent("de "));

			t.translateIType(it.getArrayVariables().get(0).expression().getType(), false);
			secondLine.add(new TextComponent(" "));
			t.linkVariable(it.getArrayVariables().get(0).expression());

			if (!it.isIteratingWholeArray()) {
				secondLine.add(new TextComponent(" desde "));

				String s1 = "";
				if (iterationType == 1) {
					s1 = "linha";
				} else {
					s1 = "posição";
				}

				if (it.getInitializationValue() instanceof IBinaryExpression) {
					t.translateExpression(it.getInitializationValue(), false);
				} else {
					if (it.getInitializationValue() instanceof IVariableExpression) {

						secondLine.add(new TextComponent("a " + s1 + " "));
						t.linkVariable(it.getInitializationValue());
						secondLine.add(new TextComponent(" "));
					} else {
						secondLine.add(new TextComponent("a " + s1 + " " + it.getInitializationValue() + " "));
					}
				}

				secondLine.add(new TextComponent("até "));

				// TODO Fix Condition Loop
				if (it.getCycleLimit() instanceof IArrayLength) {
					secondLine.add(new TextComponent("à última " + s1 + " "));

				} else if (it.getCycleLimit() instanceof IBinaryExpression) {
					t.translateExpression(it.getCycleLimit(), false);
				} else {
					if (it.getCycleLimit() instanceof IVariableExpression) {
						secondLine.add(new TextComponent("à " + s1 + " "));
						t.linkVariable(it.getCycleLimit());
						secondLine.add(new TextComponent(" "));

					} else {
						secondLine.add(new TextComponent("à " + s1 + it.getCycleLimit()));
					}

				}

				if (iterationType == 2) {
					secondLine.add(new TextComponent("da linha "));
				}

				if (it.getStepSize() > 1) {
					secondLine.add(new TextComponent(" a cada " + it.getStepSize() + "posições"));
				}

			}

//			if(it.getCycleLimit() instanceof IBinaryExpression) {
//				t.translateExpression(it.getCycleLimit(), false);
//			}
//			if(it.getCycleLimit() instanceof IArrayLength) {
//				
//			}
		}

		// Stepper
		if (comp.getIteratorComponent().getRole() instanceof IStepper
				&& !(comp.getIteratorComponent().getRole() instanceof IArrayIndexIterator)) {
			IStepper it = (IStepper) comp.getIteratorComponent().getRole();

			secondLine.add(new TextComponent("As iterações deste "));
			secondLine.add(new TextComponent("ciclo", comp.getElement()));
			secondLine.add(
					new TextComponent(" são controladas através da variável " + comp.getIteratorComponent().getVar(),
							comp.getIteratorCondition()));
			secondLine.add(new TextComponent(" que é "));
			secondLine.add(new TextComponent(
					t.translateDirection(it.getDirection()) + " por " + it.getStepSize() + " em cada iteração "));
		}

		if(comp.getIteratorComponent().getRole() instanceof IStepper
				&& comp.getIteratorComponent().getRole() instanceof IArrayIndexIterator)
			explanationByComponents.add(secondLine);

//		List<TextComponent> thirdLine = new ArrayList<TextComponent>();
//		t = new ExpressionTranslatorPT(thirdLine, comp.getMethodComponent());
//		addDepthLevel(thirdLine);
		// thirdLine.add(new TextComponent("Por cada iteração:"));
		// explanationByComponents.add(thirdLine);

	}

	public List<List<TextComponent>> getExplanationByComponents() {
		return explanationByComponents;
	}

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
		for (int i = 0; i < depthLevel; i++) {
			line.add(new TextComponent("       "));
		}
		if (depthLevel != 0) {
			line.add(new TextComponent("\u2022 "));
		}
	}
}

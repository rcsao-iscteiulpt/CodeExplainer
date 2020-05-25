package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier.MethodType;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.*;

public class TranslatorMethodComponentPT implements TranslatorPT {

	MethodComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();
	List<ReturnComponent> returnList = new ArrayList<>();

	public TranslatorMethodComponentPT(MethodComponent comp, List<ReturnComponent> returnList) {
		this.comp = comp;
		this.returnList = returnList;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents);
		List<IVariableDeclaration> parameters = comp.getParameters();
		MethodType classifyType = comp.getFunctionClassifier().getClassification();

		String s1 = "o ";
		String s2 = "e ";
		if (classifyType.equals(MethodType.FUNCTION)) {
			s1 = "a ";
			s2 = "a ";
		}

		explanationByComponents.add(new TextComponent("Est" + s2));
		t.translateMethodType(classifyType);

		if (comp.getReturnType().equals(IType.VOID)) {
			// TODO VOID type method
			// explanationByComponents.add(new TextComponent("vai "));
		} else {

			explanationByComponents.add(new TextComponent(" devolve um "));
			t.translateIType(comp.getReturnType());

			// If a single variable is returned
			if (returnList.size() == 1) {
				ReturnComponent c = returnList.get(0);
				if (c.isParameter()) {
					explanationByComponents.add(new TextComponent("passado como argumento "));
				} else {
					explanationByComponents.add(new TextComponent(", é variável local d" + s1));
					t.translateMethodType(classifyType);
					explanationByComponents.add(new TextComponent(" "));
				}

				if (!c.getVarReturnRole().equals(IVariableRole.NONE)) {
					explanationByComponents.add(new TextComponent("que "));
					// ROLE explanation
					if (c.getVarReturnRole() instanceof IMostWantedHolder) {
						IMostWantedHolder m = (IMostWantedHolder) c.getVarReturnRole();
						explanationByComponents.add(new TextComponent(
								"tem o valor mais " + t.translateMostWantedholderObjective(m.getObjective())));
						explanationByComponents.add(new TextComponent("do "));
						t.translateIType(m.getTargetArray().getVariable().getType());
						if (comp.getParameters().contains(m.getTargetArray().getVariable()))
							explanationByComponents.add(new TextComponent("parametro"));
						explanationByComponents.add(new TextComponent(" " + m.getTargetArray() + " "));

					} else if (c.getVarReturnRole() instanceof IGatherer) {
						IGatherer g = (IGatherer) c.getVarReturnRole();
						explanationByComponents.add(new TextComponent("contém a acumulação de vários "));
						if (g.getAccumulationExpression() instanceof IArrayElement) {
							IArrayElement el = (IArrayElement) g.getAccumulationExpression();
							explanationByComponents.add(new TextComponent("elementos do vetor"));
							if (comp.getParameters().contains(((IVariableExpression) el.getTarget()).getVariable()))
								explanationByComponents.add(new TextComponent(" parametro "));
							explanationByComponents.add(new TextComponent(
									(((IArrayElement) g.getAccumulationExpression()).getTarget() + " ")));
						} else {
							// Caso default
							explanationByComponents.add(new TextComponent("valores "));
						}

					} else if (c.getVarReturnRole() instanceof IFixedValue) {
						IFixedValue f = (IFixedValue) c.getVarReturnRole();
						if (f.isModified()) {
							explanationByComponents.add(new TextComponent("vai ter os seus valores alterados "));
						} else {
							explanationByComponents.add(new TextComponent(
									"contém o valor da sua inicialização independentemente das intruções d" + s1));
							t.translateMethodType(classifyType);
						}
					} else if (c.getVarReturnRole() instanceof IStepper) {

					}
				}
//				else if(c.getVarReturnRole() instanceof IArrayIndexIterator) {
//					
//				}

			}

		}

		// explanationByComponents.add(new TextComponent( + " ", comp.getReturnType()));
		if (!parameters.isEmpty()) {
			explanationByComponents.add(new TextComponent("e recebe um "));
			for (int i = 0; i < parameters.size(); i++) {
				IVariableDeclaration v = parameters.get(i);
				IType type = v.getType();
				t.translateIType(type);
				explanationByComponents.add(new TextComponent(v.toString() + " "));

				if (i < parameters.size() - 2) {
					explanationByComponents.add(new TextComponent(", um "));
				}

				if (i == parameters.size() - 2) {
					explanationByComponents.add(new TextComponent("e um "));
				}
			}
		}

		// Procedimento
		if (classifyType.equals(MethodType.PROCEDURE)) {
			explanationByComponents.add(new TextComponent(
					"\nO facto de ser um procedimento significa que valores internos de vetores ou objectos são alterados durante "
							+ s1));
			t.translateMethodType(comp.getFunctionClassifier().getClassification());
			explanationByComponents.add(new TextComponent("\nNeste caso: os valores "));

			if (!parameters.isEmpty()) {
				explanationByComponents.add(new TextComponent("do "));
				for (int i = 0; i < parameters.size(); i++) {
					IVariableDeclaration v = parameters.get(i);
					IType type = v.getType();
					t.translateIType(type);
					explanationByComponents.add(new TextComponent(v.toString() + " "));

					if (i < parameters.size() - 2) {
						explanationByComponents.add(new TextComponent(", do "));
					}

					if (i == parameters.size() - 2) {
						explanationByComponents.add(new TextComponent("e do "));
					}
				}
			}

			explanationByComponents
					.add(new TextComponent("são alterados", comp.getFunctionClassifier().getAssignments()));

		}
		// explanationByComponents.add(new TextComponent());

		// Recursividade
		if (comp.IsRecursive()) {

			explanationByComponents.add(new TextComponent());
			explanationByComponents.add(new TextComponent("Est" + s2));

			t.translateMethodType(comp.getFunctionClassifier().getClassification());

			explanationByComponents.add(new TextComponent(" é recursiv" + s1));

			explanationByComponents.add(new TextComponent("porque "));
			explanationByComponents.add(new TextComponent("se volta a chamar ", comp.getRecursive().getExpressions()));
			// System.out.println(comp.getRecursive().getExpressions());
			explanationByComponents.add(new TextComponent("a si mesm" + s1 + "durante a execução do programa"));

		}
		
		explanationByComponents.add(new TextComponent());

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

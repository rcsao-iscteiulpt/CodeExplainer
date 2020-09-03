package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.AssignmentComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IStatement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IGatherer;
import pt.iscte.paddle.model.roles.IGatherer.Operation;
import pt.iscte.paddle.model.roles.IMostWantedHolder;
import pt.iscte.paddle.model.roles.IMostWantedHolder.Objective;
import pt.iscte.paddle.model.roles.IStepper;

public class TranslatorAssignmentComponentPT implements TranslatorPT {

	AssignmentComponent comp;
	List<List<TextComponent>> explanationByComponents = new ArrayList<>();
	private int depthLevel;

	public TranslatorAssignmentComponentPT(AssignmentComponent comp, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
	}

	@Override
	public void translatePT() {
		List<TextComponent> line = new ArrayList<TextComponent>();
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(line, comp.getMethodComponent());
		VariableRoleComponent roleComp = comp.getStatementRoleComponent();

		addDepthLevel(line);
		// IMostWantedHolder
		if (roleComp.getRole() instanceof IMostWantedHolder) {
			IMostWantedHolder m = (IMostWantedHolder) roleComp.getRole();
			if (!m.getObjective().equals(IMostWantedHolder.Objective.UNDEFINED)) {
				IVariableAssignment assign = (IVariableAssignment) comp.getStatement();
				if (!comp.isDeclaration()) {

					String s1 = "baixo";
					if (((IMostWantedHolder) roleComp.getRole()).getObjective().equals(Objective.GREATER)) {
						s1 = "alto";
					}

					line.add(new TextComponent(
							"O novo " + t.translateMostWantedholderObjective(m.getObjective()) + "valor é "));
					line.add(new TextComponent("guardado em " + roleComp.getVar(), comp.getStatement()));
					explanationByComponents.add(line);
					return;

				} else {
					t.translateDeclarationAssignment(comp.getStatement());
				}
			} else {
				t.translateAssignment(comp.getStatement());
			}
		} else

		// ArrayIndexIterator
		if (roleComp.getRole() instanceof IArrayIndexIterator) {
			IArrayIndexIterator it = (IArrayIndexIterator) roleComp.getRole();
			if (!comp.isDeclaration() && it.getArrayVariables().size() == 1) {
				// explanationByComponents.add(new TextComponent("A cada iteração "));
				line.add(new TextComponent(roleComp.getVar() + " é " + t.translateDirection(it.getDirection()),
						comp.getStatement()));
				line.add(new TextComponent(" para prosseguir para a próxima "));

				for (IProgramElement e : it.getArrayExpressionsMap().get(it.getArrayVariables().get(0))) {
					if (e instanceof IArrayElement) {
						IArrayElement el = (IArrayElement) e;

						if (el.getIndexes().size() == 1) {
							line.add(new TextComponent("posição de "));
							break;
						} else if (el.getIndexes().size() == 2) {
							IExpression ind1 = el.getIndexes().get(0);
							IExpression ind2 = el.getIndexes().get(1);

							if (roleComp.getVar().expression().isSame(ind1)) {
								line.add(new TextComponent("linha de "));
								break;
							} else if (roleComp.getVar().expression().isSame(ind2)) {
								line.add(new TextComponent("posição da linha " + el.getIndexes().get(0) + " de "));
								break;
							}
						}
					}
				}
				// System.out.println(it.getArrayExpressionsMap().get(it.getArrayVariables().get(0)));
				t.translateIType(it.getArrayVariables().get(0).getType(), false);
				explanationByComponents.add(line);
				return;
			} else {
				t.translateDeclarationAssignment(comp.getStatement());
			}
		} else

		// Stepper
		if (roleComp.getRole() instanceof IStepper) {
			if (!comp.isDeclaration()) {
				IStepper stepper = (IStepper) roleComp.getRole();

				String word = " unidade";
				if (stepper.getStepSize() > 1)
					word += "s";

				line.add(new TextComponent(roleComp.getVar() + " é " + t.translateDirection(stepper.getDirection())
						+ " em " + stepper.getStepSize() + word, comp.getStatement()));
				line.add(new TextComponent(" "));
				explanationByComponents.add(line);
				return;
			} else {
				t.translateDeclarationAssignment(comp.getStatement());
			}
		} else
		// Gatherer
		if (roleComp.getRole() instanceof IGatherer) {
			if (!comp.isDeclaration()) {
				IGatherer gatherer = (IGatherer) roleComp.getRole();

				String s1 = "adicionado";
				if (gatherer.getOperation().equals(Operation.SUB)) {
					s1 = "subtraído";
				}
				if (gatherer.getOperation().equals(Operation.MUL)) {
					s1 = "multiplicado";
				}
				if (gatherer.getOperation().equals(Operation.DIV)) {
					s1 = "dividido";
				}

				line.add(new TextComponent("Em cada iteração "));
				line.add(new TextComponent("é " + s1 + " à variável " + roleComp.getVar() + ":",
						gatherer.getExpressions()));
				line.add(new TextComponent(" "));
				t.translateExpression(gatherer.getAccumulationExpression(), false);
				explanationByComponents.add(line);
				return;
			} else {
				t.translateDeclarationAssignment(comp.getStatement());
			}
		} else {
			// Basic Explanation
			if (comp.isDeclaration()) {
				t.translateDeclarationAssignment(comp.getStatement());
			} else {
				t.translateAssignment(comp.getStatement());
			}
		}

		explanationByComponents.add(line);

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
		for (int i = 0; i < depthLevel; i++)
			line.add(new TextComponent("       "));
		if (depthLevel != 0) {
			line.add(new TextComponent("\u2022 "));
		}
	}
}

package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.AssignmentComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IStatement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IGatherer;
import pt.iscte.paddle.model.roles.IGatherer.Operation;
import pt.iscte.paddle.model.roles.IMostWantedHolder;
import pt.iscte.paddle.model.roles.IMostWantedHolder.Objective;
import pt.iscte.paddle.model.roles.IStepper;

public class TranslatorAssignmentComponentPT implements TranslatorPT {

	AssignmentComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();
	private int depthLevel;

	public TranslatorAssignmentComponentPT(AssignmentComponent comp, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents, comp.getMethodComponent());
		VariableRoleComponent roleComp = comp.getStatementRoleComponent();

		// IMostWantedHolder
		if (roleComp.getRole() instanceof IMostWantedHolder) {
			IMostWantedHolder m = (IMostWantedHolder) roleComp.getRole();
			IVariableAssignment assign = (IVariableAssignment) comp.getStatement();

			if (comp.isDeclaration()) {
				if (assign.getExpression() instanceof IArrayElement) {
					IArrayElement element = (IArrayElement) assign.getExpression();
					explanationByComponents.add(new TextComponent("Para tornar a execução mais eficiente a variável "
							+ comp.getTarget() + " é inicializada com "));
					t.translateArrayElement(element);
					explanationByComponents.add(new TextComponent(",reduzindo 1 iteração do ciclo"));
				} else {
					// Basic Explanation
					addDepthLevel();
					t.translateAssignment((IStatement) comp.getStatement());
				}
				explanationByComponents.add(new TextComponent(""));

				return;
			}

			String s1 = "baixo";
			if (((IMostWantedHolder) roleComp.getRole()).getObjective().equals(Objective.GREATER)) {
				s1 = "alto";
			}

			addDepthLevel();
			explanationByComponents.add(new TextComponent("O novo valor mais " + s1 + " é "));
			explanationByComponents
					.add(new TextComponent("guardado na variável " + roleComp.getVar(), comp.getStatement()));
			// explanationByComponents.add(new TextComponent());
			return;
		} else

		// ArrayIndexIterator
		if (roleComp.getRole() instanceof IArrayIndexIterator) {
			if (!comp.isDeclaration()) {
				IArrayIndexIterator iterator = (IArrayIndexIterator) roleComp.getRole();

				addDepthLevel();
				explanationByComponents.add(new TextComponent("No final da iteração a variável "));
				explanationByComponents.add(
						new TextComponent(roleComp.getVar() + " é " + t.translateDirection(iterator.getDirection()),
								comp.getStatement()));
				explanationByComponents.add(new TextComponent(" para prosseguir para a próxima posição do vetor"));

				return;
			} else {
				t.translateDeclarationAssignment(comp.getStatement());
			}
		} else

		// Stepper
		if (roleComp.getRole() instanceof IStepper) {
			if (!comp.isDeclaration()) {
				IStepper stepper = (IStepper) roleComp.getRole();

				addDepthLevel();
				explanationByComponents.add(new TextComponent("A variável "));
				explanationByComponents
						.add(new TextComponent(roleComp.getVar() + " é " + t.translateDirection(stepper.getDirection())
								+ " por " + stepper.getStepSize(), comp.getStatement()));
				explanationByComponents.add(new TextComponent(" "));

				return;
			} else {
				t.translateDeclarationAssignment(comp.getStatement());
			}
		} else

		// Gatherer
		if (roleComp.getRole() instanceof IGatherer) {
			if (!comp.isDeclaration()) {
				IGatherer gatherer = (IGatherer) roleComp.getRole();

				String s1 = "adiciona";
				if (gatherer.getOperation().equals(Operation.SUB)) {
					s1 = "subtraí";
				}
				if (gatherer.getOperation().equals(Operation.MUL)) {
					s1 = "multiplica";
				}
				if (gatherer.getOperation().equals(Operation.DIV)) {
					s1 = "divide";
				}

				addDepthLevel();
				explanationByComponents.add(new TextComponent("A cada iteração a variável "));
				explanationByComponents
						.add(new TextComponent(roleComp.getVar() + " " + s1 + " ", gatherer.getExpressions()));
				t.translateExpression(gatherer.getAccumulationExpression());
				explanationByComponents.add(new TextComponent("", comp.getStatement()));

				return;
			} else {
				t.translateDeclarationAssignment(comp.getStatement());
			}
		} else {
			// Basic Explanation
			addDepthLevel();
			if (comp.isDeclaration()) {
				t.translateDeclarationAssignment(comp.getStatement());
			} else {
				t.translateAssignment(comp.getStatement());
			}
		}

		// explanationByComponents.add(new TextComponent());

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
		for (int i = 0; i < depthLevel; i++)
			explanationByComponents.add(new TextComponent("--"));
	}
}

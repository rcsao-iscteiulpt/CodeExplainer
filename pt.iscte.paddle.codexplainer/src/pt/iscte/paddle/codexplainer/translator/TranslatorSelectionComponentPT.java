package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.SelectionComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.model.roles.IMostWantedHolder;
import pt.iscte.paddle.model.roles.IMostWantedHolder.Objective;
import pt.iscte.paddle.model.roles.impl.MostWantedHolder;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IVariableExpression;

public class TranslatorSelectionComponentPT implements TranslatorPT {

	SelectionComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();
	private int depthLevel;

	public TranslatorSelectionComponentPT(SelectionComponent comp, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents, comp.getMethodComponent());
		addDepthLevel();
		explanationByComponents.add(new TextComponent("Se "));

		if (!comp.getListVariablesToExplain().isEmpty()) {
			for (VariableRoleComponent v : comp.getListVariablesToExplain()) {
				if (v.getRole() instanceof MostWantedHolder) {
					String s1 = "menor ";
					
					if(((IMostWantedHolder)v.getRole()).getObjective().equals(Objective.GREATER)) {
						s1 = "maior ";
					}
					
					MostWantedHolder role = (MostWantedHolder) v.getRole();
					
					
					explanationByComponents.add(new TextComponent("o valor da posi��o do vetor " + role.getTargetArray()));
					explanationByComponents.add(new TextComponent(" for " + s1 + "que o "+ s1 
							+ "valor encontrado at� ao momento", role.getExpressions().get(0))); 
					explanationByComponents.add(new TextComponent(" guardado na vari�vel "+ v.getVar()));
					
				}
			}
			
			
			
		} else {
			//Basic Explanation
			t.translateExpression(comp.getGuard());
		}

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

	public List<TextComponent> getExplanationByComponents() {
		return explanationByComponents;
	}
	
	public void addDepthLevel() {
		for(int i = 0; i < depthLevel; i++)
			explanationByComponents.add(new TextComponent("--"));
	}

	
}

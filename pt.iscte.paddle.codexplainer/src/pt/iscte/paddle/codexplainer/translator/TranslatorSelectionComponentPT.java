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
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents);
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
					
					
					explanationByComponents.add(new TextComponent("o valor da posição do vetor " + role.getTargetArray()
						+ " for " + s1 + "que o "+ s1 
							+ "valor encontrado até ao momento guardado na variável "+ v.getVar()));
					explanationByComponents.add(new TextComponent());
					
					addDepthLevel();
					explanationByComponents.add(new TextComponent(" então: "));
				}
			}
			
			
			
		} else {
			//Basic Explanation
			for(IProgramElement e: comp.getGuardParts()) {	
				if(e instanceof IBinaryExpression) {
					t.translateBinaryExpression((IBinaryExpression) e, false);
				}
				if(e instanceof IUnaryExpression) {
					t.translateUnaryExpression((IUnaryExpression) e);		
				}
				if(e instanceof IOperator) {
					t.translateOperator((IBinaryOperator)e, false);
				}
				if(e instanceof IVariableExpression) {
					t.translateBooleanVariable((IVariableExpression)e, false);
				}
				
				if(e instanceof IProcedureCall) {
					//TODO Procedure call 
				}
			}
			
			explanationByComponents.add(new TextComponent("então: "));
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

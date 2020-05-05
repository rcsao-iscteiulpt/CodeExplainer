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

public class TranslatorSelectionComponentPT implements TranslatorPT {

	SelectionComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();

	public TranslatorSelectionComponentPT(SelectionComponent comp) {
		this.comp = comp;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents);
		//TODO Alternative Branch
		explanationByComponents.add(new TextComponent("Se ", TextType.NORMAL));

		if (!comp.getListVariablesToExplain().isEmpty()) {
			for (VariableRoleComponent v : comp.getListVariablesToExplain()) {
				if (v.getRole() instanceof MostWantedHolder) {
					String s1 = "menor ";
					
					if(((IMostWantedHolder)v.getRole()).getOperation().equals(Objective.GREATER)) {
						s1 = "maior ";
					}
					
					MostWantedHolder role = (MostWantedHolder) v.getRole();
					
					explanationByComponents.add(new TextComponent("o valor da posição do vetor " + role.getTargetArray()
						+ " for " + s1 + "que o "+ s1 
							+ "valor encontrado até ao momento guardado na variável "+ v.getVar() , TextType.NORMAL));
					explanationByComponents.add(new TextComponent(TextType.NEWLINE));
					explanationByComponents.add(new TextComponent(" então: ",TextType.NORMAL));
				}
			}
			
			
			
		} else {
			//Basic Explanation
			for(IProgramElement e: comp.getGuardParts()) {	
				if(e instanceof IBinaryExpression) {
					t.translateBinaryExpression((IBinaryExpression) e);
				}
				if(e instanceof IUnaryExpression) {
					t.translateUnaryExpression((IUnaryExpression) e);		
				}
				if(e instanceof IOperator) {
					t.translateOperator((IBinaryOperator)e);
				}
				if(e instanceof IProcedureCall) {
					//TODO Procedure call 
				}
			}
			
			explanationByComponents.add(new TextComponent("então vai", TextType.NORMAL));
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

	
}

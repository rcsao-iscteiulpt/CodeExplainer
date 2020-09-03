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
	List<List<TextComponent>> explanationByComponents = new ArrayList<>();
	private int depthLevel;

	public TranslatorSelectionComponentPT(SelectionComponent comp, int depthLevel) {
		this.comp = comp;
		this.depthLevel = depthLevel;
	}

	@Override
	public void translatePT() {
		List<TextComponent> line = new ArrayList<TextComponent>();
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(line, comp.getMethodComponent());
		addDepthLevel(line);
		line.add(new TextComponent("Se "));

		if (!comp.getListVariablesToExplain().isEmpty()) {
			for (VariableRoleComponent v : comp.getListVariablesToExplain()) {
				//MWH
				if (v.getRole() instanceof MostWantedHolder) {
					
					MostWantedHolder role = (MostWantedHolder) v.getRole();
					
					String obj = "";
					if(!	role.getObjective().equals(IMostWantedHolder.Objective.UNDEFINED)) {
						obj = t.translateMostWantedholderObjective(role.getObjective());
					}
					
					line.add(new TextComponent("o valor da posição do vetor ")); 
					t.linkVariable(role.getTargetArray());
					line.add(new TextComponent(" "));
					line.add(new TextComponent("é " + obj + "que o "+  obj 
							+ "valor encontrado até ao momento", role.getExpressions().get(0))); 
					line.add(new TextComponent(" guardado na variável "));
					t.linkVariable(v.getVar().expression());
					
				}
			}
		} else {
			//Basic Explanation
			t.translateExpression(comp.getGuard(), true);
		}
		
		explanationByComponents.add(line);

	}

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
}

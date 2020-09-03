package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.AssignmentComponent;
import pt.iscte.paddle.codexplainer.components.FVParameterComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.FVParameterComponent.MatrixDimension;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;

public class TranslatorFVParameterComponent implements TranslatorPT {

	
	private FVParameterComponent comp;
	private List<List<TextComponent>> explanationByComponents = new ArrayList<>();
	List<ReturnComponent> returnList;
	
	public TranslatorFVParameterComponent(FVParameterComponent comp, List<ReturnComponent> returnList) {
		this.comp = comp;
		this.returnList = returnList;
	}
	
	
	@Override
	public void translatePT() {
		List<TextComponent> line = new ArrayList<TextComponent>();
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(line);
		if(comp.getArrayVar() != null) {
			if(!comp.isDefinesMatrixLength()) {
				line.add(new TextComponent("que define o comprimento do vetor "));
				
				if(returnList.size() == 1 && comp.getArrayVar().isSame(returnList.get(0).getElement())) {
					line.add(new TextComponent("devolvido "));
				} else {
					t.linkVariable(comp.getArrayVar().expression());
					line.add(new TextComponent(" "));
				}
			} else {
				line.add(new TextComponent("que define "));
				if(comp.getMatrixdefinition().equals(MatrixDimension.BOTH)) {
					line.add(new TextComponent("ambos o número de linhas e o comprimento dessas da matriz "));
				} else 
				if(comp.getMatrixdefinition().equals(MatrixDimension.ROWS)) {
					line.add(new TextComponent("o número de linhas da matrix "));
				} else {
					line.add(new TextComponent("o comprimento das linhas da matrix "));
				}
				
				if(returnList.size() == 1 && comp.getArrayVar().isSame(returnList.get(0).getElement())) {
					line.add(new TextComponent("devolvida "));
				} else {
					t.linkVariable(comp.getArrayVar().expression());
					line.add(new TextComponent(" "));
				}
			}
			
		}
		
		explanationByComponents.add(line);
		
	}

	@Override
	public List<List<TextComponent>> getExplanationByComponents() {
		return explanationByComponents;
	}

//	@Override
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

}

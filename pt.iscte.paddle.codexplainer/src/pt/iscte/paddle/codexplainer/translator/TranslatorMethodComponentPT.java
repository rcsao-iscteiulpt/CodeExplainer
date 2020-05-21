package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier.MethodType;
import pt.iscte.paddle.model.IArrayType;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IReferenceType;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TranslatorMethodComponentPT implements TranslatorPT {

	MethodComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();

	public TranslatorMethodComponentPT(MethodComponent comp) {
		this.comp = comp;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents);
		List<IVariableDeclaration> parameters = comp.getParameters();
		MethodType classifyType = comp.getFunctionClassifier().getClassification();
		
		
		String s1 = "o ";
		String s2 = "e ";
		if(classifyType.equals(MethodType.FUNCTION)) {
			s1 = "a ";
			s2 = "a ";
		}
		
		//explanationByComponents.add(new TextComponent());
		if (classifyType.equals(MethodType.FUNCTION)) {
			// explanationByComponents.add(new TextComponent("Este m�todo � considerado uma
			// fun��o porque n�o s�o executadas instru��es que alterem o valor da mem�ria",
			// TextType.NORMAL));
		} else {
			
			// explanationByComponents.add(new TextComponent("Este m�todo � considerado um
			// procedimento porque s�o executadas ", TextType.NORMAL));
			// explanationByComponents.add(new TextComponent("instru��es " , TextType.LINK,
			// comp.getFunctionClassifier().getAssignments()));
			// explanationByComponents.add(new TextComponent("que alteram o valor da
			// mem�ria",TextType.NORMAL));
		}
		
		
		//Recursividade
		if(comp.getIsRecursive()) {
	
			explanationByComponents.add(new TextComponent());
			explanationByComponents.add(new TextComponent("Est" +s2 ));

			t.translateMethodType(comp.getFunctionClassifier().getClassification());
			
			explanationByComponents.add(new TextComponent("� recursiv" + s1));
			
			explanationByComponents.add(new TextComponent("porque "));
			explanationByComponents.add(new TextComponent("se volta a chamar ", comp.getRecursive().getExpressions()));
			explanationByComponents.add(new TextComponent("a si mesm"+s1+"durante a execu��o do programa"));

		}
		
		

		explanationByComponents.add(new TextComponent("Est" + s2));
		t.translateMethodType(classifyType);
		explanationByComponents.add(new TextComponent(" devolve um "));
		t.translateIType(comp.getReturnType());
		//explanationByComponents.add(new TextComponent( + " ", comp.getReturnType()));
		if (!parameters.isEmpty()) {
			explanationByComponents.add(new TextComponent("e recebe um "));
			for (int i = 0; i < parameters.size(); i++) {
				IVariableDeclaration v = parameters.get(i);
				IType type = v.getType();
				t.translateIType(type);
				
				if (i < parameters.size() - 2) {
					explanationByComponents.add(new TextComponent(", um "));
				}

				if (i == parameters.size() - 2) {
					explanationByComponents.add(new TextComponent("e um "));
				}
			}
		}
		//explanationByComponents.add(new TextComponent());

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

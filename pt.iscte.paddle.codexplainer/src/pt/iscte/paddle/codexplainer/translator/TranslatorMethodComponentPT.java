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
		List<IVariableDeclaration> parameters = comp.getParameters();
		
		explanationByComponents.add(new TextComponent("Este m�todo devolve um " + comp.getReturnType() + ". ", TextType.NORMAL));
		if(!parameters.isEmpty()) {
			explanationByComponents.add(new TextComponent("Como parametros recebe um", TextType.NORMAL));
			for(int i = 0; i < parameters.size(); i++) {
				IVariableDeclaration v = parameters.get(i);
				IType type = v.getType();
				if(type.equals(IType.INT)) {
					explanationByComponents.add(new TextComponent("inteiro ", TextType.NORMAL));
				}
				if(type.equals(IType.BOOLEAN)) {
					explanationByComponents.add(new TextComponent("booleano ", TextType.NORMAL));
				}
				if(type.equals(IType.DOUBLE)) {
					explanationByComponents.add(new TextComponent("double ", TextType.NORMAL));
				}
				
				//Arrays
				if(type instanceof IReferenceType) {
					IReferenceType refType = (IReferenceType) type;
					if(refType.getTarget() instanceof IArrayType) {
						explanationByComponents.add(new TextComponent(" vetor de ", TextType.NORMAL));
						
						IType compType = ((IArrayType) refType.getTarget()).getComponentType();
						System.out.println(compType);
						if(compType.equals(IType.INT)) {
							explanationByComponents.add(new TextComponent("inteiros ",TextType.NORMAL));
						}
						if(compType.equals(IType.DOUBLE)) {
							explanationByComponents.add(new TextComponent("doubles ",TextType.NORMAL));
						}
						if(compType.equals(IType.BOOLEAN)) {
							explanationByComponents.add(new TextComponent("booleanos ",TextType.NORMAL));
						}
					
					}
				}
				
				if(type instanceof IRecordType) {
					//TODO paremeters record types
				}
				
				if(i < parameters.size() - 2) {
					explanationByComponents.add(new TextComponent(", um ", TextType.NORMAL));
				}
				
				if(i == parameters.size() - 2) {
					explanationByComponents.add(new TextComponent("e um ", TextType.NORMAL));
				}
				
			}
			
			
		}
		
		//Classify
		explanationByComponents.add(new TextComponent(TextType.NEWLINE));
		if(comp.getFunctionClassifier().getClassification().equals(MethodType.FUNCTION)) {
			explanationByComponents.add(new TextComponent("Este m�todo � considerado uma fun��o porque n�o s�o executadas instru��es que alterem o valor da mem�ria", TextType.NORMAL));
		} else {
			explanationByComponents.add(new TextComponent("Este m�todo � considerado um procedimento porque s�o executadas ", TextType.NORMAL));
			explanationByComponents.add(new TextComponent("instru��es " , TextType.LINK, comp.getFunctionClassifier().getAssignments()));
			explanationByComponents.add(new TextComponent("que alteram o valor da mem�ria",TextType.NORMAL));
		}
		
		explanationByComponents.add(new TextComponent(TextType.NEWLINE));
			
		
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

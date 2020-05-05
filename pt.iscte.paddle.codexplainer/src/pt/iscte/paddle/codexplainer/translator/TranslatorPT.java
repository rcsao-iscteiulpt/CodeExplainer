package pt.iscte.paddle.codexplainer.translator;

import java.util.List;

import pt.iscte.paddle.codexplainer.components.TextComponent;

public interface TranslatorPT {
 
	public void translatePT();
	
	public List<TextComponent> getExplanationByComponents();

	public String getExplanationText();
//		this.components = components;
//		
//		MethodComponent mc = (MethodComponent) components.get(0);
//		if(mc.getReturnType().equals(IType.VOID)) {
//			//TranslatorVoidPT 
//		} else {
//			
//		}
//		
		
			
	
}

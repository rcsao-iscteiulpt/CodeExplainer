package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.AssignmentComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IStatement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IGatherer;
import pt.iscte.paddle.model.roles.IGatherer.Operation;
import pt.iscte.paddle.model.roles.IMostWantedHolder;
import pt.iscte.paddle.model.roles.IMostWantedHolder.Objective;
import pt.iscte.paddle.model.roles.IVariableRole;

public class TranslatorAssignmentComponentPT implements TranslatorPT {

	
	AssignmentComponent comp;
	List<TextComponent> explanationByComponents = new ArrayList<TextComponent>();

	public TranslatorAssignmentComponentPT(AssignmentComponent comp) {
		this.comp = comp;
	}

	@Override
	public void translatePT() {
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(explanationByComponents);
		VariableRoleComponent roleComp = comp.getStatementRoleComponent();
		
		//IMostWantedHolder
		if(roleComp.getRole() instanceof IMostWantedHolder) {
			String s1 = "baixo";
			
			if(((IMostWantedHolder)roleComp.getRole()).getOperation().equals(Objective.GREATER)) {
				s1 = "alto";
			}
			
			explanationByComponents.add(new TextComponent("Vai ", TextType.NORMAL));
			explanationByComponents.add(new TextComponent("guardar o novo valor mais " + s1 + " na variável "
			+ roleComp.getVar(), TextType.LINK, comp.getStatement()));
					explanationByComponents.add(new TextComponent(TextType.NEWLINE));
		} else 
			
		//ArrayIndexIterator	
		if(roleComp.getRole() instanceof IArrayIndexIterator) {
			IArrayIndexIterator iterator = (IArrayIndexIterator) roleComp.getRole();
			
			explanationByComponents.add(new TextComponent("No final de cada iteração a variável ", TextType.NORMAL));
			explanationByComponents.add(new TextComponent(roleComp.getVar() + " vai ser " + t.translateDirection(iterator.getDirection()),TextType.LINK, comp.getStatement()));
			explanationByComponents.add(new TextComponent(" para prosseguir para a próxima posição do vetor", TextType.NORMAL)); 
			
			
		} else 
			
		//Gatherer	
		if(roleComp.getRole() instanceof IGatherer) {
			IGatherer gatherer = (IGatherer) roleComp.getRole();
			
			String s1 = "adição";
			if(gatherer.getOperation().equals(Operation.SUB)) {
				s1 = "subtração";
			}
			if(gatherer.getOperation().equals(Operation.MUL)) {
				s1 = "multiplicação";			
			}
			if(gatherer.getOperation().equals(Operation.DIV)) {
				s1 = "divisão";
			}
			
			explanationByComponents.add(new TextComponent("A cada iteração a variável ", TextType.NORMAL));
			explanationByComponents.add(new TextComponent(roleComp.getVar() + "vai acumulando cada valor com uma " + s1, TextType.LINK, comp.getStatement()));
				
	    } else {
	    	//Basic Explanation
	    	t.translateAssignment((IStatement) comp.getStatement(), comp.isDeclaration());
	    	
		}
		
		
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
}

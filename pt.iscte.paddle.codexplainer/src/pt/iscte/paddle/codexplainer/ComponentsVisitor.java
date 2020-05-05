package pt.iscte.paddle.codexplainer;
	
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.iscte.paddle.codexplainer.components.AssignmentComponent;
import pt.iscte.paddle.codexplainer.components.Component;
import pt.iscte.paddle.codexplainer.components.DeclarationComponent;
import pt.iscte.paddle.codexplainer.components.LoopComponent;
import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.SelectionComponent;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableAssignment;

public class ComponentsVisitor implements IBlock.IVisitor {

	List<Component> components;
	List<VariableRoleComponent> list;
	ArrayList<IVariableDeclaration> declaredVariables = new ArrayList<IVariableDeclaration>(); 
		
	public ComponentsVisitor(IBlock block, List<Component> components, List<VariableRoleComponent> variablesRolesExplanation) {
		this.components = components;
		this.list = variablesRolesExplanation;
		block.accept(this);
	}



	@Override
	public void visit(IVariableDeclaration variable) {
		declaredVariables.add(variable);
	}

	@Override
	public boolean visit(IVariableAssignment assignment) {
		boolean isDeclarationAssignment = false;
		if(declaredVariables.contains(assignment.getTarget())) {
			isDeclarationAssignment = true;
			declaredVariables.remove(assignment.getTarget());
		}
		components.add(new AssignmentComponent(list,assignment, assignment.getTarget().expression(), isDeclarationAssignment));
		return false;
	}
	
	@Override
	public boolean visit(IRecordFieldAssignment assignment) {	
		components.add(new AssignmentComponent(list,assignment, assignment.getTarget().expression(), false));
		return false;
	}

	@Override
	public boolean visit(IArrayElementAssignment assignment) {
		components.add(new AssignmentComponent(list,assignment, assignment.getTarget().expression(), false));
		return false;
	}

	@Override
	public boolean visit(ILoop expression) {
		components.add(new LoopComponent(list, expression));
		return false;
	}

	@Override
	public boolean visit(ISelection expression) {
		
		components.add(new SelectionComponent(list, expression));
		return false;
	}

	@Override
	public boolean visit(IReturn expression) {
		
		components.add(new ReturnComponent(list, expression));
		return false;
	}
	
	
	public List<Component> getBlockComponents() {
		return components;
	}

	

}

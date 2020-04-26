package pt.iscte.paddle.codexplainer;
	
import java.util.ArrayList;
import java.util.List;
import pt.iscte.paddle.codexplainer.components.Component;
import pt.iscte.paddle.codexplainer.components.LoopComponent;
import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.SelectionComponent;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableAssignment;

public class ComponentsVisitor implements IBlock.IVisitor {

	List<Component> components = new ArrayList<Component>();
		
	public ComponentsVisitor(IProcedure method) {
		components.add(new MethodComponent(method));
		
		method.accept(this);
		
		
	}



	@Override
	public void visit(IVariableDeclaration variable) {
		
	}

	@Override
	public boolean visit(IVariableAssignment assignment) {
		
		return false;
	}

	@Override
	public boolean visit(IArrayElementAssignment assignment) {
		
		return false;
	}

	@Override
	public boolean visit(ILoop expression) {
		
		components.add(new LoopComponent(expression));
		return true;
	}

	@Override
	public boolean visit(ISelection expression) {
		
		components.add(new SelectionComponent(expression));
		return true;
	}

	@Override
	public boolean visit(IReturn expression) {
		
		components.add(new ReturnComponent(expression));
		return false;
	}
	
	
	public List<Component> getMethodComponents() {
		return components;
	}

	

}

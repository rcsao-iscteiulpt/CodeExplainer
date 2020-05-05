package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.iscte.paddle.codexplainer.ComponentsVisitor;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IStatement;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.model.roles.impl.Stepper;

public class LoopComponent extends Component {

	private List<IProgramElement> guardParts = new ArrayList<IProgramElement>();
	private IBlock loopBlock;
	
	private List<Component> branchComponents = new ArrayList<Component>();

	private VariableRoleComponent iteratorComponent;

//	ArrayElementType arrayType;
//	enum ArrayElementType {
//		INT, BOOLEAN, RECORD
//	}

	public LoopComponent(List<VariableRoleComponent> list, ILoop loop) {
		IExpression guard = loop.getGuard();
		this.loopBlock = loop.getBlock();
		ComponentsVisitor v = new ComponentsVisitor(loop.getBlock(),branchComponents, list);
		
		//Decompose Guard
		if (guard instanceof IUnaryExpression) {
			// TODO
			guardParts.add(guard);
		}
		if (guard instanceof IBinaryExpression) {
			IBinaryExpression binaryGuard = (IBinaryExpression) guard;
			decomposeBinaryExpressionGuard(binaryGuard);

			if (!binaryGuard.getOperator().equals(IOperator.AND) && !binaryGuard.getOperator().equals(IOperator.OR))
				guardParts.add(guard);
		}

		
		//Add components to list and Search Possible Iterator Role 
		List<IVariableDeclaration> possibleIterators = new ArrayList<>();
		possibleIterators = determinePossibleIteratorsInGuard(guard);

		for (IProgramElement e : loop.getBlock().getChildren()) {
//			//Loop
//			if(e instanceof ILoop) {
//				branchComponents.add(new LoopComponent(list, (ILoop) e));
//			}
//			//Selection
//			if(e instanceof ISelection) {
//				branchComponents.add(new SelectionComponent(list, (ISelection) e));
//			}
//			//Declaration
//			if(e instanceof IVariableDeclaration ) {
//				branchComponents.add(new DeclarationComponent());
//			}	
//			//Assignments
//			if(e instanceof IArrayElementAssignment) {
//			//	branchComponents.add(new AssignmentComponent(list,(IArrayElementAssignment) e));
//			
//			}
//			if(e instanceof IRecordFieldAssignment) {
//			//	branchComponents.add(new AssignmentComponent(list,(IRecordFieldAssignment)e));
//			}
			if (e instanceof IVariableAssignment) {
			//	branchComponents.add(new AssignmentComponent(list,(IVariableAssignment)e));
				
				//Iterator Search
				IVariableAssignment ass = (IVariableAssignment) e;
				if ((ass.isIncrement() || ass.isDecrement()) && possibleIterators.contains(ass.getTarget())) {
					IVariableDeclaration it = ass.getTarget();
					for (VariableRoleComponent comp : list) {
						if (comp.getVar().isSame(it)) {
							iteratorComponent = comp;
						}		
					}
				}
			}
		}
	}


	List<IVariableDeclaration> determinePossibleIteratorsInGuard(IExpression guard) {
		List<IVariableDeclaration> possibleIterators = new ArrayList<>();
		for (IProgramElement e : guardParts) {
			if (e instanceof IBinaryExpression) {
				IExpression left = ((IBinaryExpression) e).getLeftOperand();
				IExpression right = ((IBinaryExpression) e).getRightOperand();
				if (left instanceof IVariableExpression)
					possibleIterators.add((((IVariableExpression) left).getVariable()));
				if (right instanceof IVariableExpression)
					possibleIterators.add((((IVariableExpression) right).getVariable()));
			}
		}
		return possibleIterators;
	}

	void decomposeBinaryExpressionGuard(IBinaryExpression guard) {
		IExpression left = guard.getLeftOperand();
		IExpression right = guard.getRightOperand();
		IOperator guardOp = guard.getOperator();

		if (left instanceof IBinaryExpression) {
			IOperator opL = ((IBinaryExpression) left).getOperator();
			if (!opL.equals(IOperator.AND) && !opL.equals(IOperator.OR)) {
				guardParts.add(left);
			} else {
				decomposeBinaryExpressionGuard((IBinaryExpression) left);
			}
			if (guardOp.equals(IOperator.AND) || guardOp.equals(IOperator.OR))
				guardParts.add(guardOp);
		}
		if (right instanceof IBinaryExpression) {
			IOperator opR = ((IBinaryExpression) right).getOperator();
			if (!opR.equals(IOperator.AND) && !opR.equals(IOperator.OR)) {
				guardParts.add(right);
			} else {
				decomposeBinaryExpressionGuard((IBinaryExpression) right);
			}
		}

		if (left instanceof IUnaryExpression)
			guardParts.add(left);
		if (right instanceof IUnaryExpression)
			guardParts.add(right);
	}

	public IBlock getLoopBlock() {
		return loopBlock;
	}

	public List<IProgramElement> getGuardParts() {
		return guardParts;
	}
	
	public VariableRoleComponent getIteratorComponent() {
		return iteratorComponent;
	}
	
	public List<Component> getBranchComponents() {
		return branchComponents;
	}


}

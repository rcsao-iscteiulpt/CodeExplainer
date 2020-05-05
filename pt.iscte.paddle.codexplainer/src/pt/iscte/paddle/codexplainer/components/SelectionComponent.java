package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.codexplainer.ComponentsVisitor;
import pt.iscte.paddle.codexplainer.role.*;
import pt.iscte.paddle.model.roles.impl.MostWantedHolder;

public class SelectionComponent extends Component {

	private List<IExpression> guardParts = new ArrayList<IExpression>();
	private IBlock selectionBlock;
	
	private List<Component> branchComponents = new ArrayList<Component>();
	
	

	private List<VariableRoleComponent> listVariablesToExplain = new ArrayList<VariableRoleComponent>();

	public SelectionComponent(List<VariableRoleComponent> list, ISelection selection) {
		IExpression guard = selection.getGuard();
		this.selectionBlock = selection.getBlock();
		new ComponentsVisitor(selection.getBlock(),branchComponents, list);

		for(VariableRoleComponent v: list) {
			if(v.getRole() instanceof MostWantedHolder) {
				MostWantedHolder role = (MostWantedHolder) v.getRole();
				if(role.getExpressions().get(0).isSame(selection.getGuard()))
					listVariablesToExplain.add(v);
			}
//			if(v.getRole() instanceof IOneWayFlag) {
//				//TODO one way flag selection component
//			}
		}
		
		if (guard instanceof IUnaryExpression) {
			//TODO SelectionComponent UnaryExpression
			guardParts.add(guard);
		}
		if (guard instanceof IBinaryExpression) {
			IBinaryExpression binaryGuard = (IBinaryExpression) guard;
			decomposeBinaryExpressionGuard(binaryGuard);

			if (!binaryGuard.getOperator().equals(IOperator.AND) && !binaryGuard.getOperator().equals(IOperator.OR))
				guardParts.add(guard);
		}
	}



	public List<VariableRoleComponent> getListVariablesToExplain() {
		return listVariablesToExplain;
	}



	void decomposeBinaryExpressionGuard(IBinaryExpression guard) {
		IExpression left = guard.getLeftOperand();
		IExpression right = guard.getRightOperand();

		if (left instanceof IBinaryExpression) {
			decomposeBinaryExpressionGuard((IBinaryExpression) left);

			IOperator opL = ((IBinaryExpression) left).getOperator();
			if (!opL.equals(IOperator.AND) && !opL.equals(IOperator.OR))
				guardParts.add(left);
		}
		if (right instanceof IBinaryExpression) {
			decomposeBinaryExpressionGuard((IBinaryExpression) right);

			IOperator opR = ((IBinaryExpression) right).getOperator();
			if (!opR.equals(IOperator.AND) && !opR.equals(IOperator.OR))
				guardParts.add(right);
		}
		if (left instanceof IUnaryExpression)
			guardParts.add(left);
		if (right instanceof IUnaryExpression)
			guardParts.add(right);
	}

	public List<IExpression> getGuardParts() {
		return guardParts;
	}
	
	public IBlock getSelectionBlock() {
		return selectionBlock;
	}
	
	public List<Component> getBranchComponents() {
		return branchComponents;
	}

}

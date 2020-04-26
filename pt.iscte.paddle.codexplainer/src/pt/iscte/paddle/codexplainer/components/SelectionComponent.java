package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IUnaryExpression;

public class SelectionComponent extends Component {

	List<IExpression> guardParts = new ArrayList<IExpression>();
	IBlock selectionBlock;

	public SelectionComponent(ISelection selection) {
		IExpression guard = selection.getGuard();
		this.selectionBlock = selection.getBlock();

		
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

}

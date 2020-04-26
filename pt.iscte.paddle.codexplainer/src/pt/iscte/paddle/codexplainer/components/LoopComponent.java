package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;

public class LoopComponent extends Component {

	List<IExpression> guardParts = new ArrayList<IExpression>();
	IBlock loopBlock;
	
	

	Set<IVariableDeclaration> possibleIterators = new HashSet<IVariableDeclaration>();
	
	ArrayElementType collectionType;
	enum ArrayElementType {
		INT, BOOLEAN, RECORD
	}

	public LoopComponent(ILoop loop) {
		IExpression guard = loop.getGuard();
		this.loopBlock = loop.getBlock();
		
		

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

		determinePossibleIteratorsInGuard(guard);

	}

	void determinePossibleIteratorsInGuard(IExpression guard) {

		for (IExpression e : guardParts) {
			if (e instanceof IBinaryExpression) {
				IExpression left = ((IBinaryExpression) e).getLeftOperand();
				IExpression right = ((IBinaryExpression) e).getRightOperand();

				if (left instanceof IVariableExpression)
					possibleIterators.add((((IVariableExpression) left).getVariable()));
				if (right instanceof IVariableExpression)
					possibleIterators.add((((IVariableExpression) right).getVariable()));
			}
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

	public Set<IVariableDeclaration> getPossibleIterators() {
		return possibleIterators;
	}
}

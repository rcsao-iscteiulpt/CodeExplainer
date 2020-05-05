package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IVariableRole;


public class ReturnComponent extends Component {

	IType returnType;
	IExpression returnExpression;
	List<IExpression> returnExpressionParts = new ArrayList<IExpression>();
	
	IVariableRole varReturnRole = IVariableRole.NONE;
	
	
	public ReturnComponent(List<VariableRoleComponent> variableList, IReturn returnEx) {
		this.returnExpression = (IExpression) returnEx.getExpression();
		this.returnType = returnEx.getReturnValueType();
		
		if(returnExpression instanceof IBinaryExpression)  {
			decomposeBinaryExpression((IBinaryExpression) returnExpression);
		} else {
			returnExpressionParts.add(returnExpression);
			
			for(VariableRoleComponent v: variableList) {
				if(returnExpression instanceof IVariableExpression) {
					if(v.getVar().expression().isSame(returnExpression)) {
						varReturnRole = v.getRole();
					}
				}
				if(returnExpression instanceof IUnaryExpression) {
					IUnaryExpression uExpression = (IUnaryExpression) returnExpression;
					//TODO UnaryExpression Return Component
				}
				
			}
		}
		
	}
	


	void decomposeBinaryExpression(IBinaryExpression expression) {
		IExpression left = expression.getLeftOperand();
		IExpression right = expression.getRightOperand();

		if (left instanceof IBinaryExpression) {
			decomposeBinaryExpression((IBinaryExpression) left);		
		} else {
			returnExpressionParts.add(left);
		}
		
		if (right instanceof IBinaryExpression) {
			decomposeBinaryExpression((IBinaryExpression) right);
		} else {
			returnExpressionParts.add(right);
		}
	}
	
	public IType getReturnType() {
		return returnType;
	}

	public List<IExpression> getReturnExpressionParts() {
		return returnExpressionParts;
	}
	
	public IVariableRole getVarReturnRole() {
		return varReturnRole;
	}
	
	
}

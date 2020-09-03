package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IVariableRole;


public class ReturnComponent extends Component {

	IType returnType;
	IExpression returnExpression;
	IExpression condition;
	List<IExpression> returnExpressionParts = new ArrayList<IExpression>();
	
	
	private IVariableRole varReturnRole = IVariableRole.NONE;
	


	/**
	 * Will only be true when the return expression is a single variable and it is a parameter
	 */
	private boolean returnsSingleVarAndIsParameter = false;
	
	
	public ReturnComponent(List<VariableRoleComponent> variableList, IReturn returnEx, MethodComponent mc) {
		this.returnExpression = (IExpression) returnEx.getExpression();
		this.returnType = returnEx.getReturnValueType();
		super.element = returnEx;
		super.mc = mc;
		
		if(returnEx.getParent().getParent() instanceof ILoop) 
			condition = ((ILoop)returnEx.getParent().getParent()).getGuard();
		
		if(returnEx.getParent().getParent() instanceof ISelection) 
			condition = ((ISelection)returnEx.getParent().getParent()).getGuard();
		
		if(returnExpression instanceof IBinaryExpression)  {
			decomposeBinaryExpression((IBinaryExpression) returnExpression);
		} else {
			returnExpressionParts.add(returnExpression);
			
			for(VariableRoleComponent v: variableList) {
				if(returnExpression instanceof IVariableExpression) {
					if(v.getVar().expression().isSame(returnExpression)) {
						varReturnRole = v.getRole();
						
						if(mc.getParameters().contains(v.getVar())) {
							returnsSingleVarAndIsParameter = true;
						}
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
	
	public boolean isParameter() {
		return returnsSingleVarAndIsParameter;
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
	
	public IExpression getCondition() {
		return condition;
	}

	public IExpression getReturnExpression() {
		return returnExpression;
	}

	public boolean getReturnList() {
		// TODO Auto-generated method stub
		return false;
	}
	

	
	
}

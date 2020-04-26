package pt.iscte.paddle.codexplainer.components;

import pt.iscte.paddle.codexplainer.role.impl.FunctionClassifier;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier.MethodType;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IType;

public class MethodComponent extends Component {
	
    IType returnType;
    IFunctionClassifier c;
	
	Boolean isRecursive;
	
	
	
	




	public MethodComponent(IProcedure method) {
		c = new FunctionClassifier(method);
		returnType = method.getReturnType();
		isRecursive = method.isRecursive();
	}
	
	
	public IType getReturnType() {
		return returnType;
	}

	public Boolean getIsRecursive() {
		return isRecursive;
	}

}

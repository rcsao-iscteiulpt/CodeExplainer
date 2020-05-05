package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.role.impl.FunctionClassifier;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;

public class MethodComponent extends Component {
	
    IType returnType;
    IFunctionClassifier c;
	Boolean isRecursive;
	
	List<IVariableDeclaration> parameters = new ArrayList<>();




	public MethodComponent(IProcedure method) {
		c = new FunctionClassifier(method);
		returnType = method.getReturnType();
		isRecursive = method.isRecursive();
		parameters = method.getParameters();
	}
	
	
	public IType getReturnType() {
		return returnType;
	}

	public Boolean getIsRecursive() {
		return isRecursive;
	}
	
	public IFunctionClassifier getFunctionClassifier() {
		return c;
	}

	public List<IVariableDeclaration> getParameters() {
		return parameters;
	}
}

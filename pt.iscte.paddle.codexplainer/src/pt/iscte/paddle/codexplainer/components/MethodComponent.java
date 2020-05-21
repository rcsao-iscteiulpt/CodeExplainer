package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.role.impl.FunctionClassifier;
import pt.iscte.paddle.codexplainer.role.impl.Recursive;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier;
import pt.iscte.paddle.codexplainer.roles.IRecursive;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;

public class MethodComponent extends Component{
	
    IType returnType;
    IFunctionClassifier c;
	Boolean isRecursive;
	IRecursive recursive;
	
	List<IVariableDeclaration> parameters = new ArrayList<>();




	public MethodComponent(IProcedure method) {
		c = new FunctionClassifier(method);
		returnType = method.getReturnType();
		isRecursive = method.isRecursive();
		System.out.println(method.isRecursive());
		parameters = method.getParameters();
		super.element = method;
		
		this.recursive = new Recursive(method);
		System.out.println(recursive.getExpressions());
		
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
	public IRecursive getRecursive() {
		return recursive;
	}
}

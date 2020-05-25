package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.role.impl.FunctionClassifier;
import pt.iscte.paddle.codexplainer.role.impl.Recursive;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier;
import pt.iscte.paddle.codexplainer.roles.IRecursive;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;

public class MethodComponent{
	
    private IType returnType;
    private IFunctionClassifier c;
	private Boolean isRecursive;
	private IRecursive recursive;
	private IProgramElement method;
	
	private List<IVariableDeclaration> parameters = new ArrayList<>();
	

	public MethodComponent(IProcedure method) {
		c = new FunctionClassifier(method);
		returnType = method.getReturnType();
		isRecursive = method.isRecursive();
		parameters = method.getParameters();
		this.method = method;
		this.recursive = new Recursive(method);
		
	}
	
	
	public IType getReturnType() {
		return returnType;
	}

	public Boolean IsRecursive() {
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

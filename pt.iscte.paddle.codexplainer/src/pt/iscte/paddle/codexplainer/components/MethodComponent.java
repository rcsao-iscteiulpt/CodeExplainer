package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.roles.impl.FunctionClassifier;
import pt.iscte.paddle.codexplainer.role.impl.Recursive;
import pt.iscte.paddle.codexplainer.roles.IRecursive;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;

public class MethodComponent {
	
    private IType returnType;
    private FunctionClassifier c;
	private Boolean isRecursive;
	private IRecursive recursive;
	
	private List<IVariableDeclaration> parameters = new ArrayList<>();
	private List<IVariableDeclaration> parametersMencioned = new ArrayList<>();
	private List<FVParameterComponent> fixedValueParameters = new ArrayList<>();
	

	public MethodComponent(IProcedure method, List<FVParameterComponent> fixedValueParameters) {
		c = new FunctionClassifier(method);
		returnType = method.getReturnType();
		isRecursive = method.isRecursive();
		parameters = method.getParameters();
		this.fixedValueParameters = fixedValueParameters;
		this.recursive = new Recursive(method);	
	}
	
	
	public List<FVParameterComponent> getFixedValueParameters() {
		return fixedValueParameters;
	}

	public List<IVariableDeclaration> getParametersMencioned() {
		return parametersMencioned;
	}
	public IType getReturnType() {
		return returnType;
	}

	public Boolean IsRecursive() {
		return isRecursive;
	}
	
	public FunctionClassifier getFunctionClassifier() {
		return c;
	}

	public List<IVariableDeclaration> getParameters() {
		return parameters;
	}
	public IRecursive getRecursive() {
		return recursive;
	}
}

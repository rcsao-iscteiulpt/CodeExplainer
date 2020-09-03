package pt.iscte.paddle.codexplainer.components;

import pt.iscte.paddle.model.IVariableDeclaration;

public class FVParameterComponent {
	
	IVariableDeclaration var;
	
	boolean definesMatrixLength = false;
	IVariableDeclaration arrayVar;
	
	public enum MatrixDimension {
		ROWS,ROWSLENGTH,BOTH;
	}
	MatrixDimension matrixdefinition;
	
	
	
	
	
	public IVariableDeclaration getVar() {
		return var;
	}

	public boolean isDefinesMatrixLength() {
		return definesMatrixLength;
	}

	public IVariableDeclaration getArrayVar() {
		return arrayVar;
	}

	public MatrixDimension getMatrixdefinition() {
		return matrixdefinition;
	}

	public FVParameterComponent(IVariableDeclaration var) {
		this.var = var;
	}


	public void setDefinesMatrixLength(boolean definesMatrixLength) {
		this.definesMatrixLength = definesMatrixLength;
	}

	public void setArrayVar(IVariableDeclaration arrayVar) {
		this.arrayVar = arrayVar;
	}

	public void setMatrixdefinition(MatrixDimension matrixdefinition) {
		this.matrixdefinition = matrixdefinition;
	}

	public boolean hasObjective() {
		return arrayVar != null;
	}	
}


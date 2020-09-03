package pt.iscte.paddle.codexplainer;

import pt.iscte.paddle.codexplainer.components.FVParameterComponent;
import pt.iscte.paddle.codexplainer.components.FVParameterComponent.MatrixDimension;
import pt.iscte.paddle.model.IArrayAllocation;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class FixedValueObjectiveVisitor implements IBlock.IVisitor {

	IVariableDeclaration var;
	FVParameterComponent parameterComponent;

	public FixedValueObjectiveVisitor(IVariableDeclaration var) {
		this.var = var;
		parameterComponent = new FVParameterComponent(var);
		var.getOwnerProcedure().accept(this);
	}

	@Override
	public boolean visit(IVariableAssignment assignment) {
		if (assignment.getExpression() instanceof IArrayAllocation) {
			IArrayAllocation all = (IArrayAllocation) assignment.getExpression();
			if (all.getDimensions().size() == 1) {
				if (all.getDimensions().get(0).isSame(var.expression()))
					parameterComponent.setArrayVar(assignment.getTarget());
			} else if (all.getDimensions().size() == 2) {
				parameterComponent.setDefinesMatrixLength(true);
				if (all.getDimensions().get(0).isSame(var.expression())) {
					parameterComponent.setArrayVar(assignment.getTarget());
					parameterComponent.setMatrixdefinition(MatrixDimension.ROWS);
				}	
				if (all.getDimensions().get(1).isSame(var.expression())) {
					parameterComponent.setArrayVar(assignment.getTarget());
					parameterComponent.setMatrixdefinition(MatrixDimension.ROWSLENGTH);
				}	
				if (all.getDimensions().get(0).isSame(var.expression()) 
						&& all.getDimensions().get(1).isSame(var.expression())) {
					parameterComponent.setArrayVar(assignment.getTarget());
					parameterComponent.setMatrixdefinition(MatrixDimension.BOTH);
				}
			}
		}
		return false;
	}

	public FVParameterComponent getParameterComponent() {
		return parameterComponent;
	}
}

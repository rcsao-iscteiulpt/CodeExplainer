package pt.iscte.paddle.model.demo2;
import java.util.List;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordFieldExpression;
import pt.iscte.paddle.model.IReferenceType;
import pt.iscte.paddle.model.IVariableDeclaration;

public interface IFunctionClassifier {

	enum Status {
		FUNCTION, PROCEDURE;
	}

	class Visitor implements IBlock.IVisitor {
		IVariableDeclaration var;

		Boolean isMemoryValueChanged = false;

		Visitor(IVariableDeclaration var) {
			this.var = var;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			//System.out.println(assignment);
			if (assignment.getTarget().equals(var)) {
				isMemoryValueChanged = true;
			}	
			return false;
			
		}
		@Override
		public boolean visit(IRecordFieldAssignment assignment) {
			//TODO Untested
			IRecordFieldExpression tempTarget = assignment.getTarget();
			
			
			while(tempTarget.getTarget() != null && !(tempTarget.getTarget() instanceof IVariableDeclaration)) {
				tempTarget = (IRecordFieldExpression) tempTarget.getTarget();
			}
			if (tempTarget.getTarget().equals(var)) {
				isMemoryValueChanged = true;
			}	
			return false;
			
		}
	}

	static Status getClassification(IProcedure method) {
		for(IVariableDeclaration var: method.getParameters()) {
			//System.out.println(var.getType());
			if(var.getType() instanceof IReferenceType) {
				//System.out.println(var);
				Visitor v = new Visitor(var);
				var.getOwnerProcedure().accept(v);
				if (v.isMemoryValueChanged) {
					return Status.PROCEDURE;
				}
			}	
		}
		return Status.FUNCTION;
		

	}

}

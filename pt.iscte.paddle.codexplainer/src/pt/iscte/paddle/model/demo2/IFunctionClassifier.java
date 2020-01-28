package pt.iscte.paddle.model.demo2;
import java.util.List;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;

public interface IFunctionClassifier {

	enum Status {
		FUNCTION, PROCEDURE;
	}

	class Visitor implements IBlock.IVisitor {
		IVariable var;

		Boolean isParameterValueChanged = false;

		Visitor(IVariable var) {
			this.var = var;
		}

		@Override
		public boolean visit(IVariableAssignment assignment) {
			//System.out.println(assignment);
			if (assignment.getTarget().equals(var)) {
				isParameterValueChanged = true;
			}	
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			//System.out.println(assignment);
			if (assignment.getTarget().equals(var)) {
				isParameterValueChanged = true;
			}	
			return false;
			
		}
	}

	static Status getClassification(List<IVariable> parameters) {
		for(IVariable var : parameters) {
			Visitor v = new Visitor(var);
			var.getOwnerProcedure().accept(v);
			if (v.isParameterValueChanged) {
				return Status.FUNCTION;
			}
		}
		return Status.PROCEDURE;
		

	}

}

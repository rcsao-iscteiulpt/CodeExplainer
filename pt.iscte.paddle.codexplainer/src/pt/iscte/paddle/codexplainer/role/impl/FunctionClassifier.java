package pt.iscte.paddle.codexplainer.role.impl;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordFieldExpression;
import pt.iscte.paddle.model.IReferenceType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;

public class FunctionClassifier implements IFunctionClassifier {

	List<IProgramElement> assignments;
	
	public FunctionClassifier(IProcedure proc2) {
		assignments = new ArrayList<IProgramElement>();
		for(IVariableDeclaration var: proc2.getParameters()) {
			Visitor v = new Visitor(var);
			proc2.accept(v);
			if(!v.assignments.isEmpty())
				assignments.addAll(v.assignments);
		}
		
	}
	
	
	class Visitor implements IBlock.IVisitor {
		IVariableExpression var;

		Boolean isMemoryValueChanged = false;
		List<IProgramElement> assignments = new ArrayList<IProgramElement>();

		Visitor(IVariableDeclaration var) {
			this.var = var.expression();
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			//System.out.println(assignment);
			if (assignment.getTarget().equals(var)) {
				assignments.add(assignment);
				isMemoryValueChanged = true;
			}	
			return false;
			
		}
		@Override
		public boolean visit(IRecordFieldAssignment assignment) {
			//TODO Untested
			IRecordFieldExpression tempTarget = assignment.getTarget();
			
			
			while(tempTarget.getTarget() != null && !(tempTarget.getTarget() instanceof IVariableExpression)) {
				tempTarget = (IRecordFieldExpression) tempTarget.getTarget();
			}
			if (tempTarget.getTarget().toString().equals(var.toString())) {
				assignments.add(assignment);
				isMemoryValueChanged = true;
			}	
			return false;
			
		}
		
		
	}
	
	public Status getClassification(IProcedure method) {
		for(IVariableDeclaration var: method.getParameters()) {
			//System.out.println(var.getType());
			if(var.getType() instanceof IReferenceType) {
				System.out.println(var);
				Visitor v = new Visitor(var);
				var.getOwnerProcedure().accept(v);
				if (v.isMemoryValueChanged) {
					return Status.PROCEDURE;
				}
			}	
		}
		return Status.FUNCTION;
	}
	
	public List<IProgramElement> getAssignments() {
		return assignments;	
	}
	
}

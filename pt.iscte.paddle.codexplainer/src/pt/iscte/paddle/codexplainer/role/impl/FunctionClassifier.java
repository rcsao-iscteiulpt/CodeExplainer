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
	
	MethodType classification = MethodType.FUNCTION;
	
	public FunctionClassifier(IProcedure method) {
	
		assignments = new ArrayList<IProgramElement>();
		for(IVariableDeclaration var: method.getParameters()) {
			if(var.getType() instanceof IReferenceType) {
				Visitor v = new Visitor(var);
				method.accept(v);
				
				if (v.isMemoryValueChanged) 
					classification = MethodType.PROCEDURE;
				
				if(!v.assignments.isEmpty())
					assignments.addAll(v.assignments);
			}
		
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
			if (assignment.getTarget().isSame(var)) {
				assignments.add(assignment);
				isMemoryValueChanged = true;
			}	
			return false;
			
		}
		@Override
		public boolean visit(IRecordFieldAssignment assignment) {
			IRecordFieldExpression tempTarget = assignment.getTarget();
			
			while(tempTarget.getTarget() != null && !(tempTarget.getTarget() instanceof IVariableExpression)) {
				tempTarget = (IRecordFieldExpression) tempTarget.getTarget();
			}
			if (tempTarget.getTarget().isSame(var)) {
				assignments.add(assignment);
				isMemoryValueChanged = true;
			}	
			return false;
			
		}
		
		
	}
	
	public MethodType getClassification() {
		return classification;
	}
	
	public List<IProgramElement> getAssignments() {
		return assignments;	
	}
	
}

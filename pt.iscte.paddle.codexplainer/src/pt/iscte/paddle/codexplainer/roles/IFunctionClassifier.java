package pt.iscte.paddle.codexplainer.roles;
import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordFieldExpression;
import pt.iscte.paddle.model.IReferenceType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;

public interface IFunctionClassifier {

	public enum MethodType {
		FUNCTION, PROCEDURE;
	}

	public MethodType getClassification();
	
	/**
	 * @return Returns a list containing all assignments which are deemed to be the reason of why the method is considered a procedure
	 * and returns an empty list if the method is a function.
	 */
	public List<IProgramElement> getAssignments();	

}

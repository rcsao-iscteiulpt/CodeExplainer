package pt.iscte.paddle.codexplainer.roles;
import java.util.List;


import pt.iscte.paddle.model.IProgramElement;


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

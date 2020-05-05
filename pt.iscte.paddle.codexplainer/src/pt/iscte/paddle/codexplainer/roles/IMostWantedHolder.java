package pt.iscte.paddle.codexplainer.roles;


import java.util.List;



import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IVariableRole;


public interface IMostWantedHolder extends IVariableRole {

	Objective getOperation();
	
	IVariableExpression getTargetArray();
	
	//IVariableExpression getIterator();
	
	
	/**
	 * returns a list containing expressions used to determine whether the variable is or not a MostWantedHolder.
	 * Useful for expression marking on Javardise.
	 * Order in List: 1- Selection Guard, 2- Loop Guard, 3- Max/Min value assignment. 
	 * @return
	 */
	List<IProgramElement> getExpressions();

	default String getName() {
		return "MostWantedHolder";
	}

	public enum Objective {
		GREATER, SMALLER, UNDEFINED;
	}

	enum VarPosition {
		RIGHT, LEFT, NONE;
	}
	
}

package pt.iscte.paddle.codexplainer.roles;


import java.util.List;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
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

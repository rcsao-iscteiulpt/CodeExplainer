package pt.iscte.paddle.codexplainer.roles;

import java.util.List;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;


public interface IRecursive {

	List<IProgramElement> getExpressions();
	
	ISelection getConditionElement();

}


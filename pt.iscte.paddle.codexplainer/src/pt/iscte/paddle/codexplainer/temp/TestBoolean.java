package pt.iscte.paddle.codexplainer.temp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IType.BOOLEAN;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;

// TODO if if
public class TestBoolean extends BaseTest {
	
	IProcedure testCondition = module.addProcedure(BOOLEAN);
	IVariableDeclaration condition = testCondition.getBody().addVariable(BOOLEAN);
	IVariableAssignment conditionAss = testCondition.getBody().addAssignment(condition, BOOLEAN.literal(true));
	IVariableExpression guard = condition.expression();
	ISelection ifElse = testCondition.getBody().addSelectionWithAlternative(guard);
	IReturn ret = ifElse.addReturn(BOOLEAN.literal(true));
	IBlock elseBlock = ifElse.getAlternativeBlock();
	IReturn retElse = elseBlock.addReturn(BOOLEAN.literal(false));
	

}

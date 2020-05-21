package pt.iscte.paddle.codexplainer.temp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;

// TODO if if
public class TestSelection extends BaseTest {
	
	IProcedure max = module.addProcedure(INT);
	IVariableDeclaration a = max.addParameter(INT);
	IVariableDeclaration b = max.addParameter(INT);
	IBinaryExpression guard = GREATER.on(a, b);
	ISelection ifElse = max.getBody().addSelectionWithAlternative(guard);
	IReturn ret = ifElse.addReturn(a);
	IBlock elseBlock = ifElse.getAlternativeBlock();
	IReturn retElse = elseBlock.addReturn(b);
	

}

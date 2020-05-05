package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.DIFFERENT;
import static pt.iscte.paddle.model.IType.DOUBLE;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.interpreter.IArray;
import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.cfg.IBranchNode;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.paddle.model.cfg.IStatementNode;

public class TestSum extends BaseTest {

	IProcedure summation = module.addProcedure(DOUBLE);
	IVariableDeclaration array = summation.addParameter(DOUBLE.array().reference());
	IBlock sumBody = summation.getBody();
	IVariableDeclaration sum = sumBody.addVariable(DOUBLE, DOUBLE.literal(0.0));
	IVariableDeclaration i = sumBody.addVariable(INT, INT.literal(0));
	ILoop loop = sumBody.addLoop(DIFFERENT.on(i, array.length()));
	IVariableAssignment ass1 = loop.addAssignment(sum, ADD.on(sum, array.element(i)));
	IVariableAssignment ass2 = loop.addIncrement(i);
	IReturn ret = sumBody.addReturn(sum);

	
}

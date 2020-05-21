package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.AND;
import static pt.iscte.paddle.model.IOperator.EQUAL;
import static pt.iscte.paddle.model.IOperator.NOT;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.BOOLEAN;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.cfg.IBranchNode;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.paddle.model.cfg.IStatementNode;

public class TestArrayFind extends BaseTest {

	IProcedure exists = module.addProcedure(BOOLEAN);
	IVariableDeclaration array = exists.addParameter(INT.array().reference());
	IVariableDeclaration e = exists.addParameter(INT);
	IBlock body = exists.getBody();
	IVariableDeclaration found = body.addVariable(BOOLEAN);
	IVariableAssignment foundAss = body.addAssignment(found, BOOLEAN.literal(false));
	IVariableDeclaration i = body.addVariable(INT);
	IVariableAssignment iAss = body.addAssignment(i, INT.literal(0));
	ILoop loop = body.addLoop(AND.on(NOT.on(found), SMALLER.on(i, array.length())));
	ISelection ifstat = loop.addSelection(EQUAL.on(array.element(i), e));
	IVariableAssignment foundAss_ = ifstat.addAssignment(found, BOOLEAN.literal(true));
	IVariableAssignment iInc = loop.addIncrement(i);
	IReturn ret = body.addReturn(found);


	
}

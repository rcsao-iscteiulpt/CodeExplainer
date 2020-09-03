package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestMaxMatrix extends BaseTest {

	IProcedure max = module.addProcedure(INT);
	IVariableDeclaration matrix = max.addParameter(INT.array().array().reference());
	IBlock body = max.getBody();
	IVariableDeclaration m = body.addVariable(INT, INT.literal(0));
	IVariableDeclaration i = body.addVariable(INT);
	IVariableAssignment iAss = body.addAssignment(i, INT.literal(0));
	ILoop loop = body.addLoop(SMALLER.on(i, matrix.element(INT.literal(0)).length()));
	ISelection ifstat = loop.addSelection(GREATER.on(matrix.element(INT.literal(0), i), m));
	IVariableAssignment mAss_ = ifstat.addAssignment(m, matrix.element(INT.literal(0),i));
	IVariableAssignment iInc = loop.addIncrement(i);
	IReturn ret = body.addReturn(m);
	
}

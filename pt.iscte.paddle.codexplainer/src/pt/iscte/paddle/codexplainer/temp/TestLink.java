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

public class TestLink extends BaseTest {

	IProcedure max = module.addProcedure(INT);
	IVariableDeclaration array = max.addParameter(INT.array().reference());
	IBlock body = max.getBody();
	IVariableDeclaration m = body.addVariable(INT, INT.literal(0));
	IVariableDeclaration i = body.addVariable(INT);
	IVariableAssignment iAss = body.addAssignment(i, INT.literal(0));
	ILoop loop = body.addLoop(SMALLER.on(i, array.length()));
	ISelection ifstat = loop.addSelection(GREATER.on(array.element(i), m));
	IVariableAssignment mAss_ = ifstat.addAssignment(m, array.element(i));
	IVariableAssignment iInc = loop.addIncrement(i);
	IReturn ret = body.addReturn(m);
	
}

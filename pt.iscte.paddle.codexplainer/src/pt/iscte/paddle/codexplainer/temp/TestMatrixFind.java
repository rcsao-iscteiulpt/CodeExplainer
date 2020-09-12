package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.EQUAL;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.BOOLEAN;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestMatrixFind extends BaseTest {

	IProcedure contains = module.addProcedure(BOOLEAN);
	IVariableDeclaration m = contains.addParameter(INT.array().array().reference());
	IVariableDeclaration e = contains.addParameter(INT);
	
	IBlock body = contains.getBody();
	
	IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
	
	ILoop loopI = body.addLoop(SMALLER.on(i, m.length()));
	IVariableDeclaration r = loopI.addVariable(INT, INT.literal(0));
	
	ILoop loopJ = loopI.addLoop(SMALLER.on(r, m.length(INT.literal(0))));
	ISelection ifstat = loopJ.addSelection(EQUAL.on(m.element(i,r), e));
	IReturn ret1 = ifstat.addReturn(BOOLEAN.literal(true));
	
	IVariableAssignment jAss = loopJ.addIncrement(r);
	IVariableAssignment iAss = loopI.addIncrement(i);
	
	
	IReturn ret2 = body.addReturn(BOOLEAN.literal(false));

	
}

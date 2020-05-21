package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.*;
import static pt.iscte.paddle.model.IType.*;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestIsPrime extends BaseTest {
	IProcedure isPrime = getModule().addProcedure(BOOLEAN);
	IVariableDeclaration n = isPrime.addParameter(INT);
	
	IBlock body = isPrime.getBody();
	IVariableDeclaration i = body.addVariable(INT);
	IVariableAssignment iass = body.addAssignment(i, INT.literal(2));
	
	ILoop loop = body.addLoop(SMALLER.on(i, n));
	ISelection ifstat = loop.addSelection(EQUAL.on(MOD.on(n, i), INT.literal(0)));
	IReturn ra = ifstat.addReturn(BOOLEAN.literal(false));
	
	IVariableAssignment iInc = loop.addIncrement(i);
	IReturn rb = body.addReturn(BOOLEAN.literal(true));
	

}
package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.*;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestAbs extends BaseTest {
	IProcedure abs = getModule().addProcedure(INT);
	IVariableDeclaration n = abs.addParameter(INT);
	
	ISelection ifstat = abs.getBody().addSelection(SMALLER.on(n, INT.literal(0)));
	IReturn ra = ifstat.addReturn(IOperator.MINUS.on(n));
	IReturn rb = abs.getBody().addReturn(n);
	

}
package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestMin extends BaseTest {
	IProcedure min = getModule().addProcedure(INT);
	IVariableDeclaration a = min.addParameter(INT);
	IVariableDeclaration b = min.addParameter(INT);
	IReturn ret = min.getBody().addReturn(SMALLER.on(a, b).conditional(a, b));
	

}

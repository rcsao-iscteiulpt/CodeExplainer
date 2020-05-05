package pt.iscte.paddle.codexplainer.temp;



import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestNaturals extends BaseTest  {
	IProcedure naturals = module.addProcedure(INT.array());
	IVariableDeclaration n = naturals.addParameter(INT);
	IBlock body = naturals.getBody();
	IVariableDeclaration array = body.addVariable(INT.array());
	IVariableAssignment ass1 = body.addAssignment(array, INT.array().stackAllocation(n));
	IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
	ILoop loop = body.addLoop(SMALLER.on(i, n));
	IArrayElementAssignment ass2 = loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
	IVariableAssignment ass3 = loop.addAssignment(i, ADD.on(i, INT.literal(1)));
	IReturn addReturn = body.addReturn(array);

}

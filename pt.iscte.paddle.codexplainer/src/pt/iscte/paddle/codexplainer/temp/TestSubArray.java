package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.*;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestSubArray extends BaseTest {
	IProcedure subArray = getModule().addProcedure(INT.array());
	IVariableDeclaration array = subArray.addParameter(INT.array());
	IVariableDeclaration from = subArray.addParameter(INT);
	IVariableDeclaration to = subArray.addParameter(INT);
	
	IBlock body = subArray.getBody();
	
	IVariableDeclaration sArray = body.addVariable(INT.array());
	IVariableAssignment assSubArray = body.addAssignment(sArray, 
			INT.array().stackAllocation(ADD.on(ADD.on(to, from), INT.literal(1))));
	
	IVariableDeclaration i = body.addVariable(INT);
	IVariableAssignment iAss = body.addAssignment(i, from);
	
	ILoop loop = body.addLoop(SMALLER_EQ.on(i, to));
	IArrayElementAssignment assSubArray2 = loop
			.addArrayElementAssignment(sArray, array.element(i), SUB.on(i, from));
	IVariableAssignment iAss2 = loop.addIncrement(i);
	IReturn rb = body.addReturn(sArray);
	

	
	
}
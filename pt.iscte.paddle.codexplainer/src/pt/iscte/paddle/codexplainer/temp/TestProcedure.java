package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.*;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestProcedure extends BaseTest {
	IProcedure multiplyArrayValues = getModule().addProcedure(IType.VOID);
	
	IVariableDeclaration array = multiplyArrayValues.addParameter(INT.array().reference());
	IVariableDeclaration n = multiplyArrayValues.addParameter(INT);
	
	IBlock body = multiplyArrayValues.getBody();
	
	IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
	ILoop loop = body.addLoop(SMALLER.on(i, array.length()));
	IArrayElementAssignment arrayAss = loop.addArrayElementAssignment(array, MUL.on(array.element(i), n),i);
	IVariableAssignment iass = loop.addIncrement(i);

}
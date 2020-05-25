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
	IProcedure proc = getModule().addProcedure(INT.array().reference());
	
	IRecordType Point = getModule().addRecordType();
	IVariableDeclaration x = Point.addField(IType.INT);
	IVariableDeclaration y = Point.addField(IType.INT);
//	IVariableDeclaration p = proc.addParameter(Point.reference());
	IVariableDeclaration array = proc.addParameter(INT.array().reference());
	
	IBlock body = proc.getBody();
	
	//IRecordFieldAssignment ass = body.addRecordFieldAssignment(p.field(x), IType.INT.literal(7));
	IArrayElementAssignment iAss = body.addArrayElementAssignment(array, INT.literal(1), INT.literal(0));
	
	IReturn rb = body.addReturn(array);
	

	
	
}
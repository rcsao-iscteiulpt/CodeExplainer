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

public class TestProcedureObjects extends BaseTest {
	IProcedure enlarge = getModule().addProcedure(IType.VOID);
	
	IRecordType rect = getModule().addRecordType();
	
	IVariableDeclaration height = rect.addField(IType.DOUBLE);
	IVariableDeclaration width = rect.addField(IType.DOUBLE);
	IVariableDeclaration perimeter = rect.addField(IType.DOUBLE);
	IVariableDeclaration area = rect.addField(IType.DOUBLE);
	
	IVariableDeclaration r = enlarge.addParameter(rect.reference());
	IVariableDeclaration n = enlarge.addParameter(INT);
	
	IBlock body = enlarge.getBody();
	
	IVariableDeclaration newHeight = body.addVariable(IType.DOUBLE, MUL.on(r.field(height), n));
	IVariableDeclaration newWidth = body.addVariable(IType.DOUBLE, MUL.on(r.field(width), n));
	
	IRecordFieldAssignment assHeight = body.addRecordFieldAssignment(r.field(height), newHeight);
	IRecordFieldAssignment assWidth = body.addRecordFieldAssignment(r.field(width), newWidth);

	

	
	
}
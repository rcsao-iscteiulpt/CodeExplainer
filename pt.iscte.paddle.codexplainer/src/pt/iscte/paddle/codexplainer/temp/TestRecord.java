package pt.iscte.paddle.codexplainer.temp;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;


public class TestRecord extends BaseTest {

	IRecordType object = module.addRecordType();
	IVariableDeclaration x = object.addField(IType.INT);
	
	IProcedure recordTest = module.addProcedure(IType.INT);
	IVariableDeclaration dec = recordTest.addParameter(object.reference());
	IBlock body = recordTest.getBody();
	IVariableDeclaration v = body.addVariable(object.reference());
	IVariableAssignment assDec = body.addAssignment(v, object.heapAllocation());
	IRecordFieldAssignment ass = body.addRecordFieldAssignment(dec.field(x), IType.INT.literal(7));
	IReturn addReturn = body.addReturn(dec.field(x));
}

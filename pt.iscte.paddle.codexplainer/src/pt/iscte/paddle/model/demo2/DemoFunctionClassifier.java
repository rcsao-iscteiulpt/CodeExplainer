package pt.iscte.paddle.model.demo2;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;


public class DemoFunctionClassifier {

	public static void main(String[] args) {
		//Translator translator = new Translator(new File("max.javali").getAbsolutePath());
		IModule module = IModule.create();
		
		
		IRecordType objTypeParent = module.addRecordType();
		IRecordType objType = module.addRecordType();
		IVariableDeclaration a = objTypeParent.addField(objType);
		a.setId("field1");
		
		IVariableDeclaration b = objType.addField(IType.INT);
		b.setId("field2");
		
		IProcedure proc2 = module.addProcedure(IType.VOID);
		IBlock block2 = proc2.getBody();
		IVariableDeclaration param = proc2.addParameter(objType.reference());
		IVariableDeclaration param2 = proc2.addParameter(objTypeParent.reference());
		param.setId("parameter");
		IRecordFieldAssignment recordAssignment =  block2.addRecordFieldAssignment(param2.field(a).field(b), IType.INT.literal(7));
		//IRecordFieldAssignment recordAssignment2 =  block2.addRecordFieldAssignment(recordAssignment, IType.INT.literal(7));
		System.out.println(recordAssignment.getField());
		System.out.println(recordAssignment.getTarget().getTarget());
		System.out.println(recordAssignment.getTarget());
		
		//objType.addField(IType.INT);
		System.out.println(proc2);

		
		
		

		IProcedure method = module.getProcedures().iterator().next();
		System.out.println(IFunctionClassifier.getClassification(method));
	}
}
	


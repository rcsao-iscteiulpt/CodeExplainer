package pt.iscte.paddle.model.demo2;
import java.io.File;

import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordFieldExpression;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.roles.IVariableRole;


public class DemoFunctionClassifier {

	public static void main(String[] args) {
		//Translator translator = new Translator(new File("max.javali").getAbsolutePath());
		IModule module = IModule.create();
		
		
		IRecordType objTypeParent = module.addRecordType();
		IRecordType objType = module.addRecordType();
		
		
		objType.setId("Object");
		IVariable element = objType.addField(IType.INT);
		element.setId("field");
		
		IProcedure proc2 = module.addProcedure(IType.VOID);
		IBlock block2 = proc2.getBody();
		IVariable param = proc2.addParameter(objType.reference());
		IVariable param2 = proc2.addParameter(objTypeParent.reference());
		param.setId("parameter");
		IRecordFieldAssignment recordAssignment =  block2.addRecordFieldAssignment(param.field(element), IType.INT.literal(7));
		//IRecordFieldAssignment recordAssignment2 =  block2.addRecordFieldAssignment(recordAssignment, IType.INT.literal(7));
		System.out.println(recordAssignment.getField());
		System.out.println(recordAssignment.getTarget().getTarget());
		
		//objType.addField(IType.INT);
		System.out.println(proc2);

		
		
		

		IProcedure method = module.getProcedures().iterator().next();
		System.out.println(IFunctionClassifier.getClassification(method));
	}
}
	


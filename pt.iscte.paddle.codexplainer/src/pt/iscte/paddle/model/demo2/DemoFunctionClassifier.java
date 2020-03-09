package pt.iscte.paddle.model.demo2;

import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.codexplainer.role.impl.FunctionClassifier;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier;
import pt.iscte.paddle.model.IBinaryOperator;
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
		IVariableDeclaration param2 = proc2.addParameter(objTypeParent.reference());
		param2.setId("parameter");
		IRecordFieldAssignment recordAssignment =  block2.addRecordFieldAssignment(param2.field(a).field(b), IType.INT.literal(7));
		//IRecordFieldAssignment recordAssignment2 =  block2.addRecordFieldAssignment(recordAssignment, IType.INT.literal(7));
	
		//objType.addField(IType.INT);
		System.out.println(proc2);

		
		
		
		IFunctionClassifier classifier = new FunctionClassifier(proc2);
		IProcedure method = module.getProcedures().iterator().next();
		System.out.println(classifier.getClassification(method));
	}
}
	


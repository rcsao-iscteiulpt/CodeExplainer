package pt.iscte.paddle.model.demo2;
import java.io.File;

import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordFieldExpression;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.roles.IVariableRole;


public class DemoFunctionClassifier {

	public static void main(String[] args) {
		Translator translator = new Translator(new File("max.javali").getAbsolutePath());
		IModule module = translator.createProgram();
		
		IRecordType objType = module.addRecordType();
		IVariable element = objType.addField(IType.INT);
	
		/*
		IProcedure proc2 = module.addProcedure(IType.VOID);
		IVariable param = proc2.addParameter(objType);
		IRecordFieldExpression ex = new RecordFieldExpression(param, element);

		objType.addField(IType.INT);
		proc2.getBody().addRecordFieldAssignment();
		System.out.println(proc2);

		*/
		
		

		IProcedure method = module.getProcedures().iterator().next();
		System.out.println(IFunctionClassifier.getClassification(method));
	}
}
	


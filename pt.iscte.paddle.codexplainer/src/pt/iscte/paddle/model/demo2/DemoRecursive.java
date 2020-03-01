package pt.iscte.paddle.model.demo2;

import java.io.File;

import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariable;

public class DemoRecursive {
	
	
	public static void main(String[] args) {
		Translator translator = new Translator(new File("TestFunctions/recursive.javali").getAbsolutePath());
		IModule module = translator.createProgram();
		IProcedure recursive = module.getProcedures().iterator().next();
		
		
		//objType.addField(IType.INT);
		System.out.println(IRecursive.isRecursive(recursive));

		
		
		


	}
}

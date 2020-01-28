package pt.iscte.paddle.model.demo2;
import java.io.File;

import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.roles.IGatherer;
import pt.iscte.paddle.roles.IVariableRole;



public class DemoMostWantedHolder {

	public static void main(String[] args) {
		Translator translator = new Translator(new File("max.javali").getAbsolutePath());
		IModule module = translator.createProgram();
		IProcedure sum = module.getProcedures().iterator().next(); // first procedure
		System.out.println(sum);

		for (IVariable var : sum.getVariables()) {
			System.out.println(var);
			if(IMostWantedHolder.isMostWantedHolder(var)) {
				IVariableRole g = IMostWantedHolder.createMostWantedHolder(var);
				System.out.println(var + ": " + g);
			}
		}

	}
}

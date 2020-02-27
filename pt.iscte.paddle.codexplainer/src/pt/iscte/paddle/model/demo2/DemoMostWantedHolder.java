package pt.iscte.paddle.model.demo2;
import java.io.File;

import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.roles.IVariableRole;




public class DemoMostWantedHolder {

	public static void main(String[] args) {
		Translator translator = new Translator(new File("max.javali").getAbsolutePath());
		IModule module = translator.createProgram();
		IProcedure sum = module.getProcedures().iterator().next(); // first procedure
		System.out.println(sum);

		for (IVariable var : sum.getVariables()) {
			System.out.println(var);
			if(MostWantedHolder.isMostWantedHolder(var)) {
				IVariableRole g = new MostWantedHolder(var);
				IMostWantedHolder g1 = (IMostWantedHolder) g;
				System.out.println(g1.getOperation());
				System.out.println(var + ": " + g);
			}
		}

	}
}

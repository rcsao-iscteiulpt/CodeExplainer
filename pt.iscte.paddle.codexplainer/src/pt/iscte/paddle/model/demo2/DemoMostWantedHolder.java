package pt.iscte.paddle.model.demo2;
import java.io.File;

import pt.iscte.paddle.codexplainer.temp.TestMaxArray;
import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.IVariableRole;




public class DemoMostWantedHolder {

	public static void main(String[] args) {
		TestMaxArray t = new TestMaxArray();
		t.setup();
		IModule module = t.getModule();
		IProcedure proc = module.getProcedure("max");

		System.out.println(proc);

		for (IVariableDeclaration var : proc.getVariables()) {
			System.out.println(var);
			if(MostWantedHolder.isMostWantedHolder(var)) {
				System.out.println("yes");
				IVariableRole g = new MostWantedHolder(var);
				IMostWantedHolder g1 = (IMostWantedHolder) g;
				System.out.println(var + ": " + g);
			}
		}

	}
}

package pt.iscte.paddle.model.demo2;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.roles.impl.Gatherer;
import pt.iscte.paddle.model.roles.IVariableRole;


public class DemoGatherer {

	public static void main(String[] args) {
		IModule module = IModule.create();
		IProcedure sum = Examples.createArraySumFunction(module);

		for (IVariable var : sum.getVariables()) {
			if(Gatherer.isGatherer(var)) {
				IVariableRole g = new Gatherer(var);
				System.out.println(var + ": " + g);
			}
		}
	}
}

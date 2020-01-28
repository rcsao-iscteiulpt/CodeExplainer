package pt.iscte.paddle.codexplainer;


import java.util.Map;




import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.demo2.IMostWantedHolder;
import pt.iscte.paddle.roles.IVariableRole;
import pt.iscte.paddle.semantics.java.VariableScope;


public class Analyzer {
	

	 static Map<IVariable, IVariableRole> variables;
	 ISelection[] selections;
	 IReturn[] returns;
	

	static void AnalyzeFuntion(IProcedure procedure) {

		for(IVariable var : procedure.getVariables()) {
			IVariableRole role = getVariableRole(var);
			variables.put(var, role);
		}
	
	}

	private static IVariableRole getVariableRole(IVariable var) {

		if(IMostWantedHolder.isMostWantedHolder(var)) {
			return IMostWantedHolder.createMostWantedHolder(var);
		}
		
//		if(IGatherer.isGatherer(var)) {
//			return IGatherer.createGatherer(var);
//		}
//      etc....
		return null;
	}
}

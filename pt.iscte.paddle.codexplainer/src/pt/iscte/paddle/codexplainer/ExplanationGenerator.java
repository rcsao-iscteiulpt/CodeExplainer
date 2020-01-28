package pt.iscte.paddle.codexplainer;


import java.io.File;
import java.util.Map;

import pt.iscte.paddle.javali.translator.Translator;

import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.paddle.model.demo2.IMostWantedHolder;
import pt.iscte.paddle.roles.IVariableRole;


public class ExplanationGenerator {
	
    static Map<IVariable, IVariableRole> localVariables;
	
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
	
	

	public static void main(String[] args) {
		Translator translator = new Translator(new File("max.javali").getAbsolutePath());
		IModule module = translator.createProgram();
		IProcedure method = module.getProcedures().iterator().next(); // first procedure
		//IProcedure max = module.getProcedure();
		//System.out.println(method);
		
		//IControlFlowGraph cfg = IControlFlowGraph.create(method);
		//CFGGeneration.getControlFlowGraph(cfg, method.getBody());
		
//		for(IVariable var : method.getVariables()) {
//			System.out.println(var);
//			
//			IVariableRole role = getVariableRole(var);
//			System.out.println(role);
//			if(role != null) {
//				localVariables.put(var, role);
//			}
//		}
		
		//System.out.println(localVariables);
		
		String explanation = NLTranslator.getExplanation(method.getBody());
		
		System.out.println(explanation);
		
		
		
	}
}

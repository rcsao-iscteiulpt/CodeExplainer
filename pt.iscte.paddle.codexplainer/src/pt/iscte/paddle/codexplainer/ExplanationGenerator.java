package pt.iscte.paddle.codexplainer;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pt.iscte.paddle.javali.translator.Translator;

import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.paddle.model.demo2.IMostWantedHolder;
import pt.iscte.paddle.model.demo2.MostWantedHolder;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.model.demo2.IVariableRoleExplainer;


public class ExplanationGenerator {
	
    static Map<IVariable, String> variablesRolesExplanation = new HashMap<IVariable, String>();
	
	private static void getVariableRole(IVariable var) {

		if(MostWantedHolder.isMostWantedHolder(var)) {
			//System.out.println(var);
			IVariableRole role =  new MostWantedHolder(var);
			variablesRolesExplanation.put(var, IVariableRoleExplainer.getMostWantedHolderExplanation(role));
		}
		
//		if(IGatherer.isGatherer(var)) {
//			return IGatherer.createGatherer(var);
//		}
//      etc....
		return;
	}
	
	

	public static void main(String[] args) {
		Translator translator = new Translator(new File("TestFunctions/max.javali").getAbsolutePath());
		IModule module = translator.createProgram();
		IRecordType objType = module.addRecordType();
		IVariable element = objType.addField(IType.INT);
		

		IProcedure method = module.getProcedures().iterator().next(); // first procedure
		method.addParameter(objType);
		//IProcedure max = module.getProcedure();
		//System.out.println(method);
		
		//IControlFlowGraph cfg = IControlFlowGraph.create(method);
		//CFGGeneration.getControlFlowGraph(cfg, method.getBody());
		
		for(IVariable var : method.getVariables()) {
			//System.out.println(var);
			
			getVariableRole(var);
		}
		
		//System.out.println(localVariables);
		
		String explanation = NLTranslator.getExplanation(method.getBody(), variablesRolesExplanation);
		
		System.out.println(explanation);
		
		
		
	}
}

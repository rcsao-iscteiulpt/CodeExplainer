package pt.iscte.paddle.codexplainer;


import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.demo2.MostWantedHolder;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.model.demo2.VariableRoleExplainer;


public class ExplanationGenerator {
	
    static Map<IVariableDeclaration, String> variablesRolesExplanation = new HashMap<IVariableDeclaration, String>();
	
	private static void getVariableRole(IVariableDeclaration var) {

		if(MostWantedHolder.isMostWantedHolder(var)) {
			System.out.println(var);
			IVariableRole role =  new MostWantedHolder(var);
			variablesRolesExplanation.put(var, VariableRoleExplainer.getMostWantedHolderExplanation(role));
		}
		
//		if(IGatherer.isGatherer(var)) {
//			return IGatherer.createGatherer(var);
//		}
//      etc....
		return;
	}
	
	

	public static void main(String[] args) {
		IModule module = IModule.create();
		IProcedure max = module.addProcedure(INT);
		IVariableDeclaration array = max.addParameter(INT.array().reference());
		array.setId("v");
		
		IBlock body = max.getBody();
		
		IVariableDeclaration m = body.addVariable(INT);
		m.setId("m");
		IVariableAssignment mAss = body.addAssignment(m, array.element(INT.literal(0)));
		
		IVariableDeclaration i = body.addVariable(INT);
		i.setId("i");
		IVariableAssignment iAss = body.addAssignment(i, INT.literal(1));
		
		ILoop loop = body.addLoop(SMALLER.on(i, array.length()));
		ISelection ifstat = loop.addSelection(GREATER.on(array.element(i), m));
		IVariableAssignment mAss_ = ifstat.addAssignment(m, array.element(i));
		IVariableAssignment iInc = loop.addIncrement(i);
		IReturn ret = body.addReturn(m);
		
		//Translator translator = new Translator(new File("TestFunctions/max.javali").getAbsolutePath());
		//IModule module = translator.createProgram();
		IProcedure method = module.getProcedures().iterator().next(); // first procedure
	
		
		
		
		IRecordType objType = module.addRecordType();
		IVariableDeclaration element = objType.addField(IType.INT);
		

		//IProcedure method = module.getProcedures().iterator().next(); // first procedure
		//method.addParameter(objType);
		//IProcedure max = module.getProcedure();
		//System.out.println(method);
		
		//IControlFlowGraph cfg = IControlFlowGraph.create(method);
		//CFGGeneration.getControlFlowGraph(cfg, method.getBody());
		
		for(IVariableDeclaration var : method.getVariables()) {
			//System.out.println(var);	
			getVariableRole(var);
		}
		
		//System.out.println(localVariables);
		String explanation = NLTranslator.getExplanation(method.getBody(), variablesRolesExplanation);	
		System.out.println(explanation);
		
	}
}

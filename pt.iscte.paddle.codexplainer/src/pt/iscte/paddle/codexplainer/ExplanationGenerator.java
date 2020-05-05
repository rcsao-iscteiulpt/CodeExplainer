package pt.iscte.paddle.codexplainer;


import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Text;

import pt.iscte.paddle.codexplainer.components.AssignmentComponent;
import pt.iscte.paddle.codexplainer.components.Component;
import pt.iscte.paddle.codexplainer.components.LoopComponent;
import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.SelectionComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.codexplainer.translator.TranslatorAssignmentComponentPT;
import pt.iscte.paddle.codexplainer.translator.TranslatorLoopComponentPT;
import pt.iscte.paddle.codexplainer.translator.TranslatorMethodComponentPT;
import pt.iscte.paddle.codexplainer.translator.TranslatorReturnComponentPT;
import pt.iscte.paddle.codexplainer.translator.TranslatorSelectionComponentPT;
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
import pt.iscte.paddle.model.roles.impl.MostWantedHolder;
import pt.iscte.paddle.model.roles.*;
import pt.iscte.paddle.model.roles.impl.*;
import pt.iscte.paddle.model.demo2.VariableRoleExplainer;


public class ExplanationGenerator {
	
	
	static List<VariableRoleComponent> variablesRolesExplanation = new ArrayList<VariableRoleComponent>();
	IProcedure method;
	List<List<TextComponent>> explanation = new ArrayList<>();
	
	public ExplanationGenerator(IProcedure method) {
		this.method = method;
		generateExplanation(method);
	}
	
	public void generateExplanation(IProcedure proc) {
		for (IVariableDeclaration var : proc.getVariables()) {
			getVariableRole(var);
		}
		
		List<Component> components = new ArrayList<>(); 
		components.add(new MethodComponent(proc));
		new ComponentsVisitor(proc.getBody(), components, variablesRolesExplanation);
		
		
		addTextComponents(components);
		
//		for(Component c: components) {
//			System.out.println(c);
//			if(c instanceof LoopComponent)
//				System.out.println(((LoopComponent) c).getBranchComponents());
//		}
		
		return;	
	}
	
	public List<List<TextComponent>> getExplanation() {
		return explanation;
	}

	public void addTextComponents(List<Component> components) {
		for(Component c: components) {
			if(c instanceof MethodComponent) {
				TranslatorMethodComponentPT t = new TranslatorMethodComponentPT((MethodComponent) c);
				t.translatePT();
				explanation.add(t.getExplanationByComponents());			
			}
			if(c instanceof LoopComponent) {
				TranslatorLoopComponentPT t = new TranslatorLoopComponentPT((LoopComponent) c);
				t.translatePT();
				explanation.add(t.getExplanationByComponents());
				addTextComponents(((LoopComponent) c).getBranchComponents());
			}
			if(c instanceof SelectionComponent) {
				TranslatorSelectionComponentPT t = new TranslatorSelectionComponentPT((SelectionComponent) c);
				t.translatePT();
				explanation.add(t.getExplanationByComponents());
				addTextComponents(((SelectionComponent) c).getBranchComponents());			
			}
			if(c instanceof AssignmentComponent) {
				TranslatorAssignmentComponentPT t = new TranslatorAssignmentComponentPT((AssignmentComponent) c);
				t.translatePT();
				explanation.add(t.getExplanationByComponents());	
				
				AssignmentComponent comp = (AssignmentComponent) c;
				if(comp.isDeclaration()) {
					for(VariableRoleComponent v: variablesRolesExplanation) {
						if(comp.getTarget().isSame(v.getVar().expression())) {
							List<TextComponent> line = new ArrayList<>();
							line.add(new TextComponent(VariableRoleExplainer.getRoleExplanationPT(v.getVar(), v.getRole()), TextType.NORMAL));
							explanation.add(line);
						}
					}
				}
					
			}
			
			if(c instanceof ReturnComponent) {
				TranslatorReturnComponentPT t = new TranslatorReturnComponentPT((ReturnComponent) c);
				t.translatePT();
				explanation.add(t.getExplanationByComponents());	
			}
		}
		
		return;
		
	}
	
	
	private static void getVariableRole(IVariableDeclaration var) {
		IVariableRole role = IVariableRole.match(var);
		//System.out.println("Var:  "+ var + "|| role: "+role);
		
		if(role instanceof MostWantedHolder) {
			variablesRolesExplanation.add(new VariableRoleComponent(var, role, VariableRoleExplainer.getRoleExplanationPT(var, (MostWantedHolder) role)));
		}
		if(role instanceof ArrayIndexIterator) {
			variablesRolesExplanation.add(new VariableRoleComponent(var, role, VariableRoleExplainer.getRoleExplanationPT(var, (ArrayIndexIterator) role)));
		}
		if(role instanceof Gatherer) {
			variablesRolesExplanation.add(new VariableRoleComponent(var, role, VariableRoleExplainer.getRoleExplanationPT(var, (Gatherer) role)));
		}
		if(role instanceof Stepper) {
			variablesRolesExplanation.add(new VariableRoleComponent(var, role, VariableRoleExplainer.getRoleExplanationPT(var, (Stepper) role)));
		}
		if(role instanceof FixedValue) {
			variablesRolesExplanation.add(new VariableRoleComponent(var, role, VariableRoleExplainer.getRoleExplanationPT(var, (FixedValue) role)));
		}
		//etc....
		return;
	}
	
	
	
	
	
	
	
	//For testing purposes
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
		
		for(IVariableDeclaration var : method.getVariables()) {
			//System.out.println(var);	
			getVariableRole(var);
		}
		
		
	
	}
}

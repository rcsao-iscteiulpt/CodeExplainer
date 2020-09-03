package pt.iscte.paddle.codexplainer;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.AssignmentComponent;
import pt.iscte.paddle.codexplainer.components.Component;
import pt.iscte.paddle.codexplainer.components.FVParameterComponent;
import pt.iscte.paddle.codexplainer.components.LoopComponent;
import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.ProcedureCallComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.SelectionComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.codexplainer.translator.TranslatorAssignmentComponentPT;
import pt.iscte.paddle.codexplainer.translator.TranslatorLoopComponentPT;
import pt.iscte.paddle.codexplainer.translator.TranslatorMethodComponentPT;
import pt.iscte.paddle.codexplainer.translator.TranslatorProcedureCallComponentPT;
import pt.iscte.paddle.codexplainer.translator.TranslatorReturnComponentPT;
import pt.iscte.paddle.codexplainer.translator.TranslatorSelectionComponentPT;
import pt.iscte.paddle.codexplainer.translator.VariableRoleExplainer;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.*;

public class ExplanationGenerator {

	static List<VariableRoleComponent> variablesRoles = new ArrayList<VariableRoleComponent>();
	MethodComponent mc;

	List<ReturnComponent> returnList = new ArrayList<>();
	List<List<TextComponent>> explanation = new ArrayList<>();

	private int depthLevel = 0;
	private List<IProgramElement> lastBranchElement = new ArrayList<IProgramElement>();
	private boolean addElse = false;

	public ExplanationGenerator(IProcedure method) {
		generateExplanation(method);
	}

	public void generateExplanation(IProcedure proc) {
		List<FVParameterComponent> fixedValueParameters = new ArrayList<FVParameterComponent>();
		if(!proc.getVariables().isEmpty()) {
			for (IVariableDeclaration var : proc.getVariables()) {
				IVariableRole role = IVariableRole.match(var);
				variablesRoles.add(new VariableRoleComponent(var, role));
			}
		}
		if(!proc.getParameters().isEmpty()) {
			for (IVariableDeclaration var : proc.getParameters()) {
				IVariableRole role = IVariableRole.match(var);
				variablesRoles.add(new VariableRoleComponent(var, role));
				
				if(role instanceof IFixedValue) {
					IFixedValue f = (IFixedValue) role;
					if(!f.isModified()) {
						FixedValueObjectiveVisitor v = new FixedValueObjectiveVisitor(var);
						fixedValueParameters.add(v.getParameterComponent());
					}
				}
			}
			
		}
		
		List<Component> components = new ArrayList<>(); 
		
		mc = new MethodComponent(proc, fixedValueParameters);
		
		new ComponentsVisitor(proc.getBody(), components, variablesRoles, mc);
		
		getReturnComponents(components);
		
		TranslatorMethodComponentPT tm = new TranslatorMethodComponentPT(mc,returnList);
		tm.translatePT();
		explanation.addAll(tm.getExplanationByComponents());
		
		List<TextComponent> space = new ArrayList<TextComponent>();
		explanation.add(space);
		
		addTextComponents(components);
		
		ExplanationTextSmoother smoother = new ExplanationTextSmoother(proc.getVariables());
		smoother.SmoothText(explanation);
		
		return;	
	}

	public void addTextComponents(List<Component> components) {
		for (Component c : components) {
			if (c instanceof LoopComponent) {
				ILoop loop = (ILoop) c.getElement();
				lastBranchElement.add(loop.getBlock().getChildren().get(loop.getBlock().getChildren().size() - 1));

				
				
				TranslatorLoopComponentPT t = new TranslatorLoopComponentPT((LoopComponent) c, depthLevel);
				t.translatePT();
				explanation.addAll(t.getExplanationByComponents());

				depthLevel++;
				addTextComponents(((LoopComponent) c).getBranchComponents());

				CheckBranch(c);

			}
			if (c instanceof SelectionComponent) {
				ISelection sel = (ISelection) c.getElement();

				if (sel.getAlternativeBlock() != null && !sel.getAlternativeBlock().getChildren().isEmpty()) {
					lastBranchElement.add(sel.getAlternativeBlock().getChildren()
							.get(sel.getAlternativeBlock().getChildren().size() - 1));
				} else {
					lastBranchElement.add(sel.getBlock().getChildren().get(sel.getBlock().getChildren().size() - 1));
				}

				TranslatorSelectionComponentPT t = new TranslatorSelectionComponentPT((SelectionComponent) c,
						depthLevel);
				t.translatePT();
				explanation.addAll(t.getExplanationByComponents());

				depthLevel++;
				addTextComponents(((SelectionComponent) c).getBranchComponents());

				if (!((SelectionComponent) c).getAlternativeBranchComponents().isEmpty()) {
					List<TextComponent> line = new ArrayList<TextComponent>();

					line.add(new TextComponent("Caso contrário:"));
					explanation.add(line);
					addTextComponents(((SelectionComponent) c).getAlternativeBranchComponents());
				}
				
				CheckBranch(c);
				
				if(returnList.size() == 2) {
					IReturn ret1 = (IReturn) returnList.get(0).getElement();
					IReturn ret2 = (IReturn) returnList.get(1).getElement();
					if(ret1.getParent().getParent().equals(c.getElement()) 
							&& ret2.getParent().getParent().equals(((ISelection)c.getElement()).getParent().getParent()) ) {
						//addElse = true;
						List<TextComponent> line = new ArrayList<TextComponent>();
						line.add(new TextComponent("Caso contrário:"));
						explanation.add(line);
					}
				}

			}
			if (c instanceof AssignmentComponent) {
				AssignmentComponent comp = (AssignmentComponent) c;
				List<TextComponent> line = new ArrayList<TextComponent>();
			

				TranslatorAssignmentComponentPT t = new TranslatorAssignmentComponentPT((AssignmentComponent) c,
						depthLevel);
				t.translatePT();
				line.addAll(t.getExplanationByComponents().get(0));
				explanation.add(line);
				
				
				if (comp.isDeclaration()) {
					for (VariableRoleComponent v : variablesRoles) {
						if (comp.getTarget().isSame(v.getVar().expression()) && !(v.getRole() instanceof IArrayIndexIterator)) {
							line.addAll(VariableRoleExplainer.getRoleExplanationPT(v.getVar(), v.getRole()));
							break;
						}
					}
				}

				CheckBranch(c);
			}
			if (c instanceof ProcedureCallComponent) {
				ProcedureCallComponent procCall = (ProcedureCallComponent) c;

				TranslatorProcedureCallComponentPT t = new TranslatorProcedureCallComponentPT(
						(ProcedureCallComponent) c, depthLevel);
				t.translatePT();
				explanation.addAll(t.getExplanationByComponents());

				CheckBranch(c);
			}

			if (c instanceof ReturnComponent) {
				ReturnComponent ret = (ReturnComponent) c;

				
//				if(addElse && returnList.get(1).equals(ret)) {
//					List<TextComponent> line = new ArrayList<TextComponent>();
//					line.add(new TextComponent("Caso contrário:"));
//					explanation.add(line);
//					addElse = false;
//				}
				
				TranslatorReturnComponentPT t = new TranslatorReturnComponentPT((ReturnComponent) c, returnList, depthLevel);
				t.translatePT();
				explanation.addAll(t.getExplanationByComponents());

				CheckBranch(c);
			}
		}

		return;

	}

	void getReturnComponents(List<Component> components) {
		for (Component c : components) {
			if (c instanceof LoopComponent) {
				ILoop loop = (ILoop) c.getElement();
				getReturnComponents(((LoopComponent) c).getBranchComponents());
			}
			if (c instanceof SelectionComponent) {
				ISelection sel = (ISelection) c.getElement();
				getReturnComponents(((SelectionComponent) c).getBranchComponents());
				if (!((SelectionComponent) c).getAlternativeBranchComponents().isEmpty()) {

					getReturnComponents(((SelectionComponent) c).getAlternativeBranchComponents());
				}
			}

			if (c instanceof ReturnComponent) {
				ReturnComponent ret = (ReturnComponent) c;
				returnList.add(ret);
			}
		}
		return;
	}

	void CheckBranch(Component c) {
		for (IProgramElement e : lastBranchElement) {
			if (c.getElement().isSame(e)) {
				depthLevel--;
				lastBranchElement.remove(e);
				return;
			}
		}
	}


	public List<List<TextComponent>> getExplanation() {
		return explanation;
	}

//	//For testing purposes
//	public static void main(String[] args) {
//		IModule module = IModule.create();
//		IProcedure max = module.addProcedure(INT);
//		IVariableDeclaration array = max.addParameter(INT.array().reference());
//		array.setId("v");
//		
//		IBlock body = max.getBody();
//		
//		IVariableDeclaration m = body.addVariable(INT);
//		m.setId("m");
//		IVariableAssignment mAss = body.addAssignment(m, array.element(INT.literal(0)));
//		
//		IVariableDeclaration i = body.addVariable(INT);
//		i.setId("i");
//		IVariableAssignment iAss = body.addAssignment(i, INT.literal(1));
//		
//		ILoop loop = body.addLoop(SMALLER.on(i, array.length()));
//		ISelection ifstat = loop.addSelection(GREATER.on(array.element(i), m));
//		IVariableAssignment mAss_ = ifstat.addAssignment(m, array.element(i));
//		IVariableAssignment iInc = loop.addIncrement(i);
//		IReturn ret = body.addReturn(m);
//		
//		//Translator translator = new Translator(new File("TestFunctions/max.javali").getAbsolutePath());
//		//IModule module = translator.createProgram();
//		IProcedure method = module.getProcedures().iterator().next(); // first procedure
//	
//		
//		
//		
//		IRecordType objType = module.addRecordType();
//		IVariableDeclaration element = objType.addField(IType.INT);
//		
//		for(IVariableDeclaration var : method.getVariables()) {
//			//System.out.println(var);	
//			getVariableRole(var);
//		}
//
//	}
}

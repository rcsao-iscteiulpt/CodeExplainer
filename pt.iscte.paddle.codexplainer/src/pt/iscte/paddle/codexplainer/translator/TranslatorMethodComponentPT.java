package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.FVParameterComponent;
import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.model.roles.IFunctionClassifier.Status;
import pt.iscte.paddle.model.roles.IGatherer.Operation;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.impl.PrimitiveType;
import pt.iscte.paddle.model.roles.*;

public class TranslatorMethodComponentPT implements TranslatorPT {

	MethodComponent comp;
	List<List<TextComponent>> explanationByComponents = new ArrayList<>();
	List<ReturnComponent> returnList = new ArrayList<>();
	List<IVariableDeclaration> parametersMencioned;

	public TranslatorMethodComponentPT(MethodComponent comp, List<ReturnComponent> returnList) {
		this.comp = comp;
		this.returnList = returnList;
		parametersMencioned = comp.getParametersMencioned();
	}

	@Override
	public void translatePT() {
		List<TextComponent> firstLine = new ArrayList<TextComponent>();
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(firstLine);
		List<IVariableDeclaration> parameters = comp.getParameters();
		Status classifyType = comp.getFunctionClassifier().getClassification();

		String s1 = "o ";
		String s2 = "e ";
		if (classifyType.equals(Status.FUNCTION)) {
			s1 = "a ";
			s2 = "a ";
		}

		// identify if the conjuciton "e" is used
		boolean isEUsed = false;

		firstLine.add(new TextComponent("Est" + s2));
		t.translateMethodType(classifyType);

		List<IVariableDeclaration> modifiedVariables = new ArrayList<>();
		if (classifyType.equals(Status.PROCEDURE)) {
			modifiedVariables = comp.getFunctionClassifier().getVariables();
			firstLine.add(new TextComponent(" "));
			firstLine.add(new TextComponent("altera", comp.getFunctionClassifier().getExpressions()));
			firstLine.add(new TextComponent(" o "));
			for (int i = 0; i < comp.getFunctionClassifier().getVariables().size(); i++) {
				IVariableDeclaration v = comp.getFunctionClassifier().getVariables().get(i);
				t.translateIType(v.getType(), true);

				t.linkVariable(v.expression());
				if (comp.getParameters().contains(v)) {
					parametersMencioned.add(v);
				}

				if (i < comp.getFunctionClassifier().getVariables().size() - 2) {
					firstLine.add(new TextComponent(", "));
				}

				if (i == comp.getFunctionClassifier().getVariables().size() - 2) {
					firstLine.add(new TextComponent("e "));
				}
			}

			if (comp.getFunctionClassifier().getVariables().size() == 1) {
				firstLine.add(new TextComponent(" passado por referência "));
				// TODO parameter check
			} else {
				firstLine.add(new TextComponent(" passados por referências "));
			}

			if (!comp.getReturnType().equals(IType.VOID)) {
				firstLine.add(new TextComponent(" e"));
				isEUsed = true;
			} else {
				firstLine.add(new TextComponent(", "));
			}
		}
		if (comp.getReturnType().equals(IType.VOID)) {
			firstLine.add(new TextComponent("não devolve nada "));
		} else {

			firstLine.add(new TextComponent(" devolve "));

			// If a single variable is returned
			if (returnList.size() == 1 && returnList.get(0).getReturnExpression() instanceof IVariableExpression) {
				IVariableExpression var = (IVariableExpression) returnList.get(0).getReturnExpression();

				// Case of Procedure and returning that variable
				if (comp.getFunctionClassifier().getClassification().equals(Status.PROCEDURE)
						&& modifiedVariables.contains(var.getVariable())) {
					firstLine.add(new TextComponent("esse "));
					t.translateIType(var.getType(), false);
					firstLine.add(new TextComponent(" "));
					parametersMencioned.add(var.getVariable());
				} else {

					firstLine.add(new TextComponent("um "));
					ReturnComponent c = returnList.get(0);
					if (c.isParameter()) {
						firstLine.add(new TextComponent(" "));
						t.translateIType(comp.getReturnType(), true);
						t.linkVariable(returnList.get(0).getReturnExpression());
						firstLine.add(new TextComponent(" passado como argumento "));
					} else {
						t.translateIType(comp.getReturnType(), true);
						t.linkVariable(returnList.get(0).getReturnExpression());
						firstLine.add(new TextComponent(" "));
					}
					System.out.println(c.getVarReturnRole());
					if (!c.getVarReturnRole().equals(IVariableRole.NONE)) {
						// ROLE explanation
						if (c.getVarReturnRole() instanceof IMostWantedHolder) {
							firstLine.add(new TextComponent("que "));
							IMostWantedHolder m = (IMostWantedHolder) c.getVarReturnRole();
							
							firstLine.add(new TextComponent(
									"contém o " + t.translateMostWantedholderObjective(m.getObjective()) + "valor "));
							
							//GetItera
							boolean allValues = false;
							for(VariableRoleComponent varc: comp.getVariablesRoles()) {
								if(varc.getVar().expression().isSame(m.getIteratorVariable())) {
									if(varc.getRole() instanceof IArrayIndexIterator) {
										IArrayIndexIterator role = (IArrayIndexIterator) varc.getRole();
										allValues = role.isIteratingWholeArray();
									}	
								}
							}	
							if(allValues) {
								firstLine.add(new TextComponent("das posições iteradas "));
								
							}
							firstLine.add(new TextComponent("de "));
							t.translateIType(m.getTargetArray().getVariable().getType(), false);
							firstLine.add(new TextComponent(" "));
							t.linkVariable(m.getTargetArray());
							
							
							if (comp.getParameters().contains(m.getTargetArray().getVariable())) {
								parametersMencioned.add(m.getTargetArray().getVariable());
								firstLine.add(new TextComponent(" "));
								firstLine.add(new TextComponent("(parâmetro)"));
							}
						} else if (c.getVarReturnRole() instanceof IGatherer) {
							firstLine.add(new TextComponent("que "));
							IGatherer g = (IGatherer) c.getVarReturnRole();
							firstLine.add(new TextComponent("resulta de uma acumulação de "));

							System.out.println(g.getOperation().equals(Operation.ADD));

							if (g.getOperation().equals(Operation.ADD)) {
								firstLine.add(new TextComponent("adições "));
							}
							if (g.getOperation().equals(Operation.SUB)) {
								firstLine.add(new TextComponent("subtrações "));
							}
							if (g.getOperation().equals(Operation.MUL)) {
								firstLine.add(new TextComponent("multiplicações "));
							}
							if (g.getOperation().equals(Operation.DIV)) {
								firstLine.add(new TextComponent("divisões "));
							}
							if (g.getOperation().equals(Operation.DIV)) {
								firstLine.add(new TextComponent("restos de divisões "));
							}

							firstLine.add(new TextComponent("de "));

							if (g.getAccumulationExpression() instanceof IArrayElement) {
								IArrayElement el = (IArrayElement) g.getAccumulationExpression();
								firstLine.add(new TextComponent("elementos "));
								firstLine.add(new TextComponent("de "));
								if (el.getIndexes().size() == 1) {
									firstLine.add(new TextComponent("o vetor "));
								} else if (el.getIndexes().size() == 2) {
									firstLine.add(new TextComponent("a matrix "));
								}

								t.linkVariable((((IArrayElement) g.getAccumulationExpression()).getTarget()));

								if (comp.getParameters()
										.contains(((IVariableExpression) el.getTarget()).getVariable())) {
									parametersMencioned.add(((IVariableExpression) el.getTarget()).getVariable());
									firstLine.add(new TextComponent(" "));
									firstLine.add(new TextComponent("(parâmetro)"));
								}
							} else {
								// Caso default
								firstLine.add(new TextComponent("valores "));
							}
						} else if (c.getVarReturnRole() instanceof IFixedValue) {
							firstLine.add(new TextComponent("que "));
							IFixedValue f = (IFixedValue) c.getVarReturnRole();
							if (f.isModified()) {
								if (((IVariableExpression) c.getReturnExpression()).getVariable().isLocalVariable()) {
									firstLine.add(new TextComponent("é criado pela função "));
								} else {
									firstLine.add(new TextComponent("é alterado pela função "));
								}
							} else {
								firstLine.add(new TextComponent("que "));
								firstLine.add(new TextComponent(
										"mantém o valor da sua inicialização ao longo da execução d" + s1));
								t.translateMethodType(classifyType);
							}
						} else if (c.getVarReturnRole() instanceof IStepper) {
							firstLine.add(new TextComponent("que contém o numero de occurências de uma determinada condição"));
						} else if (c.getVarReturnRole() instanceof IOneWayFlag) {
							IOneWayFlag m = (IOneWayFlag) c.getVarReturnRole();
							firstLine.add(new TextComponent(
									"cujo valor vai depender de certas "));
							firstLine.add(new TextComponent("condições", m.getConditions()));
							
							firstLine.add(new TextComponent(" "));
						}
					}
				}
			} else
			// two returns cases
			if (returnList.size() == 2) {
				IReturn ret1 = (IReturn) returnList.get(0).getElement();
				IReturn ret2 = (IReturn) returnList.get(1).getElement();
				System.out.println(ret1.getExpression().getType().getClass());
				ISelection sel = (ISelection) ret1.getParent().getParent();

				if (comp.getReturnType().equals(IType.BOOLEAN)
						&& ret1.getExpression().getType() instanceof PrimitiveType
						&& ret2.getExpression().getType() instanceof PrimitiveType) {
					t.translateBooleanPrimitive(ret1.getExpression());
					firstLine.add(new TextComponent("se "));
					t.translateExpression(sel.getGuard(), true);
				} else if (ret1.getExpression() instanceof IVariableExpression
						&& ret2.getExpression() instanceof IVariableExpression) {
					IVariableExpression var1 = (IVariableExpression) ret1.getExpression();
					IVariableExpression var2 = (IVariableExpression) ret2.getExpression();

//					if(comp.getParameters().contains(var1.getVariable()) && comp.getParameters().contains(var2.getVariable())) {
//						firstLine.add(new TextComponent("um dos valores dos parâmetros "));
//					} 

//					if(comp.getParameters().contains(var1.getVariable()) && !comp.getParameters().contains(var2.getVariable())) 
//						firstLine.add(new TextComponent("o valor do parâmetro"));
//					firstLine.add(new TextComponent(ret1.getExpression().toString()));
//					
//					firstLine.add(new TextComponent(" ou "));
//					
//					if(comp.getParameters().contains(var2.getVariable()) && !comp.getParameters().contains(var1.getVariable())) 
//						firstLine.add(new TextComponent("o valor do parâmetro"));
//					firstLine.add(new TextComponent(ret2.getExpression().toString()));
//					firstLine.add(new TextComponent(" "));

					firstLine.add(new TextComponent("o valor de "));
					t.translateIType(var1.getType(), false);
					t.linkVariable(var1);
					if (comp.getParameters().contains(var1.getVariable())) {
						firstLine.add(new TextComponent(" "));
						firstLine.add(new TextComponent("(parâmetro)"));
					}

					firstLine.add(new TextComponent(" ou "));

					firstLine.add(new TextComponent("o valor de "));
					t.translateIType(var2.getType(), false);
					t.linkVariable(var2);
					if (comp.getParameters().contains(var2.getVariable())) {
						firstLine.add(new TextComponent(" "));
						firstLine.add(new TextComponent("(parâmetro)"));
					}

					parametersMencioned.add(var1.getVariable());
					parametersMencioned.add(var2.getVariable());
				} else { // Basic Explanation
					firstLine.add(new TextComponent("um "));
					t.translateIType(comp.getReturnType(), true);
				}
			} else {
				// Basic Explanation
				firstLine.add(new TextComponent("um "));
				t.translateIType(comp.getReturnType(), true);
			}
		}

		// firstLine.add(new TextComponent( + " ", comp.getReturnType()));
		if (!parameters.isEmpty() && parametersMencioned.size() != parameters.size()) {
			if (!isEUsed)
				firstLine.add(new TextComponent("e "));
			else
				firstLine.add(new TextComponent(", "));

			firstLine.add(new TextComponent("recebe um "));
			for (int i = 0; i < parameters.size(); i++) {
				IVariableDeclaration v = parameters.get(i);
				if (!parametersMencioned.contains(v)) {

					IType type = v.getType();
					t.translateIType(type, true);
					t.linkVariable(v.expression());
					firstLine.add(new TextComponent(" "));
					firstLine.add(new TextComponent("(parâmetro)"));
					firstLine.add(new TextComponent(" "));

					for (FVParameterComponent fvpcomp : comp.getFixedValueParameters()) {
						if (fvpcomp.getVar().isSame(v) && fvpcomp.hasObjective()) {
							TranslatorFVParameterComponent tfvp = new TranslatorFVParameterComponent(fvpcomp,
									returnList);
							tfvp.translatePT();
							firstLine.addAll(tfvp.getExplanationByComponents().get(0));
							firstLine.add(new TextComponent(" "));
						}
					}
					if (i < parameters.size() - 2) {
						firstLine.add(new TextComponent(", um "));
					}

					if (i == parameters.size() - 2) {
						firstLine.add(new TextComponent("e um "));
					}
				}
			}
		}

		explanationByComponents.add(firstLine);
		List<TextComponent> secondLine = new ArrayList<TextComponent>();
		// Recursividade Explicação
		if (comp.IsRecursive()) {
			t = new ExpressionTranslatorPT(secondLine);
			secondLine.add(new TextComponent("Est" + s2));

			t.translateMethodType(comp.getFunctionClassifier().getClassification());

			secondLine.add(new TextComponent(" é recursiv" + s1));

			secondLine.add(new TextComponent("porque "));
			secondLine.add(new TextComponent("se invoca", comp.getRecursive().getExpressions()));
			// System.out.println(comp.getRecursive().getExpressions());
			secondLine.add(new TextComponent(" a si mesm" + s1 + "durante a sua execução"));

			explanationByComponents.add(secondLine);

		}
	}

	@Override
	public List<List<TextComponent>> getExplanationByComponents() {
		return explanationByComponents;
	}

//	@Override
//	public String getExplanationText() {
//		String explanationText = "";
//
//		for (TextComponent t : explanationByComponents) {
//			explanationText += t.getText();
//			if (t.getType().equals(TextType.NEWLINE))
//				explanationText += "\n";
//		}
//		return explanationText;
//	}

}

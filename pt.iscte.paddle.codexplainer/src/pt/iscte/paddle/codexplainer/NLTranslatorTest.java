package pt.iscte.paddle.codexplainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import pt.iscte.paddle.javardise.MarkerService;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayElementAssignment;

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IUnaryExpression;

import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.IVariableAssignment;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;;

public class NLTranslatorTest {
	
	//static String explanation = "";
	
	
	static class Visitor implements IBlock.IVisitor {
		
		ArrayList<String> declaredVariables = new ArrayList<String>(); 
		Map<IVariableDeclaration, String> variablesRolesExplanations;
		List<List<TextComponent>> explanationtext = new ArrayList<>();
		
		StringBuilder explanation = new StringBuilder();
		int tabLevel = 0;
		IBlock currentBlock;
	    boolean alternativeBlock;


		public Visitor(Map<IVariableDeclaration, String> variablesRolesExplanation, IBlock body) {
			this.variablesRolesExplanations = variablesRolesExplanation;
			this.currentBlock = body;
		}	
		
		@Override
		public void visit(IVariableDeclaration variable) {
			System.out.println(variable);
			declaredVariables.add(variable.toString());
		}

		@Override
		public boolean visit(IVariableAssignment assignment) {
			List<TextComponent> line = new ArrayList<>();
			
			checkBranch(assignment.getParent());
			addTabs();
			if (declaredVariables.contains(assignment.getTarget().toString())) {
				line.add(new TextComponent("A variável " + assignment.getTarget() + " é inicializada"
												, TextType.LINK, assignment));
				line.add(new TextComponent(" com o valor igual a ", TextType.NORMAL));
				explainVariableAssignment(assignment, line);
				if (variablesRolesExplanations.containsKey(assignment.getTarget())) {
					line.add(new TextComponent(TextType.NEWLINE));
					line.add(new TextComponent(variablesRolesExplanations.get(assignment.getTarget()), TextType.NORMAL));
				}
				declaredVariables.remove(assignment.getTarget().toString());
			} else {
				line.add(new TextComponent("Vai ser guardado na variável " + assignment.getTarget() + " ", TextType.NORMAL));
				explainVariableAssignment(assignment, line);
			}
			
			explanationtext.add(line);
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			List<TextComponent> line = new ArrayList<>();
			ExpressionTranslator translator = new ExpressionTranslator(line);
			
			checkBranch(assignment.getParent());
			addTabs();
			translator.translateArrayElement((IArrayElement)assignment.getTarget());
			return false;
		}
		
		@Override
		public boolean visit(ILoop expression) {
			List<TextComponent> line = new ArrayList<>();
			
			IExpression guard = expression.getGuard();
			checkBranch(expression.getParent());
			
			line.add(new TextComponent("Este ciclo continuará a repetir-se enquanto ", TextType.NORMAL));
			explainGuardCondition(guard, line);
			this.currentBlock = expression.getBlock();
			
			explanationtext.add(line);
			return true;
		}

		@Override
		public boolean visit(ISelection expression) {
			List<TextComponent> line = new ArrayList<>();
			
			IExpression guard = expression.getGuard();
			checkBranch(expression.getParent());
			addTabs();
			
			line.add(new TextComponent("Condição: ", TextType.LINK, guard));
			explainGuardCondition(guard, line);
			explanation.append("\n");
			addTabs();
			
			line.add(new TextComponent("Caso a ", TextType.NORMAL));
			line.add(new TextComponent("condição", TextType.LINK, guard));
			line.add(new TextComponent(" seja verdadeira:", TextType.NORMAL));
			tabLevel++;
			
			if(expression.getAlternativeBlock() != null) {
				alternativeBlock = true;
			}
			this.currentBlock = expression.getBlock();
			
			explanationtext.add(line);
			return true;
		}
		
		@Override
		public boolean visit(IReturn expression) {
			List<TextComponent> line = new ArrayList<>();

			checkBranch(expression.getParent());
			addTabs();
			explainReturn(expression, line);
			
			explanationtext.add(line);
			return false;
		}
		
		void checkBranch(IBlock currentBlock) {
			if(!currentBlock.equals(this.currentBlock)) {
				if(alternativeBlock != true) {
					tabLevel--;
					this.currentBlock = currentBlock;
				} else {
					tabLevel--;
					appendLine("Caso contrário:" + "\n");
					tabLevel++;
					
					this.currentBlock = currentBlock;
					alternativeBlock = false;
				}
			}
		}
		
		void addTabs() {
			for(int i = 0; i <  tabLevel; i++) {
				//linkedText.words("\t");
			}
		}
		
		void appendLine(String line) {
			addTabs();
			explanation.append(line);
		}
		
		void explainReturn(IReturn ret, List<TextComponent> line) {
			ExpressionTranslator translator = new ExpressionTranslator(line);

			line.add(new TextComponent("Este ", TextType.NORMAL));
			line.add(new TextComponent("return", TextType.LINK, ret));
			//System.out.println(ret.getExpression().getClass());
			IExpression expression = ret.getExpression();
			if(ret.isVoid()) {
				line.add(new TextComponent(" pára a execução do método sem devolver nada", TextType.NORMAL));
			} else {
				line.add(new TextComponent(" devolve o do tipo ", TextType.LINK, ret));
				line.add(new TextComponent(ret.getReturnValueType().toString() +" ", TextType.LINK, ret.getReturnValueType()));
	
				if(expression instanceof IBinaryExpression) {
					line.add( new TextComponent("o resultado de ", TextType.NORMAL));
					translator.translateBinaryExpression((IBinaryExpression) expression);
				}
				if(expression instanceof IUnaryExpression) {
					//TODO
				}
				if(expression instanceof IVariableExpression) {
					line.add( new TextComponent("o valor da variável " + expression, TextType.NORMAL));
				}
				
			}
			line.add(new TextComponent(TextType.NEWLINE));
			
			
		}
		
		void explainVariableAssignment(IVariableAssignment assignment, List<TextComponent> line) {
			ExpressionTranslator translator = new ExpressionTranslator(line);
			
			IVariableDeclaration target = assignment.getTarget();
			IExpression expression = assignment.getExpression();
		
			
			if(expression instanceof IUnaryExpression) 
				translator.translateUnaryExpression((IUnaryExpression)expression);
			
			if(expression instanceof IBinaryExpression) {
				IBinaryExpression ex = (IBinaryExpression) expression;
				IBinaryOperator op = ex.getOperator();
				if(op.equals(IBinaryOperator.AND) || op.equals(IBinaryOperator.OR)) {
					explainGuardCondition(ex.getLeftOperand(), line);
					translator.translateOperator(ex.getOperator());
					explainGuardCondition(ex.getRightOperand(), line);
				} else {
					translator.translateBinaryExpression(ex);
				}
			}
			
			if(expression instanceof IArrayElement) {
				translator.translateArrayElement((IArrayElement)expression);
			}
			
			if(expression instanceof ILiteral || expression instanceof IVariableExpression) {
				translator.translateSimple(expression);
			}
			
			
		}

		
		
		void explainGuardCondition(IExpression guard, List<TextComponent> line) {	
			ExpressionTranslator translator = new ExpressionTranslator(line);
			
			if(guard instanceof IUnaryExpression) {
				translator.translateUnaryExpression((IUnaryExpression)guard);
			} else {
				IBinaryExpression ex = (IBinaryExpression) guard;
				IBinaryOperator op = ex.getOperator();
				
				if(op.equals(IBinaryOperator.AND) || op.equals(IBinaryOperator.OR)) {
					explainGuardCondition(ex.getLeftOperand(), line);
					translator.translateOperator(ex.getOperator());
					explainGuardCondition(ex.getRightOperand(), line);
				} else {
					translator.translateBinaryExpression(ex);
				}
			}
			
		}
		

	}
	
	
	static String getExplanation(IBlock body, Map<IVariableDeclaration, String> variablesRolesExplanation) {
		Visitor v = new Visitor(variablesRolesExplanation, body);
		body.getOwnerProcedure().accept(v);
		return v.explanation.toString();
	}
	
//	static HyperlinkedText getHyperLinkedText(IBlock body, Map<IVariableDeclaration, String> variablesRolesExplanation) {
//		Visitor v = new Visitor(variablesRolesExplanation, body);
//		body.getOwnerProcedure().accept(v);
//		return v.expl;
//	}
	
	static List<List<TextComponent>> getExplanationText(IBlock body, Map<IVariableDeclaration, String> variablesRolesExplanation) {
		Visitor v = new Visitor(variablesRolesExplanation, body);
		body.getOwnerProcedure().accept(v);
		return v.explanationtext;
	}

}

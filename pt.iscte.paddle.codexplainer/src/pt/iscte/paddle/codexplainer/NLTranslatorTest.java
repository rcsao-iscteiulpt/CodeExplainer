package pt.iscte.paddle.codexplainer;

import java.util.ArrayList;

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

public class NLTranslatorTest {
	
	//static String explanation = "";
	
	
	static class Visitor implements IBlock.IVisitor {
		
		ArrayList<String> declaredVariables = new ArrayList<String>(); 
		Map<IVariableDeclaration, String> variablesRolesExplanations;
		HyperlinkedText linkedText;
		StringBuilder explanation = new StringBuilder();
		int tabLevel = 0;
		IBlock currentBlock;
	    boolean alternativeBlock;


		public Visitor(Map<IVariableDeclaration, String> variablesRolesExplanation, IBlock body) {
			this.variablesRolesExplanations = variablesRolesExplanation;
			this.currentBlock = body;
			Color blue = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
			linkedText = new HyperlinkedText(e -> MarkerService.mark(blue, e)); //TESTE
		}
		
		@Override
		public void visit(IVariableDeclaration variable) {
			System.out.println(variable);
			declaredVariables.add(variable.toString());
		}

		@Override
		public boolean visit(IVariableAssignment assignment) {
			checkBranch(assignment.getParent());
			addTabs();
			if (declaredVariables.contains(assignment.getTarget().toString())) {
				linkedText.link("A variável " + assignment.getTarget().toString() + " é inicializada com o valor igual ", assignment);
				explainVariableAssignment(assignment);
				System.out.println(variablesRolesExplanations);
				if(variablesRolesExplanations.containsKey(assignment.getTarget())) {
					explanation.append(variablesRolesExplanations.get(assignment.getTarget()));
				}
				declaredVariables.remove(assignment.getTarget().toString());
			} else {
				linkedText.link("Vai ser guardado na variável " + assignment.getTarget() + " ");
				explainVariableAssignment(assignment);
			}
			explanation.append("\n");
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			checkBranch(assignment.getParent());
			addTabs();
			appendLine("Vai ser guardado na " + ExpressionTranslator.translateArrayElement((IArrayElement)assignment.getTarget()));
			return false;
		}
		
		@Override
		public boolean visit(ILoop expression) {
			IExpression guard = expression.getGuard();
			checkBranch(expression.getParent());	
			appendLine("Este ciclo continuará a repetir-se enquanto ");
			explainGuardCondition(guard);
			explanation.append("\n");
			tabLevel++;
			this.currentBlock = expression.getBlock();
			return true;
		}

		@Override
		public boolean visit(ISelection expression) {
			IExpression guard = expression.getGuard();
			checkBranch(expression.getParent());
			addTabs();
			linkedText.words("Condição: ");
			explainGuardCondition(guard);
			explanation.append("\n");
			addTabs();
			linkedText.words("Caso a condição seja verdadeira:" + "\n");
			tabLevel++;
			
			if(expression.getAlternativeBlock() != null) {
				alternativeBlock = true;
			}
			this.currentBlock = expression.getBlock();
			return true;
		}
		
		@Override
		public boolean visit(IReturn expression) {
			checkBranch(expression.getParent());
			addTabs();
			explainReturn(expression);
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
				linkedText.words("\t");
			}
		}
		
		void appendLine(String line) {
			addTabs();
			explanation.append(line);
		}
		
		void explainReturn(IReturn ret) {
			System.out.println(ret.getExpression().getClass());
			IExpression expression = ret.getExpression();
			if(ret.isVoid()) {
				explanation.append("Este return pára a execução do método sem devolver nada");
			} else {
				explanation.append("Este return devolve o valor do/a " + ret.getReturnValueType() + " ");
				if(expression instanceof IBinaryExpression) {
					explanation.append("de " + ExpressionTranslator.translateBinaryExpression((IBinaryExpression) expression));
				}
				if(expression instanceof IUnaryExpression) {
					//TODO
				}
				if(expression instanceof IVariableDeclaration) {
					explanation.append(expression);
				}
				
			}
		}
		
		void explainVariableAssignment(IVariableAssignment assignment) {
			//assignment.is
			IVariableDeclaration target = assignment.getTarget();
			IExpression expression = assignment.getExpression();
		
			
			if(expression instanceof IUnaryExpression) 
				linkedText.words(ExpressionTranslator.translateUnaryExpression((IUnaryExpression)expression));
			
			if(expression instanceof IBinaryExpression) {
				IBinaryExpression ex = (IBinaryExpression) expression;
				IBinaryOperator op = ex.getOperator();
				if(op.equals(IBinaryOperator.AND) || op.equals(IBinaryOperator.OR)) {
					explainGuardCondition(ex.getLeftOperand());
					linkedText.words(ExpressionTranslator.translateOperator(ex.getOperator()));
					explainGuardCondition(ex.getRightOperand());
				} else {
					linkedText.words(ExpressionTranslator.translateBinaryExpression(ex));
				}
			}
			
			if(expression instanceof IArrayElement) {
				linkedText.words(ExpressionTranslator.translateArrayElement((IArrayElement)expression));
			}
			
			if(expression instanceof ILiteral || expression instanceof IVariableExpression) {
				linkedText.words(ExpressionTranslator.translateSimple(expression));
			}
			
			linkedText.newline();
			
		}

		
		
		void explainGuardCondition(IExpression guard) {
			String line = "";
			
			if(guard instanceof IUnaryExpression) {
				line +=ExpressionTranslator.translateUnaryExpression((IUnaryExpression)guard);
			} else {
				IBinaryExpression ex = (IBinaryExpression) guard;
				IBinaryOperator op = ex.getOperator();
				
				if(op.equals(IBinaryOperator.AND) || op.equals(IBinaryOperator.OR)) {
					explainGuardCondition(ex.getLeftOperand());
					line +=ExpressionTranslator.translateOperator(ex.getOperator());
					explainGuardCondition(ex.getRightOperand());
				} else {
					line +=ExpressionTranslator.translateBinaryExpression(ex);
				}
			}
			
			linkedText.link(line, guard);
			
			linkedText.newline();
		}
		

	}
	
	
	static String getExplanation(IBlock body, Map<IVariableDeclaration, String> variablesRolesExplanation) {
		Visitor v = new Visitor(variablesRolesExplanation, body);
		body.getOwnerProcedure().accept(v);
		return v.explanation.toString();
	}
	
	static HyperlinkedText getHyperLinkedText(IBlock body, Map<IVariableDeclaration, String> variablesRolesExplanation) {
		Visitor v = new Visitor(variablesRolesExplanation, body);
		body.getOwnerProcedure().accept(v);
		return v.linkedText;
	}

}

package pt.iscte.paddle.codexplainer;

import java.util.ArrayList;

import java.util.Map;



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

import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;

public class NLTranslator {
	
	//static String explanation = "";
	
	
	static class Visitor implements IBlock.IVisitor {
		
		ArrayList<String> declaredVariables = new ArrayList<String>(); 
		Map<IVariable, String> variablesRolesExplanations;
		StringBuilder explanation = new StringBuilder();
		int tabLevel = 0;
		IBlock currentBlock;
	    boolean alternativeBlock;


		public Visitor(Map<IVariable, String> variablesRolesExplanation, IBlock body) {
			this.variablesRolesExplanations = variablesRolesExplanation;
			this.currentBlock = body;
		}

		@Override
		public boolean visit(IVariableAssignment assignment) {
			checkBranch(assignment.getParent());
			//System.out.println(assignment);
			//TODO declaration changes
			explanation.append(assignment +" : ");
			if (!declaredVariables.contains(assignment.getTarget().toString())) {
				declaredVariables.add(assignment.getTarget().toString());
				appendLine("A variável " + assignment.getTarget().toString() + " é inicializada com o valor igual ");
				explainVariableAssignment(assignment);
				if(variablesRolesExplanations.containsKey(assignment.getTarget())) {
					explanation.append(variablesRolesExplanations.get(assignment.getTarget()));
				}
			} else {
				appendLine("Vai ser guardado na variável " +  assignment.getTarget().toString() + " ");
				explainVariableAssignment(assignment);
			}
			explanation.append("\n");
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			checkBranch(assignment.getParent());
			addTabs();
			//System.out.println(assignment);
			explanation.append("\n");
			return false;
		}
		
		@Override
		public boolean visit(ILoop expression) {
			IExpression guard = expression.getGuard();
			checkBranch(expression.getParent());	
			appendLine("while("+guard+"): Este ciclo continuará a repetir-se enquanto ");
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
			appendLine("if("+guard+"): ");
			explainGuardCondition(guard);
			explanation.append("\n");
			appendLine("Caso a condição seja verdadeira:" + "\n");
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
				explanation.append("\t");
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
				if(expression instanceof IVariable) {
					explanation.append(expression);
				}
				
			}
		}
		
		void explainVariableAssignment(IVariableAssignment assignment) {
			
			IExpression expression = assignment.getExpression();
		
			if(expression instanceof IUnaryExpression) 
				explanation.append(ExpressionTranslator.translateUnaryExpression((IUnaryExpression)expression));
			
			if(expression instanceof IBinaryExpression) {
				IBinaryExpression ex = (IBinaryExpression) expression;
				IBinaryOperator op = ex.getOperator();
				if(op.equals(IBinaryOperator.AND) || op.equals(IBinaryOperator.OR)) {
					explainGuardCondition(ex.getLeftOperand());
					explanation.append(ExpressionTranslator.translateOperator(ex.getOperator()));
					explainGuardCondition(ex.getRightOperand());
				} else {
					explanation.append(ExpressionTranslator.translateBinaryExpression(ex));
				}
			}
			
			if(expression instanceof IArrayElement) {
				explanation.append(ExpressionTranslator.translateArrayElement((IArrayElement)expression));
			}
			
			if(expression instanceof ILiteral || expression instanceof IVariable) {
				explanation.append(ExpressionTranslator.translateSimple(expression));
			}
			
		}

		
		
		void explainGuardCondition(IExpression guard) {

			
			if(guard instanceof IUnaryExpression) {
				explanation.append(ExpressionTranslator.translateUnaryExpression((IUnaryExpression)guard));
			} else {
				IBinaryExpression ex = (IBinaryExpression) guard;
				IBinaryOperator op = ex.getOperator();
				if(op.equals(IBinaryOperator.AND) || op.equals(IBinaryOperator.OR)) {
					explainGuardCondition(ex.getLeftOperand());
					explanation.append(ExpressionTranslator.translateOperator(ex.getOperator()));
					explainGuardCondition(ex.getRightOperand());
				} else {
					explanation.append(ExpressionTranslator.translateBinaryExpression(ex));
				}
			}
			
	
		}
		

	}
	
	
	static String getExplanation(IBlock body, Map<IVariable, String> variablesRolesExplanation) {
		Visitor v = new Visitor(variablesRolesExplanation, body);
		body.getOwnerProcedure().accept(v);
		return v.explanation.toString();
	}

}

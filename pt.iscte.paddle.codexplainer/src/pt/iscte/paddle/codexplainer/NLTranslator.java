package pt.iscte.paddle.codexplainer;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.CFGGeneration.Visitor;
import pt.iscte.paddle.interpreter.IArray;
import pt.iscte.paddle.model.IArrayAllocation;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IArrayLength;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IUnaryOperator;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;

public class NLTranslator {
	
	static String explanation = "";
	
	static class Visitor implements IBlock.IVisitor {
		
		ArrayList<String> declaredVariables = new ArrayList<String>(); 
		


		@Override
		public boolean visit(IVariableAssignment assignment) {
			//System.out.println(assignment);
			explanation += assignment +" : ";
			if (!declaredVariables.contains(assignment.getTarget().toString())) {
				declaredVariables.add(assignment.getTarget().toString());
				explanation += "A variável " + assignment.getTarget().toString() + " é inicializada com o valor igual ";
				explainVariableAssignment(assignment);
			} else {
				explanation += "Vai ser guardado na variável " +  assignment.getTarget().toString() + " o valor ";
				explainVariableAssignment(assignment);
			}
			
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			//System.out.println(assignment);
			return false;
		}
		
		@Override
		public boolean visit(ILoop expression) {
			explanation += "While : Este while irá continuar a fazer loop enquanto ";
			explainGuardCondition(expression.getGuard());
			//System.out.println(expression);
			//expression,
			explanation += "\n";
			return true;
		}

		@Override
		public boolean visit(ISelection expression) {
			explanation += "If : A condição deste If irá devolver true quando ";
			IBinaryExpression guard = (IBinaryExpression) expression.getGuard();
			explainGuardCondition(guard);
			//System.out.println(expression);
			explanation += "\n";
			return true;
		}
		
		void explainVariableAssignment(IVariableAssignment assignment) {
			
			IExpression expression = assignment.getExpression();
			
			if(!(expression instanceof IBinaryExpression)) {
				if (!(expression instanceof IArrayElement)) {
					explanation += expression.toString();
				} else {
					IArrayElement element = (IArrayElement) expression;
					System.out.println();
					explanation += "à posição " + element.getIndexes().get(0) 
							+ " do vetor " + element.getTarget();
				}
			} else {		
				IBinaryExpression ex = (IBinaryExpression) expression;
				IExpression leftEx = ex.getLeftOperand();
				IExpression rightEx = ex.getRightOperand();
				
				if(ex.getOperator().equals(IBinaryOperator.ADD)) {
					explanation += "à soma dos valores ";
				}		
				if(ex.getOperator().equals(IBinaryOperator.SUB)) {
					explanation += "à subtração entre os valores ";
				}
				
//				List<IExpression> parts = ex.getParts();
//				for(IExpression e: parts) {
//					if(e instanceof IArrayElement) {
//						IArrayElement element = (IArrayElement) e;
//						explanation += "da posição " + element.getIndexes().get(0) 
//								+ " do vetor " + element.getTarget();
//					} else {
//						explanation += expression.toString();
//					}
//					if(!parts.get(parts.size() - 1).equals(e)) {
//						explanation += " e ";
//					} else {
//						explanation += ".";
//					}
				
				if(leftEx instanceof IArrayElement) {
					IArrayElement leftExArray = (IArrayElement) leftEx;
					explanation += "o valor da posição " + leftExArray.getIndexes().get(0) 
							+ " do vetor " + leftExArray.getTarget() + " ";		
				} else {
					explanation += "o valor de " + leftEx.toString() + " ";	
				}
				
				explanation += "e ";
				
				if(rightEx instanceof IArrayElement) {
					IArrayElement rightExArray = (IArrayElement) rightEx;
					explanation += "o valor da posição " + rightExArray.getIndexes().get(0) 
							+ " do vetor " + rightExArray.getTarget();		
				} else {
					explanation += "o valor de " + rightEx.toString();	
				}
				
						
				
			}
			explanation += "\n";
		}

		
		
		void explainGuardCondition(IExpression guard) {

			
			if(guard instanceof IUnaryExpression) {
				explanation += ExpressionTranslator.translateUnaryExpression((IUnaryExpression)guard);
			} else {
				IBinaryExpression ex = (IBinaryExpression) guard;
				IBinaryOperator op = ex.getOperator();
				if(op.equals(IBinaryOperator.AND) || op.equals(IBinaryOperator.OR)) {
					explainGuardCondition(ex.getLeftOperand());
					explanation += ExpressionTranslator.translateOperator(ex.getOperator());
					explainGuardCondition(ex.getRightOperand());
				} else {
					explanation += ExpressionTranslator.translateBinaryExpression(ex);
				}
			}
			
	
		}
		

	}
	
	
	static String getExplanation(IBlock body) {
		Visitor v = new Visitor();
		body.getOwnerProcedure().accept(v);
		return explanation;
	}

}

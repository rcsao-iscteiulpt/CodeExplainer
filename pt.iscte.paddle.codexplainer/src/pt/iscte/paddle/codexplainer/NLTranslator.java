package pt.iscte.paddle.codexplainer;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.CFGGeneration.Visitor;
import pt.iscte.paddle.interpreter.IArray;
import pt.iscte.paddle.model.IArrayAllocation;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;

public class NLTranslator {
	
	static String explanation = "";
	
	static class Visitor implements IBlock.IVisitor {
		
		ArrayList<String> declaredVariables = new ArrayList<String>(); 
		


		@Override
		public boolean visit(IVariableAssignment assignment) {
			System.out.println(assignment);
			if (!declaredVariables.contains(assignment.getTarget().toString())) {
				declaredVariables.add(assignment.getTarget().toString());
				explainVariableDeclaration(assignment);
			} else {

			}
			
			//System.out.println(assignment.getTarget() instanceof IVariable);
			
			//System.out.println(assignment);
			//System.out.println(assignment.getTarget());
			//System.out.println(assignment.getExpressionParts());
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			//System.out.println(assignment);
			return false;
		}
		
		@Override
		public boolean visit(ILoop expression) {
			
			//System.out.println(expression);
			//expression,
			return true;
		}

		@Override
		public boolean visit(ISelection expression) {
			
			//System.out.println(expression);
			return true;
		}
		
		void explainVariableDeclaration(IVariableAssignment assignment) {
			explanation += "A variável " + assignment.getTarget().toString() + " é inicializada com o valor igual ";
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
				if(ex.getOperator().equals(IBinaryOperator.ADD)) {
					explanation += "à soma dos valores ";
				}		
				if(ex.getOperator().equals(IBinaryOperator.SUB)) {
					explanation += "à subtração entre os valores ";
				}
				List<IExpression> parts = ex.getParts();
				
				for(IExpression e: parts) {
					if(e instanceof IArrayElement) {
						IArrayElement element = (IArrayElement) e;
						explanation += "da posição " + element.getIndexes().get(0) 
								+ " do vetor " + element.getTarget();
					} else {
						explanation += expression.toString();
					}
					if(!parts.get(parts.size() - 1).equals(e)) {
						explanation += " e ";
					} else {
						explanation += ".";
					}
						
				}
			}

			
			
			
			explanation += "\n";
		}

	}
	

	
	static String getExplanation(IBlock body) {
		Visitor v = new Visitor();
		body.getOwnerProcedure().accept(v);
		return explanation;
	}

}

package pt.iscte.paddle.codexplainer.tests;


import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration; 

public class ExpressionTranslatorTest {
	
	static void TestArrayElement(IArrayElement element) {
		//System.out.println(ExpressionTranslator.translateArrayElement(element));
	}
	
	public static void main(String[] args) {
		IModule module = IModule.create();
		IProcedure proc = module.addProcedure(INT);
		
		IVariableDeclaration array = proc.addParameter(INT.array().reference());
		array.setId("array");
		
		IBlock body = proc.getBody();
		
		IBinaryExpression lastPositionArray = IBinaryOperator.SUB.on(array.length(), INT.literal(1));
		IBinaryExpression secondtoLastPositionArray = IBinaryOperator.SUB.on(array.length(), INT.literal(2));
		IVariableDeclaration c = body.addVariable(INT);
		c.setId("c");
		
		IArrayElement lastElement = array.element(lastPositionArray);
		IArrayElement secondtoLastElement = array.element(secondtoLastPositionArray);
		IArrayElement firstElement = array.element(INT.literal(0));
		//IVariableAssignment variableAss1 = body.addAssignment(c, lastElement);
		//IVariableAssignment variableAss2 = body.addAssignment(c, firstElement);
		
		
		System.out.println(array.length().getClass());
		System.out.println(array.length().getType());
		TestArrayElement(firstElement);
		TestArrayElement(secondtoLastElement);
		TestArrayElement(lastElement);
	}
	

	
	
}

package pt.iscte.paddle.codexplainer.tests;


import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.translator.ExpressionTranslatorPT;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration; 

public class ExpressionTranslatorTest {
	
	@Test
    public void TestArrayElementAssignment() {
		IModule module = IModule.create();
		IProcedure naturals = module.addProcedure(INT.array());
		IVariableDeclaration n = naturals.addParameter(INT);
		IBlock body = naturals.getBody();
		IVariableDeclaration array = body.addVariable(INT.array());
		IVariableAssignment ass1 = body.addAssignment(array, INT.array().stackAllocation(n));
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		IArrayElementAssignment ass2 = loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);


		List<TextComponent> comp = new ArrayList<>();
		ExpressionTranslatorPT t = new ExpressionTranslatorPT(comp);
		t.translateAssignment(ass2, false);
	}
	
//	public static void main(String[] args) {
//		IModule module = IModule.create();
//		IProcedure proc = module.addProcedure(INT);
//		
//		IVariableDeclaration array = proc.addParameter(INT.array().reference());
//		array.setId("array");
//		
//		IBlock body = proc.getBody();
//		
//		IBinaryExpression lastPositionArray = IBinaryOperator.SUB.on(array.length(), INT.literal(1));
//		IBinaryExpression secondtoLastPositionArray = IBinaryOperator.SUB.on(array.length(), INT.literal(2));
//		IVariableDeclaration c = body.addVariable(INT);
//		c.setId("c");
//		
//		IArrayElement lastElement = array.element(lastPositionArray);
//		IArrayElement secondtoLastElement = array.element(secondtoLastPositionArray);
//		IArrayElement firstElement = array.element(INT.literal(0));
//		//IVariableAssignment variableAss1 = body.addAssignment(c, lastElement);
//		//IVariableAssignment variableAss2 = body.addAssignment(c, firstElement);
//		
//		
//		
//		System.out.println(array.length().getClass());
//		System.out.println(array.length().getType());
//		TestArrayElement(firstElement);
//		TestArrayElement(secondtoLastElement);
//		TestArrayElement(lastElement);
//	}
//	

	
	
}

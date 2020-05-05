package pt.iscte.paddle.codexplainer.tests;

import static org.junit.jupiter.api.Assertions.*;
import static pt.iscte.paddle.model.IOperator.*;
import static pt.iscte.paddle.model.IType.*;


import org.junit.jupiter.api.Test;

import pt.iscte.paddle.codexplainer.components.LoopComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

class ReturnComponentTest {

	@Test
	void testReturnExpression() {
		IModule module = IModule.create();
		
		IProcedure max = module.addProcedure(INT);
		
		IVariableDeclaration array = max.addParameter(INT.array().reference());
		array.setId("array");
		
		IBlock body = max.getBody();
		
		IVariableDeclaration m = body.addVariable(INT);
		m.setId("max");
		
		IVariableDeclaration m2 = body.addVariable(INT);
		m2.setId("max2");
		
		IVariableAssignment m2Ass = body.addAssignment(m, array.element(INT.literal(0)));
		
		IVariableDeclaration i = body.addVariable(INT);
		i.setId("i");
		
		IVariableAssignment iAss = body.addAssignment(i, INT.literal(1));
		ILoop loop = body.addLoop(AND.on(SMALLER.on(i, array.length()), 
				AND.on(GREATER.on(m, array.length()), DIFFERENT.on(m.expression(), i.expression()))));
		ISelection ifstat = loop.addSelection(GREATER.on(array.element(i), m));
		IVariableAssignment mAss_ = ifstat.addAssignment(m, array.element(i));
		IVariableAssignment iInc = loop.addIncrement(i);
		
		IReturn ret = body.addReturn(m);
		
		IReturn ret2 = body.addReturn(ADD.on(m, m2));
		
		IReturn ret3 = body.addReturn(ADD.on(m,ADD.on(m2, INT.literal(1))));
		
		//System.out.println(max);
		
		
		ReturnComponent comp = new ReturnComponent(ExplanationGeneratorTest.getVariableRoleTest(max.getVariables()), ret);
		ReturnComponent comp1 = new ReturnComponent(ExplanationGeneratorTest.getVariableRoleTest(max.getVariables()), ret2);
		ReturnComponent comp2 = new ReturnComponent(ExplanationGeneratorTest.getVariableRoleTest(max.getVariables()), ret3);
		
//		System.out.println(comp.getReturnExpressionParts());	
//		System.out.println(comp1.getReturnExpressionParts());
//		System.out.println(comp2.getReturnExpressionParts());
		
		assertEquals("[max]", comp.getReturnExpressionParts().toString());
		assertEquals("[max, max2]",comp1.getReturnExpressionParts().toString());
		assertEquals("[max, max2, 1]", comp2.getReturnExpressionParts().toString());	
	}
	
	@Test
	void testReturnType() {
		
		IModule module = IModule.create();
		
		//INT
		IProcedure method1 = module.addProcedure(INT);	
		IBlock body1 = method1.getBody();
		IVariableDeclaration m1 = body1.addVariable(INT);
		IReturn ret1 = body1.addReturn(m1);
		
		//BOOL
		IProcedure method2 = module.addProcedure(BOOLEAN);
		IBlock body2 = method2.getBody();
		IVariableDeclaration m2 = body2.addVariable(BOOLEAN);
		IReturn ret2 = body2.addReturn(m2);
		
		//ARRAY INT
		IProcedure method3 = module.addProcedure(INT.array());
		IBlock body3 = method3.getBody();
		IVariableDeclaration m3 = body3.addVariable(INT.array());
		IReturn ret3 = body3.addReturn(m3);
		
		
		
		
		
		ReturnComponent comp = new ReturnComponent(ExplanationGeneratorTest.getVariableRoleTest(method1.getVariables()),ret1);
		ReturnComponent comp1 = new ReturnComponent(ExplanationGeneratorTest.getVariableRoleTest(method2.getVariables()),ret2);
		ReturnComponent comp2 = new ReturnComponent(ExplanationGeneratorTest.getVariableRoleTest(method3.getVariables()),ret3);
		
		System.out.println(comp.getReturnType());
		System.out.println(comp1.getReturnType());
		System.out.println(comp2.getReturnType());
		
		assertEquals(INT, comp.getReturnType());
		assertEquals(BOOLEAN, comp1.getReturnType());
		assertEquals(INT.array(), comp2.getReturnType());
	}

}

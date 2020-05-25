package pt.iscte.paddle.codexplainer.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pt.iscte.paddle.model.IOperator.*;
import static pt.iscte.paddle.model.IType.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.ReturnComponent;
import pt.iscte.paddle.codexplainer.translator.TranslatorMethodComponentPT;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class MethodComponentTest {

	
	@Test
	void test() {
	IModule module = IModule.create();
		
		IProcedure max = module.addProcedure(INT);
		
		IVariableDeclaration array = max.addParameter(INT.array().reference());
		array.setId("array");
		
		
		IBlock body = max.getBody();
		
		IVariableDeclaration m = body.addVariable(INT);
		m.setId("max");
		
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
		
		System.out.println("Method ==" +max);
		
		
		MethodComponent comp = new MethodComponent(max);
		
		System.out.println(comp.getReturnType());
		System.out.println(comp.IsRecursive());
		
		assertEquals(IType.INT, comp.getReturnType());
		assertEquals(false, comp.IsRecursive());
	}
	
	@Test
	void testLoopTranslate() {
		System.out.println("TRANSLATE TEST ||||| \n\n");
		
		IModule module = IModule.create();
		
		IProcedure max = module.addProcedure(INT);
		
		IVariableDeclaration array = max.addParameter(INT.array().reference());
		array.setId("v");
		IVariableDeclaration r = max.addParameter(INT);
		array.setId("r");
		IVariableDeclaration r2 = max.addParameter(INT);
		array.setId("r2");
		
		IBlock body = max.getBody();
		
		IVariableDeclaration m = body.addVariable(INT);
		m.setId("max");
		
		IVariableAssignment m2Ass = body.addAssignment(m, array.element(INT.literal(0)));
		
		IVariableDeclaration i = body.addVariable(INT);
		i.setId("i");
		
		IVariableAssignment iAss = body.addAssignment(i, INT.literal(1));
		ILoop loop = body.addLoop(IOperator.AND.on(SMALLER.on(i, array.length()), 
				IOperator.AND.on(GREATER.on(m, array.length()), IOperator.DIFFERENT.on(m.expression(), i.expression()))));
		ISelection ifstat = loop.addSelection(GREATER.on(array.element(i), m));
		IVariableAssignment mAss_ = ifstat.addAssignment(m, array.element(i));
		IVariableAssignment iInc = loop.addIncrement(i);
		IReturn ret = body.addReturn(m);
		
		//System.out.println("Method == " +max);
	
		//ReturnComponent retComp = new ReturnComponent(null, ret, null);
		
		MethodComponent comp = 
				new MethodComponent(max);
		
		
		TranslatorMethodComponentPT t = new TranslatorMethodComponentPT(comp, new ArrayList<>());
		t.translatePT();
		System.out.println("\nExplicaçao método:\n");
		System.out.println(t.getExplanationText());
		
		System.out.println(comp.getReturnType());
		System.out.println(comp.IsRecursive());
		
		assertEquals(IType.INT, comp.getReturnType());
		assertEquals(false, comp.IsRecursive());
	
	}
	
}

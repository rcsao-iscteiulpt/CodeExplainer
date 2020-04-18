package pt.iscte.paddle.codexplainer.tests;

import static org.junit.jupiter.api.Assertions.*;
import static pt.iscte.paddle.model.IOperator.*;
import static pt.iscte.paddle.model.IType.*;

import org.junit.jupiter.api.Test;

import pt.iscte.paddle.codexplainer.components.LoopComponent;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

class LoopComponentTest {

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
		
		System.out.println(max);
		
		
		LoopComponent comp = new LoopComponent(loop);
		
		System.out.println(comp.getGuardParts());
		System.out.println(comp.getPossibleIterators());
		
		assertEquals("[i < array.length, max > array.length, max != i]", comp.getGuardParts().toString());
		assertEquals("[max, i]", comp.getPossibleIterators().toString());
	}
	
	void testManual() {
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
		ILoop loop = body.addLoop(IOperator.AND.on(SMALLER.on(i, array.length()), 
				IOperator.AND.on(GREATER.on(m, array.length()), IOperator.DIFFERENT.on(m.expression(), i.expression()))));
		ISelection ifstat = loop.addSelection(GREATER.on(array.element(i), m));
		IVariableAssignment mAss_ = ifstat.addAssignment(m, array.element(i));
		IVariableAssignment iInc = loop.addIncrement(i);
		IReturn ret = body.addReturn(m);
		
		System.out.println(max);
		
		
		LoopComponent comp = new LoopComponent(loop);
		
		System.out.println(comp.getPossibleIterators());
		System.out.println(comp.getGuardParts());
		
	}


}

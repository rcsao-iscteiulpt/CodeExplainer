package pt.iscte.paddle.codexplainer.tests;

import static org.junit.jupiter.api.Assertions.*;
import static pt.iscte.paddle.model.IOperator.AND;
import static pt.iscte.paddle.model.IOperator.DIFFERENT;
import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import org.junit.jupiter.api.Test;

import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.SelectionComponent;
import pt.iscte.paddle.codexplainer.translator.TranslatorSelectionComponentPT;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

class SelectionComponentTest {

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
			
			MethodComponent mc = new MethodComponent(max);

			SelectionComponent comp = 
					new SelectionComponent(ExplanationGeneratorTest.getVariableRoleTest(max.getVariables()), ifstat, mc);
			
			System.out.println(comp.getGuardParts());
			
			assertEquals("[array[i] > max]", comp.getGuardParts().toString());
		}
	
	@Test
	void testSelectionTranslateNormal() {
		System.out.println("TRANSLATE TEST ||||| \n\n");
		
		IModule module = IModule.create();
		
		IProcedure max = module.addProcedure(INT);
		
		IVariableDeclaration array = max.addParameter(INT.array().reference());
		array.setId("v");
		
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
		
		MethodComponent mc = new MethodComponent(max);
		SelectionComponent comp = 
				new SelectionComponent(ExplanationGeneratorTest.getVariableRoleTest(max.getVariables()), ifstat, mc);
		
		
		TranslatorSelectionComponentPT t = new TranslatorSelectionComponentPT(comp, 0);
		t.translatePT();
		System.out.println("\nExplicaçao Selection:\n");
		System.out.println(t.getExplanationText());
		
		System.out.println(comp.getGuardParts());
		//System.out.println(comp);
		
		//assertEquals("[i < v.length, AND, max > v.length, AND, max != i]", comp.getGuardParts().toString());
		//assertEquals(i, comp.getIteratorComponent().getVar());
	
	}
	
	@Test
	void testSelectionTranslateMostWantedHolder() {
		System.out.println("TRANSLATE MostWantedHolder TEST ||||| \n\n");
		
		//True Greater
		IModule module = IModule.create();
		
		IProcedure max = module.addProcedure(INT);
		max.setId("max");
		IVariableDeclaration array = max.addParameter(INT.array().reference());
		array.setId("v");
		
		IBlock body = max.getBody();
		
		IVariableDeclaration m = body.addVariable(INT);
		m.setId("m");
		body.addAssignment(m, array.element(INT.literal(0)));
		
		IVariableDeclaration i = body.addVariable(INT);
		i.setId("i");
		body.addAssignment(i, INT.literal(1));
		
		ILoop loop = body.addLoop(SMALLER.on(i, array.length()));
		ISelection ifstat = loop.addSelection(GREATER.on(array.element(i), m));
		ifstat.addAssignment(m, array.element(i));
		loop.addIncrement(i);
		body.addReturn(m);
		
		//System.out.println("Method == " +max);
		
		MethodComponent mc = new MethodComponent(max);
		SelectionComponent comp = 
				new SelectionComponent(ExplanationGeneratorTest.getVariableRoleTest(max.getVariables()), ifstat, mc);
		
		
		TranslatorSelectionComponentPT t = new TranslatorSelectionComponentPT(comp, 0);
		t.translatePT();
		System.out.println("\nExplicaçao Selection:\n");
		System.out.println(t.getExplanationText());
		
		System.out.println(comp.getGuardParts());
		//System.out.println(comp);
		
		//assertEquals("[i < v.length, AND, max > v.length, AND, max != i]", comp.getGuardParts().toString());
		//assertEquals(i, comp.getIteratorComponent().getVar());
	
	}


}

package pt.iscte.paddle.codexplainer.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static pt.iscte.paddle.model.IOperator.AND;
import static pt.iscte.paddle.model.IOperator.DIFFERENT;
import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import java.util.List;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.model.roles.impl.MostWantedHolder;
import pt.iscte.paddle.codexplainer.translator.VariableRoleExplainer;
import pt.iscte.paddle.model.IArrayType;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.impl.ReferenceType;

class MostWantedHolderTest {

	@Test
	void testGreater() {
		
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
	
		assertTrue(MostWantedHolder.isMostWantedHolder(max.getVariable("m")));		
		assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("i")));
		assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("v")));
		assertEquals("MostWantedHolder(GREATER)", new MostWantedHolder(max.getVariable("m")).toString());
		
	}
	
	@Test 
	void testSmaller() {
		
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
	ISelection ifstat = loop.addSelection(SMALLER.on(array.element(i), m));
    ifstat.addAssignment(m, array.element());
	loop.addIncrement(i);
	body.addReturn(m);
		
	assertTrue(MostWantedHolder.isMostWantedHolder(max.getVariable("m")));		
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("i")));
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("v")));
	assertEquals("MostWantedHolder(SMALLER)", new MostWantedHolder(max.getVariable("m")).toString());		
	}
	
	@Test
	void testFalseBranches() {
		
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
	
	ISelection ifstat = body.addSelection(SMALLER.on(array.element(i), m)); //For m to be a mostWantedHolder the Selection must be inside a Loop
    ifstat.addAssignment(m, array.element());
	
	ILoop loop = body.addLoop(SMALLER.on(i, array.length()));
	loop.addIncrement(i);
	body.addReturn(m);	
	
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("m")));
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("i")));
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("v")));
	}
	
	
	@Test
	void testSelectionAssignment() {
		
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
	
	ISelection ifstat = loop.addSelection(SMALLER.on(array.element(i), m));
    ifstat.addAssignment(i, array.element()); //This Statement should make m not a mostWantedHolder
	loop.addIncrement(i);
	body.addReturn(m);
	
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("m")));
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("i")));
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("v")));
	}
	
	@Test
	void testSelectionGuard() {
		
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
	
	ISelection ifstat = loop.addSelection(SMALLER.on(array.element(i), i));
    ifstat.addAssignment(m, array.element()); //This Statement should make m not a mostWantedHolder
	loop.addIncrement(i);
	body.addReturn(m);
	
	//System.out.println(max);
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("m")));
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("i")));
	assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("v")));
	}
	
	
	@Test
	void testWhileGuard() {		
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
		
		ILoop loop = body.addLoop(AND.on(SMALLER.on(i, m), 
				AND.on(GREATER.on(m, m), DIFFERENT.on(m.expression(), i.expression()))));
		
		
		ISelection ifstat = loop.addSelection(SMALLER.on(array.element(i), i));
	    ifstat.addAssignment(m, array.element()); //This Statement should make m not a mostWantedHolder
		loop.addIncrement(i);
		body.addReturn(m);
		
		//System.out.println(max);
		assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("m")));
		assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("i")));
		assertFalse(MostWantedHolder.isMostWantedHolder(max.getVariable("v")));
		
	}
	
	@Test
	void testExplanation() {
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
		
		assertTrue(MostWantedHolder.isMostWantedHolder(max.getVariable("m")));		
		
		MostWantedHolder mhw = new MostWantedHolder(m);
		
		
		List<TextComponent> list =VariableRoleExplainer.getRoleExplanationPT(m, mhw);
	}
	
	

}

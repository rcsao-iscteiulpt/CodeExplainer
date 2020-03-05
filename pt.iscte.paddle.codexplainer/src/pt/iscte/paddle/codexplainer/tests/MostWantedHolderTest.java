package pt.iscte.paddle.codexplainer.tests;
import static org.junit.jupiter.api.Assertions.*;
import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import java.io.File;

import org.junit.jupiter.api.Test;

import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.demo2.MostWantedHolder;

class MostWantedHolderTest {

	@Test
	void test() {
		
		//True Greater
		IModule moduleFalseCase = IModule.create();
		IModule moduleTrueCase = IModule.create();
		
		IProcedure max = moduleTrueCase.addProcedure(INT);
		IVariableDeclaration array = max.addParameter(INT.array().reference());
		array.setId("v");
		
		IBlock body = max.getBody();
		
		IVariableDeclaration m = body.addVariable(INT);
		m.setId("m");
		IVariableAssignment mAss = body.addAssignment(m, array.element(INT.literal(0)));
		
		IVariableDeclaration i = body.addVariable(INT);
		i.setId("i");
		IVariableAssignment iAss = body.addAssignment(i, INT.literal(1));
		ILoop loop = body.addLoop(SMALLER.on(i, array.length()));
		ISelection ifstat = loop.addSelection(GREATER.on(array.element(i), m));
		IVariableAssignment mAss_ = ifstat.addAssignment(m, array.element(i));
		IVariableAssignment iInc = loop.addIncrement(i);
		IReturn ret = body.addReturn(m);
	
	
		//True Smaller
		
		IProcedure max2 = moduleTrueCase.addProcedure(INT);
		IVariableDeclaration array2 = max2.addParameter(INT.array().reference());
		array2.setId("v");
		
		IBlock body2 = max2.getBody();
		
		IVariableDeclaration m2 = body2.addVariable(INT);
		m2.setId("m");
		IVariableAssignment mAss2 = body2.addAssignment(m2, array2.element(INT.literal(0)));
		IVariableDeclaration i2 = body2.addVariable(INT);
		i2.setId("i");
		IVariableAssignment iAss2 = body2.addAssignment(i2, INT.literal(1));
		ILoop loop2 = body2.addLoop(SMALLER.on(i2, array2.length()));
		ISelection ifstat2 = loop2.addSelection(SMALLER.on(array2.element(i), m2));
		IVariableAssignment mAss2_ = ifstat2.addAssignment(m2, array2.element(i));
		IVariableAssignment iInc2 = loop2.addIncrement(i);
		IReturn ret2 = body2.addReturn(m2);
		
		
		//False method
		
		for(IProcedure proc : moduleFalseCase.getProcedures()) {
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("m")));
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("i")));
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("v")));
		}
		
		for(IProcedure proc : moduleTrueCase.getProcedures()) {
			assertTrue(MostWantedHolder.isMostWantedHolder(proc.getVariable("m")));
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("i")));
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("v")));
		}
		
		IProcedure proc1 = moduleTrueCase.getProcedures().get(0);
		assertEquals("MostWantedHolder(GREATER)", new MostWantedHolder(proc1.getVariable("m")).toString());
		IProcedure proc2 = moduleTrueCase.getProcedures().get(1);
		assertEquals("MostWantedHolder(SMALLER)", new MostWantedHolder(proc2.getVariable("m")).toString());
			
		
		
		
	
		
		
		
		//fail("Not yet implemented");
	}

}

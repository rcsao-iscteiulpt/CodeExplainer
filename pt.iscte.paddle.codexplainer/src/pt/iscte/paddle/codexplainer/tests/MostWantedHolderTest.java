package pt.iscte.paddle.codexplainer.tests;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.demo2.MostWantedHolder;

class MostWantedHolderTest {

	@Test
	void test() {
		Translator translator1 = new Translator(new File("TestFunctions/mostwantedholderFalse.javali").getAbsolutePath());
		Translator translator2 = new Translator(new File("TestFunctions/mostwantedholderTrue.javali").getAbsolutePath());
		
		IModule module1 = translator1.createProgram();
		IModule module2 = translator2.createProgram();
		
		//False method
		
		for(IProcedure proc : module1.getProcedures()) {
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("m")));
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("i")));
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("v")));
		}
		
		for(IProcedure proc : module2.getProcedures()) {
			assertTrue(MostWantedHolder.isMostWantedHolder(proc.getVariable("m")));
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("i")));
			assertFalse(MostWantedHolder.isMostWantedHolder(proc.getVariable("v")));
		}
		
		IProcedure proc1 = module2.getProcedures().get(0);
		assertEquals("MostWantedHolder(GREATER)", new MostWantedHolder(proc1.getVariable("m")).toString());
		IProcedure proc2 = module2.getProcedures().get(1);
		assertEquals("MostWantedHolder(SMALLER)", new MostWantedHolder(proc2.getVariable("m")).toString());
			
		
		
		
	
		
		
		
		//fail("Not yet implemented");
	}

}

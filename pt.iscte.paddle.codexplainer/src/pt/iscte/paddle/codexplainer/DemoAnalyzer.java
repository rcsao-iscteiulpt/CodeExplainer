package pt.iscte.paddle.codexplainer;

import java.io.File;

import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;

public class DemoAnalyzer {
	
	public static void main(String[] args) throws ExecutionError {
		Translator translator = new Translator(new File("naturals.javali").getAbsolutePath());
		IModule module = translator.createProgram();
		IProcedure nats = module.getProcedures().iterator().next(); // first
		
		Analyzer.AnalyzeFuntion(nats);
		
	}
	
}

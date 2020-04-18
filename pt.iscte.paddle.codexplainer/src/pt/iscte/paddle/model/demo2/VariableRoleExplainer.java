package pt.iscte.paddle.model.demo2;



import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.impl.*;


public class VariableRoleExplainer {

	
	public static String getRoleExplanation(IVariableDeclaration var, MostWantedHolder role) {
//		MostWantedHolder mostWantedHolder = (MostWantedHolder) role;
//		
//		List<IProgramElement> expressionList = mostWantedHolder.getExpressions();
//		
//	    IVariableExpression targetVar = var.expression();
//		
//		IBinaryExpression e = ((IBinaryExpression)expressionList.get(0));
//		if( e.getLeftOperand().isSame(targetVar)) {
//			arrayEl = (IArrayElement) e.getRightOperand();
//		} else {
//			arrayEl = (IArrayElement) e.getLeftOperand();
//		}
		
		return "\nA função da variável "+ var + " é MostWanteHolder. É uma variável que vai guardar o valor mais apropriado(maior/menor) de um certo conjunto de valores." + "\n"; 
//		+ "Neste caso, o vetor " + arrayVar 
//				+ " vai ser iterado e cada vez que " + arrayEl + " for " + s2 + " " + targetVar+ ", " + targetVar+ " irá guardar o valor de "+ arrayEl + "\nSendo assim, após o while "+ targetVar +" irá conter o valor mais " + s1 + " do vetor " +arrayVar+".";	
	}
	

	public static String getRoleExplanation(IVariableDeclaration var, Stepper role) {
			
		return "\nA função da variável "+ var + " é Stepper. É uma variável que vai ser sujeita a uma sucessão sistemática de valores." + "\n"; 
	}
	
	public static String getRoleExplanation(IVariableDeclaration var, Gatherer role) {
		
		return "\nA função da variável "+ var + " é Gatherer. É uma variável que vai ser acumulada por uma série de valores." + "\n"; 
	}
	
	public static String getRoleExplanation(IVariableDeclaration var, FixedValue role) {
		
		return "\nA função da variável "+ var + " é FixedValue. É uma variável que vai manter o seu valor após ser inicializada." + "\n"; 
	}
	
//	public static List<List<TextComponent>> getMethodClassificationExplanation(FunctionClassifier func) {
//		List<List<TextComponent>> line = new ArrayList<List<TextComponent>>();
//		
//		func.getAssignments();
//		
//		//TODO
//		
//		
//		return line;
//	}

	
	
}

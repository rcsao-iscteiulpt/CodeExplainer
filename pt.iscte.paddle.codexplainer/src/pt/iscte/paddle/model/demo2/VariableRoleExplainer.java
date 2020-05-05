package pt.iscte.paddle.model.demo2;



import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.*;
import pt.iscte.paddle.model.roles.IVariableRole;



public class VariableRoleExplainer {

	
	public static String getRoleExplanationPT(IVariableDeclaration var, IVariableRole varRole) {
		if(varRole instanceof IMostWantedHolder)
			return "\nA função da variável "+ var + " é MostWanteHolder. "
					+ "É uma variável que vai guardar o valor mais apropriado(maior/menor) de um certo conjunto de valores." + "\n"; 
		
		if(varRole instanceof IStepper)
			return "\nA função da variável "+ var + " é Stepper. "
					+ "É uma variável que vai ser sujeita a uma sucessão sistemática de valores." + "\n"; 
		
		if(varRole instanceof IGatherer)
			return "\nA função da variável "+ var + " é Gatherer. "
					+ "É uma variável que vai acumular uma série de valores diferentes." + "\n"; 

				
		if(varRole instanceof IFixedValue)
			return "\nA função da variável "+ var + " é FixedValue. "
					+ "É uma variável que vai manter o seu valor após ser inicializada." + "\n"; 

			
		if(varRole instanceof IArrayIndexIterator)	
			return "\nA função da variável "+ var + " é FixedValue. É uma variável que vai manter o seu valor após ser inicializada." + "\n";
		
		return ""; 
	}
	



	
	
}

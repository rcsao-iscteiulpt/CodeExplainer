package pt.iscte.paddle.model.demo2;



import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.*;
import pt.iscte.paddle.model.roles.IVariableRole;



public class VariableRoleExplainer {

	
	public static String getRoleExplanationPT(IVariableDeclaration var, IVariableRole varRole) {
		if(varRole instanceof IMostWantedHolder)
			return "\nA fun��o da vari�vel "+ var + " � MostWanteHolder. "
					+ "� uma vari�vel que vai guardar o valor mais apropriado(maior/menor) de um certo conjunto de valores." + "\n"; 
		
		if(varRole instanceof IStepper)
			return "\nA fun��o da vari�vel "+ var + " � Stepper. "
					+ "� uma vari�vel que vai ser sujeita a uma sucess�o sistem�tica de valores." + "\n"; 
		
		if(varRole instanceof IGatherer)
			return "\nA fun��o da vari�vel "+ var + " � Gatherer. "
					+ "� uma vari�vel que vai acumular uma s�rie de valores diferentes." + "\n"; 

				
		if(varRole instanceof IFixedValue)
			return "\nA fun��o da vari�vel "+ var + " � FixedValue. "
					+ "� uma vari�vel que vai manter o seu valor ap�s ser inicializada." + "\n"; 

			
		if(varRole instanceof IArrayIndexIterator)	
			return "\nA fun��o da vari�vel "+ var + " � FixedValue. � uma vari�vel que vai manter o seu valor ap�s ser inicializada." + "\n";
		
		return ""; 
	}
	



	
	
}

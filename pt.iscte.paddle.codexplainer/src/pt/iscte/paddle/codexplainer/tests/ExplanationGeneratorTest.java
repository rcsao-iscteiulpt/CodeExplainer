package pt.iscte.paddle.codexplainer.tests;


import java.util.ArrayList;
import java.util.List;


import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;

import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.demo2.VariableRoleExplainer;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.model.roles.impl.ArrayIndexIterator;
import pt.iscte.paddle.model.roles.impl.FixedValue;
import pt.iscte.paddle.model.roles.impl.Gatherer;
import pt.iscte.paddle.model.roles.impl.MostWantedHolder;
import pt.iscte.paddle.model.roles.impl.Stepper;

public class ExplanationGeneratorTest {

	

	public static List<VariableRoleComponent> getVariableRoleTest(List<IVariableDeclaration> list) {
		List<VariableRoleComponent> variablesRolesExplanation = new ArrayList<VariableRoleComponent>();
		
		for (IVariableDeclaration var : list) {
			IVariableRole role = IVariableRole.match(var);
			System.out.println("Var:  " + var + "|| role: " + role);
			
			if (role instanceof MostWantedHolder) {
				IVariableRole m = new MostWantedHolder(var);
				variablesRolesExplanation.add(new VariableRoleComponent(var, role,
						VariableRoleExplainer.getRoleExplanationPT(var, (MostWantedHolder) m)));
			}
			if (role instanceof ArrayIndexIterator) {
				variablesRolesExplanation.add(new VariableRoleComponent(var, role,
						VariableRoleExplainer.getRoleExplanationPT(var, (ArrayIndexIterator) role)));
			}
			if (role instanceof Gatherer) {
				variablesRolesExplanation.add(new VariableRoleComponent(var, role,
						VariableRoleExplainer.getRoleExplanationPT(var, (Gatherer) role)));
			}
			if (role instanceof Stepper) {
				variablesRolesExplanation.add(new VariableRoleComponent(var, role,
						VariableRoleExplainer.getRoleExplanationPT(var, (Stepper) role)));
			}
			if (role instanceof FixedValue) {
				variablesRolesExplanation.add(new VariableRoleComponent(var, role,
						VariableRoleExplainer.getRoleExplanationPT(var, (FixedValue) role)));
			}
		}
		// etc....
		return variablesRolesExplanation;
	}

}

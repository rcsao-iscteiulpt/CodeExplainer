package pt.iscte.paddle.codexplainer.components;

import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.IVariableRole;

public class VariableRoleComponent {

	private IVariableDeclaration var;
	private IVariableRole role = IVariableRole.NONE;
	private String roleExplanation;

	
	public VariableRoleComponent(IVariableDeclaration var, IVariableRole role, String roleExplanation) {
		this.role = role;
		this.var = var;
		this.roleExplanation = roleExplanation;
	}
	
	public VariableRoleComponent() {
		
	}
	
	public IVariableDeclaration getVar() {
		return var;
	}
	
	public IVariableRole getRole() {
		return role;
	}
	
	public String getRoleExplanation() {
		return roleExplanation;
	}
}	


package pt.iscte.paddle.codexplainer.components;

import java.util.List;

import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.IVariableRole;

public class VariableRoleComponent {

	private IVariableDeclaration var;
	private IVariableRole role = IVariableRole.NONE;

	
	public VariableRoleComponent(IVariableDeclaration var, IVariableRole role) {
		this.role = role;
		this.var = var;
	}
	
	public VariableRoleComponent() {
		
	}
	
	public IVariableDeclaration getVar() {
		return var;
	}
	
	public IVariableRole getRole() {
		return role;
	}

}	


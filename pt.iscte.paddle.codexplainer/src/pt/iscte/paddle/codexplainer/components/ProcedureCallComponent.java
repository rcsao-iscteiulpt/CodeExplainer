package pt.iscte.paddle.codexplainer.components;

import java.util.List;

import pt.iscte.paddle.model.IProcedureCall;

public class ProcedureCallComponent extends Component{

	
	
	boolean isRecursive;
	
	public ProcedureCallComponent(List<VariableRoleComponent> list, IProcedureCall procCall) {
		super.element = procCall;
		
		
	}	
}

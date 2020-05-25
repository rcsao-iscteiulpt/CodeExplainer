package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IProgramElement;

public class ProcedureCallComponent extends Component{

	
	
	boolean isRecursive = false;
	List<IExpression> arguments = new ArrayList<IExpression>();
	
	public ProcedureCallComponent(List<VariableRoleComponent> list, IProcedureCall procCall, MethodComponent mc) {
		super.element = procCall;
		super.mc = mc;
		arguments = procCall.getArguments();
		
		if(mc.getRecursive().getExpressions().contains(procCall)) {
			isRecursive = true;
		}
		
		
	}	
}

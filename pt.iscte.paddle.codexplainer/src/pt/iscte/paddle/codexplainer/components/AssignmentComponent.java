package pt.iscte.paddle.codexplainer.components;

import java.util.List;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IStatement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IGatherer;
import pt.iscte.paddle.model.roles.IMostWantedHolder;
import pt.iscte.paddle.model.roles.IStepper;

public class AssignmentComponent extends Component {

	private VariableRoleComponent statementRole = new VariableRoleComponent();
	private IStatement statement;
	private boolean isDeclaration;
	private IExpression target;

	public AssignmentComponent(List<VariableRoleComponent> list, IStatement statement, IExpression target,boolean isDeclaration) {
		this.statement = statement;
		this.isDeclaration = isDeclaration;
		this.target = target;
		super.element = statement;

		for (VariableRoleComponent v : list) {
			if (v.getRole() instanceof IMostWantedHolder) {
				IMostWantedHolder role = (IMostWantedHolder) v.getRole();
				if (role.getExpressions().get(2).isSame(statement)) {
					statementRole = v;
				}
			}
			if (v.getRole() instanceof IArrayIndexIterator || v.getRole() instanceof IStepper) {
				IStepper iterator = (IStepper) v.getRole();
				if (iterator.getExpressions().get(0).isSame(statement))
					statementRole = v;
			}
			if (v.getRole() instanceof IGatherer) {
				IGatherer gat = (IGatherer) v.getRole();
				for(IProgramElement e: gat.getExpressions()) {
					if(e.isSame(statement))
						statementRole = v;
				}
					
			}
		}

	}

	public IExpression getTarget() {
		return target;
	}

	public boolean isDeclaration() {
		return isDeclaration;
	}

	public IStatement getStatement() {
		return statement;
	}
	
	public VariableRoleComponent getStatementRoleComponent() {
		return statementRole;
	}
	

}

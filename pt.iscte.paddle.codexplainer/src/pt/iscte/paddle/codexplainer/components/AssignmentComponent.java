package pt.iscte.paddle.codexplainer.components;

import java.util.List;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IStatement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
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

		for (VariableRoleComponent v : list) {
			if (v.getRole() instanceof IMostWantedHolder) {
				IMostWantedHolder role = (IMostWantedHolder) v.getRole();
				if (role.getExpressions().get(2).isSame(statement)) {
					statementRole = v;
				}
			}
			if (v.getRole() instanceof IArrayIndexIterator || v.getRole() instanceof IStepper) {
				if (statement instanceof IVariableAssignment) {
					IVariableAssignment assign = (IVariableAssignment) statement;
					if ((assign.isIncrement() || assign.isDecrement()) && target.isSame(assign.getTarget()))
						statementRole = v;
				}
			}
			if (v.getRole() instanceof IGatherer) {
				if (statement instanceof IVariableAssignment
						&& v.getVar().isSame(((IVariableAssignment) statement).getTarget())) {
					IVariableAssignment assign = (IVariableAssignment) statement;
					if (assign instanceof IBinaryExpression) {
						IBinaryExpression ex = (IBinaryExpression) assign.getExpression();
						IExpression left = ex.getLeftOperand();
						IExpression right = ex.getRightOperand();
						if (ex.getOperator().isArithmetic() && (left instanceof IVariableExpression
								&& ((IVariableExpression) left).is(assign.getTarget()) && (right instanceof ILiteral)
								|| right instanceof IVariableExpression
										&& ((IVariableExpression) right).is(assign.getTarget())
										&& (left instanceof ILiteral))) {
							statementRole = v;
						}
					}

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

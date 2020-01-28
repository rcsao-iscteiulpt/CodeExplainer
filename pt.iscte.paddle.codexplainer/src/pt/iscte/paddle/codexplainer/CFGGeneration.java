package pt.iscte.paddle.codexplainer;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.paddle.model.cfg.IStatementNode;
import pt.iscte.paddle.roles.IVariableRole;

/**
 * An experimentation to generate a Control Flow graph for a method(still incomplete)
 * @author Ricard0
 *
 */
public interface CFGGeneration {

	class Visitor implements IBlock.IVisitor {
		IControlFlowGraph cfg;
		Boolean firstAddition = false;

		Visitor(IControlFlowGraph cfg) {
			this.cfg = cfg;
		}

		@Override
		public boolean visit(IVariableAssignment assignment) {
			System.out.println(assignment);
			cfg.newStatement(assignment);

			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			cfg.newStatement(assignment);
			return false;
		}
		
		@Override
		public boolean visit(ILoop expression) {
			
			System.out.println(expression);
			//expression,
			return true;
		}

		@Override
		public boolean visit(ISelection expression) {
			
			System.out.println(expression);
			return true;
		}

	}

	static IControlFlowGraph getControlFlowGraph(IControlFlowGraph cfg, IBlock body) {
		Visitor v = new Visitor(cfg);
		body.getOwnerProcedure().accept(v);
		return cfg;
	}

}

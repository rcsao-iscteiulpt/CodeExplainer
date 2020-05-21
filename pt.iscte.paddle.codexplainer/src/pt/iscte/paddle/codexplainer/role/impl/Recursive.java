package pt.iscte.paddle.codexplainer.role.impl;

import java.util.List;

import pt.iscte.paddle.codexplainer.roles.IRecursive;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IVariableDeclaration;

public class Recursive implements IRecursive {

	private List<IProgramElement> expressionList;

	public Recursive(IProcedure method) {
		Visitor v = new Visitor(method.getBody());
		method.accept(v);
		expressionList = v.expressionList;
	}

	class Visitor implements IBlock.IVisitor {
		List<IProgramElement> expressionList;

		String methodId;

		Boolean isRecursive = false;

		Visitor(IBlock body) {
			methodId = body.getParent().getId();
		}

		@Override
		public boolean visit(IReturn ret) {
			if (ret.getExpression() instanceof IProcedureCall) {
				IProcedureCall proc = (IProcedureCall) ret.getExpression();
				String pattern = "\\(.*\\)";
				String procName = proc.toString().replaceAll(pattern, "");
				if (procName.equals(methodId))
					expressionList.add(proc);
			}
			return false;
		}

		@Override
		public boolean visit(IProcedureCall proc) {
			String pattern = "\\(.*\\)";
			String procName = proc.toString().replaceAll(pattern, "");
			if (procName.equals(methodId))
				expressionList.add(proc);

			return false;
		}
	}

	@Override
	public List<IProgramElement> getExpressions() {
		return expressionList;
	}

}

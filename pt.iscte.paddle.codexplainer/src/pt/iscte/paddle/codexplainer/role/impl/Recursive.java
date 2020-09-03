package pt.iscte.paddle.codexplainer.role.impl;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.roles.IRecursive;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IProcedureCallExpression;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;

public class Recursive implements IRecursive {

	private List<IProgramElement> expressionList;
	private ISelection conditionElement;

	public Recursive(IProcedure method) {
		Visitor v = new Visitor(method.getBody());
		method.accept(v);
		expressionList = v.expressionList;
		conditionElement = v.conditionElement;
	}

	class Visitor implements IBlock.IVisitor {
		List<IProgramElement> expressionList = new ArrayList<IProgramElement>();
		ISelection conditionElement;

		IProcedure proc;

		Visitor(IBlock body) {
			proc = (IProcedure) body.getParent();
		}
		
		@Override
		public boolean visit(IReturn ret) {	
			if(ret.getParent().getParent() instanceof ISelection)
				conditionElement = (ISelection) ret.getParent().getParent();
			return true;
		}

		@Override
		public boolean visit(IProcedureCallExpression ex) {		 			
			if (proc.isSame(ex.getProcedure())) {
				expressionList.add(ex);				
			}	
			return false;
		}

		@Override
		public boolean visit(IProcedureCall proc) {
			if (proc.isSame(proc.getProcedure())) {
				expressionList.add(proc);
				if(proc.getParent() instanceof ISelection)
					conditionElement = (ISelection) proc.getParent();
			}	
			return false;
		}
	}

	@Override
	public List<IProgramElement> getExpressions() {
		return expressionList;
	}

	@Override
	public ISelection getConditionElement() {
		return conditionElement;
	}

}

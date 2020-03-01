package pt.iscte.paddle.model.demo2;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IReturn;


public interface IRecursive {

	class Visitor implements IBlock.IVisitor {
		String methodId;

		Boolean isRecursive = false;

		Visitor(IBlock body) {
			methodId = body.getParent().getId();
		}

		@Override
		public boolean visit(IReturn ret) {
			if(ret.getExpression() instanceof IProcedureCall) {
				IProcedureCall proc = (IProcedureCall) ret.getExpression();
				String pattern = "\\(.*\\)";
				String procName = proc.toString().replaceAll(pattern, "");
				if(procName.equals(methodId)) 
					isRecursive = true;
			}
			
			return false;
		}
	}

	static boolean isRecursive(IProcedure method) {
		IBlock body = method.getBody();
		Visitor v = new Visitor(body);
		body.getOwnerProcedure().accept(v);
		return v.isRecursive;

	}

}


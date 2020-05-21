package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.*;
import static pt.iscte.paddle.model.IType.*;

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProcedureCallExpression;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestFactorialRecursive extends BaseTest {
	IProcedure factorial = module.addProcedure(INT);
	IVariableDeclaration n = factorial.addParameter(INT);
	IBinaryExpression guard = EQUAL.on(n, INT.literal(0));
	ISelection sel = factorial.getBody().addSelectionWithAlternative(guard);
	IReturn return1 = sel.addReturn(INT.literal(1));
	IProcedureCallExpression recCall = factorial.expression(SUB.on(n, INT.literal(1)));
	IBinaryExpression retExp = MUL.on(n, recCall);
	IBlock elseBlock = sel.getAlternativeBlock();
	IReturn return2 = elseBlock.addReturn(retExp);
	

}
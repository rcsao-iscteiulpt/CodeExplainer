package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestSumMatrix extends BaseTest {

	IProcedure sumMatrixLine = module.addProcedure(INT);
	IVariableDeclaration array = sumMatrixLine.addParameter(INT.array().array().reference());
	IBlock sumBody = sumMatrixLine.getBody();
	IVariableDeclaration sum = sumBody.addVariable(INT, INT.literal(0));	
	IVariableDeclaration i = sumBody.addVariable(INT, INT.literal(0));
	ILoop loopI = sumBody.addLoop(SMALLER.on(i, array.length()));
	IVariableDeclaration j = loopI.addVariable(INT, INT.literal(0));
	ILoop loopJ = loopI.addLoop(SMALLER.on(j, array.length(i)));
	IVariableAssignment sumAss = loopJ.addAssignment(sum, ADD.on(sum, array.element(i).element(j)));
	IVariableAssignment assJ = loopJ.addIncrement(j);
	IVariableAssignment assI = loopI.addIncrement(i);
	IReturn ret = sumBody.addReturn(sum);

}

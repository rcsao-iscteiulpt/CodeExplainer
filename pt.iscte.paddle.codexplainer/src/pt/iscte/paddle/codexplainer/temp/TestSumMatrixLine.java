package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestSumMatrixLine extends BaseTest {

	IProcedure sumMatrixLine = module.addProcedure(INT);
	IVariableDeclaration matrix = sumMatrixLine.addParameter(INT.array().array().reference());
	IVariableDeclaration n = sumMatrixLine.addParameter(INT);
	IBlock sumBody = sumMatrixLine.getBody();
	
	IVariableDeclaration sum = sumBody.addVariable(INT, INT.literal(0));	
	IVariableDeclaration i = sumBody.addVariable(INT, INT.literal(0));
	ILoop loop = sumBody.addLoop(SMALLER.on(i, matrix.length(n)));
	
	IArrayElement el = matrix.element(n, i);
	IVariableAssignment sumAss = loop.addAssignment(sum, ADD.on(sum, el));
	IReturn ret = sumBody.addReturn(sum);

	
}

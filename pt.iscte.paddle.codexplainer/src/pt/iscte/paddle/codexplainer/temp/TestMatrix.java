package pt.iscte.paddle.codexplainer.temp;
import static pt.iscte.paddle.model.IOperator.*;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IReturn;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;

public class TestMatrix extends BaseTest {
	IProcedure matrixTest = getModule().addProcedure(INT.array2D().reference());
	
	IVariableDeclaration w = matrixTest.addParameter(INT);
	IVariableAssignment wAss = matrixTest.getBody().addAssignment(w, IOperator.MINUS.on(MINUS.on(
			ADD.on(INT.literal(1), MINUS.on(w)))));
	
	IVariableDeclaration n = matrixTest.addParameter(INT);
	IVariableDeclaration m = matrixTest.getBody().addVariable(INT.array2D().reference());
	IVariableAssignment mAss = matrixTest.getBody().addAssignment(m, INT.array2D().heapAllocation(n, n));
	IArrayElementAssignment assEl = matrixTest.getBody().addArrayElementAssignment(m, INT.literal(1), INT.literal(0), INT.literal(0));
	IVariableDeclaration i = matrixTest.getBody().addVariable(INT, m.length());
	IVariableDeclaration j = matrixTest.getBody().addVariable(INT, m.length(INT.literal(0)));
	IVariableDeclaration array = matrixTest.getBody().addVariable(INT.array(), m.element(INT.literal(0)));
	
	ILoop loop = matrixTest.getBody().addLoop(GREATER.on(i, m.length(INT.literal(0))));
	IVariableAssignment iass = loop.addIncrement(i);
	
	IReturn ret = matrixTest.getBody().addReturn(m);
}
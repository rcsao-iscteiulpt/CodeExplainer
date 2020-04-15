package pt.iscte.paddle.codexplainer.tests;

import static org.junit.jupiter.api.Assertions.*;
import static pt.iscte.paddle.model.IType.INT;
import org.junit.jupiter.api.Test;
import pt.iscte.paddle.codexplainer.role.impl.FunctionClassifier;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier.Status;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordType;

import pt.iscte.paddle.model.IType;

import pt.iscte.paddle.model.IVariableDeclaration;



class FunctionClassifierTest {

	@Test
	/**
	 * Tests the alteration of a field of a Record(e.g record.field = int)
	 */
	void testRecordField() {
		System.out.println("\ntestRecordField || \n");
		
		IModule module = IModule.create();
		
		
		IRecordType objType = module.addRecordType();
		
		IVariableDeclaration a = objType.addField(IType.INT);
		a.setId("field");
		
		IProcedure proc = module.addProcedure(IType.VOID);
		IBlock body = proc.getBody();
		IVariableDeclaration param = proc.addParameter(objType.reference());
		param.setId("parameter");
		IRecordFieldAssignment recordAssignment =  body.addRecordFieldAssignment(param.field(a), IType.INT.literal(7));
	

		System.out.println(proc);
		IFunctionClassifier classifier = new FunctionClassifier(proc);
		System.out.println(classifier.getClassification());
		System.out.println(classifier.getAssignments());
		assertEquals(Status.PROCEDURE, classifier.getClassification());
	}
	
	@Test
	/**
	 * Tests the alteration of a field of a Record which is a field of another Record(e.g record.record.field = int)
	 */
	void testMultipleRecordField() {
		System.out.println("\ntestMultipleRecordField || \n");
		
		IModule module = IModule.create();
		
		
		IRecordType objTypeParent = module.addRecordType();
		IRecordType objType = module.addRecordType();
		IVariableDeclaration a = objTypeParent.addField(objType);
		a.setId("field1");
		
		IVariableDeclaration b = objType.addField(IType.INT);
		b.setId("field2");
		
		IProcedure proc = module.addProcedure(IType.VOID);
		IBlock body = proc.getBody();
		
		IVariableDeclaration param2 = proc.addParameter(objTypeParent.reference());
		param2.setId("parameter");
		IRecordFieldAssignment recordAssignment =  body.addRecordFieldAssignment(param2.field(a).field(b), IType.INT.literal(7));


		
		
		
		System.out.println(proc);
		IFunctionClassifier classifier = new FunctionClassifier(proc);
		System.out.println(classifier.getClassification());
		System.out.println(classifier.getAssignments());
		assertEquals(Status.PROCEDURE, classifier.getClassification());
	}
	
	@Test
	/**
	 * Tests the alteration of a value in an array (e.g array[i] = int)
	 */
	void testArrayAssignment() {
		System.out.println("\ntestArrayAssignment || \n");
		
		IModule module = IModule.create();
		
		IProcedure proc = module.addProcedure(IType.VOID);
		IBlock body = proc.getBody();
		IVariableDeclaration param = proc.addParameter(INT.array().reference());
		param.setId("arrayParam");
		
		
		
		IArrayElementAssignment ass = body.addArrayElementAssignment(param, INT.literal(1),INT.literal(1));
		ass.setId("var");
		
		IVariableDeclaration dec = body.addVariable(INT.array().reference());
		dec.setId("array");
		
		IArrayElementAssignment ass2 = body.addArrayElementAssignment(dec, INT.literal(1),INT.literal(1));
		ass.setId("var2");
	
		
		System.out.println(proc);
		IFunctionClassifier classifier = new FunctionClassifier(proc);
		System.out.println(classifier.getClassification());
		System.out.println(classifier.getAssignments());
		assertEquals(Status.PROCEDURE, classifier.getClassification());
	}
	
	@Test
	void testFunction() {
		System.out.println("\ntestFalse || \n");
		
		IModule module = IModule.create();
		
		
		IProcedure proc = module.addProcedure(IType.VOID);
		IBlock body = proc.getBody();
		IVariableDeclaration param = proc.addParameter(IType.INT);
		IVariableDeclaration param2 = proc.addParameter(INT.array());
		param.setId("i");
		param2.setId("arrayParam");
		
		IVariableDeclaration dec = body.addVariable(INT.array(), param2);
		dec.setId("array");
		IVariableDeclaration dec2 = body.addVariable(INT, INT.literal(4));
		dec2.setId("var");
		//IArrayAllocation all = body.addAssignment(param2, )
		
		
		
		System.out.println(proc);
		IFunctionClassifier classifier = new FunctionClassifier(proc);
		System.out.println(classifier.getClassification());
		System.out.println(classifier.getAssignments());
		assertEquals(Status.FUNCTION, classifier.getClassification());
	}

}

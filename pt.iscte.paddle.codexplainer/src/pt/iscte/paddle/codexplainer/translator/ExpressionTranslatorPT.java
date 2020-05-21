package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.roles.IFunctionClassifier.MethodType;
import pt.iscte.paddle.model.roles.IMostWantedHolder.Objective;
import pt.iscte.paddle.model.IArrayAllocation;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IArrayLength;
import pt.iscte.paddle.model.IArrayType;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProcedureCall;
import pt.iscte.paddle.model.IProcedureCallExpression;
import pt.iscte.paddle.model.IRecordFieldAssignment;
import pt.iscte.paddle.model.IRecordFieldExpression;
import pt.iscte.paddle.model.IRecordType;
import pt.iscte.paddle.model.IReferenceType;
import pt.iscte.paddle.model.IStatement;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IUnaryOperator;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.impl.ArithmeticOperator;
import pt.iscte.paddle.model.impl.RelationalOperator;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.roles.IStepper.Direction;

public class ExpressionTranslatorPT {

	List<TextComponent> line = new ArrayList<>();
	IStatement originalExpression;
	
	public ExpressionTranslatorPT(List<TextComponent> line) {
		this.line = line;
	}

	void translateExpression(IExpression expression) {
		// System.out.println(expression.getClass());
		if (expression instanceof IBinaryExpression) {
			translateBinaryExpression((IBinaryExpression) expression, false);
		}
		

		if (expression instanceof IUnaryExpression)
			translateUnaryExpression((IUnaryExpression) expression);

		if (expression instanceof IArrayElement)
			translateArrayElement((IArrayElement) expression);

		if (expression instanceof IRecordFieldExpression)
			translateRecordFieldExpression((IRecordFieldExpression) expression);

		if (expression instanceof IVariableExpression)
			translateVariableExpression((IVariableExpression) expression, false);

		if (expression instanceof ILiteral)
			translateLiteral((ILiteral) expression);

		if (expression instanceof IArrayAllocation) {
			line.add(new TextComponent(""));
		}
	}

	void translateBinaryExpression(IBinaryExpression ex, boolean isNegative) {
		// StringBuilder g = new StringBuilder();
		IExpression leftEx = ex.getLeftOperand();
		IExpression rightEx = ex.getRightOperand();

		Boolean specialCase = CheckBinaryExpressionSpecialCases(ex);
		if (!specialCase) {

			if (leftEx instanceof IBinaryExpression) {
				translateBinaryExpression((IBinaryExpression) leftEx, isNegative);
			}
			
			if (leftEx instanceof IArrayElement) {
				IArrayElement leftExArray = (IArrayElement) leftEx;
				translateArrayElement(leftExArray);
			}
			if (leftEx instanceof IArrayLength) {
				translateArrayLength((IArrayLength) leftEx);
			}
			if (leftEx instanceof IVariableExpression) {
				translateVariableExpression((IVariableExpression) leftEx, false);
			}
			if (leftEx instanceof ILiteral) {
				translateLiteral((ILiteral) leftEx);
			}
			if (leftEx instanceof IProcedureCallExpression) {
				translateProcedureCallInExpression((IProcedureCallExpression) leftEx);
			}

			translateOperator(ex.getOperator(), isNegative);

			if (rightEx instanceof IBinaryExpression) {
				translateBinaryExpression((IBinaryExpression) rightEx, isNegative);
			}
			
			if (rightEx instanceof IArrayElement) {
				IArrayElement rightExArray = (IArrayElement) rightEx;
				translateArrayElement(rightExArray);
			}
			if (rightEx instanceof IArrayLength) {
				translateArrayLength((IArrayLength) rightEx);
			}
			if (rightEx instanceof IVariableExpression) {
				translateVariableExpression((IVariableExpression) rightEx, false);
			}
			if (rightEx instanceof ILiteral) {
				translateLiteral((ILiteral) rightEx);
			}
			if (rightEx instanceof IProcedureCallExpression) {
				translateProcedureCallInExpression(((IProcedureCallExpression) rightEx));
			}

		}
		return;
	}

	void translateProcedureCall(IProcedureCallExpression expression) {

			
			if(originalExpression.getOwnerProcedure().isSame(expression.getProcedure())) {
				line.add(new TextComponent("Volta a chamar-se com os argumentos:"));
		
			} else {
				line.add(new TextComponent("Chama o método "+ expression.getId() + " com os argumentos:"));
			}
			
			for(IExpression e: expression.getArguments()) {
				translateExpression(e);
				line.add(new TextComponent(""));
			}
		
	}
	
	void translateProcedureCallInExpression(IProcedureCallExpression expression) {
		
		if(originalExpression.getOwnerProcedure().isSame(expression.getProcedure())) {
			line.add(new TextComponent("o resultado da chamada recursiva do mesmo com os argumentos:"));
		} else {
			line.add(new TextComponent("o resultado da chamada do método "+ expression.getId()));
		}
		
		for(IExpression e: expression.getArguments()) {
			translateExpression(e);
			line.add(new TextComponent(""));
		}
	
}

	Boolean CheckBinaryExpressionSpecialCases(IBinaryExpression ex) {
		StringBuilder g = new StringBuilder();
		IExpression leftEx = ex.getLeftOperand();
		IExpression rightEx = ex.getRightOperand();

		// array positions
		ILiteral l = null;
		IArrayLength aLenght = null;
		if (leftEx instanceof IArrayLength && rightEx instanceof ILiteral
				&& ex.getOperator().equals(IBinaryOperator.SUB)) {
			aLenght = (IArrayLength) leftEx;
			l = (ILiteral) rightEx;

		}

		if (rightEx instanceof IArrayLength && leftEx instanceof ILiteral
				&& ex.getOperator().equals(IBinaryOperator.SUB)) {
			aLenght = (IArrayLength) rightEx;
			l = (ILiteral) leftEx;

		}

		if (l != null) {
			if (l.getStringValue().equals("1")) {
				line.add(new TextComponent("a "));
				line.add(new TextComponent("última posição do vetor " + aLenght.getTarget(), ex));
				line.add(new TextComponent(" "));
				return true;
			}
			if (l.getStringValue().equals("2")) {
				line.add(new TextComponent("a "));
				line.add(new TextComponent("penúltima posição do vetor " + aLenght.getTarget(), ex));
				line.add(new TextComponent(" "));
				return true;
			}
		}
		return false;
	}

	void translateUnaryExpression(IUnaryExpression ex) {
		System.out.println("");
		System.out.println(ex.getOperand());
		System.out.println(ex.getOperationType());
		if(ex.getOperator().equals(IUnaryOperator.NOT)) {
			if(ex.getOperand() instanceof IBinaryExpression) 
				translateBinaryExpression((IBinaryExpression) ex.getOperand(), true);
			if(ex.getOperand() instanceof IVariableExpression) 
				translateBooleanVariable((IVariableExpression) ex.getOperand(), true);
			if(ex.getOperand() instanceof IVariableExpression) 
				translateBooleanVariable((IVariableExpression) ex.getOperand(), true);
		}
		if(ex.getOperator().equals(IUnaryOperator.MINUS)) {
			if(ex.getOperand() instanceof IVariableExpression) 
				translateVariableExpression((IVariableExpression) ex.getOperand(), true);		
		}
		
		//TODO MULTIPLE UnaryOperators
	}

	void translateArrayLength(IArrayLength length) {
		line.add(new TextComponent("o valor do "));
		line.add(new TextComponent("tamanho do vetor " + length.getTarget() + " ", length));
	}

	void translateRecordFieldExpression(IRecordFieldExpression expression) {
		// IRecordFieldExpression tempTarget = (IRecordFieldExpression)
		// expression.getTarget();

		// TODO Record Field Translation

	}

	void translateVariableExpression(IVariableExpression expression, boolean isNegative) {
		line.add(new TextComponent("o valor "));
		if((expression.getType().equals(IType.INT) || expression.getType().equals(IType.DOUBLE))) {
			if(isNegative)
				line.add(new TextComponent("negativo "));
		}
		line.add(new TextComponent(" de "+expression.toString() + " "));
	}

	void translateLiteral(ILiteral expression) {
		line.add(new TextComponent(expression.toString() + " "));
	}

	void translateArrayElement(IArrayElement element) {
		IExpression e = element.getIndexes().get(0);
		line.add(new TextComponent("o valor da"));
		if (element.getIndexes().get(0) instanceof IBinaryExpression) {
			IBinaryExpression ex = (IBinaryExpression) e;
			translateBinaryExpression(ex, false);
		} else {
			if (e.equals(IType.INT.literal(0))) {
				line.add(new TextComponent("primeira posição do vetor " + element.getTarget(), element));
				line.add(new TextComponent(" "));
			} else {
				line.add(new TextComponent(
						" posição " + element.getIndexes().get(0) + " do vetor " + element.getTarget() + " "));
			}
		}

		return;
	}

	void translateArrayElement(IExpression array, IExpression index) {

		if (index instanceof IBinaryExpression) {
			IBinaryExpression ex = (IBinaryExpression) index;
			translateBinaryExpression(ex, false);
		} else {
			if (index.equals(IType.INT.literal(0))) {
				line.add(new TextComponent("primeira posição do vetor " + array, index));
				line.add(new TextComponent(" "));
			} else {
				line.add(new TextComponent("posição " + index + " do vetor " + array + " "));
			}
		}

		return;
	}

	String translateDirection(Direction direction) {
		if (direction.equals(Direction.INC)) {
			return "incrementada ";
		}
		if (direction.equals(Direction.DEC)) {
			return "decrementada ";
		}
		return "";
	}

	void translateOperator(IBinaryOperator op, boolean isNegative) {

		if (op.equals(IBinaryOperator.AND)) {
			line.add(new TextComponent("e "));
			return;
		}
		if (op.equals(IBinaryOperator.OR)) {
			line.add(new TextComponent("ou "));
			return;
		}

		if (op instanceof RelationalOperator) {
			line.add(new TextComponent("é "));
			if (op.equals(IBinaryOperator.GREATER)) {
				if(isNegative) {
					line.add(new TextComponent("menor ou igual a "));
					return;
				} 
				line.add(new TextComponent("maior que "));
				return;
			}
			if (op.equals(IBinaryOperator.GREATER_EQ)) {
				if(isNegative) {
					line.add(new TextComponent("menor que "));
					return;
				} 
				line.add(new TextComponent("maior ou igual a "));
				return;
			}
			if (op.equals(IBinaryOperator.SMALLER)) {
				if(isNegative) {
					line.add(new TextComponent("maior ou igual a "));
					return;
				} 
				line.add(new TextComponent("menor que "));
				return;
			}
			if (op.equals(IBinaryOperator.SMALLER_EQ)) {
				if(isNegative) {
					line.add(new TextComponent("maior que "));
					return;
				} 
				line.add(new TextComponent("menor ou igual a "));
				return;
			}
			if (op.equals(IBinaryOperator.EQUAL)) {
				if(isNegative) {
					line.add(new TextComponent("diferente d"));
					return;
				} 
				line.add(new TextComponent("igual a "));
				return;
			}
			if (op.equals(IBinaryOperator.DIFFERENT)) {
				if(isNegative) {
					line.add(new TextComponent("igual a "));
					return;
				} 
				line.add(new TextComponent("diferente d"));
				return;
			}
		}

		if (op instanceof ArithmeticOperator) {
			if (op.equals(IBinaryOperator.MOD)) {
				line.add(new TextComponent("resto de "));
				return;
			}

			line.add(new TextComponent("a "));
			if (op.equals(IBinaryOperator.ADD)) {
				line.add(new TextComponent("somar com "));
				return;
			}
			if (op.equals(IBinaryOperator.DIV)) {
				line.add(new TextComponent("dividir por "));
				return;
			}
			if (op.equals(IBinaryOperator.SUB)) {
				line.add(new TextComponent("subtrair por "));
				return;
			}
			if (op.equals(IBinaryOperator.MUL)) {
				line.add(new TextComponent("multiplicar por "));
				return;
			}
		}

		return;
	}

	String translateMostWantedholderObjective(Objective o) {
		if (o.equals(Objective.GREATER)) {
			return "alto ";
		}
		if (o.equals(Objective.SMALLER)) {
			return "baixo ";
		}
		return "";
	}

	void translateUnaryOperator(IUnaryOperator op) {
		if (op.equals(IUnaryOperator.NOT)) {
			// TODO TranslateUnaryOperator
		}
		return;
	}

	void translateIType(IType type) {
		if (type instanceof IReferenceType) {
			IReferenceType ref = (IReferenceType) type;
			if (ref.getTarget() instanceof IArrayType) {
				line.add(new TextComponent("vetor de "));

				IType compType = ((IArrayType) ref.getTarget()).getComponentType();
				if (compType.equals(IType.BOOLEAN))
					line.add(new TextComponent("booleanos "));
				if (compType.equals(IType.INT))
					line.add(new TextComponent("inteiros "));
				if (compType.equals(IType.DOUBLE))
					line.add(new TextComponent("doubles "));
				// TODO test Record and matrixes
			}
			if (ref.getTarget() instanceof IRecordType)
				line.add(new TextComponent("objecto "));
		} else {
			if (type.equals(IType.BOOLEAN))
				line.add(new TextComponent("booleano "));
			if (type.equals(IType.INT))
				line.add(new TextComponent("inteiro "));
			if (type.equals(IType.DOUBLE))
				line.add(new TextComponent("double "));

			if (type instanceof IArrayType) {
				line.add(new TextComponent("vetor de "));

				IType compType = ((IArrayType) type).getComponentType();
				if (compType.equals(IType.BOOLEAN))
					line.add(new TextComponent("booleanos "));
				if (compType.equals(IType.INT))
					line.add(new TextComponent("inteiros "));
				if (compType.equals(IType.DOUBLE))
					line.add(new TextComponent("doubles "));

			}
		}

	}
	
	void translateMethodType(MethodType type) {
		if(type.equals(MethodType.FUNCTION))  {
			line.add(new TextComponent("função"));
		} else {
			line.add(new TextComponent("procedimento"));
		}

	}

	public void translateAssignment(IStatement assignment, boolean isDeclaration) {
		originalExpression = assignment;
		if (assignment instanceof IVariableAssignment) {
			IVariableAssignment vAssign = (IVariableAssignment) assignment;
			line.add(new TextComponent("A variável " + vAssign.getTarget() + " "));
			if (isDeclaration)
				line.add(new TextComponent("é inicializada com"));
			else
				line.add(new TextComponent("contém "));

			if (vAssign.getExpression() instanceof IBinaryExpression)
				line.add(new TextComponent(" o resultado d"));
			if (vAssign.getExpression() instanceof IArrayAllocation) {
				IArrayAllocation all = (IArrayAllocation) vAssign.getExpression();
				// TODO dimensions fix
				line.add(new TextComponent("o um vetor vazio com tamanho igual a "));
				translateExpression(all.getDimensions().get(0));
			}
			if (!(vAssign.getExpression() instanceof IBinaryExpression)
					&& !(vAssign.getExpression() instanceof IArrayAllocation))
				line.add(new TextComponent(" o valor "));

			translateExpression(vAssign.getExpression());
		}

		if (assignment instanceof IRecordFieldAssignment) {
			IRecordFieldAssignment rAssign = (IRecordFieldAssignment) assignment;
			// TODO RecordField Assignments translations
		}
		if (assignment instanceof IArrayElementAssignment) {
			IArrayElementAssignment aAssign = (IArrayElementAssignment) assignment;
			line.add(new TextComponent("A "));
			translateArrayElement(aAssign.getTarget(), aAssign.getIndexes().get(0));
			line.add(new TextComponent("é igual a "));
			translateExpression(aAssign.getExpression());

			// TODO ArrayElementAssignment Assignments translations

		}

	}

	void translateBooleanVariable(IVariableExpression varExpression, boolean isNegative) {
		if(!isNegative) {
			line.add(new TextComponent("o valor do booleano " + varExpression.getVariable() + " for false "));
		} else {
			line.add(new TextComponent("o valor do booleano " + varExpression.getVariable() + " for true "));
		}
		
	}
}

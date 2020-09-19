package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.MethodComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.model.roles.impl.FunctionClassifier;
import pt.iscte.paddle.model.roles.IFunctionClassifier.Status;
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
import pt.iscte.paddle.model.IOperator;
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
	private MethodComponent mc;

	public ExpressionTranslatorPT(List<TextComponent> line) {
		this.line = line;
	}

	public ExpressionTranslatorPT(List<TextComponent> line, MethodComponent mc) {
		this.line = line;
		this.mc = mc;
	}

	void translateExpression(IExpression expression, boolean isCondition) {
		// System.out.println(expression.getClass());
		if (expression instanceof IBinaryExpression) {
			translateBinaryExpression((IBinaryExpression) expression, false, isCondition);
		}

		if (expression instanceof IUnaryExpression)
			translateUnaryExpression((IUnaryExpression) expression, false);

		if (expression instanceof IArrayElement) {
			IArrayElement el = (IArrayElement) expression;
			if (el.getIndexes().size() == 1) {
				translateArrayElement(el.getTarget(), el.getIndexes().get(0));
			} else if (el.getIndexes().size() == 2) {
				translateMatrixElement(el.getTarget(), el.getIndexes());
			}
		}

		if (expression instanceof IProcedureCall)
			translateProcedureCall((IProcedureCallExpression) expression);

		if (expression instanceof IRecordFieldExpression)
			translateRecordFieldExpression((IRecordFieldExpression) expression);

		if (expression instanceof IVariableExpression) {
			if (expression.getType().equals(IType.BOOLEAN) && isCondition) {
				translateBooleanVariableCondition((IVariableExpression) expression, false);
			} else {
				translateVariableExpression((IVariableExpression) expression, false);
			}
		}
		if (expression instanceof ILiteral)
			translateLiteral((ILiteral) expression, false);

		if (expression instanceof IArrayAllocation) {
			line.add(new TextComponent(""));
		}
		if (expression instanceof IArrayLength)
			translateArrayLength((IArrayLength) expression);
	}

	void translateBinaryExpression(IBinaryExpression ex, boolean isNegative, boolean isCondition) {
		// StringBuilder g = new StringBuilder();
		IExpression leftEx = ex.getLeftOperand();
		IExpression rightEx = ex.getRightOperand();

		Boolean specialCase = CheckBinaryExpressionSpecialCases(ex);
		if (!specialCase) {

			
			if(ex.getOperator().equals(IOperator.MOD)) {
				translateOperator(ex.getOperator(), isNegative);
			}
			
			if (leftEx instanceof IBinaryExpression)
				translateBinaryExpression((IBinaryExpression) leftEx, isNegative, isCondition);

			if (leftEx instanceof IArrayElement) {
				IArrayElement el = (IArrayElement) leftEx;
				IArrayType t = (IArrayType) ((IReferenceType) ((IVariableExpression) el.getTarget()).getVariable()
						.getType()).getTarget();
				if (t.getDimensions() == 1) {
					translateArrayElement(el.getTarget(), el.getIndexes().get(0));
				} else if (t.getDimensions() == 2) {
					translateMatrixElement(el.getTarget(), el.getIndexes());
				}
			}

			if (leftEx instanceof IArrayLength)
				translateArrayLength((IArrayLength) leftEx);

			if (leftEx instanceof IVariableExpression) {
				if (leftEx.getType().equals(IType.BOOLEAN) && isCondition) {
					translateBooleanVariableCondition((IVariableExpression) leftEx, isNegative);
				} else {
					translateVariableExpression((IVariableExpression) leftEx, isNegative);
				}
			}

			if (leftEx instanceof ILiteral)
				translateLiteral((ILiteral) leftEx, isNegative);

			if (leftEx instanceof IProcedureCallExpression)
				translateProcedureCallInExpression((IProcedureCallExpression) leftEx);

			if (leftEx instanceof IUnaryExpression)
				translateUnaryExpression((IUnaryExpression) leftEx, isNegative);
			
			if (leftEx instanceof IRecordFieldExpression)
				translateRecordFieldExpression((IRecordFieldExpression) leftEx);

			if(!ex.getOperator().equals(IOperator.MOD)) {
				translateOperator(ex.getOperator(), isNegative);
			} else {
				line.add(new TextComponent("por "));
			}

			if (rightEx instanceof IBinaryExpression)
				translateBinaryExpression((IBinaryExpression) rightEx, isNegative, isCondition);

			if (rightEx instanceof IArrayElement) {
				IArrayElement el = (IArrayElement) rightEx;
				IArrayType t = (IArrayType) ((IReferenceType) ((IVariableExpression) el.getTarget()).getVariable()
						.getType()).getTarget();
				if (t.getDimensions() == 1) {
					translateArrayElement(el.getTarget(), el.getIndexes().get(0));
				} else if (t.getDimensions() == 2) {
					translateMatrixElement(el.getTarget(), el.getIndexes());
				}

			}
			if (rightEx instanceof IArrayLength)
				translateArrayLength((IArrayLength) rightEx);

			if (rightEx instanceof IVariableExpression) {
				if (rightEx.getType().equals(IType.BOOLEAN) && isCondition) {
					translateBooleanVariableCondition((IVariableExpression) rightEx, isNegative);
				} else {
					translateVariableExpression((IVariableExpression) rightEx, isNegative);
				}
			}
			if (rightEx instanceof ILiteral)
				translateLiteral((ILiteral) rightEx, isNegative);

			if (rightEx instanceof IProcedureCallExpression)
				translateProcedureCallInExpression(((IProcedureCallExpression) rightEx));

			if (rightEx instanceof IUnaryExpression)
				translateUnaryExpression((IUnaryExpression) rightEx, isNegative);
			
			if (rightEx instanceof IRecordFieldExpression)
				translateRecordFieldExpression((IRecordFieldExpression) rightEx);

			
		}
		return;
	}

	void translateProcedureCall(IProcedureCallExpression expression) {
		boolean isRecursive = false;
		String s1 = "função ";
		String s2 = "a ";
		if (mc.getFunctionClassifier().getClassification().equals(Status.PROCEDURE)) {
			s1 = "procedimento ";
			s2 = "o ";
		}

		if (mc.IsRecursive() && mc.getRecursive().getExpressions().contains(expression)) {
			isRecursive = true;
			line.add(new TextComponent(s2.toUpperCase() + s1 + "é sucessivamente invocada ", expression));
			line.add(new TextComponent("com "));
		} else {
			String pattern = "\\(.*\\)";
			String procName = expression.toString().replaceAll(pattern, "");
			line.add(new TextComponent(s2.toUpperCase() + s1));
			line.add(new TextComponent("invoca o método " + procName, expression));
			line.add(new TextComponent(" com os argumentos: "));
		}

		if (isRecursive && expression.getArguments().size() == 1
				&& expression.getArguments().get(0) instanceof IBinaryExpression) {
			IBinaryExpression ex = (IBinaryExpression) expression.getArguments().get(0);
			IExpression left = ex.getLeftOperand();
			IExpression right = ex.getRightOperand();

			if (ex.getOperator().equals(IOperator.ADD)) {
				if (left.isSame(IType.INT.literal(1).expression()) && right instanceof IVariableExpression) {
					line.add(new TextComponent("um valor superior a "));
					linkVariable(right);
				}	
				if (right.isSame(IType.INT.literal(1).expression()) && left instanceof IVariableExpression) {
					line.add(new TextComponent("um valor superior a "));
					linkVariable(left);
				}
			} else if (ex.getOperator().equals(IOperator.SUB)) {
				if (right.isSame(IType.INT.literal(1).expression()) && left instanceof IVariableExpression) {
					line.add(new TextComponent("um valor inferior a "));
					linkVariable(left);
				}
			} else {
				line.add(new TextComponent(" os argumentos:"));
				for (IExpression e : expression.getArguments()) {
					//translateExpression(e, false);
					line.add(new TextComponent(e.toString()));
				}
			}
		} else {
			for (IExpression e : expression.getArguments()) {
				//translateExpression(e, false);
				line.add(new TextComponent(e.toString()));
			}
		}

	}

	void translateProcedureCallInExpression(IProcedureCallExpression expression) {
		String s1 = "função";
		String s2 = "a ";
		String s3 = "a ";
		if (mc.getFunctionClassifier().getClassification().equals(Status.PROCEDURE)) {
			s1 = "procedimento";
			s2 = "o ";
			s3 = "e ";
		}

		if (mc.IsRecursive() && mc.getRecursive().getExpressions().contains(expression)) {
			line.add(new TextComponent("o resultado da "));
			line.add(new TextComponent("invocação recursiva dest" + s3 + s1, expression));
			line.add(new TextComponent(" de "));
		} else {
			FunctionClassifier c = new FunctionClassifier((IProcedure) expression.getProcedure());
			line.add(new TextComponent("o resultado da "));
			line.add(new TextComponent("invocação d" + s2 + s1 + expression, expression));
			line.add(new TextComponent(" com os argumentos: "));
		}
		
		//TODO Robustez Varios argumentos

		for (IExpression e : expression.getArguments()) {
			//translateExpression(e, false);
			line.add(new TextComponent(e.toString()));
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
				line.add(new TextComponent("última posição do vetor " + aLenght.getTarget()+ " ", ex));
				line.add(new TextComponent(" "));
				return true;
			}
			if (l.getStringValue().equals("2")) {
				line.add(new TextComponent("a "));
				line.add(new TextComponent("penúltima posição do vetor " + aLenght.getTarget() + " ", ex));	
				return true;
			}
		}
		return false;
	}

	void translateUnaryExpression(IUnaryExpression ex, boolean isNegative) {
		//isNegative vem sempre a false inicialmente
		
		if (ex.getOperator().equals(IUnaryOperator.NOT)) {
			if (ex.getOperand() instanceof IBinaryExpression)
				translateBinaryExpression((IBinaryExpression) ex.getOperand(), !isNegative, true);
			if (ex.getOperand() instanceof IVariableExpression)
				translateBooleanVariableCondition((IVariableExpression) ex.getOperand(), !isNegative);
			if (ex.getOperand() instanceof IUnaryExpression)
				translateUnaryExpression(ex, !isNegative);
		}
		if (ex.getOperator().equals(IUnaryOperator.MINUS)) {
			if (ex.getOperand() instanceof IVariableExpression)
				translateVariableExpression((IVariableExpression) ex.getOperand(), !isNegative);
			if (ex.getOperand() instanceof ILiteral)
				translateLiteral((ILiteral) ex.getOperand(), !isNegative);
			if (ex.getOperand() instanceof IBinaryExpression) {
				if (!isNegative)
					line.add(new TextComponent("a forma negativa da expressão "));
				translateBinaryExpression((IBinaryExpression) ex.getOperand(), !isNegative, false);
			}	
			if (ex.getOperand() instanceof IUnaryExpression)
				translateUnaryExpression((IUnaryExpression) ex.getOperand(), !isNegative);
		}
		

		// TODO MULTIPLE UnaryOperators
	}

	void translateArrayLength(IArrayLength length) {
		IArrayType t = (IArrayType) ((IReferenceType) ((IVariableExpression) length.getTarget()).getVariable()
				.getType()).getTarget();

		if (t.getDimensions() == 1) {
			line.add(new TextComponent("o valor do "));
			line.add(new TextComponent("tamanho do vetor " + length.getTarget(), length));
			line.add(new TextComponent(" "));
			int i = -2;
			
			int v = -(i+1);
		} else if (t.getDimensions() == 2) {
			if (length.getIndexes().size() == 0) {
				line.add(new TextComponent("o valor do "));
				line.add(new TextComponent("número de linhas da matriz " + length.getTarget(), length));
				line.add(new TextComponent(" "));
			} else {
				if (length.getIndexes().get(0).isSame(IType.INT.literal(0).expression())) {
					line.add(new TextComponent("o valor do "));
					line.add(new TextComponent("tamanho da primeira linha da matriz " + length.getTarget(),
							length));
					line.add(new TextComponent(" "));
				} else if (length.getIndexes().get(0).isSame(IType.INT.literal(1).expression())) {
					line.add(new TextComponent("o valor do "));
					line.add(new TextComponent("tamanho da segunda linha da matriz " + length.getTarget(),
							length));
					line.add(new TextComponent(" "));
				} else {
					line.add(new TextComponent("o valor do "));
					line.add(new TextComponent(
							"tamanho da linha " + length.getIndexes().get(0) + " da matriz " + length.getTarget(),
							length));
					line.add(new TextComponent(" "));
				}
			}
		}

	}

	void translateRecordFieldExpression(IRecordFieldExpression expression) {
		line.add(new TextComponent(
				"o valor do campo " + expression.getField(), expression.expression()));
		line.add(new TextComponent(" do objecto "));
		linkVariable(expression.getTarget());
	}

	void translateVariableExpression(IVariableExpression expression, boolean isNegative) {
		line.add(new TextComponent("o valor "));
		if ((expression.getType().equals(IType.INT) || expression.getType().equals(IType.DOUBLE))) {
			if (isNegative)
				line.add(new TextComponent("negativo "));
		}
		if ((expression.getType().equals(IType.BOOLEAN) )) {
			if (isNegative)
				line.add(new TextComponent("contrário "));
		}
		line.add(new TextComponent("de "));
		linkVariable(expression);
		line.add(new TextComponent(" "));
	}
	
	void linkVariable(IExpression e) {
		line.add(new TextComponent(((IVariableExpression)e).toString(), ((IVariableExpression)e).getVariable()));
	}

	void translateLiteral(ILiteral expression, boolean isNegative) {
		if (expression.getType().equals(IType.BOOLEAN)) {
			if (expression.getStringValue().equals("true")) {
				if (isNegative) {
					line.add(new TextComponent("falso "));
					return;
				}
				line.add(new TextComponent("verdadeiro "));
			} else {
				if (isNegative) {
					line.add(new TextComponent("verdadeiro "));
					return;
				}
				line.add(new TextComponent("falso "));
			}
		} else {
			if (isNegative) {
				line.add(new TextComponent("menos "));
			}
			line.add(new TextComponent(expression.toString() + " "));
		}

	}

	void translateArrayElement(IExpression target, IExpression index) {
		line.add(new TextComponent("o valor da "));
		if (index instanceof IBinaryExpression) {
			line.add(new TextComponent("posição "));
			IBinaryExpression ex = (IBinaryExpression) index;
			translateBinaryExpression(ex, false, false);
		} else {
			if (index.isSame(IType.INT.literal(0).expression())) {
				line.add(new TextComponent("primeira posição", index));
				line.add(new TextComponent(" do vetor "));
				linkVariable(target);
				line.add(new TextComponent(" "));
			} else if (index.isSame(IType.INT.literal(1).expression())) {
				line.add(new TextComponent("segunda posição", index));
				line.add(new TextComponent(" do vetor "));
				linkVariable(target);
				line.add(new TextComponent(" "));
			} else if (index.isSame(IType.INT.literal(2).expression())) {
				line.add(new TextComponent("terceira posição", index));
				line.add(new TextComponent(" do vetor "));
				linkVariable(target);
				line.add(new TextComponent(" "));
			} else {
				if(index instanceof IVariableExpression) {
					line.add(new TextComponent("posição "));
					linkVariable(index);
					line.add(new TextComponent(" do vetor "));
					linkVariable(target);
					line.add(new TextComponent(" "));
				} else {
					line.add(new TextComponent("posição", index));
					line.add(new TextComponent(" do vetor "));
					linkVariable(target);
					line.add(new TextComponent(" "));
				}
			}
		}
		return;
	}

	void translateMatrixElement(IExpression target, List<IExpression> list) {

		if (list.size() == 2) {
			line.add(new TextComponent("o valor da "));
			if (list.get(1) instanceof IBinaryExpression) {
				line.add(new TextComponent("posição "));
				IBinaryExpression ex = (IBinaryExpression) list.get(0);
				translateBinaryExpression(ex, false, false);
			} else {
				if (list.get(1).isSame(IType.INT.literal(0).expression())) {
					line.add(new TextComponent("primeira posição", list.get(1)));
					line.add(new TextComponent(" da "));
				} else {
					if(list.get(1) instanceof IVariableExpression) {
						line.add(new TextComponent("posição "));
						linkVariable(list.get(1));
						line.add(new TextComponent(" da "));
					} else {
						line.add(new TextComponent("posição "));
						linkVariable(list.get(1));
						line.add(new TextComponent(" da "));
					}	
				}
			}

			if (list.get(0) instanceof IBinaryExpression) {
				line.add(new TextComponent("linha "));
				IBinaryExpression ex = (IBinaryExpression) list.get(0);
				translateBinaryExpression(ex, false, false);
			} else {
				if (list.get(0).isSame(IType.INT.literal(0).expression())) {
					line.add(new TextComponent("primeira linha", list.get(0)));
					line.add(new TextComponent(" da matriz "));
					linkVariable(target);
					line.add(new TextComponent(" "));
				} else {
					line.add(new TextComponent("linha " + list.get(0), list.get(0)));
					line.add(new TextComponent(" da matriz "));
					linkVariable(target);
					line.add(new TextComponent(" "));
				}
			}
		} else {
			if (list.get(0).isSame(IType.INT.literal(0).expression())) {
				line.add(new TextComponent("primeira linha", list.get(0)));
				line.add(new TextComponent(" da matriz "));
				linkVariable(target);
				line.add(new TextComponent(" "));
			} else {
				line.add(new TextComponent("linha " + list.get(0), list.get(0)));
				line.add(new TextComponent(" da matriz "));
				linkVariable(target);
				line.add(new TextComponent(" "));
			}
		}

	}

	String translateDirection(Direction direction) {
		if (direction.equals(Direction.INC)) {
			return "incrementado";
		}
		if (direction.equals(Direction.DEC)) {
			return "decrementado";
		}
		return "";
	}

	void translateOperator(IBinaryOperator op, boolean isNegative) {

		if (op.equals(IBinaryOperator.AND)) {
			if (isNegative) {
				line.add(new TextComponent("ou "));
				return;
			}
			line.add(new TextComponent("e "));
			return;
		}
		if (op.equals(IBinaryOperator.OR)) {
			if (isNegative) {
				line.add(new TextComponent("e "));
				return;
			}
			line.add(new TextComponent("ou "));
			return;
		}

		if (op instanceof RelationalOperator) {
			line.add(new TextComponent("é "));
			if (op.equals(IBinaryOperator.GREATER)) {
				if (isNegative) {
					line.add(new TextComponent("menor ou igual a "));
					return;
				}
				line.add(new TextComponent("maior que "));
				return;
			}
			if (op.equals(IBinaryOperator.GREATER_EQ)) {
				if (isNegative) {
					line.add(new TextComponent("menor que "));
					return;
				}
				line.add(new TextComponent("maior ou igual a "));
				return;
			}
			if (op.equals(IBinaryOperator.SMALLER)) {
				if (isNegative) {
					line.add(new TextComponent("maior ou igual a "));
					return;
				}
				line.add(new TextComponent("menor que "));
				return;
			}
			if (op.equals(IBinaryOperator.SMALLER_EQ)) {
				if (isNegative) {
					line.add(new TextComponent("maior que "));
					return;
				}
				line.add(new TextComponent("menor ou igual a "));
				return;
			}
			if (op.equals(IBinaryOperator.EQUAL)) {
				if (isNegative) {
					line.add(new TextComponent("diferente d"));
					return;
				}
				line.add(new TextComponent("igual a "));
				return;
			}
			if (op.equals(IBinaryOperator.DIFFERENT)) {
				if (isNegative) {
					line.add(new TextComponent("igual a "));
					return;
				}
				line.add(new TextComponent("diferente d"));
				return;
			}
		}

		if (op instanceof ArithmeticOperator) {
			if (op.equals(IBinaryOperator.MOD)) {
				line.add(new TextComponent("o resto da divisão de "));
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
			return "maior ";
		}
		if (o.equals(Objective.SMALLER)) {
			return "menor ";
		}
		return "";
	}

//	void translateBooleanPrimitive(IExpression ex) {
//		if (ex.isSame(IType.BOOLEAN.literal(true))) {
//			line.add(new TextComponent("verdadeiro "));
//		} else {
//			line.add(new TextComponent("falso "));
//		}
//	}

	void translateIType(IType type, boolean listswithType) {
		if (type instanceof IReferenceType) {
			IReferenceType ref = (IReferenceType) type;
			if (ref.getTarget() instanceof IArrayType) {
				IArrayType t = (IArrayType) ref.getTarget();
				IType compType;
				if (t.getComponentType() instanceof IArrayType) {
					line.add(new TextComponent("a matriz"));
					compType = ((IArrayType) t.getComponentType()).getComponentType();
				} else {
					line.add(new TextComponent("o vetor"));
					compType = t.getComponentType();
				}

				if (listswithType) {
					line.add(new TextComponent(" de "));
					if (compType.equals(IType.BOOLEAN))
						line.add(new TextComponent("booleanos "));
					if (compType.equals(IType.INT))
						line.add(new TextComponent("inteiros "));
					if (compType.equals(IType.DOUBLE))
						line.add(new TextComponent("doubles "));
				}
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
		}
	}

	void translateMethodType(Status type) {
		if (type.equals(Status.FUNCTION)) {
			line.add(new TextComponent("função"));
		} else {
			line.add(new TextComponent("procedimento"));
		}
	}

	public void translateAssignment(IStatement assignment) {

		if (assignment instanceof IVariableAssignment) {
			IVariableAssignment vAssign = (IVariableAssignment) assignment;
			linkVariable(vAssign.getTarget().expression()); 
			line.add(new TextComponent(" "));

			if (vAssign.getExpression() instanceof IUnaryExpression) {
				line.add(new TextComponent("é alterado para "));
				translateUnaryExpression((IUnaryExpression) vAssign.getExpression(), false);
				
			} else if (vAssign.getExpression() instanceof IBinaryExpression) {
				line.add(new TextComponent("é alterado para "));
				// line.add(new TextComponent(" o resultado d"));
				
				translateBinaryExpression((IBinaryExpression) vAssign.getExpression(), false, false);
			} else if (vAssign.getExpression() instanceof IArrayElement) {
				line.add(new TextComponent("é alterado para "));
				IArrayElement el = (IArrayElement) vAssign.getExpression();
				IArrayType t = (IArrayType) ((IReferenceType) ((IVariableExpression) el.getTarget()).getVariable()
						.getType()).getTarget();
				if (t.getDimensions() == 1) {
					translateArrayElement(el.getTarget(), el.getIndexes().get(0));
				} else if (t.getDimensions() == 2) {
					translateMatrixElement(el.getTarget(), el.getIndexes());
				}
				
			} else if (vAssign.getExpression() instanceof IArrayAllocation) {
				IArrayAllocation all = (IArrayAllocation) vAssign.getExpression();
				if (all.getDimensions().size() == 1) {
					line.add(new TextComponent("é alterado para "));
					line.add(new TextComponent("um vetor vazio com tamanho igual a "));
					translateExpression(all.getDimensions().get(0), false);
				} else if (all.getDimensions().size() == 2) {
					line.add(new TextComponent("é alterado para uma matriz vazia"));
					if (all.getDimensions().get(0).isSame(all.getDimensions().get(1))) {
						line.add(new TextComponent(" com o número de linhas e comprimento das mesmas igual a "));
						translateExpression(all.getDimensions().get(0), false);
					} else {
						line.add(new TextComponent(" com o número de linhas igual a "));
						translateExpression(all.getDimensions().get(0), false);
						line.add(new TextComponent("e comprimento das linhas igual a "));
						translateExpression(all.getDimensions().get(1), false);
					}
				}

			} else if (vAssign.getExpression() instanceof IVariableExpression) {
				IVariableExpression v = (IVariableExpression) vAssign.getExpression();
				if (v.getType() instanceof IReferenceType) {
					if (((IReferenceType) v.getType()).getTarget() instanceof IArrayType) {
						IArrayType t = (IArrayType) ((IReferenceType) v.getType()).getTarget();
						if (t.getDimensions() == 1) {
							line.add(new TextComponent("é alterado para "));
							line.add(new TextComponent("apontar para o vetor " + vAssign.getExpression(), assignment));
						} else if (t.getDimensions() == 2) {
							line.add(new TextComponent("é alterado para "));
							line.add(new TextComponent("apontar para a matriz" + vAssign.getExpression(), assignment));
						}
					}
				}
			} else if (vAssign.getExpression() instanceof ILiteral) {
				line.add(new TextComponent("é alterado para "));
				translateExpression(vAssign.getExpression(), false);
			}
		}

		if (assignment instanceof IRecordFieldAssignment) {
			IRecordFieldAssignment rAssign = (IRecordFieldAssignment) assignment;
			// TODO RecordField Assignments translations
		}
		if (assignment instanceof IArrayElementAssignment) {
			IArrayElementAssignment aAssign = (IArrayElementAssignment) assignment;

			if (aAssign.getDimensions() == 1) {
				translateArrayElement(aAssign.getTarget(), aAssign.getIndexes().get(0));
				line.add(new TextComponent("é alterado para "));
				translateExpression(aAssign.getExpression(), false);
			} else if (aAssign.getDimensions() == 2) {
				translateMatrixElement(aAssign.getTarget(), aAssign.getIndexes());
				line.add(new TextComponent("é alterado para "));
				translateExpression(aAssign.getExpression(), false);
			}

		}
	}

	public void translateDeclarationAssignment(IStatement assignment) {
		if (assignment instanceof IVariableAssignment) {
			IVariableAssignment vAssign = (IVariableAssignment) assignment;
			
			line.add(new TextComponent("A variável "));
			linkVariable(vAssign.getTarget().expression());
			line.add(new TextComponent(" é "));
			line.add(new TextComponent("inicializada", assignment));
			line.add(new TextComponent(" "));
			if (vAssign.getExpression() instanceof IUnaryExpression) {
				line.add(new TextComponent("com "));
				translateUnaryExpression((IUnaryExpression) vAssign.getExpression(), false);
			} else if (vAssign.getExpression() instanceof IBinaryExpression) {
				line.add(new TextComponent("com "));
//				line.add(new TextComponent(" o resultado d"));
				translateBinaryExpression((IBinaryExpression) vAssign.getExpression(), false, false);

			} else if (vAssign.getExpression() instanceof IArrayAllocation) {
				IArrayAllocation all = (IArrayAllocation) vAssign.getExpression();
				if (all.getDimensions().size() == 1) {
					line.add(new TextComponent("como um vetor com tamanho igual a "));
					translateExpression(all.getDimensions().get(0), false);
					System.out.println(all.getType().getClass());
					IArrayType aType = (IArrayType) ((IReferenceType)all.getType()).getTarget();
					if(aType.getComponentType().equals(IType.INT)) 
						line.add(new TextComponent("(elementos a 0)"));
					if(aType.getComponentType().equals(IType.DOUBLE)) 
						line.add(new TextComponent("(elementos a 0.0)"));	
					if(aType.getComponentType().equals(IType.BOOLEAN)) 
						line.add(new TextComponent("(elementos a false)"));		
						
					
						
				} else if (all.getDimensions().size() == 2) {
					line.add(new TextComponent("como uma matriz "));
					IArrayType aType = (IArrayType) ((IReferenceType)all.getType()).getTarget();
					IArrayType mType = (IArrayType) aType.getComponentType();
					if (all.getDimensions().get(0).isSame(all.getDimensions().get(1))) {
						line.add(new TextComponent("com o número de linhas e comprimento das mesmas igual a "));
						translateExpression(all.getDimensions().get(0), false);
					} else {
						line.add(new TextComponent("com o número de linhas igual a "));
						translateExpression(all.getDimensions().get(0), false);
						line.add(new TextComponent("e comprimento das linhas igual a "));
						translateExpression(all.getDimensions().get(1), false);
					}
					if(mType.getComponentType().equals(IType.INT)) 
						line.add(new TextComponent("(elementos a 0)"));
					if(mType.getComponentType().equals(IType.DOUBLE)) 
						line.add(new TextComponent("(elementos a 0.0)"));	
					if(mType.getComponentType().equals(IType.BOOLEAN)) 
						line.add(new TextComponent("(elementos a false)"));	
				}
			} else if (vAssign.getExpression() instanceof IArrayElement) {
				IArrayElement el = (IArrayElement) vAssign.getExpression();
				IArrayType t = (IArrayType) ((IReferenceType) ((IVariableExpression) el.getTarget()).getVariable()
						.getType()).getTarget();
				if (t.getDimensions() == 1) {
					line.add(new TextComponent("com "));
					translateArrayElement(el.getTarget(), el.getIndexes().get(0));
				} else if (t.getDimensions() == 2) {
					if (el.getIndexes().size() == 1) {
						line.add(new TextComponent("com "));
						translateMatrixElement(el.getTarget(), el.getIndexes());
					} else {
						line.add(new TextComponent("com "));
						translateMatrixElement(el.getTarget(), el.getIndexes());
					}

				}

			} else if (vAssign.getExpression() instanceof IVariableExpression) {
				IVariableExpression v = (IVariableExpression) vAssign.getExpression();
				if (v.getType() instanceof IReferenceType) {
					if (((IReferenceType) v.getType()).getTarget() instanceof IArrayType) {
						IArrayType t = (IArrayType) ((IReferenceType) v.getType()).getTarget();
						if (t.getDimensions() == 1) {
							line.add(new TextComponent("a "));
							line.add(new TextComponent("apontar para o vetor " + vAssign.getExpression()));
						} else if (t.getDimensions() == 2) {
							line.add(new TextComponent("a "));
							line.add(new TextComponent("apontar para a matriz" + vAssign.getExpression()));
						}
					}
				} else {
					line.add(new TextComponent("com"));
					translateExpression(vAssign.getExpression(), false);
				}
			} else {
				line.add(new TextComponent("com "));
				translateExpression(vAssign.getExpression(), false);
			}
		}
		if (assignment instanceof IRecordFieldAssignment) {
			IRecordFieldAssignment rAssign = (IRecordFieldAssignment) assignment;
			// TODO RecordField Assignments translations
		}
	}

	void translateBooleanVariableCondition(IVariableExpression varExpression, boolean isNegative) {
		line.add(new TextComponent("o valor da variável "));
		linkVariable(varExpression.getVariable().expression());
		if (!isNegative) {	
			line.add(new TextComponent(" for verdadeiro "));
		} else {
			line.add(new TextComponent(" for falso "));
		}

	}
}

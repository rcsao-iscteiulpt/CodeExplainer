package pt.iscte.paddle.codexplainer.translator;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.TextComponent.TextType;
import pt.iscte.paddle.codexplainer.roles.IMostWantedHolder.Objective;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayLength;
import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBinaryOperator;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IUnaryExpression;
import pt.iscte.paddle.model.IUnaryOperator;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.impl.ArithmeticOperator;
import pt.iscte.paddle.model.impl.RelationalOperator;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.roles.IStepper.Direction;

public class ExpressionTranslatorPT {

	List<TextComponent> line = new ArrayList<>();

	ExpressionTranslatorPT(List<TextComponent> line) {
		this.line = line;
	}

//	static String TranslateVariableAssignment(IVariableAssignment assignment) {
//		StringBuilder g = new StringBuilder();
//		// TODOPOSSIBLE
//		
//		return g.toString();	
//	}

	void translateBinaryExpression(IBinaryExpression ex) {
		// StringBuilder g = new StringBuilder();
		IExpression leftEx = ex.getLeftOperand();
		IExpression rightEx = ex.getRightOperand();

		Boolean specialCase = CheckBinaryExpressionSpecialCases(ex);
		if (!specialCase) {

			if (leftEx instanceof IArrayElement) {
				IArrayElement leftExArray = (IArrayElement) leftEx;
				translateArrayElement(leftExArray);
			}
			if (leftEx instanceof IArrayLength) {
				line.add(new TextComponent("o valor do ", TextType.NORMAL));
				line.add(new TextComponent("tamanho do vetor " + ((IArrayLength) leftEx).getTarget(), TextType.LINK,
						leftEx));
				line.add(new TextComponent(" ", TextType.NORMAL));
			}
			if (leftEx instanceof IVariableExpression) {
				line.add(new TextComponent("o valor de " + leftEx.toString() + " ", TextType.NORMAL));
			}
			if (leftEx instanceof ILiteral) {
				line.add(new TextComponent(leftEx.toString() + " ", TextType.NORMAL));
			}

			translateOperator(ex.getOperator());

			if (rightEx instanceof IArrayElement) {
				IArrayElement rightExArray = (IArrayElement) rightEx;
				translateArrayElement(rightExArray);
			}
			if (rightEx instanceof IArrayLength) {
				line.add(new TextComponent("o valor do ", TextType.NORMAL));
				line.add(new TextComponent("tamanho do vetor " + ((IArrayLength) rightEx).getTarget(), TextType.LINK,
						leftEx));
				line.add(new TextComponent(" ", TextType.NORMAL));
			}
			if (rightEx instanceof IVariableExpression) {
				line.add(new TextComponent("o valor de " + rightEx.toString() + " ", TextType.NORMAL));
			}
			if (rightEx instanceof ILiteral) {
				line.add(new TextComponent(rightEx.toString() + " ", TextType.NORMAL));
			}

		}

		return;
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
				line.add(new TextComponent("última posição do vetor " + aLenght.getTarget(), TextType.LINK, ex));
				line.add(new TextComponent(" ", TextType.NORMAL));
				return true;
			}
			if (l.getStringValue().equals("2")) {
				line.add(new TextComponent("penúltima posição do vetor " + aLenght.getTarget(), TextType.LINK, ex));
				line.add(new TextComponent(" ", TextType.NORMAL));
				return true;
			}
		}
		return false;
	}

	Boolean CheckAssignmentsSpecialCases(IVariableAssignment ex) {

		// TODO
		return null;

	}

	void translateUnaryExpression(IUnaryExpression ex) {
		// TODO
		return;
	}

	void translateArrayElement(IArrayElement element) {
		StringBuilder g = new StringBuilder();
		IExpression e = element.getIndexes().get(0);

		if (element.getIndexes().get(0) instanceof IBinaryExpression) {
			IBinaryExpression ex = (IBinaryExpression) e;
			translateBinaryExpression(ex);
		} else {
			if (e.equals(IType.INT.literal(0))) {
				line.add(new TextComponent("primeira posição do vetor " + element.getTarget(), TextType.LINK, element));
				line.add(new TextComponent(" ", TextType.NORMAL));
			} else {
				line.add(new TextComponent(
						"a posição " + element.getIndexes().get(0) + " do vetor " + element.getTarget() + " ",
						TextType.NORMAL));
			}
		}

		return;
	}

	void translateSimple(IExpression ex) {
		// TODO
		line.add(new TextComponent(ex.toString(), TextType.NORMAL));
		return;
	}
	
	void translateDirection(Direction direction) {
		if(direction.equals(Direction.INC)) {
			line.add(new TextComponent("incrementada por ", TextType.NORMAL));
		}
		if(direction.equals(Direction.DEC)) {
			line.add(new TextComponent("decrementada por ", TextType.NORMAL));
		}
	}
	
	void translateOperator(IBinaryOperator op) {
		
		if (op.equals(IBinaryOperator.AND)) {
			line.add(new TextComponent("e ", TextType.NORMAL));
			return;
		}
		if (op.equals(IBinaryOperator.OR)) {
			line.add(new TextComponent("ou ", TextType.NORMAL));
			return;
		}
		
		
		if(op instanceof RelationalOperator) {
			line.add(new TextComponent("seja ", TextType.NORMAL));
			if (op.equals(IBinaryOperator.GREATER)) {
				line.add(new TextComponent("maior que ", TextType.NORMAL));
				return;
			}
			if (op.equals(IBinaryOperator.GREATER_EQ)) {
				line.add(new TextComponent("maior ou igual a ", TextType.NORMAL));
				return;
			}
			if (op.equals(IBinaryOperator.SMALLER)) {
				line.add(new TextComponent("menor que ", TextType.NORMAL));
				return;
			}
			if (op.equals(IBinaryOperator.SMALLER_EQ)) {
				line.add(new TextComponent("menor ou igual a ", TextType.NORMAL));
				return;
			}
			if (op.equals(IBinaryOperator.EQUAL)) {
				line.add(new TextComponent("igual a ", TextType.NORMAL));
				return;
			}
			if (op.equals(IBinaryOperator.DIFFERENT)) {
				line.add(new TextComponent("diferente d", TextType.NORMAL));
				return;
			}
		}	
		
		if(op instanceof ArithmeticOperator) {
			line.add(new TextComponent("a ", TextType.NORMAL));
			if (op.equals(IBinaryOperator.ADD)) {
				line.add(new TextComponent("somar com ", TextType.NORMAL));
				return;
			}
			if (op.equals(IBinaryOperator.DIV)) {
				line.add(new TextComponent("dividir por ", TextType.NORMAL));
				return;
			}
			if (op.equals(IBinaryOperator.SUB)) {
				line.add(new TextComponent("subtrair por ", TextType.NORMAL));
				return;
			}
			if (op.equals(IBinaryOperator.MUL)) {
				line.add(new TextComponent("multiplicar por ", TextType.NORMAL));
				return;
			}
		}	
		
		/*
		 * if (op.equals(IBinaryOperator.MOD)) { return "multiplicar por "; }
		 */
		return;
	}
	
	String translateMostWantedholderObjective(Objective o) {
		if (o.equals(Objective.GREATER)) {
			return "maior que ";
		}
		if (o.equals(Objective.SMALLER)) {
			return "menor que ";
		}
		return "";
	}
	
	

	String translateUnaryOperator(IUnaryOperator op) {
		if (op.equals(IUnaryOperator.NOT)) {
			// TODO
		}

		/*
		 * if (op.equals(IBinaryOperator.MOD)) { return "multiplicar por "; }
		 */
		return "";
	}
}

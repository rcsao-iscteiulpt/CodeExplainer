package pt.iscte.paddle.codexplainer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Text;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.model.IVariableDeclaration;

public class ExplanationTextSmoother {

	List<IVariableDeclaration> variables = new ArrayList<IVariableDeclaration>();

	public ExplanationTextSmoother(List<IVariableDeclaration> variables) {
		this.variables = variables;
	}

	void SmoothText(List<List<TextComponent>> explanationText) {
		for (List<TextComponent> line : explanationText) {

			if (line.size() != 0 && line.get(0).getText().length() != 0) {
				boolean firstWord = false;
				
				for (int i = 0; i != line.size(); i++) {
					TextComponent comp = line.get(i);

					if (!firstWord && comp.getText().trim().length() != 0 && !(comp.getText().charAt(0) == '•')) {
						// isVariable
						firstWord = true;
						boolean isVariable = false;
						String[] words = comp.getText().split(" ");
						for (IVariableDeclaration v : variables) {
							if (v.toString().equals(words[0]))
								isVariable = true;
						}

						if (!isVariable) {
							// change to UpperCase
							String newText = comp.getText().substring(0, 1).toUpperCase()
									+ comp.getText().substring(1);
							comp.changeText(newText);
						}

					}
					
					//remove Spaces before comma
					String pattern = " *,";
					String textWithoutSpaces = comp.getText().replaceAll(pattern, ",");
					comp.changeText(textWithoutSpaces);
					if(comp.getText().charAt(0) == ',') {
						pattern = " *$";
						textWithoutSpaces = line.get(i - 1).getText().replaceAll(pattern, "");
						line.get(i - 1).changeText(textWithoutSpaces);
					}
					
				}

				TextComponent lastComp = line.get(line.size() - 1);
				// Space before the end mark
				if (lastComp.getText().length() != 0) {
					String endOfLineCharacters = "[,.:;]";
					String lineLastCharacter = lastComp.getText().substring(lastComp.getText().length() - 1,
							lastComp.getText().length());
					if (!lineLastCharacter.matches(endOfLineCharacters)) {
						String pattern = "[\\s]*$";
						String newText = lastComp.getText().replaceFirst(pattern, "");
						lastComp.changeText(newText);
						
						line.add(new TextComponent("."));
					}
				} else {
					lastComp.changeText(".");
				}
			}

		}
	}
}

package pt.iscte.paddle.codexplainer;

import java.util.ArrayList;
import java.util.List;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import pt.iscte.paddle.codexplainer.components.TextComponent;
import pt.iscte.paddle.codexplainer.components.VariableRoleComponent;
import pt.iscte.paddle.model.IVariableDeclaration;

//What it does:
//First letter of a line UpperCase unless its a variable
//merge into prepositions
//remove unnacessary spaces
//add comma at the end of a line
public class ExplanationTextSmoother {

	List<IVariableDeclaration> variables = new ArrayList<IVariableDeclaration>();

	final Table<String, String, String> contractionTable = HashBasedTable.create();
	
	public ExplanationTextSmoother(List<IVariableDeclaration> variables) {
		this.variables = variables;
		
		contractionTable.put("de", "o", "do");
		contractionTable.put("de", "os", "dos");
		contractionTable.put("de", "a", "da");
		contractionTable.put("de", "as", "das");	
		contractionTable.put("de", "ele", "dele");
		contractionTable.put("de", "eles", "deles");
		contractionTable.put("de", "ela", "dela");
		contractionTable.put("de", "elas", "delas");
		contractionTable.put("de", "este", "deste");
		contractionTable.put("de", "estes", "destes");
		contractionTable.put("de", "esta", "desta");
		contractionTable.put("de", "estas", "destas");
		contractionTable.put("de", "esse", "desse");
		contractionTable.put("de", "esses", "desses");
		contractionTable.put("de", "essa", "dessa");
		contractionTable.put("de", "essas", "dessas");
		contractionTable.put("de", "aquele", "daquele");
		contractionTable.put("de", "aqueles", "daqueles");
		contractionTable.put("de", "aquela", "daquela");
		contractionTable.put("de", "aquelas", "daquelas");
		contractionTable.put("de", "isto", "disto");
		contractionTable.put("de", "aqui", "daqui");
		contractionTable.put("de", "aí", "daí");
		contractionTable.put("de", "ali", "dali");
	
		contractionTable.put("por", "o", "pelo");
		contractionTable.put("por", "os", "pelos");
		contractionTable.put("por", "a", "pela");
		contractionTable.put("por", "as", "pelas");
		
		contractionTable.put("em", "o", "no");
		contractionTable.put("em", "os", "nos");
		contractionTable.put("em", "a", "na");
		contractionTable.put("em", "um", "num");
		contractionTable.put("em", "uns", "nuns");
		contractionTable.put("em", "uma", "numa");
		contractionTable.put("em", "umas", "numas");
		contractionTable.put("em", "este", "neste");
		contractionTable.put("em", "estes", "nestes");
		contractionTable.put("em", "esta", "nesta");
		contractionTable.put("em", "estas", "nestas");
		contractionTable.put("em", "aquele", "naquele");
		contractionTable.put("em", "aqueles", "naqueles");
		contractionTable.put("em", "aquela", "naquela");
		contractionTable.put("em", "aquelas", "naquelas");
		contractionTable.put("em", "isto", "nisto");
		contractionTable.put("em", "isso", "nisso");
		contractionTable.put("em", "aquilo", "naquilo");
		
		contractionTable.put("um", "o", "um");
		contractionTable.put("um", "os", "uns");
		contractionTable.put("um", "a", "uma");
		contractionTable.put("um", "as", "umas");
		
		
		contractionTable.put("a", "o", "ao");
		contractionTable.put("a", "os", "aos");
		contractionTable.put("a", "a", "à");
		contractionTable.put("a", "as", "às");
		contractionTable.put("a", "aquele", "àquele");
		contractionTable.put("a", "aqueles", "àqueles");
		contractionTable.put("a", "aquela", "àquela");
		contractionTable.put("a", "aquelas", "àquelas");
		contractionTable.put("a", "aquilo", "àquilo");
		
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
							String newText = comp.getText().substring(0, 1).toUpperCase() + comp.getText().substring(1);
							comp.changeText(newText);
						}

					}
					
					
					
					String text = "de o de a sd por o por a asdasdsa por o add a o a a porsdfsdf";
				
					
					for (Cell<String, String, String> cell: contractionTable.cellSet()) {	
					    comp.changeText(comp.getText().replaceAll(" "+ cell.getRowKey() + " " + cell.getColumnKey() + " ", cell.getValue()));
					}
					
					//In case the 2 preposition words being separated by TextComponents
					if(i != line.size() - 1) {
						TextComponent nextComp = line.get(i+1);
						String[] compSplit = comp.getText().split(" ");
						String[] nextCompSplit = nextComp.getText().split(" ");
						
						String firstWordPreposition = "";
						String secondWordPreposition = "";
						if(compSplit.length != 0) {
							firstWordPreposition = compSplit[compSplit.length - 1];
						}
						
						if(nextCompSplit.length != 0)
							secondWordPreposition = nextCompSplit[0];
						
						if(!firstWordPreposition.equals(" ") && !secondWordPreposition.equals(" ") && contractionTable.get(firstWordPreposition, secondWordPreposition) != null) {
							String contraction = contractionTable.get(firstWordPreposition, secondWordPreposition);
							comp.changeText(comp.getText().replaceAll(firstWordPreposition+"( *)$", contraction + " "));
						
							nextComp.changeText(nextComp.getText().replaceFirst(secondWordPreposition + " ", ""));
						}
					}
					

					// remove Spaces before comma
					String pattern = " *,";
					String textWithoutSpaces = comp.getText().replaceAll(pattern, ",");
					comp.changeText(textWithoutSpaces);
					if (comp.getText().charAt(0) == ',') {
						pattern = " *$";
						textWithoutSpaces = line.get(i - 1).getText().replaceAll(pattern, "");
						line.get(i - 1).changeText(textWithoutSpaces);
					}
				}
				
				//add comma at the end of phrase
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

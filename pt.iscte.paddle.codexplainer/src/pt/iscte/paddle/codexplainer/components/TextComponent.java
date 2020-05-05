package pt.iscte.paddle.codexplainer.components;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IProgramElement;

public class TextComponent {

	
	private String text;
	private TextType type;
	private List<IProgramElement> elementList = new ArrayList<IProgramElement>();
	
	public enum TextType {
		LINK, NORMAL, NEWLINE;
	}
	
	public TextComponent(String text, TextType type, IProgramElement element) {
		this.text = text;
		this.type = type;
		this.elementList.add(element);
	}
	
	public TextComponent(String text, TextType type, List<IProgramElement> element) {
		this.text = text;
		this.type = type;
		this.elementList = element;
	}
	
	public TextComponent(String text, TextType type) {
		this.text = text;
		this.type = type;
	}
	
	public TextComponent(TextType type) {
		this.type = type;
		this.text = "";
	}

	public String getText() {
		return text;
	}

	public TextType getType() {
		return type;
	}
	
	public List<IProgramElement> getElement() {
		return elementList;
	}
	
	
}

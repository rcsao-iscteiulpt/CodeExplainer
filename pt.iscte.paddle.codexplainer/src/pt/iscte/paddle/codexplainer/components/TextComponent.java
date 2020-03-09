package pt.iscte.paddle.codexplainer.components;

import pt.iscte.paddle.model.IProgramElement;

public class TextComponent {

	
	private String text;
	private TextType type;
	private IProgramElement element;
	
	public enum TextType {
		LINK, NORMAL, NEWLINE;
	}
	
	public TextComponent(String text, TextType type, IProgramElement element) {
		this.text = text;
		this.type = type;
		this.element = element;
	}
	
	public TextComponent(String text, TextType type) {
		this.text = text;
		this.type = type;
	}
	
	public TextComponent(TextType type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public TextType getType() {
		return type;
	}
	
	public IProgramElement getElement() {
		return element;
	}
	
	
}

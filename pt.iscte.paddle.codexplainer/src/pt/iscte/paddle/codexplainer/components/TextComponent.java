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
	
	public TextComponent(String text, IProgramElement element) {
		this.text = text;
		this.type = TextType.LINK;
		this.elementList.add(element);
	}
	
	public TextComponent(String text, List<IProgramElement> element) {
		this.text = text;
		this.type = TextType.LINK;
		this.elementList = element;
	}
	
	public TextComponent(String text) {
		this.text = text;
		this.type = TextType.NORMAL;
	}
	
	public TextComponent() {
		this.type = TextType.NEWLINE;
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

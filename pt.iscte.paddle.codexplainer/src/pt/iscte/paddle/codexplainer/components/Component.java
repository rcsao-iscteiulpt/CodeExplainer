package pt.iscte.paddle.codexplainer.components;

import pt.iscte.paddle.model.IProgramElement;

/**
 * might be temporary if a better solution is found
 * @author Ricard0
 *
 */
public abstract class Component {

	IProgramElement element;
	
	public IProgramElement getElement() {
		return element;
	}
	
	
}

package pt.iscte.paddle.codexplainer.components;

import pt.iscte.paddle.model.IProgramElement;


public abstract class Component {

	IProgramElement element;
	MethodComponent mc;
	
	public MethodComponent getMethodComponent() {
		return mc;
	}

	public IProgramElement getElement() {
		return element;
	}
		
}

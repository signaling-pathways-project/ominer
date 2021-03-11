package edu.bcm.dldcc.big.nursa.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named
public class AjaxPanelController implements Serializable {

	private static final long serialVersionUID = 2025588498998012041L;

	private Map<String, Boolean> renderComponent = new HashMap<String, Boolean>();

	public Map<String, Boolean> getRenderComponent() {
		return renderComponent;
	}

	public void setRenderComponent(Map<String, Boolean> renderComponent) {
		this.renderComponent = renderComponent;
	}

	public Boolean renderComponent(String componentId) {
		Boolean render = renderComponent.get(componentId);
		if (render == null) {
			renderComponent.put(componentId, false);
			render = false;
		}
		return render;
	}

	public void alternateComponentState(String componentId) {
		Boolean render = renderComponent.get(componentId);
		if (render == null) {
			render = false;
		}
		render = !render;
		renderComponent.put(componentId, render);
	}

	public void setComponentRenderTrue(String componentId) {
		renderComponent.put(componentId, true);
	}

	public void setComponentRenderFalse(String componentId) {
		renderComponent.put(componentId, false);
	}

}

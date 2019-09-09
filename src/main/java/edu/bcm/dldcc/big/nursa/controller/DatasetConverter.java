package edu.bcm.dldcc.big.nursa.controller;

import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;

@RequestScoped
@FacesConverter(value = "datasetConverter")//TODO, forClass = NURSADataset.class)
public class DatasetConverter implements Converter {
	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		NURSADataset object = null;
		if ((value == null) || (value.equals(""))) {
			return null;
		}
		try {
			object = objectEntityManager.find(NURSADataset.class,
					Long.parseLong(value));
		} catch (NumberFormatException nfe) {
			return null;
		}
		return object;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if ((value == null) || (value.equals(""))) {
			return "";
		}
		return String.valueOf(((NURSADataset) value).getId());
	}

}

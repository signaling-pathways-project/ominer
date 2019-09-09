package edu.bcm.dldcc.big.nursa.converter;

import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.nursa.model.common.MoleculeSynonym;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;

@RequestScoped
@FacesConverter(value = "moleculeSynonymConverter")//TODO ??, forClass = MoleculeSynonym.class)
public class MoleculeSynonymConverter implements Converter {
	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		MoleculeSynonym object = null;
		if ((value == null) || (value.equals(""))) {
			return null;
		}
		object = objectEntityManager.find(MoleculeSynonym.class,
				Long.parseLong(value));
		return object;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if ((value == null) || (value.equals(""))) {
			return "";
		}
		return String.valueOf(((MoleculeSynonym) value).getId());
	}

}

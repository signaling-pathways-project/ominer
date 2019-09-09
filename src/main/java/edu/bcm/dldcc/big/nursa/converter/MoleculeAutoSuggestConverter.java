package edu.bcm.dldcc.big.nursa.converter;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.nursa.model.common.MoleculeAutoSuggest;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;

@RequestScoped
@FacesConverter(value = "moleculeAutoSuggestConverter")//TODO exclusive, only enable if has error, forClass = MoleculeAutoSuggest.class)
public class MoleculeAutoSuggestConverter implements Converter {
	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		MoleculeAutoSuggest object = null;
		if ((value == null) || (value.equals(""))) {
			return null;
		}
		try {
			object = objectEntityManager.find(MoleculeAutoSuggest.class,
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
		return String.valueOf(((MoleculeAutoSuggest) value).getId());
	}

}

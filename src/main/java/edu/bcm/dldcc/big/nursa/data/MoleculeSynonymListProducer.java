package edu.bcm.dldcc.big.nursa.data;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import edu.bcm.dldcc.big.nursa.model.common.MoleculeAutoSuggest;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;
import java.io.Serializable;

@Named
@ConversationScoped
public class MoleculeSynonymListProducer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2660865162573568880L;

	private MoleculeAutoSuggest selectedMolecule;

	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;

	public void redirect() {
		redirect(getSelectedMolecule());
	}

	public void redirect(MoleculeAutoSuggest mas) {
		try {
			// put the search term into flash scope to display on the target
			// page
			if(!mas.getName().equals(mas.getOfficial())) {
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put("searchTerm",
			mas.getName());
			}
			// doi.url should be absolute
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect("./index.jsf?doi=" + mas.getDoi());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<MoleculeAutoSuggest> completeSymbol(String query) {
//		String moleculeType = (String) UIComponent
//				.getCurrentComponent(FacesContext.getCurrentInstance())
//				.getAttributes().get("moleculeType");

		String sql = "select ID, NAME, OFFICIAL, DOI, RANK from (select ID, NAME, OFFICIAL, DOI, RANK, ROW_NUMBER() OVER (PARTITION BY OFFICIAL ORDER BY OFFICIAL) AS rn from molecule_as mas where UPPER(NAME) LIKE :query order by RANK asc) where rn <=1 order by RANK asc, NAME asc";
		Query returnQuery = objectEntityManager.createNativeQuery(sql, MoleculeAutoSuggest.class);
		returnQuery.setParameter("query", "%" + query.toUpperCase() + "%");
		returnQuery.setMaxResults(5);
		List<MoleculeAutoSuggest> rawResults = returnQuery.getResultList();
		return rawResults;

	}
	
	
	public MoleculeAutoSuggest getSelectedMolecule() {
		return selectedMolecule;
	}

	public void setSelectedMolecule(MoleculeAutoSuggest selectedMolecule) {
		this.selectedMolecule = selectedMolecule;
	}
}

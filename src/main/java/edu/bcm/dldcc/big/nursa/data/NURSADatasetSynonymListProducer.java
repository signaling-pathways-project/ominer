package edu.bcm.dldcc.big.nursa.data;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset_;
import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;
import java.io.Serializable;

@Named("datasetSynonymListProducer")
@ConversationScoped
public class NURSADatasetSynonymListProducer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private NURSADataset selectedDataset;
	
	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;

	
	public void redirect() {
		redirect(getSelectedDataset());
	}

	public void redirect(NURSADataset nds) {
		try {
			// put the search term into flash scope to display on the target
			// page
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put("searchTerm",
					nds.getName());
			// doi.url should be absolute
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect("./index.jsf?doi=" + nds.getDoi().getDoi());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<NURSADataset> complete(String query) {
		CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();

		CriteriaQuery<NURSADataset> cq = cb.createQuery(NURSADataset.class);
		Root<NURSADataset> nds = cq.from(NURSADataset.class);
		cq.where(cb.like(cb.upper(nds.get(NURSADataset_.name)), "%" + query.toUpperCase() + "%"));

		cq.select(nds);
		TypedQuery<NURSADataset> returnQuery = objectEntityManager.createQuery(cq);
		
		returnQuery.setMaxResults(10);
		return returnQuery.getResultList();
	}

	/**
	 * @return the selectedDataset
	 */
	public NURSADataset getSelectedDataset() {
		return selectedDataset;
	}

	/**
	 * @param selectedDataset the selectedDataset to set
	 */
	public void setSelectedDataset(NURSADataset selectedDataset) {
		this.selectedDataset = selectedDataset;
	}
}

package edu.bcm.dldcc.big.nursa.data;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.nursa.model.common.Species;
import edu.bcm.dldcc.big.nursa.model.common.Species_;
import edu.bcm.dldcc.big.nursa.model.core.GOTerm;
import edu.bcm.dldcc.big.nursa.model.core.GOTerm_;
import edu.bcm.dldcc.big.nursa.util.SiteVariables;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;

@Named
@RequestScoped
public class SpeciesListProducer {

	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;

	public List<Species> getSpecies() {
		CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();
		CriteriaQuery<Species> criteria = cb.createQuery(Species.class);
		Root<Species> speciesRoot = criteria.from(Species.class);
		criteria.select(speciesRoot);

		return objectEntityManager.createQuery(criteria).getResultList();
	}

	public Species getSpeciesFromId(long taxonomyID) {
		CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();
		CriteriaQuery<Species> criteria = cb.createQuery(Species.class);
		Root<Species> speciesRoot = criteria.from(Species.class);
		criteria.select(speciesRoot);
		
		criteria.where(cb.equal(speciesRoot.get(Species_.taxonomyId), new Long(
				taxonomyID)));

		return objectEntityManager.createQuery(criteria).getSingleResult();
	}
	
	/**
	 * Need to refactor this and the next to be generic
	 * @param ignoreFly
	 * @return
	 */
	public List<Species> getSpeciesFromSiteOrthologs() {
		CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();
		CriteriaQuery<Species> criteria = cb.createQuery(Species.class);
		Root<Species> speciesRoot = criteria.from(Species.class);
		criteria.select(speciesRoot);
		
		criteria.where(speciesRoot.get(Species_.taxonomyId).in(SiteVariables.ORTHOLOGS));
		criteria.orderBy(cb.asc(speciesRoot.get(Species_.displayOrder)));
		
		return objectEntityManager.createQuery(criteria).getResultList();
	}
	
	public List<Species> getSpeciesFromSiteOrthologsMinusFly() {
		CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();
		CriteriaQuery<Species> criteria = cb.createQuery(Species.class);
		Root<Species> speciesRoot = criteria.from(Species.class);
		criteria.select(speciesRoot);
		
		criteria.where(speciesRoot.get(Species_.taxonomyId).in(SiteVariables.SOME_ORTHOLOGS));
		criteria.orderBy(cb.asc(speciesRoot.get(Species_.displayOrder)));

		return objectEntityManager.createQuery(criteria).getResultList();
	}

}

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


import edu.bcm.dldcc.big.nursa.model.core.GOTerm;
import edu.bcm.dldcc.big.nursa.model.core.GOTerm_;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;

@Named("goTermListProducer")
@RequestScoped
public class GOTermListProducer {
	
	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;
	
	public List<GOTerm> completeGOTerm(String query) {
		CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();
		CriteriaQuery<GOTerm> cq = cb.createQuery(GOTerm.class);
		
		Root<GOTerm> goTermRoot = cq.from(GOTerm.class);
		
		
		Predicate restrictions = cb.like(cb.upper(goTermRoot.get(GOTerm_.goTermID)), "%" + query.toUpperCase() + "%");
		restrictions = cb.or(restrictions, cb.like(cb.upper(goTermRoot.get(GOTerm_.termName)), "%" + query.toUpperCase() + "%"));
		
		cq.where(restrictions);
		cq.distinct(true);
		cq.select(goTermRoot);
		
		cq.orderBy(cb.asc(goTermRoot.get(GOTerm_.termName)));
		
		return objectEntityManager.createQuery(cq).setMaxResults(5).getResultList();
	}
}

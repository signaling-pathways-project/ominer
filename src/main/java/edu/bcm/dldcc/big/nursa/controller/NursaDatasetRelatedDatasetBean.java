package edu.bcm.dldcc.big.nursa.controller;

import edu.bcm.dldcc.big.nursa.data.SQL;
import edu.bcm.dldcc.big.nursa.model.Molecule;
import edu.bcm.dldcc.big.nursa.model.Molecule_;
import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.DOI_;
import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset_;
import edu.bcm.dldcc.big.nursa.model.omics.Experiment;
import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.model.omics.RelatedDatasetBy;
import edu.bcm.dldcc.big.nursa.model.omics.dto.NursaDatasetDTO;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Related Dataset SLSB 
 * Datasets may be related by RNA Source or Regulatory Molecule
 * Biz logix: for a given dataset, find other datasets for which there is an associated  regualtory
 * molecules or Rna source
 * @author mcowiti
 *
 */
@Stateless
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class NursaDatasetRelatedDatasetBean implements Serializable {

	private static Logger log = Logger.getLogger(NursaDatasetRelatedDatasetBean.class.getName());

	
	private static final long serialVersionUID = -2340014787429773380L;
	
	@PersistenceContext(unitName = "NURSA")
	private EntityManager em;

	public static final int MAX_RELATED = 100;
	
	public static final RelatedDatasetBy[] MODES=RelatedDatasetBy.values();
	
	public List<NursaDatasetDTO> findRelatedDataset(Integer byType,Long datasetId,
			String doi,
			Integer number){
		
		if(number.intValue() >MAX_RELATED)
			number=MAX_RELATED;
		
		List<Long> moleIds=null;
		List<String> rnaSources=null;
		RelatedDatasetBy mode=MODES[byType];
		
		NURSADataset dataset=null;
		if(datasetId > 0L)
			dataset=getNURSADatasetById(datasetId);
		else
			dataset=getDatasetByDoi(doi);
		
		if(dataset == null){
			log.severe("No source dataset found. This should not be happening.");
			 return new ArrayList<NursaDatasetDTO>();
		}
		
		switch(mode){
			case RNA_SOURCE:
			rnaSources=new ArrayList<String>(extractRnaSources(dataset));
			break;
			
			case REGUALTORY_MOLECULE:
			default:
				moleIds=new ArrayList<Long>(extractMoleculeIds(dataset));
				break;
		}
		
		return findRelatedNursaDataset(mode,datasetId,moleIds,rnaSources,number);
	}
	
	/**
	 * Finds datasets related to this one by Rna or Regulatory Molecule
	 * @param mode
	 * @param datasetId
	 * @param moleIds
	 * @param rnaSources
	 * @param max
	 * @return
	 */
	public List<NursaDatasetDTO> findRelatedNursaDataset(RelatedDatasetBy mode,
														 Long datasetId,
														 List<Long> moleIds,
														 List<String> rnaSources,
														 int max) {

		Query q = null;
		switch (mode) {
			case RNA_SOURCE:
				if (rnaSources.size() == 0)
					return new ArrayList<NursaDatasetDTO>();
				q = (Query) (em.unwrap(Session.class)).createSQLQuery(SQL.RELATED_DATASET_BY_RNA_SQL);
				((SQLQuery) q).addScalar("id", StandardBasicTypes.LONG);
				((SQLQuery) q).addScalar("name", StandardBasicTypes.STRING);
				((SQLQuery) q).addScalar("doi", StandardBasicTypes.STRING);
				q.setParameterList("valCollection", rnaSources);
				q.setParameter("datasetID", datasetId);
				break;
			case REGUALTORY_MOLECULE:
			default:
				if (moleIds.size() == 0)
					return new ArrayList<NursaDatasetDTO>();
				q = (Query) (em.unwrap(Session.class)).createSQLQuery(SQL.RELATED_DSET_BY_MOL);
				((SQLQuery) q).addScalar("id", StandardBasicTypes.LONG);
				((SQLQuery) q).addScalar("name", StandardBasicTypes.STRING);
				((SQLQuery) q).addScalar("doi", StandardBasicTypes.STRING);
				q.setParameterList("valCollection", moleIds);
				q.setParameter("datasetID", datasetId);
				break;
		}

		q.setResultTransformer(Transformers.aliasToBean(NursaDatasetDTO.class));
		q.setCacheable(true);
		return (List<NursaDatasetDTO>) q.setFirstResult(0).setMaxResults(max).list();
	}
	
	
	
	public Set<String> extractRnaSources(NURSADataset dataset){
		Set<String> ids= new HashSet<String>();
		/*
		FIXME for(Experiment experiment:dataset.getExperiments()){
			ids.add(experiment.getTissueSource().getName());
		}
		*/
		return ids;
	}
	
	public Set<Long> extractMoleculeIds(NURSADataset dataset){
		Set<Long> ids= new HashSet<Long>();
		/*FIXME to be removed and replaced by family
		for(Experiment experiment:dataset.getExperiments()){
			if(experiment != null)
				ids.addAll(getMoleculeIdsFromExperiment(experiment));
		}*/
		return ids;
	}
	
	private Set<Long> getMoleculeIdsFromExperiment(Experiment experiment){
		Set<Long> ids= new HashSet<Long>();
		Molecule mole=null;
		for(String node:experiment.getIpags()){
			mole=getMoleculeBySymbol(node);
			if(mole != null)
			ids.add(mole.getId());
		}

		/* OLD if(experiment.getAnnotations().size() > 0)
			for(MoleculeTreatment treat:experiment.getAnnotations()){
				if(treat.getMolecule()!=null)
					ids.add(treat.getMolecule().getId());
			}
			*/
		return ids;
	}

	private Molecule getMoleculeBySymbol(String symbol){

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Molecule> criteria = cb.createQuery(Molecule.class);
		Root<Molecule> moleculeRoot = criteria.from(Molecule.class);

		criteria.select(moleculeRoot).where(cb.equal(moleculeRoot.get(Molecule_.officialSymbol), symbol));
		return em.createQuery(criteria).getSingleResult();
	}
	
	private NURSADataset getNURSADatasetById(Long id){
		return em.find(NURSADataset.class, id);
	}
	
	private NURSADataset getDatasetByDoi(String dsdoi){

		log.log(Level.WARNING,"@getDatasetByDoi ???");
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<NURSADataset> cq = cb.createQuery(NURSADataset.class);
		Root<NURSADataset> nds = cq.from(NURSADataset.class);
		Join<NURSADataset, DOI> doi = nds.join(NURSADataset_.doi);

		cq.where(cb.equal(doi.get(DOI_.doi), dsdoi));
		cq.select(nds);
		TypedQuery<NURSADataset> result = em.createQuery(cq);

		NURSADataset dataset = result.getSingleResult();
		
		return dataset;
	}
}

package edu.bcm.dldcc.big.nursa.services.rest.omics;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.core.Response;

import edu.bcm.dldcc.big.nursa.model.cistromic.*;
import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.DOI_;
import edu.bcm.dldcc.big.nursa.model.common.Reference;
import edu.bcm.dldcc.big.nursa.model.common.Reference_;
import edu.bcm.dldcc.big.nursa.model.omics.*;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.ApiQueryParameter;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.DatasetMetadata;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.ExperimentMetadata;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.NodeFamily;

/**
 * Cistromic/Transcriptomic Dataset List Service
 * @author mcowiti
 *
 */

@Local
@Stateless
public class DatasetServiceBean implements Serializable {

	 private static final long serialVersionUID = 8531390587801757294L;

	private static Logger log = Logger.getLogger(DatasetServiceBean.class.getName());

	@PersistenceContext(unitName = "NURSA")
    private EntityManager em;

    @Inject
    private OmicsServicebean omicsServicebean;

    public NURSADataset findById(Long id){
        return em.find(NURSADataset.class,id);
    }

    public Response findDatasetByBsmOrNode(List<ApiQueryParameter> parameters,int max){
        long b=System.currentTimeMillis();
        List<DatasetViewDTO> datasets= findDatasetViewDTOByTerm(parameters,max);
        if((System.currentTimeMillis()-b) > 5000)
            log.log(Level.INFO,"parameters"+ parameters.toString()+" # datasets="+datasets.size()+" query in (ms) "+(System.currentTimeMillis()-b));

        return getResponseFromDatasetViewDTO(datasets);
    }

    /**
     * Locate dataset by PMID. Assumes One2One relationship
     * @param pmid
     * @param ldate
     * @return
     */
    public Response findDatasetByPmid(String pmid, LocalDate ldate,int maxCount){
        Optional<Response> invalidDoiResponse=omicsServicebean.isGotInvalidPubmedResponse(pmid);
        if(invalidDoiResponse.isPresent())
            return invalidDoiResponse.get();

        return findNursaDatasetByType(DatasetQueryType.pmid,pmid,ldate,maxCount);
    }


    public Response findDatasetsAddedSinceDate(LocalDate ldate,int maxCount){

        return findNursaDatasetByType(DatasetQueryType.datesince,null,ldate,maxCount);
    }

    private Response findNursaDatasetByType(DatasetQueryType type,String id,LocalDate ldate,int maxCount){

        List<NURSADataset> list=this.getDatasetsByTerm(type,id,ldate,maxCount);
        DatasetMetadata data=null;
        List<DatasetMetadata> datasets= new ArrayList<DatasetMetadata>();
        for(NURSADataset dataset:list)
        {
            data= new DatasetMetadata(dataset.getId(), dataset.getType(),
                    dataset.getName(), dataset.getRepo(),
                    dataset.getDoi().getDoi(), dataset.getReleaseDate(),
                    dataset.getContributors(), dataset.getActive());

            data.getExperiments().addAll(getExperimentMetadata(null,dataset,true));
            datasets.add(data);
        }

        return Response.ok().entity(datasets).build();
    }

    /**
     * Returns DatasetMetadata DTO
     * @param doi
     * @param ldate yyyyMMdd format, ie ISO 8601 without time and zone
     * @return
     */
    @Deprecated
    public Response findDatasetByDoi(String doi, LocalDate ldate){

        Optional<Response> invalidDoiResponse=omicsServicebean.isGotInvalidDOIResponse(doi);
        if(invalidDoiResponse.isPresent())
            return invalidDoiResponse.get();

        return findNursaDatasetByType(DatasetQueryType.doi,doi,ldate,1);
    }

    private boolean hasTheFieldAttribute(String[] fields,String field){
        for(String afield:fields)
            if(afield.trim().equalsIgnoreCase(field))
                return true;
            return false;
    }
    public Response getDatasetViewMini(DatasetQueryType type,String query, int maxCount,String fieldAttrs){

        List<DatasetViewMiniDTO> list= null;
        String[] fields=null;
        boolean isPubmed=false;
        if(fieldAttrs!=null){
            fields=fieldAttrs.split(",");
            isPubmed=hasTheFieldAttribute(fields,"pubmed");
        }
        switch(type) {
            case datesince:
                list = getDatasetViewMiniDTOByReleaseDate(query, maxCount, isPubmed);
                break;
            case doi:
                list=getDatasetViewMiniDTOByDOI(query,maxCount,isPubmed);
                break;
        }

        DatasetMetadata data;
        List<DatasetMetadata> datasets= new ArrayList<DatasetMetadata>();

        Map<Long, List<DatasetViewMiniDTO>> listByDataset
                = list.stream().collect(Collectors.groupingBy(DatasetViewMiniDTO::getDid));

        DatasetViewMiniDTO adata;
        for(Map.Entry<Long, List<DatasetViewMiniDTO>> entry:listByDataset.entrySet()){
            adata=entry.getValue().get(0);
            if(!isPubmed)
                data= new DatasetMetadata(adata.getDid(), adata.getType(),
                    adata.getDoi(), adata.getName(), adata.getDescription(),
                    adata.getReleaseDate(),null);
            else
                data= new DatasetMetadata(adata.getDid(), adata.getType(),
                        adata.getDoi(), adata.getName(), adata.getDescription(),
                        adata.getReleaseDate(),adata.getPubmed());

            data.getExperiments().addAll(extractExperimentMetadata(entry.getValue()));
            datasets.add(data);
        }
        return Response.ok().entity(datasets).build();
    }

    private  List<ExperimentMetadata> extractExperimentMetadata(List<DatasetViewMiniDTO> list){
        List<ExperimentMetadata> experiments= new ArrayList<ExperimentMetadata>();
        ExperimentMetadata emeta;
        for(DatasetViewMiniDTO meta:list){
            experiments.add(new ExperimentMetadata(meta.getExpId(),meta.getExpName(),meta.getExpDesc(),meta.getSpecies()));
        }
        return experiments;
    }

    public List<DatasetViewMiniDTO> getDatasetViewMiniDTOByReleaseDate(String ldate, int maxCount, final boolean isPubmed){

        Query q=null;
        if(!isPubmed) {
            q=em.createNamedQuery("DatasetViewMiniDTOByLdate");
        }else {
            q=em.createNamedQuery("DatasetViewMiniDTOByLdateInclPubmed");
        }

        return q.setParameter("ldate",ldate)
                .setMaxResults(maxCount)
                .getResultList();
    }

    public List<DatasetViewMiniDTO> getDatasetViewMiniDTOByDOI(String doi, int maxCount, final boolean isPubmed){

        Query q=null;
        if(!isPubmed) {
            q = em.createNamedQuery("DatasetViewMiniDTOByDOI");
        }else {
            q = em.createNamedQuery("DatasetViewMiniDTOByDOIInclPubmed");
        }
        return q.setParameter("doi",doi)
                .setMaxResults(maxCount)
                .getResultList();
    }


    private Response getResponseFromDatasetViewDTO(List<DatasetViewDTO> datasetList){

        Map<Long, List<DatasetViewDTO>> listByDatasets
                = datasetList.stream().collect(Collectors.groupingBy(DatasetViewDTO::getDid));

        log.log(Level.INFO,"# UniQ datasets="+listByDatasets.size());
        DatasetViewDTO dataview;
        DatasetMetadata data=null;
        List<DatasetMetadata> datasets= new ArrayList<DatasetMetadata>();
        for(Map.Entry<Long, List<DatasetViewDTO>> entry:listByDatasets.entrySet()) {
            dataview = entry.getValue().get(0);
            data= new DatasetMetadata(dataview.getDid(), dataview.getDtype(),
                    dataview.getDname(), dataview.getRepo(),
                    dataview.getDoi(), dataview.getReleaseDate(),
                    dataview.getContributors(), dataview.isDactive());
            data.getExperiments().addAll(getExperimentMetadata(entry.getValue(),null,false));
            datasets.add(data);
        }
        return Response.ok().entity(datasets).build();
    }

    private  List<ExperimentMetadata> getExperimentMetadata(List<DatasetViewDTO> datasets, NURSADataset dataset, boolean isDataset){

        List<ExperimentMetadata> experiments= new ArrayList<ExperimentMetadata>();
        if(isDataset){
            ExperimentMetadata experiment=null;
            for (Experiment data : dataset.getExperiments()) {
                experiment=new ExperimentMetadata(
                        data.getId(), data.getName(), data.getDescription(),
                        data.getSpecies().getCommonName(), data.getInternalExperimentId(),
                        data.getTissueSource().getName(),
                        data.getTissueSource().getTissueCategory().getParent().getId(), data.getTissueSource().getTissueCategory().getParent().getName(),
                        data.getTissueSource().getTissueCategory().getId(),data.getTissueSource().getTissueCategory().getName());

                experiment.setNodeFamily(new HashSet<>());
                experiment.getNodeFamily().addAll(getNodeFamilies(data.getId(),null,null));
                experiments.add(experiment);
            }
        } else{
            ExperimentMetadata experiment=null;
            DatasetViewDTO data=null;
            Map<Long, List<DatasetViewDTO>> listByExperiments
                    = datasets.stream().collect(Collectors.groupingBy(DatasetViewDTO::getExpid));

            for(Map.Entry<Long, List<DatasetViewDTO>> entry:listByExperiments.entrySet()) {
                data=entry.getValue().get(0);
                experiment=new ExperimentMetadata(
                        data.getExpid(), data.getEname(), data.getEdesc(),
                        data.getSpecies(), data.getInternalexperimentid(),
                        data.getTissue(), data.getPsid(), data.getPs(),
                        data.getOrganid(), data.getOrgan());

                experiment.setNodeFamily(new HashSet<>());
                experiment.getNodeFamily().addAll(getNodeFamilies(null,null,entry.getValue()));
            }
            experiments.add(experiment);
        }
        return experiments;
    }

    private List<NodeFamily> getNodeFamilies(Long expid, Set<SignalingPathway> pathList, List<DatasetViewDTO> explist){
        List<NodeFamily> families=new ArrayList<NodeFamily>();

        if(pathList != null) {
            for(SignalingPathway path:pathList){
                families.add(new NodeFamily(path.getNode().type,path.getNode().typeid,
                        path.getNode().category,path.getNode().categoryid,
                        path.getNode().clazz,path.getNode().classid,
                        path.getNode().family,path.getNode().familyid));
            }
        } else if(expid != null) {
            List<ExperimentNodesHierarchyView> nodes=this.getExperimentNodesView(expid);
            for (ExperimentNodesHierarchyView path : nodes) {
                families.add(new NodeFamily(path.getType(),path.getTypeId(),
                        path.getCategory(),path.getCategoryId(),
                        path.getClazz(),path.getClassId(),
                        path.getFamily(),path.getFamilyId()));
            }
        }else{
            for (DatasetViewDTO nodeView : explist) {
                families.add(new NodeFamily(nodeView.getType(), nodeView.getTypeid(),
                        nodeView.getCategory(), nodeView.getCatid(),
                        nodeView.getCclass(), nodeView.getClassid(),
                        nodeView.getFamily(), nodeView.getFamilyid()) );
            }
        }
        return families;
    }
    public NURSADataset findInitialDataset(EntityManager objectEntityManager,String adoi){

        CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();

        CriteriaQuery<NURSADataset> cq = cb.createQuery(NURSADataset.class);
        Root<NURSADataset> nds = cq.from(NURSADataset.class);
        Join<NURSADataset, DOI> doi = nds.join(NURSADataset_.doi);

        nds.fetch("experiments", JoinType.INNER);

        cq.where(cb.equal(doi.get(DOI_.doi), adoi));
        cq.select(nds);

        TypedQuery<NURSADataset> result = objectEntityManager.createQuery(cq);

        return result.getSingleResult();
    }

    private List<ExperimentNodesHierarchyView> getExperimentNodesView(Long expid){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExperimentNodesHierarchyView> cq = cb.createQuery(ExperimentNodesHierarchyView.class);
        Root<ExperimentNodesHierarchyView> nodesView = cq.from(ExperimentNodesHierarchyView.class);
        cq.where(cb.equal(nodesView.get(ExperimentNodesHierarchyView_.expid), expid));
        cq.select(nodesView);
        return em.createQuery(cq).getResultList();
    }

    /**
     * Avoid use of this method that fetches Experiments
     * @param type
     * @param id
     * @param dateSince
     * @param maxCount
     * @return
     */
    private List<NURSADataset> getDatasetsByTerm(DatasetQueryType type,String id,LocalDate dateSince,int maxCount){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NURSADataset> cq = cb.createQuery(NURSADataset.class);
        Root<NURSADataset> nds = cq.from(NURSADataset.class);

        switch(type) {
            case doi:
                Join<NURSADataset, DOI> doi = nds.join(NURSADataset_.doi);
                cq.where(cb.equal(doi.get(DOI_.doi), id));
            break;
            case pmid:
                Join<NURSADataset, Reference> ref = nds.join(NURSADataset_.reference);
                cq.where(cb.equal(ref.get(Reference_.pubmedId), id));
                break;
            case datesince:
                cq.where(cb.greaterThan(nds.get(NURSADataset_.releaseDate), Date.valueOf(dateSince)));
                break;
        }

        //FIXME this is gonna be slow
        nds.fetch("experiments", JoinType.INNER);
        cq.select(nds);

        TypedQuery<NURSADataset> result = em.createQuery(cq);

        return result.setMaxResults(maxCount).getResultList();
    }

    private List<DatasetViewDTO> findDatasetViewDTOByTerm(List<ApiQueryParameter> parameters,int max){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DatasetViewDTO> criteria = cb.createQuery(DatasetViewDTO.class);
        Root<DatasetViewDTO> dsRoot = criteria.from(DatasetViewDTO.class);

        Root<ExperimentNodesView> nodesRoot =null;
        List<Predicate> predicates = new ArrayList<Predicate>();
        if(hasBsmNodesViewJoinNeed(parameters)) {
            nodesRoot = criteria.from(ExperimentNodesView.class);
            predicates.add(cb.equal(dsRoot.get(DatasetViewDTO_.did), nodesRoot.get(ExperimentNodesView_.did)));
        }

        for(ApiQueryParameter parameter:parameters) {
            switch (parameter.type) {
                case bsm:
                    predicates.add(cb.equal(nodesRoot.get(ExperimentNodesView_.bsm), parameter.value));
                    break;
                case node://do or on oags
                    Predicate dis = cb.disjunction();
                    Predicate p1 = cb.equal(nodesRoot.get(ExperimentNodesView_.ipags), parameter.value);
                    Predicate p2 = cb.equal(nodesRoot.get(ExperimentNodesView_.oags), parameter.value);
                    dis = cb.or(p1, p2);
                    predicates.add(dis);
                    break;
                case bsmidcas:
                    predicates.add(cb.equal(nodesRoot.get(ExperimentNodesView_.cas), parameter.value));
                    break;
                case bsmidchebi:
                    predicates.add(cb.equal(nodesRoot.get(ExperimentNodesView_.chebi), parameter.value));
                    break;
                case bsmidiuphar:
                    predicates.add(cb.equal(nodesRoot.get(ExperimentNodesView_.iuphar), parameter.value));
                    break;
                case bsmidpubchem:
                    predicates.add(cb.equal(nodesRoot.get(ExperimentNodesView_.pubchemId), parameter.value));
                    break;
                case biosample:
                    if(parameter.biosampleNameValues != null){
                        if(parameter.biosampleNameValues[0]!=null && !parameter.biosampleNameValues[0].trim().equals(""))
                            predicates.add(cb.equal(dsRoot.get(DatasetViewDTO_.ps), parameter.biosampleNameValues[0].trim()));
                        if(parameter.biosampleNameValues[1]!=null &&  !parameter.biosampleNameValues[1].trim().equals(""))
                            predicates.add(cb.equal(dsRoot.get(DatasetViewDTO_.organ), parameter.biosampleNameValues[1].trim()));
                    }
                    break;
                case biosampleid:
                    if(parameter.biosampleIdValues != null){
                        if(parameter.biosampleIdValues[0]!=null && parameter.biosampleIdValues[0]>0L)
                            predicates.add(cb.equal(dsRoot.get(DatasetViewDTO_.psid), parameter.biosampleIdValues[0]));
                        if(parameter.biosampleIdValues[1]!=null && parameter.biosampleIdValues[1]>0L)
                            predicates.add(cb.equal(dsRoot.get(DatasetViewDTO_.organid), parameter.biosampleIdValues[1]));
                    }
                    break;
                case datesince:
                    predicates.add((cb.greaterThan(dsRoot.get(DatasetViewDTO_.releaseDate), Date.valueOf(parameter.date))));
                    break;
            }
        }
        criteria.select(dsRoot).where(predicates.toArray(new Predicate[]{}));
        return em.createQuery( criteria ).setMaxResults(max). getResultList();
    }

    private boolean hasBsmNodesViewJoinNeed(List<ApiQueryParameter> parameters){
        for(ApiQueryParameter param:parameters) {
            if (param.type == DatasetQueryType.bsm ||
                    param.type == DatasetQueryType.node ||
                    param.type == DatasetQueryType.bsmidpubchem ||
                    param.type == DatasetQueryType.bsmidchebi ||
                    param.type == DatasetQueryType.bsmidiuphar ||
                    param.type == DatasetQueryType.bsmidcas)
                return true;
        }
        return false;
    }


    public List<DatasetViewDTO> getDatasets(String noacm,String type){

        CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DatasetViewDTO> criteria = cb.createQuery(DatasetViewDTO.class);
			Root<DatasetViewDTO> pathRoot = criteria.from(DatasetViewDTO.class);

        log.log(Level.INFO, "getDatasets noacm="+noacm);

			List<Predicate> predicates = new ArrayList<Predicate>();
			if(noacm.equals("y"))
				predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.typeid), -1000L));
			
			if(type != null)
				predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.dtype), type));
			
			//exclude Metabolics
			predicates.add(cb.notEqual(pathRoot.get(DatasetViewDTO_.dtype), "Metabolomic"));

			criteria.select(pathRoot)
	        .where(predicates.toArray(new Predicate[]{}));

			List<DatasetViewDTO> list= em.createQuery(criteria).getResultList();

			Map<Long, List<DatasetViewDTO>> viewByDset
			 = list.stream().collect(Collectors.groupingBy(DatasetViewDTO::getDid));
			
			 log.log(Level.INFO, "# uniq datasets ="+viewByDset.size());
			 
			 List<DatasetViewDTO> datalist= new ArrayList<DatasetViewDTO>();
			 DatasetViewDTO data;
			 List<DatasetViewDTO> ds;
			 Set<Long> pathways=null;
			 Set<Long> bioSamples=null;
			 Set<Long> species=null;
			 
			 for(Map.Entry<Long, List<DatasetViewDTO>> entry:viewByDset.entrySet()){
				 ds=entry.getValue();
				 data= ds.get(0);
				 data.setExpSize(Integer.toString(ds.size()));
				 pathways=new HashSet<Long>();
				 bioSamples=new HashSet<Long>();
				 species=new HashSet<Long>();
				 for(DatasetViewDTO dat:ds){
					 pathways.add(dat.getTypeid());
					 pathways.add(dat.getCatid());
					 pathways.add(dat.getClassid());
					 pathways.add(dat.getFamilyid());
					 
					 bioSamples.add(data.getPsid());
					 bioSamples.add(data.getOrganid());
					 
					 species.add(data.getSpeciesid());
				 }
				 data.setPathIds(pathways);
				 data.setBioIds(bioSamples);
				 data.setSpecIds(species);
				 datalist.add(data);
			 }
			 
			 return datalist;
	}
}
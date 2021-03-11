package edu.bcm.dldcc.big.nursa.services;

import edu.bcm.dldcc.big.nursa.data.SQL;
import edu.bcm.dldcc.big.nursa.model.common.MoleculeAutoSuggest;
import edu.bcm.dldcc.big.nursa.model.common.TranslationalAutoSuggest;
import edu.bcm.dldcc.big.nursa.model.core.GOTerm;
import edu.bcm.dldcc.big.nursa.model.core.Gene;
import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset_;
import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.model.omics.PathwayCategory;
import edu.bcm.dldcc.big.nursa.model.omics.QueryForm;
import edu.bcm.dldcc.big.nursa.model.omics.QueryParamName;
import edu.bcm.dldcc.big.nursa.model.omics.QueryParameter;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway_;
import edu.bcm.dldcc.big.nursa.model.omics.Tissue;
import edu.bcm.dldcc.big.nursa.model.omics.TissueCategory;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetApiQueryDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetMinimalDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.NursaDatasetDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.Pathway;
import edu.bcm.dldcc.big.nursa.model.omics.dto.PsOrgan;
import edu.bcm.dldcc.big.nursa.model.omics.dto.AutosuggestList;
import edu.bcm.dldcc.big.nursa.model.omics.dto.AutosuggestTerm;
import edu.bcm.dldcc.big.nursa.model.omics.dto.QueryParametersData;
import edu.bcm.dldcc.big.nursa.model.omics.dto.SummaryData;
import edu.bcm.dldcc.big.nursa.model.omics.dto.TmQueryResponse;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.*;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util.ApiApplicationErrorMessage;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util.LigandScheme;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util.LigandSearchSQL;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util.NoSuchIdentifierException;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.FileDownloadMemoryCache;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.PublicApiPathwayPsOrganIdCache;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.current.APIService;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.ApiDirection;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.ApiGeneSearchMode;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.ApiMoleculeTreatmentTime;
import edu.bcm.dldcc.big.nursa.services.utils.AutosuggestHelperBean;
import edu.bcm.dldcc.big.nursa.services.utils.QueryHelper;
import edu.bcm.dldcc.big.nursa.util.filter.BaseFilter;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.Response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Stateless
public class TranscriptomineServiceImpl implements TranscriptomineService, Serializable {

    private static Logger log = Logger.getLogger(TranscriptomineServiceImpl.class.getName());

	private static final long serialVersionUID = 52610131906573909L;
	
	@PersistenceContext(unitName = "NURSA")
    private EntityManager entityManager;
	
	@Inject
	private QueryHelper queryHelper;
	
	@Inject
	private AutosuggestHelperBean autosuggestHelperBean;

	@Inject private FileDownloadMemoryCache fileDownloadMemoryCache;
	
	public QueryParametersData buildQueryParametersData(String geneSearchType, String gene, String geneList, String molecule,
			String doi,
			String moleculetreatmentTime, String foldChange, String foldChangeMax, String foldChangeMin,
			String significance, String direction, 
			String tissueCategory,String ps,String organ,String tissue, 
			String disease, String goTerm,
			String species, String signalingPathway,String pathwayCategory,
			String findCount) {
		
		QueryParametersData q= new QueryParametersData();
		if(findCount!=null && findCount.length()>0)
			q.setFindCount(findCount);
			
		if(geneSearchType!=null && geneSearchType.length()>0)
			q.setGeneSearchType(geneSearchType);
		if(gene!=null && gene.length()>0)
			q.setGene( gene);
		if(geneList!=null && geneList.length()>0)
			q.setGeneList(geneList);
		if(molecule!=null && molecule.length()>0)
			q.setBsm(molecule);
		if(doi!=null && doi.length()>0)
		    q.setDoi(doi);
		if(moleculetreatmentTime!=null && moleculetreatmentTime.length()>0)
			q.setMoleculetreatmentTime(moleculetreatmentTime);
		if(foldChange!=null && foldChange.length()>0)
			q.setFoldChange(foldChange);
		if(foldChangeMax!=null && foldChangeMax.length()>0)
			q.setFoldChangeMax(foldChangeMax);
		if(foldChangeMin!=null && foldChangeMin.length()>0)
			q.setFoldChangeMin(foldChangeMin);
		if(significance!=null && significance.length()>0)
			q.setSignificance(significance);
		if(direction!=null && direction.length()>0)
			q.setDirection(direction);
		
		if(tissueCategory !=null && tissueCategory.length()>0)
			q.setTissueCategory(tissueCategory);
		if(ps!=null && ps.length()>0)
			q.setPs(ps);
		if(organ!=null && organ.length()>0)
			q.setOrgan(organ);
		if(tissue!=null && tissue.length()>0)
			q.setTissue(tissue);
		if(disease!=null && disease.length()>0)
			q.setDisease (disease);
		if(goTerm!=null && goTerm.length()>0)
			q.setGoTerm (goTerm);
		if(species!=null && species.length()>0)
			q.setSpecies(species);
		if(signalingPathway!=null && signalingPathway.length()>0)
			q.setSignalingPathway(signalingPathway);
		if(pathwayCategory!=null && pathwayCategory.length()>0)
			q.setPathwayCategory(pathwayCategory);
		return q;
	}


	
	private boolean isFoldChangeRange(Double foldChangeMin,
    		Double foldChangeMax){
		
		if(foldChangeMin == null || foldChangeMax == null)
			return false;
		if(foldChangeMin == 0D || foldChangeMax == 0D)
			return false;
		
		return true;
	}
	
	public <T> T findBasicDatapointsByExpId(int experimentId, 
    		Double foldChange,
    		Double foldChangeMin,
    		Double foldChangeMax,
    		String expressionDirection,
    		String absoluteSort) {
    	
    	log.log(Level.FINE,"@findBasicDatapointsByExpId expId->fc->FcMin->FcMax->direction "+experimentId+"->"+foldChange+"->"+foldChangeMin+"->"+foldChangeMax+"->"+expressionDirection);
     	
    	 boolean isRange=isFoldChangeRange(foldChangeMin,foldChangeMax);
    	
         if(foldChange == null && !isRange)
        	 return (T) Response.status(400).entity("Must supply either a foldChange or a foldChange Range").build();
         
         if(expressionDirection != null){
        	 if(Direction.valueOf(expressionDirection) == null)
        		 return (T) Response.status(400).entity("Directions allowed are (any,up or down").build();
         }
         
         List list=retrievedDataPointsByExpId(experimentId,foldChange,foldChangeMin,foldChangeMax,expressionDirection,absoluteSort,isRange);
         
         String finalQueryFcMin=(foldChangeMin!=null)?foldChangeMin.toString():null;
         String finalQueryFcMax=(foldChangeMax!=null)?foldChangeMax.toString():null;
         if(list.size() == 0 && !isRange){
        	 log.log(Level.FINE,"No results, re-querying via range ...");
        	 for(ImplicitFcRange fc:ImplicitFcRange.values()){ //order implicit
        		 finalQueryFcMin= String.valueOf(fc.getMinFc());
        		 finalQueryFcMax=String.valueOf(TranscriptomineService.MAX_FC_BY_DEFOLT);
        		 list=retrievedDataPointsByExpId(experimentId,null,
        				 fc.getMinFc(),TranscriptomineService.MAX_FC_BY_DEFOLT,
        				 expressionDirection,absoluteSort,
        				 true);
        		 if(list.size() > 0)
        			 break;
             }
         }
         
         //10.28.2016 FR needs min/max based abs values for the top 20 returns
         if(!isRange){
         	Collections.sort(list,DatapointBasicDTO.Comparators.ABS);
         } else{
         	if(absoluteSort == null || absoluteSort.length() == 0)
         		Collections.sort(list,DatapointBasicDTO.Comparators.NORMAL);
         	else
         		Collections.sort(list,DatapointBasicDTO.Comparators.ABS);
         }
         int num=list.size()-1;
         // for the max num 1000 in the future
//         int max = (!isRange)?SQL.MAX_ROWS_FOR_RANGE:SQL.MAX_ROWS_FOR_RANGE;

         if (num>1000)
        	 list = new ArrayList();
         
         QueryForm queryForm= new QueryForm();
         
         
         Double largestFoldChange=getMaxFoldChangeForExperimentAsSQL(new Long(experimentId));
         
         log.log(Level.FINE,"expId->fcLargest "+experimentId+"->"+largestFoldChange);
         if(list!=null && list.size() > 0){
        	 QueryParametersData queryParameter= new QueryParametersData();
        	 queryParameter.setFoldChangeMin(finalQueryFcMin);
        	 queryParameter.setFoldChangeMax(finalQueryFcMax);
        	 queryForm.setQueryParameter(queryParameter);
        	queryForm.setLargestFoldChange(largestFoldChange);
         	queryForm.setMinFoldChange(((Datapoint)list.get(0)).getFoldChange());
         	queryForm.setMinFoldChangeRaw(((Datapoint)list.get(0)).getFoldChangeRaw());
         
         	queryForm.setMaxFoldChange(((Datapoint)list.get(num)).getFoldChange());
         	queryForm.setMaxFoldChangeRaw(((Datapoint)list.get(num)).getFoldChangeRaw());
         }
         
         TmQueryResponse<DatapointBasicDTO> response= new TmQueryResponse<DatapointBasicDTO>(queryForm,new Long(num+1), list);
         
         return (T) response;
	}
	
	
	
private Double getMaxFoldChangeForExperimentAsSQL(Long id){
		
	String sql="select min(distinct d.foldchange), max(distinct d.foldchange)"
			+ " from EXPMICROARRAYEXPRESSION d, tmexperiment e where "+
			" e.id=d.experiment_id and e.id=:experiment_id";
	org.hibernate.Query query = (org.hibernate.Query) (entityManager.unwrap(Session.class))
    		.createSQLQuery(sql);
    	query.setParameter("experiment_id", id);
    	List<Object[]> list=query.list();
    	
    	return queryHelper.getLargestFC(list);
	}
	
	private Double getMaxFcViaJPQL(Long id){
		return (Double) entityManager.createQuery("select max(e.foldChange) from MicroarrayExpression e where e.experiment.id = :id")
		  .setParameter("id", id)
		  .getSingleResult();
	}
	
	private List retrievedDataPointsByExpId(int experimentId, 
    		Double foldChange,
    		Double foldChangeMin,
    		Double foldChangeMax,
    		String expressionDirection,
    		String absoluteSort,
    		boolean isRange){
		
		Direction direction=Direction.any; 
    	if(expressionDirection != null)
    		direction=Direction.valueOf(expressionDirection);
    	
    	String metaSQl=(!isRange)?SQL.getMetabolitesRangeSQL(isRange, direction):
    		SQL.getMetabolitesRangeSQL(isRange, direction);
    	
    	boolean isNoMax=(isRange && (TranscriptomineService.MAX_FC_BY_DEFOLT == foldChangeMax.doubleValue()));
    	
    	org.hibernate.Query query = (org.hibernate.Query) (entityManager.unwrap(Session.class))
        		.createSQLQuery(metaSQl);
        query.setParameter("experiment_id", experimentId);
        if(!isRange)
        	query.setParameter("foldChange", foldChange);
        else{
        	query.setParameter("foldChangeMin", foldChangeMin);
        	query.setParameter("foldChangeMax", foldChangeMax);
        }
        SQLQuery sqlQuery = (SQLQuery) query;
        sqlQuery.addScalar("id",StandardBasicTypes.STRING);
        sqlQuery.addScalar("name",StandardBasicTypes.STRING);

        long b=System.currentTimeMillis();
        /*Apollo: we need to redo this manual mapping ...
        Manually mapping result of all Metabolites
        Alex:The idea is to create a map on the same time
         */
        final Map<String, MetaboliteDTO> metaboliteMap = new HashMap<String, MetaboliteDTO>();
        query.setResultTransformer(new ResultTransformer() {
            public Object transformTuple(Object[] result, String[] aliases) {
                MetaboliteDTO metabolite = new MetaboliteDTO();
                String id = (String) result[0];
                metabolite.setId(id);
                metabolite.setName((String) result[1]);
                metaboliteMap.put(id,metabolite);
                return metabolite;
            }
            public List transformList(List list) {
                return list;
            }
        });

        List<MetaboliteDTO> result  = (List<MetaboliteDTO>)query.list();

        log.log(Level.WARNING,"@findBasicDatapointsByExpId 1st metabolites query done #->time: "+result.size()+" -> "+(System.currentTimeMillis()-b)+"(ms)");
        
        String datapointsSQL=(!isRange)?SQL.getDatapointsRangeSQL(isRange, direction):
        	SQL.getDatapointsRangeSQL(isRange, direction);
        
        //TODO Count first
        log.log(Level.FINE,"dpoints SQL= "+datapointsSQL);
        org.hibernate.Query q = (org.hibernate.Query) (entityManager.unwrap(Session.class))
        		.createSQLQuery(datapointsSQL); 
        q.setParameter("experiment_id", experimentId);
        if(!isRange)
        	q.setParameter("foldChange", foldChange);
        else{
        	q.setParameter("foldChangeMin", foldChangeMin);
        	q.setParameter("foldChangeMax", foldChangeMax);
        }
        sqlQuery = (SQLQuery) q;
        
        sqlQuery.setResultTransformer(new ResultTransformer() {
            public Object transformTuple(Object[] result, String[] aliases) {
                DatapointBasicDTO dpoint = new DatapointBasicDTO();
                dpoint.setId(result[0].toString());
                dpoint.setSymbol((String)result[1]);
                dpoint.setSymbolUrl((String) result[2]);
                if (null != result[3]) {
                    dpoint.setFoldChange(((BigDecimal) result[3]).doubleValue());
            	}
                dpoint.setSymbolSynonym((String) result[4]);
                if (null != result[5]) {
                	dpoint.setpValue(((BigDecimal) result[5]).doubleValue());
            	}
                dpoint.setGeneOfficialId( (result[6] ==null) ? null : result[6].toString());
                
                // apollo: if removed listagg from query, also diasble this 
                String metabolitesIds = (String)result[7];
                if (null != metabolitesIds&& !metabolitesIds.isEmpty())
                {
                    String[] ids = metabolitesIds.split(",");
                    List<MetaboliteDTO> associatedMetabolites = new ArrayList<MetaboliteDTO>();
                    for (String id : ids)
                    {
                        associatedMetabolites.add(metaboliteMap.get(id));
                    }
                    dpoint.setMetabolites(associatedMetabolites);
                }
                return dpoint;
            }

            public List transformList(List list) {
                return list;
            }
        });

        //!range MAX_ROWS_FOR_TOPTEN
//        int max=(!isRange)?SQL.MAX_ROWS_FOR_RANGE:SQL.MAX_ROWS_FOR_RANGE;
//        List list = q.setMaxResults(max).list();
        List list = q.list();
        if((System.currentTimeMillis()-b)>5000)
        	log.log(Level.SEVERE,"@findBasicDatapointsByExpId 2nd datapoints query(TODO join with 1st) done # -> time: "+list.size()+" -> "+(System.currentTimeMillis()-b)+"(ms)");
        return list;	
	}
	
    

    @Override
	public SummaryData listSummary() {
        Long numberOfDatapoints = queryHelper.bigDecimalToLong(entityManager.createNativeQuery(SQL.COUNT_DPOINTS).getSingleResult());
        Long numberOfExperiments = (Long) entityManager.createQuery(SQL.COUNT_ACTIVE_EXPERIMENTS).getResultList().get(0);
        Long numberOfNuclearReceptors = queryHelper.bigDecimalToLong( entityManager.createNativeQuery(SQL.COUNT_NR).getResultList().get(0));
        Long numberOfLigands = queryHelper.bigDecimalToLong( entityManager.createNativeQuery(SQL.COUNT_LIGANDS).getResultList().get(0));
        Long numberOfCoregulators = queryHelper.bigDecimalToLong( entityManager.createNativeQuery(SQL.COUNT_COREGULATORS).getResultList().get(0));
        Long numberOfTissueAndCells = queryHelper.bigDecimalToLong(entityManager.createNativeQuery(SQL.COUNT_TISSUES).getResultList().get(0));
        Long numberOfSpecies = (Long) entityManager.createQuery(SQL.COUNT_SPECIES).getResultList().get(0);
       	return new SummaryData(numberOfDatapoints, numberOfExperiments, numberOfNuclearReceptors, numberOfCoregulators, numberOfLigands, numberOfTissueAndCells, numberOfSpecies );
	}


	
	@Override
	public List findDataPoints(QueryForm queryForm, Class type, Integer maxNumber,boolean isPersist) {

        long b=System.currentTimeMillis();
        List results = queryHelper.executeQuery(queryForm, maxNumber, type);
        if(isPersist)
        	persistQuery(queryForm);
        log.log(Level.INFO,"@findDataPoints #->(ms): "+results.size()+"->"+(System.currentTimeMillis()-b));
        return results;
	}
	
	public  void persistQuery(QueryForm queryForm){
		String id=fileDownloadMemoryCache.cache(queryForm);
		queryForm.setId(id);
	}
	
	public Double findLargestFoldChange(QueryForm queryForm,Class type){
		return queryHelper.findLargestFoldChange(queryForm, type);
	}
	

    @Override
	public Long findDatapointsCount(QueryForm queryForm) {
		return queryHelper.countDatapoints(queryForm, DatapointDTO.class);
	}

	@Override
    public List<DatapointDTO> findDatapointsByQueryFormId(String queryId) {
		return findDataPoints(findQueryForm(queryId), DatapointDTO.class, MAX_NUMBER_DATAPOINTS,false);
    }
	
	@Override
	public TmQueryResponse<DatapointDTO> findDatapointSearchResults(QueryForm queryForm)
    {
    	
    	List<DatapointDTO> list = findDataPoints(queryForm, DatapointDTO.class, MAX_NUMBER_DATAPOINTS,true);
		return new TmQueryResponse<DatapointDTO>(queryForm, queryForm.getResultCount(), list);
	}

    @Override
    public QueryForm findQueryForm(String queryId)
    {
        Query q = entityManager.createQuery(SQL.SELECT_QF_BY_ID);
        q.setParameter("id", queryId);
        List<QueryForm> queryFormList = q.getResultList();
        if (queryFormList.size() == 0||queryFormList.size() >1)
            return null;
        else
            return queryFormList.get(0);
    }

	@Override
	public <T> Collection<T> listDropDowns(Class<T> type) {
        javax.persistence.criteria.CriteriaQuery<T> criteriaQuery  = entityManager.getCriteriaBuilder().createQuery(type);
        Root<T> list = criteriaQuery.from(type);
        criteriaQuery.select(list);
        TypedQuery<T> q = entityManager.createQuery(criteriaQuery);
        List<T> theList = q.getResultList();
        return theList;
	}

	@Override
	public <T> AutosuggestList autoSuggestList(Class<T> type, String value, Integer max) {

		//if issue, check addition of this. to the HelperBean
        if (type.equals(GOTerm.class))
           return this.autosuggestHelperBean.autoSuggestGOTerm(value, max);
        else if (type.equals(MoleculeAutoSuggest.class))
            return this.autosuggestHelperBean.autoSuggestMolecule(value,max);
        else if (type.equals(Gene.class))
            return this.autosuggestHelperBean.autoSuggestGene(value, max);
        else if (type.equals(TranslationalAutoSuggest.class))
            return autosuggestHelperBean.autoSuggestDisease(value, max);
        else if (type.equals(Tissue.class))
            return autosuggestHelperBean.autoSuggesTissue(value, max);

        return null;
	}
	
	@Override
	public <T> Collection<T> autoSuggest(Class<T> type, String inputValue, Integer max) {
		return null;
	}

	@Override
	public Collection<Tissue> listTissuesAndCellLines() {
		return entityManager.createQuery(SQL.SELECT_ALL_TISSUES).getResultList();
	}

    @Override
    public Integer validateListOfGenes(List<String> list)
    {
        if (null != list && list.size() <= MAX_NUMBER_GENELIST)
        {
            Query nameQuery = entityManager.createNativeQuery(SQL.COUNT_GENES_BY_NAME);
            nameQuery.setParameter("list",list);
            Integer count = ((BigDecimal) nameQuery.getResultList().get(0)).intValueExact();
            if(count >0)
            {
                return list.size()-count;
            }
            else
            {
                Query geneIdQuery = entityManager.createNativeQuery(SQL.COUNT_GENES_BY_ENTREZGENEID);
                geneIdQuery.setParameter("list",list);
                Integer countGeneId = ((BigDecimal) geneIdQuery.getResultList().get(0)).intValueExact();
                return list.size()-countGeneId;
            }
        }
        else
            return -1;
    }

    


	@Override
	public <T> List<T> getAllTissuesAndCellLines()
    {
		return autosuggestHelperBean.getAllTissues(entityManager);
	}

    @Override
    public FoldChangeDTO getFoldchangeDetails(Integer id) {
    	return  queryHelper.getSingleFcById(id);
    }

    @Override
    public NURSADataset findDataSetById(Long id) {
        Query q =  entityManager.createQuery(SQL.SELECT_DSET_BY_ID);
        q.setParameter("id", id);
        List<NURSADataset> result = q.getResultList();

        if (result.size() == 0) {
            return null;
        }

        return result.get(0);
    }

    @Override
    public List<NURSADataset> findDatasetByPubmedId(String pubmedId) {
        Query datasetsQuery = entityManager.createQuery(SQL.SELECT_DSET_BY_PMID);
        datasetsQuery.setParameter("pubmedId", pubmedId);

        List<NURSADataset> result = datasetsQuery.getResultList();
        return result;
    }

    @Override
    public int numberOfAssociatedDatasets(String pubmedId) {
        Query countDatasets = entityManager.createNativeQuery(SQL.COUNT_ASSOSIATED_DPOINTS);
        countDatasets.setParameter("pubmedId", pubmedId);
        Integer count = ((BigDecimal) countDatasets.getSingleResult()).intValueExact();
        return count;
    }

    public List<PsOrgan> getTissueCategory()
    {
        List<TissueCategory> results = getAllTissueCategories();
        return getPSOrganTree(results);//orgonizeNodes(results);
    }
    
    public List<TissueCategory> getAllTissueCategories(){
    	return  entityManager.createQuery(SQL.SELECT_TISSUECATEGORY).getResultList();
    }
    
    private List<PsOrgan> getPSOrganTree(List<TissueCategory> tissues){
    	
    	 Map<Boolean, List<TissueCategory>> groups = tissues.stream()
 		        .collect(Collectors.partitioningBy(s -> (s.getParent() == null)));
 	    List<TissueCategory> parents = groups.get(true);
 	   List<TissueCategory> kids = groups.get(false);
 	   List<PsOrgan> psorgans= new ArrayList<PsOrgan>();
 	    for(TissueCategory ps:parents){
 	    	PsOrgan psorgan= new PsOrgan(ps.getId(),ps.getName());
 	    	pickOrgans(psorgan,kids);
 	    	psorgans.add(psorgan);
 	   }
 	    return psorgans;
    }
    
    private void pickOrgans(PsOrgan ps,List<TissueCategory> kids){
    	for(TissueCategory organ:kids){
    		if(organ.getParent().getId().longValue() == ps.getId())
    			ps.getChildren().add(new PsOrgan(organ.getId(),organ.getName()));
    	}
    }
    
    
    @Override
	public List<SignalingPathway> getAllSignalingPathways(PathwayCategory pathwayCategory,String name) {
    	return getNamedSignalingPathways(pathwayCategory,name);
   }
    
    public List<SignalingPathway> getSignalingPathways(PathwayCategory type) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<SignalingPathway> criteria = cb.createQuery(SignalingPathway.class);
		Root<SignalingPathway> pathRoot = criteria.from(SignalingPathway.class);
		criteria.select(pathRoot);
		criteria.where(cb.equal(pathRoot.get(SignalingPathway_.type),type));
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(pathRoot.get(SignalingPathway_.displayOrder)));
		orderList.add(cb.asc(pathRoot.get(SignalingPathway_.name)));
		criteria.orderBy(orderList);
		//single column order criteria.orderBy(cb.asc(pathRoot.get(SignalingPathway_.name)));
		return entityManager.createQuery(criteria).getResultList();
	}

    
    private List<Pathway> getPathwayDTOs(List<SignalingPathway> pathEntities){
    	List<Pathway> pathways=StreamSupport.stream(pathEntities.spliterator(), false)
    			.map(Pathway::new)
    			.collect(Collectors.toList());
    	return pathways;
    }
    
    @SuppressWarnings("unchecked")
    private List<SignalingPathway> getConfiguredSignalingPathways(PathwayCategory pathwayCategory,int suggest) {
    	
    		return entityManager.createQuery(SQL.SELECT_PATHWAY_CONFIGED)
    			.setParameter("type", pathwayCategory)
    			.setParameter("suggest", suggest)
    			.getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<SignalingPathway> getNamedPathways(PathwayCategory pathwayCategory,String[] names) {
    	
    	return ((org.hibernate.Query) (entityManager.unwrap(Session.class))
    			.createQuery(SQL.SELECT_NAMED_PATHWAYS))
    			.setParameter("type", pathwayCategory)
    			.setParameterList("names", names)
    			.list();
    }
    
     @SuppressWarnings("unchecked")
	private List<SignalingPathway> getNamedSignalingPathways(PathwayCategory pathwayCategory,String name) {
    	
    	List<SignalingPathway> pathways=null;
    	if(name == null){
    		return entityManager.createQuery(SQL.SELECT_PATHWAY)
    			.setParameter("type", pathwayCategory)
    			.getResultList();
    	}else {
    		 pathways = entityManager.createQuery(SQL.SELECT_NAMED_PATHWAY)
 			.setParameter("type", pathwayCategory)
 			.setParameter("name", name)
 			.getResultList();
    		 
    		//we do have name sharing, eg Other, Others etc among categories
    		 //return pathways.get(0).getPathways();
    		return pathways;
    	}
    }

    
    /**
     * This slow as selects all Entities
     */
    public List<NURSADataset> getActiveDatasets(){
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<NURSADataset> criteria = cb.createQuery(NURSADataset.class);
		Root<NURSADataset> pathRoot = criteria.from(NURSADataset.class);
		criteria.select(pathRoot);
		criteria.where(cb.equal(pathRoot.get(NURSADataset_.active),true));
		return entityManager.createQuery(criteria).getResultList();
    }
    
    public List<NursaDatasetDTO> getAllActiveDatasets(){
    	
    	List<NursaDatasetDTO> datasetDTOs = entityManager
				.createQuery(
				    "select new " +
				    "   edu.bcm.dldcc.big.nursa.model.omics.dto.NursaDatasetDTO(" +
				    "       p.id, " +
				    "       p.name, " +
				    "       p.doi.doi, " +
				    "       p.type, " +
				    "       p.species.commonName, " +
				    "       p.releaseDate " +
				    "   ) " +
				    "from NURSADataset p " +
				    "where p.active =:active", NursaDatasetDTO.class)
				.setParameter( "active", true)
				.getResultList();
		
		return datasetDTOs;	
    }
    
    

    public List<NursaDatasetDTO> getAllDatasets()
    {
        org.hibernate.Query query =   
        		(org.hibernate.Query) (entityManager.unwrap(Session.class))
        		.createSQLQuery(SQL.SELECT_ALL_DATASETS_WITH_METADATA);
        ((SQLQuery) query).addScalar("id", StandardBasicTypes.LONG);
        ((SQLQuery) query).addScalar("type", StandardBasicTypes.STRING);
        ((SQLQuery) query).addScalar("name", StandardBasicTypes.STRING);
        ((SQLQuery) query).addScalar("doi", StandardBasicTypes.STRING);
        ((SQLQuery) query).addScalar("releaseDate", StandardBasicTypes.DATE);
        ((SQLQuery) query).addScalar("tissuesCategoriesID", StandardBasicTypes.STRING);
        ((SQLQuery) query).addScalar("pathways", StandardBasicTypes.STRING);
        ((SQLQuery) query).addScalar("species", StandardBasicTypes.STRING);
        query.setResultTransformer( Transformers.aliasToBean(NursaDatasetDTO.class) );
        List<NursaDatasetDTO> result  = (List<NursaDatasetDTO>)query.list();
        return result;
    }

    /**
     * find ligands by geneId or HGNC symbol or ligand/ligandScheme
     */
	@Override
	public List<DatasetMinimalDTO> findDatasets(DatasetApiQueryDTO query) {
		org.hibernate.Query q = null;
		String sql=null;
		String term=null;
		Integer id=0;
		if(query.apiQueryMode == null)
			return null;
		
		switch(query.apiQueryMode){
					case symbol:
						sql=SQL.SELECT_DATASETS_BY_HGNC_SYMBOL;
						term=query.geneSymbol;
						break;
					case geneid:
						sql=SQL.SELECT_DATASETS_BY_EGENEID;
						id=query.geneId;
						break;
					case ligand:
						term=query.ligandId;
						switch(query.ligandScheme){
							case cas:
								default:
								sql=SQL.SELECT_DATASETS_BY_LIGAND_CAS;
								break;
							case pubchem:
							case chebi:
							case iuphar:
								sql=SQL.SELECT_DATASETS_BY_LIGAND_PIC;
								break;
						}
						break;
			}
		
		q = (org.hibernate.Query) (entityManager.unwrap(Session.class)).createSQLQuery(sql);
		((SQLQuery) q).addScalar("name", StandardBasicTypes.STRING);
		((SQLQuery) q).addScalar("description", StandardBasicTypes.STRING);
		((SQLQuery) q).addScalar("doi", StandardBasicTypes.STRING);
		((SQLQuery) q).addScalar("releaseDate", StandardBasicTypes.DATE);
		if(query.apiQueryMode == DatasetApiQueryDTO.APIQueryMode.ligand ){
			q.setParameter("term",term );
			if(query.ligandScheme != DatasetApiQueryDTO.LigandScheme.cas)
				q.setParameter("scheme",query.ligandScheme.getScheme().toString());
		}else {
			if(query.apiQueryMode == DatasetApiQueryDTO.APIQueryMode.symbol)
				q.setParameter("term",term );
			else
				q.setParameter("term",id );
		}
		
		q.setResultTransformer(Transformers.aliasToBean(DatasetMinimalDTO.class));
		q.setCacheable(true);
		return (List<DatasetMinimalDTO>) q.setFirstResult(0).setMaxResults(TranscriptomineService.MAX_API_DATASETS).list();
	}

	public void translateParams(Map<QueryParamName, QueryParameter> query,
			String apiKey, String includeExp,
			String geneSearchType, 
			String gene,String disease, String goTerm, 
			String molecule,String ligandIdType,
			String significance, String foldChange,String direction, 
			String species, String author, String doi,
			String pubmed, String treatmenttime, 
			String ps,String organ,String tissue,
			String foldChangeMin,String foldChangeMax,
			String signalingPathway,String pathwayCategory) throws Exception{
		
		if(geneSearchType != null && geneSearchType.trim().length()>0){
			if(!new BaseFilter().cleanAPIParam(geneSearchType))
				throw new Exception ("Unacceptable geneSearchType parameter "+geneSearchType);
			if(ApiGeneSearchMode.isConvertible(geneSearchType.toLowerCase()))
				query.put(QueryParamName.geneSearchType, new QueryParameter(getParams(geneSearchType),null));
			else{
				ApiApplicationErrorMessage err= 
						new ApiApplicationErrorMessage(400,
								"UnKnown geneSearchType parameter "+geneSearchType,
								"Allowed values:"+
								Arrays.asList(ApiGeneSearchMode.values()));
				throw new NoSuchIdentifierException ("UnKnown geneSearchType parameter "+geneSearchType,err);
			}
		}
		//FIXME allow ><= signs?
		if(significance !=null && !significance.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(significance))
				throw new Exception ("Unacceptable significance parameter "+significance);
			query.put(QueryParamName.significance, new QueryParameter(getParams(significance),null));
		}else{
			query.put(QueryParamName.significance, new QueryParameter(getParams("0.05"),null));
		}
		
		if(direction!=null && !direction.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(direction))
				throw new Exception ("Unacceptable direction parameter "+direction);
			
			if(ApiDirection.isConvertible(direction.toLowerCase()))
				query.put(QueryParamName.direction, new QueryParameter(getParams(direction),null));
			else{
				ApiApplicationErrorMessage err= 
						new ApiApplicationErrorMessage(400,
								"UnKnown direction parameter "+direction,
								"Allowed values:"+Arrays.asList(ApiDirection.values()));
				throw new NoSuchIdentifierException (err.getMessage(),err);
			}
		}else{
			query.put(QueryParamName.direction, new QueryParameter(getParams("any"),null));
		}
		
		if(species!=null && !species.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(species))
				throw new Exception ("Unacceptable species parameter: "+species);
			
			if(!PublicApiPathwayPsOrganIdCache.speciesNames.containsKey(species)){
				List<String> l=Collections.list(PublicApiPathwayPsOrganIdCache.speciesNames.keys());
				//l.sort(Integer::compareTo);
				
				ApiApplicationErrorMessage err= 
						new ApiApplicationErrorMessage(400,
					"UnKnown Species Taxon Id parameter "+species,
					"Supported values:"+l.stream().collect(Collectors.joining(", ")));
				throw new NoSuchIdentifierException (err.getMessage(),err);
			}else
				query.put(QueryParamName.species, 
						new QueryParameter(getParams(PublicApiPathwayPsOrganIdCache.speciesNames.get(species)),null));
		}else{
			query.put(QueryParamName.species, new QueryParameter(getParams("all"),null));
		}
		
		//FIXME broaden gene name regex to allow greek
		if(gene != null && !gene.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(gene))
				throw new Exception ("Unacceptable gene parameter "+gene);
			query.put(QueryParamName.gene, 
					new QueryParameter(getIdentifierParams(Gene.class,getParams(gene)),null));
		}
		
		if(disease != null && !disease.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(disease))
				throw new Exception ("Unacceptable disease parameter "+disease);
			query.put(QueryParamName.disease, 
					new QueryParameter(getIdentifierParams(TranslationalAutoSuggest.class,getParams(disease)),null));
		}
		
		if(goTerm != null && !goTerm.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(goTerm))
				throw new Exception ("Unacceptable goTerm parameter "+goTerm);
			query.put(QueryParamName.goTerm, 
					new QueryParameter(getIdentifierParams(GOTerm.class,getParams(goTerm)),null));
		}
		
		if((molecule!=null && !molecule.trim().equals("")) && ((ligandIdType==null || ligandIdType.trim().equals("")))){
			if(!new BaseFilter().cleanAPIParam(molecule))
				throw new Exception ("Unacceptable molecule parameter "+molecule);
			query.put(QueryParamName.molecule, 
					new QueryParameter(getIdentifierParams(MoleculeAutoSuggest.class,getParams(molecule)),null));
		}
		
		if(ligandIdType != null && !ligandIdType.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(ligandIdType))
				throw new Exception ("Unacceptable ligandIdType parameter "+ligandIdType);
			
			if(!LigandScheme.isConvertible(ligandIdType.toUpperCase())){
				ApiApplicationErrorMessage err= 
					new ApiApplicationErrorMessage(400,
							"UnKnown ligandIdType parameter "+ligandIdType,
							"Allowed values:"+Arrays.asList(LigandScheme.values()));
				throw new NoSuchIdentifierException (err.getMessage(),err);
			}
			
			LigandScheme scheme=LigandScheme.valueOf(ligandIdType.toUpperCase());
			query.put(QueryParamName.ligandIdType, new QueryParameter(getParams(ligandIdType),null));
			if((molecule != null && !molecule.trim().equals(""))){
				query.put(QueryParamName.molecule, 
						new QueryParameter(getLigandIdentifiers(scheme,getParams(molecule)),null));
			}else{
				ApiApplicationErrorMessage err= 
						new ApiApplicationErrorMessage(400,
								"Missing Ligand Identifier",
								"For Ligands, provide both ligandIdType parameter and the Ligand identifier in molecule parameter ");
					throw new NoSuchIdentifierException (err.getMessage(),err);
			}
		}
		
		//FIXME  tissue has greek characters, so may wanna only disallow CSS type
		boolean hasTissue=false;
		if(tissue != null && tissue.trim().length()>0){
					if(!new BaseFilter().cleanAPIParam(tissue))
						throw new Exception ("Unacceptable tissue parameter: "+tissue);
					hasTissue=true;
					query.put(QueryParamName.tissue, 
							new QueryParameter(getIdentifierParams(Tissue.class,getParams(tissue)),null));
		}
				
		if(!hasTissue){
			checkForHierarchicalParameters(query,ps, organ,false);
		}
		
		checkForHierarchicalParameters(query,pathwayCategory, signalingPathway,true);
	
		if(author!=null && !author.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(author))
				throw new Exception ("Unacceptable author parameter: "+author);
			
			query.put(QueryParamName.author, new QueryParameter(getParams(author),null));
		}
		
		if(doi!=null && !doi.trim().equals("")){
			query.put(QueryParamName.doi, new QueryParameter(getParams(doi),null));
		}
		
		if(includeExp!=null && !includeExp.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(includeExp))
				throw new Exception ("Unacceptable includeExp parameter: "+includeExp);
			
			query.put(QueryParamName.includeExp, new QueryParameter(getParams(includeExp),null));
		}
		
		if(treatmenttime != null && !treatmenttime.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(treatmenttime))
				throw new Exception ("Unacceptable moleculeTreatmentTime parameter: "+treatmenttime);
			
			if(!ApiMoleculeTreatmentTime.isConvertible(treatmenttime)){
				List<String> l=Arrays.asList(ApiMoleculeTreatmentTime.getTimes());
				l.sort((p1, p2) -> p1.compareTo(p2));
				
				ApiApplicationErrorMessage err= 
						new ApiApplicationErrorMessage(400,
					"UnKnown moleculeTreatmentTime parameter "+treatmenttime,
					"Allowed values:"+l);
				throw new NoSuchIdentifierException (err.getMessage(),err);
			}
			
			query.put(QueryParamName.moleculetreatmentTime, 
					new QueryParameter(getParams(treatmenttime),null));
		}
		
		if(pubmed!=null && !pubmed.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(pubmed))
				throw new Exception ("Unacceptable pubmed parameter: "+pubmed);
			if(!isAnIdentifier(pubmed))
				throw new Exception ("Unacceptable pubmed parameter: "+pubmed);
			
			query.put(QueryParamName.pmid, new QueryParameter(getParams(pubmed),null));
		}
		
		if(foldChange != null && !foldChange.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(foldChange))
				throw new Exception ("Unacceptable foldChange parameter: "+foldChange);
			
			query.put(QueryParamName.foldChange, new QueryParameter(getParams(foldChange),null));
		}else{
			if(!isRange(foldChangeMin,foldChangeMax))
				query.put(QueryParamName.foldChange, new QueryParameter(getParams("2"),null));
		}
		
		if(foldChangeMin != null && !foldChangeMin.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(foldChangeMin))
				throw new Exception ("Unacceptable foldChangeMin parameter: "+foldChangeMin);
			
			query.put(QueryParamName.foldChangeMin, 
					new QueryParameter(getParams(foldChangeMin),null));
		}
		if(foldChangeMax != null && !foldChangeMax.trim().equals("")){
			if(!new BaseFilter().cleanAPIParam(foldChangeMax))
				throw new Exception ("Unacceptable foldChangeMax parameter: "+foldChangeMax);
			query.put(QueryParamName.foldChangeMax, 
					new QueryParameter(getParams(foldChangeMax),null));
		}
	}
	
	/**
	 * Pathways and PS/Organs (aka TissueCategories) have parent/child  hierarchy
	 * @param query
	 * @param parentParameter
	 * @param childParameter
	 * @param isPathways
	 * @throws Exception
	 */
	private void checkForHierarchicalParameters(Map<QueryParamName, QueryParameter> query,
			String parentParameter, String childParameter,boolean isPathways) throws Exception{
		
		if(!hasACategoryParameter(parentParameter) && !this.hasAChildParemter(childParameter))
			return;
		
		boolean hasAnItemId=false;
		if(isAnIdParameter(parentParameter,childParameter))
		{
			String itemId=(hasACategoryParameter(parentParameter))?parentParameter:childParameter;
			boolean valid=isValidItemId(itemId,isPathways);
			if(valid){
				hasAnItemId=true;
				if(isPathways)
					query.put(QueryParamName.signalingPathway, new QueryParameter(itemId));
				else
					query.put(QueryParamName.tissueCategory, new QueryParameter(itemId));
			}else{
				if(isPathways)
					throw new Exception ("The provided pathway/pathwayCategory Id is unknown");
				else
					throw new Exception ("The provided PS/Organ Id is unknown");
			}
		}
		
		if(!hasAnItemId && hasACategoryParameter(parentParameter)){
			if(!new BaseFilter().cleanAPIParam(parentParameter)){
				if(isPathways)
					throw new Exception ("Unacceptable pathwayCategory parameter: "+parentParameter);
				else
					throw new Exception ("Unacceptable PS parameter: "+parentParameter);
			}
			
			if(!hasAChildParemter(childParameter)){
				String sid=null;
				if(!isAnIdentifier(parentParameter)){
					 sid=getItemIdByName(parentParameter,null,isPathways);
				}
				if(sid != null){
					hasAnItemId=true;
					if(isPathways)
						query.put(QueryParamName.signalingPathway, new QueryParameter(sid));
					else
						query.put(QueryParamName.tissueCategory, new QueryParameter(sid));
				}else {
					ApiApplicationErrorMessage err= getAvailableOptions(isPathways);
					if(isPathways){
						err.setMessage("Unknown pathwayCategory parameter: "+parentParameter);
						throw new NoSuchIdentifierException (err.getMessage(),err);
					}else{
						err.setMessage("Unknown PS parameter: "+parentParameter);
						throw new NoSuchIdentifierException (err.getMessage(),err);
					}
				}
			}
		}
		
		if(!hasAnItemId && hasAChildParemter(childParameter) ){
			if(!new BaseFilter().cleanAPIParam(childParameter)){
				ApiApplicationErrorMessage err= getAvailableOptions(isPathways);
				if(isPathways){
					err.setMessage("Unacceptable signalingPathway parameter: "+childParameter);
					throw new NoSuchIdentifierException (err.getMessage(),err);
				}else{
					err.setMessage("Unacceptable Organ parameter: "+childParameter);
					throw new NoSuchIdentifierException (err.getMessage(),err);
				}
			}
			
			if(!hasACategoryParameter(parentParameter)){
				ApiApplicationErrorMessage err= getAvailableOptions(isPathways);
				if(isPathways){
					err.setMessage("If you provide a pathway name, you MUST also provide the Pathway Category");
					throw new NoSuchIdentifierException(err.getMessage(),err);
				}else{
					err.setMessage("If you provide an Organ name, you MUST also provide the PS");
					throw new NoSuchIdentifierException(err.getMessage(),err);
				}
			}
			
			String sid=getItemIdByName(parentParameter,childParameter,isPathways);
			
			if(sid != null){
				if(isPathways)
					query.put(QueryParamName.signalingPathway, new QueryParameter(sid));
				else
					query.put(QueryParamName.tissueCategory, new QueryParameter(sid));
			}else { 
				
				ApiApplicationErrorMessage err= null;
				
				if(isPathways){
					if(!PublicApiPathwayPsOrganIdCache.parentPathways.containsKey(parentParameter))
						err= getAvailableOptions(isPathways);
					else
						err= getAvailableChildOptions(parentParameter,isPathways);
					
					throw new NoSuchIdentifierException ("Unknown PathwayCategory/Pathway parameter(s):"
							+ " "+parentParameter+"/"+childParameter,err);
				}else{
					if(!PublicApiPathwayPsOrganIdCache.parentTissues.containsKey(parentParameter))
						err= getAvailableOptions(isPathways);
					else
						err= getAvailableChildOptions(parentParameter,isPathways);
					throw new NoSuchIdentifierException ("Unknown PS/Organ parameter(s): "
				+parentParameter+"/"+childParameter,err);
				}
			}
		}
	}
	
	private ApiApplicationErrorMessage getAvailableChildOptions(String parent,boolean isPathways){
		String keys=null;
		if(isPathways){
			keys=(PublicApiPathwayPsOrganIdCache.childrenPathways.containsKey(parent))?
					PublicApiPathwayPsOrganIdCache.childrenPathways.get(parent).keySet().stream().collect(Collectors.joining(", ")):
				null;
			return new ApiApplicationErrorMessage(400,"pathway parameter error","For Pathway Category '"+parent +"' Available Pathway Options are:"+keys);
		}else{
			keys=(PublicApiPathwayPsOrganIdCache.childrenTissues.containsKey(parent))?
					PublicApiPathwayPsOrganIdCache.childrenTissues.get(parent).keySet()
					.stream().collect(Collectors.joining(", "))
					:null;
			return new ApiApplicationErrorMessage(400,"Organ parameter error","For PS '"+parent +"' available Organ Options are :"+keys);
		}
	}
	
	private ApiApplicationErrorMessage getAvailableOptions(boolean isPathways){
		if(isPathways)
			return new ApiApplicationErrorMessage(400,"pathwayCategory parameter error","Available pathwayCategory Options are :"+
					Collections.list(PublicApiPathwayPsOrganIdCache.parentPathways.keys())
					.stream()
					.collect(Collectors.joining(", ")));
		else
			return new ApiApplicationErrorMessage(400,"PS parameter error","Available PS Options are :"+
					Collections.list(PublicApiPathwayPsOrganIdCache.parentTissues.keys())
					.stream()
					.collect(Collectors.joining(", ")));
	}
	
	private boolean isAnIdParameter(String parent, String child){
		if(hasACategoryParameter(parent) || hasAChildParemter(child)){
			boolean isId=false;
			if(hasACategoryParameter(parent)){
				isId=isAnIdentifier(parent);
				if(isId)
					return true;
			}
			if(hasAChildParemter(child)){
				isId=isAnIdentifier(child);
				if(isId)
					return true;
			}
		}
		return false;
	}
	
	private boolean isValidItemId(String itemId, boolean isPathways){
		
		ConcurrentMap<String,String> idmap=(isPathways)? PublicApiPathwayPsOrganIdCache.pathIds: PublicApiPathwayPsOrganIdCache.tissueIds;
		return idmap.containsKey(itemId);
	
	}
	private boolean hasACategoryParameter(String cat){
		return ((cat != null && cat.trim().length()>0));
	}
	
	private boolean hasAChildParemter(String childParamName){
		return (childParamName != null && childParamName.trim().length()>0 );
	}
	
	private String getItemIdByName(String parent,String child,boolean isPathways){
		if(parent != null && child == null)
			return (isPathways)? PublicApiPathwayPsOrganIdCache.parentPathways.get(parent):
				PublicApiPathwayPsOrganIdCache.parentTissues.get(parent);
		
		ConcurrentMap<String,Map<String,String>> childMap=(isPathways)?
				PublicApiPathwayPsOrganIdCache.childrenPathways:
				PublicApiPathwayPsOrganIdCache.childrenTissues;
		return (childMap.containsKey(parent))?childMap.get(parent).get(child):null;
	}
	
	private boolean isAnIdentifier(String along) {
		try{
			Long.parseLong(along);
			return true;
		}catch (Exception e){
			return false;
		}
	}
	
	private boolean isAnDoubleNumber(String dabo) {
		try{
			Double.parseDouble(dabo);
			return true;
		}catch (Exception e){
			return false;
		}
	}

	private boolean isRange(String fcMin,String fcMax){
		return ((fcMin != null && !fcMin.trim().equals("")) || (fcMax != null && !fcMax.trim().equals("")));
	}
	private  Set<String> getParams(String query){
		String[] queries=query.split(APIService.PARAMS_SPLITTER);
		Set<String> params = new HashSet<String>();
		
		for(String param:queries){
			params.add(param);
		}
		return params;
	}
	
	private Set<String> getLigandIdentifiers(LigandScheme scheme,Set<String> params) throws NoSuchIdentifierException{
		switch(scheme){
		case CAS:
			return findLigandIdentifierByCas(new ArrayList<String>(params));
		case CHEBI:
		case PUBCHEM:
		case IUPHAR:
			return findLigandIdentifierByNonCasSchemes(new ArrayList<String>(params),scheme.name())	;
		case DRUG:
			//TODO implement drug identifiers
		default:
		}
		
		return null;
	}
	
	
	/**
	 * Get NURSA internal identifiers by Ligand IUPHAR, PUBCHEM, Chebi  ids
	 * This method assumes Identifiers are always #s, so case does not matter
	 * mol.rank is null on these identifiers
	 * @param id
	 * @param IdScheme
	 * @return
	 * @throws NoSuchIdentifierException 
	 */
	private Set<String> findLigandIdentifierByNonCasSchemes(List<String> ids,String IdScheme) throws NoSuchIdentifierException{
		String sql=LigandSearchSQL.CHEIUPPUB_SQL.getSql();
		
		org.hibernate.Query q = (org.hibernate.Query) (entityManager.unwrap(Session.class)).createSQLQuery(sql);
		List<BigDecimal> list= (List<BigDecimal>)
				q.setParameterList("id",  ids )
				  .setParameter("scheme",  IdScheme.toLowerCase() )
				 .list();
		if(list.size()==0){
			throw new NoSuchIdentifierException("No relevant identifier found for provided search parameter: "+ids);
		}
		Set<String> identifierList= new HashSet<String>();
		for(BigDecimal id:list)
			identifierList.add(Integer.toString(id.intValue()));
		
		return identifierList;
	}
	
	/**
	 * Get NURSA internal identifiers by CAS id
	 * This method assumes CAS #s are always hyphenated numbers (eg 50-28-2)so case does not matter
	 * @param ids
	 * @return
	 * @throws NoSuchIdentifierException 
	 */
	private Set<String> findLigandIdentifierByCas(List<String> ids) throws NoSuchIdentifierException{
		
		String sql=LigandSearchSQL.CAS_SQL.getSql();
		
		org.hibernate.Query q = (org.hibernate.Query) (entityManager.unwrap(Session.class)).createSQLQuery(sql);
		List<BigDecimal> list= (List<BigDecimal>) q.setParameterList("id",ids ).list();
		if(list.size() == 0){
			throw new NoSuchIdentifierException("No relevant identifier found for provided  search parameter: "+ids);
		}
		Set<String> identifierList= new HashSet<String>();
		for(BigDecimal id:list)
			identifierList.add(Integer.toString(id.intValue()));
		
		return identifierList;
	}
	
	private  <T> Set<String> getIdentifierParams(Class<T> type,Set<String> params) throws NoSuchIdentifierException{
		Set<String> IdParams = new HashSet<String>();
		AutosuggestList list;
		for(String param:params){
			 list= this.autoSuggestList(type,param,1);
			
			if(list == null || list.getExactList() == null || list.getExactList().size() == 0){
				throw new NoSuchIdentifierException("No relevant identifier found for provided search parameter: "+param);
			}
			
			if(list!=null && list.getExactList()!=null && list.getExactList().size()>0){
				
				String id=null;
				if (!type.equals(GOTerm.class)){
					if(!type.equals(TranslationalAutoSuggest.class))
						id=((AutosuggestList<AutosuggestTerm>)list).getExactList().get(0).getIdentifier();
					else
						id=((AutosuggestList<AutosuggestTerm>)list).getExactList().get(0).getOfficialSymbol();
				}else{
					id=((AutosuggestList<String>)list).getExactList().get(0);
				}
				log.log(Level.FINE,"Search term local id: "+id);
				if(id == null || id.equals("")){
					throw new NoSuchIdentifierException("No identifier for search parameter: "+param);
				}
				IdParams.add(id);
			}
		}
		return IdParams;
	}
	
}

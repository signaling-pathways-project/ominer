package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.cistromic.*;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.SimpleQueryForm;
import edu.bcm.dldcc.big.nursa.model.core.GeneInfoEtl;
import edu.bcm.dldcc.big.nursa.model.core.GeneInfoEtl_;
import edu.bcm.dldcc.big.nursa.model.omics.*;
import edu.bcm.dldcc.big.nursa.model.omics.dto.*;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.QueryParametersData;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.TmQueryResponse;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.FileDownloadMemoryCache;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.PathwaysNodesMapBean;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.ApiErrorMessage;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.ApiQueryTargetBy;
import edu.bcm.dldcc.big.nursa.util.filter.BaseFilter;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Omics Rest Service Bean
 * @author mcowiti
 */
@Stateless
public class OmicsServicebean implements Serializable {

    public static final double MAX_FC_BY_DEFOLT = 6666.6666D;
    public static final Integer MAX_NUMBER_XLS = 65535;
    public static final String BAD_INPUT_MISSING_PARAMS="Bad input. Request parameters needed";
    public static final String BAD_INPUT_MISSING_SEARCHTYPE="Bad input. Missing GeneSearchType";
    public static final String inputMessage="Provide a member of family/physiologicalSystem/Organ/Species to find a Consensome";

    public static final String inputMessageDoi="Provide a valid DOI to find a Consensome";
    public static final String pageNeedCountMessage = "If you supply page, you must provide a countDatapoints";
    public static final String INPUT_MISSING_PATHWAY_TYPE="If you supply a pathway, you must specify the type(type,category,class,family)";
    public static final String INPUT_BOTH_FC_AND_FC_RANGE="A FoldChange Value and FoldChange Ranges(Min/Max) cannot both be present ";

    private static final String API_MORE_INFO=" See API docs for more detailed information including the actual parameter names for the filters";

    public static final String apiInputMessageStart ="Provide a valid ";
    public static final String apiInputMessageEnd ="to find a Dataset/Datapoint";

    private static final String CISTROMIC = "Cistromic";
    private static final String TRANSCRIPTOMIC = "Transcriptomic";

    public static final Integer MAX_API_DATAPOINTS = 10000;
    public static final Integer MAX_API_CONSENSOMES = 20000;
    public static final Integer MAX_API_DATASETS = 10000;
    public final static Integer MAX_ABS_NUMBER_DATAPOINTS = 5000;
    private final static Integer MAX_GO_TERMS_VIA_TEMP_PRE_QUERY=1000;

    @PersistenceContext(unitName = "NURSA")
    private EntityManager em;

    @Inject private FileDownloadMemoryCache fileDownloadMemoryCache;

    @Inject
    private TranscriptomineService omineService;


    private static Logger log = Logger.getLogger(OmicsServicebean.class.getName());

    /**
     * //All="-1"
     */
    public Map<String, String> recastFamilyPsOrganOfNull(String family, String ps, String organ){

        Map<String,String> input= new HashMap<>();
        input.put("family",family);
        input.put("ps",ps);
        input.put("organ",organ);

        if(family == null)
            input.put("family","-1");
        if(ps == null && organ == null)
            input.put("ps","-1");
        if(ps  != null && organ  == null)
            input.put("organ","-1");

        return input;
    }

    public Optional<Response> isGotInvalidDOIResponse(String doi){
        if(!new BaseFilter().cleanDoi(doi))
            return Optional.ofNullable(Response.status(400).entity(
                    new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,
                            new StringBuilder(OmicsServicebean.apiInputMessageStart).append(" DOI ").append(OmicsServicebean.apiInputMessageEnd).toString())).build());

         return Optional.empty();
    }

    public Optional<Response> isGotInvalidPubmedResponse(String pmid){
        if(!new BaseFilter().cleanPmid(pmid))
            return Optional.ofNullable(Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,
                    new StringBuilder(OmicsServicebean.apiInputMessageStart).append(" PMID ").append(OmicsServicebean.apiInputMessageEnd).toString())).build());

        return Optional.empty();
    }

    public Optional<Response> isGotInvalidBsmIdResponse(String bsmId){
        if(!new BaseFilter().cleanBsmId(bsmId))
            return Optional.ofNullable(Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,
                    new StringBuilder(OmicsServicebean.apiInputMessageStart).append(" BSM Identifier ").append(OmicsServicebean.apiInputMessageEnd).toString())).build());

        return Optional.empty();
    }

    public Optional<Response> isGotInvalidBsmOrNodeResponse(String id,String type){
        if(!new BaseFilter().cleanBsmOrNode(id))
            return Optional.ofNullable(Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,
                    new StringBuilder(OmicsServicebean.apiInputMessageStart).append(type).append(OmicsServicebean.apiInputMessageEnd).toString())).build());

        return Optional.empty();
    }

    private final static String PS_INVALID_MSG1="The PhysiologicalSystem value you provided (";
    private final static String ORGAN_INVALID_MSG1="The Organ value you provided (";
    private final static String PS_INVALID_MSG2=") is invalid. ";
    public final static String PS_INPUT="PhysiologicalSystem";
    public final static String ORGAN_INPUT="organ";


    public Optional<Response> isGotInvalidBiosamplePieceResponse(String id,String type){

        if(!new BaseFilter().cleanBiosamplePieceId(id)) {
            StringBuilder sb= new StringBuilder();
            if(!type.equals(ORGAN_INPUT))
                sb.append(PS_INVALID_MSG1).append(id).append(PS_INVALID_MSG2);
            else
                sb.append(ORGAN_INVALID_MSG1).append(id).append(PS_INVALID_MSG2);

            return Optional.ofNullable(Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,
                    sb.append(OmicsServicebean.apiInputMessageStart)
                            .append(type)
                            .append(OmicsServicebean.apiInputMessageEnd).toString())).build());
        }

        return Optional.empty();
    }

    public Optional<String> isValidatedBiosamplePiece(String id,String type){
        StringBuilder sb= new StringBuilder();
        if(!new BaseFilter().cleanBiosamplePieceId(id)) {
            if(!type.equals(ORGAN_INPUT))
                sb.append(PS_INVALID_MSG1).append(id).append(PS_INVALID_MSG2);
            else
                sb.append(ORGAN_INVALID_MSG1).append(id).append(PS_INVALID_MSG2);

            return Optional.of(sb.toString());
        }

        return Optional.empty();
    }

    public Optional<Response> isGotInvalidGoTermInputResponse(String goTerm){

        if(!new BaseFilter().cleanGoTerm(goTerm))
            return Optional.ofNullable(Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,
                    new StringBuilder(OmicsServicebean.apiInputMessageStart).append(" GoTerm ").append(OmicsServicebean.apiInputMessageEnd).toString())).build());

        if(new BaseFilter().isGoIDPattern(goTerm))
            return Optional.ofNullable(Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,
                    new StringBuilder("The goTerm queryValue( " ).append(goTerm).append(" ) seems to be a GO ID. GI IDs are not supported. Use GO Term Names instead").toString())).build());

        return Optional.empty();
    }


    public Optional<SimpleQueryForm> getSimpleQueryForm(String queryId){
        SimpleQueryForm queryForm=fileDownloadMemoryCache.getQueryForm(queryId);
        return Optional.ofNullable(queryForm);
    }

    /**
     * Regulation report, show only Pathways by AGS in node
     * Show BSM,OtherAGS ONLY if resulting node is node not in AGS
     * @param form
     * @param showInUI
     * @param queryType
     * @return
     */
    public  List<?> query(SimpleQueryForm form, boolean showInUI, boolean forApi,boolean hasFilters,QueryType queryType,boolean byDatapoints,
                          final Long startId,final int max){

        long b=System.currentTimeMillis();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OmicsDatapoint> criteria=cb.createQuery(OmicsDatapoint.class);
        Root<DatasetViewDTO> pathRoot =  criteria.from(DatasetViewDTO.class);
        Root<DatasetNodesView> 	nodesRoot =  criteria.from(DatasetNodesView.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (queryType == QueryType.Cistromic) {
            Root<BindingScore> scoreRoot=criteria.from(BindingScore.class);
            setQueryCriteria(criteria,scoreRoot,pathRoot,nodesRoot,predicates,form,queryType,forApi,hasFilters,startId);
            if(forApi)
                criteria.orderBy(cb.asc(scoreRoot.get(BindingScore_.id)));

            setSelectors(criteria,scoreRoot,pathRoot,nodesRoot,predicates, queryType,true);
            //This misses node sourceif(forApi)setSelectorsForApi(criteria, scoreRoot, pathRoot, nodesRoot, predicates, queryType);
        }else{
            if(hasFilters) {
                Root<FoldChangeView> fcRoot = criteria.from(FoldChangeView.class);
                setQueryCriteria(criteria, fcRoot, pathRoot, nodesRoot, predicates, form, queryType, forApi, hasFilters, startId);
                if (forApi)
                    criteria.orderBy(cb.asc(fcRoot.get(FoldChangeView_.id)));
                setSelectors(criteria, fcRoot,pathRoot,nodesRoot,predicates, queryType,hasFilters);
            }else{
                Root<FoldChangeViewForApi> fcRoot = criteria.from(FoldChangeViewForApi.class);
                setQueryCriteria(criteria, fcRoot, pathRoot, nodesRoot, predicates, form, queryType, forApi, hasFilters, startId);
                if (forApi)
                    criteria.orderBy(cb.asc(fcRoot.get(FoldChangeViewForApi_.id)));
                setSelectors(criteria, fcRoot,pathRoot,nodesRoot,predicates, queryType,hasFilters);
            }
            //This misses node source if(forAPI)setSelectorsForApi(criteria, fcRoot, pathRoot, nodesRoot, predicates, queryType);
        }

        //TODO add lastPageId so that allow paging(WOuld need do order by datapointId
        List<OmicsDatapoint> list = em.createQuery(criteria)
                .setMaxResults(max)
                .getResultList();

        if ((System.currentTimeMillis() - b) > 2000)
            if(!forApi)
                if(showInUI)
                    log.log(Level.WARNING, queryType+ " @query Datapoints query ONLY #->time(ms) =" + list.size() + "->" + (System.currentTimeMillis() - b));
                else
                    log.log(Level.INFO, queryType+ " @query for FileDownload Datapoints query ONLY #->time(ms) =" + list.size() + "->" + (System.currentTimeMillis() - b));

        if(queryType == QueryType.Cistromic) {
            if(byDatapoints)
                return aggregatesCisDatapointsByDatapoints(list);
            else
                return aggregateCisDatapointsByPathways(list,showInUI);
        }else {
            if(byDatapoints)
                return aggregatesTxDatapointsByDatapoints(list);
            else
                return aggregatesTxDatapointsByPathways(list,showInUI);
        }
    }

    private String mapApiQueryTypeToGeneSearchType(String queryType){
        switch(queryType){
            case "doi":
                return "doi";
            case "biosample":
                return "biosample";
            case "entrezGeneId":
                return "entrezGeneId";
            case "goTerm":
                return "goTerm";
        }
        return "unknown";
    }

    /**
     * //must connect to gene to use egeneid
     *  //omicstype,biosample,macs2,pvalue,type,cat,class,family,node
     * @param queryType
     * @param queryValue
     * @param doi
     * @param ps
     * @param organ
     * @param minScore
     * @param maxScore
     * @param countMax
     * @return
     */
    public Response getAntigenBindingScoresForApi(boolean isReactome,ApiQueryTargetBy queryType, String queryValue,String doi,
                                                  String species,String pathwayNodeType,Integer signalingPathway,
                                                  String ps,String organ,
                                                  Integer minScore,Integer maxScore,
                                                  boolean isCount,Long startId,Integer countMax){

        String geneSearchType=mapApiQueryTypeToGeneSearchType(queryType.toString());

        Map map= null;
        if(!isReactome) {
            map = prepCisSimpleQueryForm(false, CISTROMIC,
                    geneSearchType, queryValue, doi, ps, organ, null, species,
                    pathwayNodeType, (signalingPathway != null) ? signalingPathway.toString() : null, minScore, maxScore, null);
        }else{
            map=this.prepSimpleCisReactomeQueryForm(doi);
        }

        if(map.containsKey("error"))
            return Response.status(400).entity(map.get("error")).build();

        SimpleQueryForm sform=(SimpleQueryForm)map.get("sform");
        QueryForm queryForm=(QueryForm)map.get("qform");

        Map cmap=checkCountCriteria(false,true,false,QueryType.Cistromic,sform,queryForm,startId,countMax);
        if(cmap.containsKey("error"))
            return (Response)cmap.get("error");

        int count=(Integer)cmap.get("count");

        if(isCount)
            return Response.status(200).entity(count).build();


        int absMax=MAX_API_DATAPOINTS;
        if(countMax > absMax)
            countMax=absMax;

        if(count > countMax){
            StringBuilder sb=new StringBuilder(" (Some filters include:").append("species,pathways");
            if(queryType != ApiQueryTargetBy.biosample)
                sb.append(",ps,organ");
            sb.append(",min/max MACS2 scores).");
            sb.append(API_MORE_INFO);
            return Response.status(413).entity("Query will return too much data: "+count+". Consider attaching filters "+ sb.toString()).build();
        }

        boolean byDatapoints=false;
        boolean hasFilters=(!isReactome);

        Integer maxCountBeforeAggregate=countMax; //duplicates of datapoints allowed for SRX
        List<OmicsDatapointReport> rdatalist = (List<OmicsDatapointReport>) this.query(sform, false, true,hasFilters,
                QueryType.Cistromic, byDatapoints,startId,maxCountBeforeAggregate);
        List<OmicsDatapoint> datalist= new ArrayList<>();
        for(OmicsDatapointReport report:rdatalist){
            datalist.addAll(report.getDatapoints());
        }

        List<ApiOmicsDatapoint> apiDatalist =new ArrayList<ApiOmicsDatapoint>();
       long b=System.currentTimeMillis();

       //////TODO if reactome, and has return fields, may add here
        for(OmicsDatapoint datapoint:datalist){
            apiDatalist.add(new ApiOmicsDatapoint(datapoint.getId(),
                    datapoint.getSymbol(),
                    datapoint.getExperimentId(),
                    datapoint.getScore(),
                    datapoint.getBioSample(),
                    datapoint.getType(),
                    datapoint.getCategory(),
                    datapoint.getCclass(),
                    datapoint.getFamily(),
                    getAgsBsmOagsForApi(datapoint.getMolecules(),datapoint.getBsms(),datapoint.getOmolecules())));
        }

        long sort=System.currentTimeMillis();
        Collections.sort(apiDatalist);
        if((System.currentTimeMillis()-sort) >2000)
            log.log(Level.WARNING,"Sorting API SRX dataponts results pre-response taking over 2s");

        if((System.currentTimeMillis()-b)>100)
            log.log(Level.WARNING,"SRX API DTO prep time slow(ms)="+(System.currentTimeMillis()-b));

        //FIXME since dups allowed-for UI RR-might datalist be > countMax??
        return Response.status(200).entity(apiDatalist).build();
    }

    /**
     * return encoded AGS|BSM|OAGS nodes
     * @return
     */
    private String getAgsBsmOagsForApi(String molecules,String bsms,String omolecules) {

        StringBuilder sb= new StringBuilder();

        if(molecules != null && molecules.trim().length()>0)
            sb.append(molecules).append("|");
        else sb.append("|");
        if(bsms != null && bsms.trim().length()>0)
            sb.append(bsms).append("|");
        else sb.append("|");

        if(omolecules != null && omolecules.trim().length()>0)
            sb.append(omolecules);
        return sb.toString();
    }

    private Map prepSimpleTxReactomeForm(String doi, String significance){
        Map map= new HashMap();
        Optional<Double> opValue=this.getDoubleValue(significance);
        Double sig=(opValue.isPresent())?opValue.get():null;
        map.put("sform", new SimpleQueryForm(TRANSCRIPTOMIC,doi,sig));
        return map;
    }


    public Response getDatapointsForReactome(QueryType queryType,String doi,Double significance,boolean isCount,Long startId,Integer countMax){

        long b=System.currentTimeMillis();
        Map map=null;

        if(queryType== QueryType.Transcriptomic)
            map=this.prepSimpleTxReactomeForm(doi,significance.toString());
        else
            map=this.prepSimpleCisReactomeQueryForm(doi);

        if(map.containsKey("error"))
            return Response.status(400).entity(map.get("error")).build();

        SimpleQueryForm sform=(SimpleQueryForm)map.get("sform");
        //QueryForm queryForm=(QueryForm)map.get("qform");
        //TODO cache query and results

        int count=0;
        ///dynamically indicate that it is managed, in liue of listing managed entity in persistence.xml
        em.getMetamodel().managedType(FoldChangeViewForApi.class);
        em.getMetamodel().managedType(DatasetViewDTO.class);
        if(queryType==QueryType.Transcriptomic)
            count=((Long)em.createNamedQuery("getReactomeTXDatapointsCount")
                .setParameter("doi",doi)
                    .setParameter("startId",startId)
                    .setParameter("pvalue",significance).getSingleResult()).intValue();
        else
            count=((Long)em.createNamedQuery("getReactomeSRXDatapointsCount")
                    .setParameter("doi",doi)
                    .setParameter("startId",startId)
                    .getSingleResult()).intValue();

        if(isCount){
            return Response.status(200).entity(count).build();
        }

        int absMax=MAX_API_DATAPOINTS;
        if(countMax > absMax)
            countMax=absMax;

        if(countMax < count)
            ;//no need check as maxResult will limit
        else {
            if (count > absMax) {
                StringBuilder sb;
                String msg = new StringBuilder("Query will return too much data: ").append(count)
                            .append(". Consider a lower significance value or perform paging operations").toString();
                return Response.status(413).entity(msg).build();
            }
        }

        String query;
        List<ApiOmicsDatapoint> list;
        if(queryType==QueryType.Transcriptomic) {
            list= em.createNamedQuery("getReactomeTXDatapoints",ApiOmicsDatapoint.class)
                    .setParameter("doi",doi)
                    .setParameter("pvalue",significance)
                    .setParameter("startId",startId)
                    .setMaxResults(countMax).getResultList();
        }else {
            list = em.createNamedQuery("getReactomeSRXDatapoints",ApiOmicsDatapoint.class)
                    .setParameter("doi",doi)
                    .setParameter("startId",startId)
                    .setMaxResults(countMax).getResultList();
        }

        Set<ApiOmicsDatapoint> slist=new HashSet<>(list);

        if((System.currentTimeMillis()-b)>6000)
            log.log(Level.WARNING,"Long datapoints call, # set =>"+slist.size()+" in(ms) =>"+(System.currentTimeMillis()-b));

        return Response.status(200).entity(slist).build();
    }

    /**
     * FR return omicstype,biosample,fc,pvalue,type,cat,class,family,node
     * @param queryType
     * @param queryValue
     * @param doi
     * @param ps
     * @param organ
     * @param fcMin
     * @param fcMax
     * @param countMax
     * @return
     */
    public Response getFoldChangesForApi(boolean isReactome,ApiQueryTargetBy queryType, String queryValue, String doi,
                                         String species, String sigPathNodeType, Integer sigpath,
                                         String ps, String organ,
                                         String fc, String fcMin, String fcMax, String significance, String direction,
                                         boolean isCount,Long startId,Integer countMax){


        String geneSearchType=mapApiQueryTypeToGeneSearchType(queryType.toString());

        //TODO why by datapoints?
        Map map=null;
        if(!isReactome) {
            map = this.prepTxSimpleQueryForm(false,
                    geneSearchType, queryValue, null, doi, ps, organ, null,
                    species, sigPathNodeType, (sigpath != null) ? sigpath.toString() : null, significance, direction,
                    fc, fcMin, fcMax, null, "datapoints", countMax);
        }else{
            map=this.prepSimpleTxReactomeForm(doi,significance);
        }

        if(map.containsKey("error"))
            return Response.status(400).entity(map.get("error")).build();

        SimpleQueryForm sform=(SimpleQueryForm)map.get("sform");
        QueryForm queryForm=(QueryForm)map.get("qform");

        boolean hasFilters=(!isReactome);
        Map cmap=checkCountCriteria(false,true,hasFilters,QueryType.Transcriptomic,sform,queryForm,startId,countMax);
        if(cmap.containsKey("error"))
            return (Response)cmap.get("error");

        int count=(Integer)cmap.get("count");

        if(isCount){
            return Response.status(200).entity(count).build();
        }

        int absMax=MAX_API_DATAPOINTS;
        if(countMax > absMax)
            countMax=absMax;

        if(countMax < count)
            ;//no need check as maxResult will limit
        else {
            if (count > absMax) {
                StringBuilder sb;
                String msg;
                if (!isReactome) {
                    sb = new StringBuilder("Query will return too much data: ").append(count).append(". Consider attaching filters ");
                    sb.append(" (Some filters include:").append("species,pathways");
                    if (queryType != ApiQueryTargetBy.biosample)
                        sb.append(",ps,organ");
                    sb.append(",foldChange,min/max foldChanges,significance).");
                    sb.append(API_MORE_INFO);
                    msg = sb.toString();
                } else {
                    msg = new StringBuilder("Query will return too much data: ").append(count)
                            .append(". Consider a lower significance value or perform paging operations").toString();
                }
                return Response.status(413).entity(msg).build();
            }
        }

        //FIXME No caching of Queries, maybe too much MB
        //FIXME actual query here
        //SRX has points dups. TX perhaps not???
        Integer maxCountBeforeAggregate=countMax*2;
        boolean byDatapoints=false;

        /////test 2.05.2019, needs to  be true
        boolean forApi=true;

        List<OmicsDatapointReport> rdatalist = (List<OmicsDatapointReport>) this.query(sform, false, forApi,hasFilters,QueryType.Transcriptomic, byDatapoints,startId,maxCountBeforeAggregate);
        List<OmicsDatapoint> datalist= new ArrayList<>();
        for(OmicsDatapointReport report:rdatalist){
            datalist.addAll(report.getDatapoints());
        }

        List<ApiOmicsDatapoint> apiDatalist =new ArrayList<ApiOmicsDatapoint>();
        long b=System.currentTimeMillis();

        ////////TODO if reactome requests return types, return those here
        for(OmicsDatapoint datapoint:datalist){
            if(!isReactome)
            apiDatalist.add(new ApiOmicsDatapoint(datapoint.getId(),
                    datapoint.getSymbol(),
                    datapoint.getExperimentId(),
                    datapoint.getFoldChangeRaw(),
                    datapoint.getPvalue(),
                    datapoint.getBioSample(),
                    datapoint.getType(),
                    datapoint.getCategory(),
                    datapoint.getCclass(),
                    datapoint.getFamily(),
                    getAgsBsmOagsForApi(datapoint.getMolecules(),datapoint.getBsms(),datapoint.getOmolecules())));
            else
                apiDatalist.add(new ApiOmicsDatapoint(datapoint.getId(),
                        datapoint.getSymbol(),
                        datapoint.getExperimentId(),
                        datapoint.getScore(),
                        datapoint.getFoldChangeRaw(),
                        datapoint.getPvalue()));
        }

        //sort, since we lose it since query
        long sort=System.currentTimeMillis();
        Collections.sort(apiDatalist);
        if((System.currentTimeMillis()-sort) >2000)
            log.log(Level.WARNING,"Sorting API TX dataponts results pre-response taking over 2s");


        if((System.currentTimeMillis()-b)>100)
            log.log(Level.WARNING,"TX API DTO prep time slow(ms)="+(System.currentTimeMillis()-b));
        return Response.status(200).entity(apiDatalist).build();
    }

    public   Integer countDatapoints(SimpleQueryForm form, final QueryType queryType,final boolean forApi,boolean hasSignificanceFilters,final Long startId){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<DatasetViewDTO> pathRoot = criteria.from(DatasetViewDTO.class);
        Root<DatasetNodesView> nodesRoot =  criteria.from(DatasetNodesView.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        if(queryType == QueryType.Cistromic) {
            Root<BindingScore> scoreRoot = criteria.from(BindingScore.class);
            setQueryCriteria(criteria,scoreRoot,pathRoot,nodesRoot,predicates,form,queryType,forApi,hasSignificanceFilters,startId);
            criteria.select(cb.countDistinct(scoreRoot.get(BindingScore_.id)));
        }else{

            if(hasSignificanceFilters) {
                Root<FoldChangeView> fcRoot = criteria.from(FoldChangeView.class);
                setQueryCriteria(criteria,fcRoot,pathRoot,nodesRoot,predicates,form,queryType,forApi,hasSignificanceFilters,startId);
                criteria.select(cb.countDistinct(fcRoot.get(FoldChangeView_.id)));
            }else {
                Root<FoldChangeViewForApi> fcRoot = criteria.from(FoldChangeViewForApi.class);
                setQueryCriteria(criteria,fcRoot,pathRoot,nodesRoot,predicates,form,queryType,forApi,hasSignificanceFilters,startId);
                criteria.select(cb.countDistinct( fcRoot.get(FoldChangeViewForApi_.id)));
            }
        }

        criteria.where(predicates.toArray(new Predicate[]{}));

        return em.createQuery( criteria ).getSingleResult().intValue();
    }

       public FoldChangeMinMax findLargestFoldChangeInQuery(SimpleQueryForm form, QueryType queryType){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FoldChangeMinMax> criteria = cb.createQuery(FoldChangeMinMax.class);
        Root<DatasetViewDTO> pathRoot = criteria.from(DatasetViewDTO.class);
        Root<DatasetNodesView> nodesRoot =  criteria.from(DatasetNodesView.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        Root<FoldChangeView> fcRoot = criteria.from(FoldChangeView.class);
        setQueryCriteria(criteria,fcRoot,pathRoot,nodesRoot,predicates,form,queryType,false,true,0L);

        criteria.multiselect(
                cb.min(fcRoot.get(FoldChangeView_.foldChange)),
                cb.max(fcRoot.get(FoldChangeView_.foldChange))
                ).where(predicates.toArray(new Predicate[]{}));

       return  em.createQuery( criteria ).getSingleResult();
    }

    public  Double getLargestFC(FoldChangeMinMax minMax){

        if(minMax == null )
            return 0D;

        double min=0d,max=0d;
        min=minMax.getMin();
        max=minMax.getMax();

        if(min > 0 && min <= 2d)
            min=(1d/min);
        if(max > 0 && max <= 2d)
            max=(1d/max);

        return (max>min)?max:min;
    }

     public List<OmicsMinimalDatapoint> findOmicsFoldChangesByExperimentId(Long experimentId,SimpleQueryForm form,int max){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OmicsMinimalDatapoint> criteria=cb.createQuery(OmicsMinimalDatapoint.class);
        Root<FoldChangeView> fcRoot=criteria.from(FoldChangeView.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(fcRoot.get(FoldChangeView_.expid), experimentId));

        setFoldChangeLimits(fcRoot,predicates,form);
        setSignificanceLimits(fcRoot,predicates,form.significance);

        criteria.multiselect(
                fcRoot.get(FoldChangeView_.id),
                fcRoot.get(FoldChangeView_.gene),
                fcRoot.get(FoldChangeView_.egeneid),
                fcRoot.get(FoldChangeView_.foldChange),
                fcRoot.get(FoldChangeView_.pValue))
                .where(predicates.toArray(new Predicate[]{}));

        return em.createQuery(criteria).setMaxResults(max).getResultList();
    }

    public FoldChangeMinMax findLargestFoldChangeInExperiment(Long experimentId){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FoldChangeMinMax> criteria = cb.createQuery(FoldChangeMinMax.class);
        Root<FoldChangeView> fcRoot=criteria.from(FoldChangeView.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(fcRoot.get(FoldChangeView_.expid), experimentId));

        criteria.multiselect(
                cb.min(fcRoot.get(FoldChangeView_.foldChange)),
                cb.max(fcRoot.get(FoldChangeView_.foldChange))
        ).where(predicates.toArray(new Predicate[]{}));

        return em.createQuery( criteria ).getSingleResult();
    }

    public Optional<OmicsFoldChangeDetail>  findOmicsFoldChangeDetail(Long id){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OmicsFoldChangeDetail> criteria=cb.createQuery(OmicsFoldChangeDetail.class);
        Root<FoldChangeView> fcRoot=criteria.from(FoldChangeView.class);
        Root<DatasetViewDTO> pathRoot =  criteria.from(DatasetViewDTO.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(fcRoot.get(FoldChangeView_.id), id));
        predicates.add(cb.equal(fcRoot.get(FoldChangeView_.expid),pathRoot.get(DatasetViewDTO_.expid)));


        criteria.multiselect(
                fcRoot.get(FoldChangeView_.id),
                fcRoot.get(FoldChangeView_.pValue),fcRoot.get(FoldChangeView_.foldChange),

                fcRoot.get(FoldChangeView_.prob),fcRoot.get(FoldChangeView_.probType),
                fcRoot.get(FoldChangeView_.gene),fcRoot.get(FoldChangeView_.egeneid),

                pathRoot.get(DatasetViewDTO_.expid),
                pathRoot.get(DatasetViewDTO_.ename),
                pathRoot.get(DatasetViewDTO_.edesc),
                pathRoot.get(DatasetViewDTO_.experimentnumber),

                pathRoot.get(DatasetViewDTO_.tissue),
                pathRoot.get(DatasetViewDTO_.ps),
                pathRoot.get(DatasetViewDTO_.species),

                pathRoot.get(DatasetViewDTO_.dname),
                pathRoot.get(DatasetViewDTO_.ddesc),
                pathRoot.get(DatasetViewDTO_.doi),
                pathRoot.get(DatasetViewDTO_.did),
                pathRoot.get(DatasetViewDTO_.repo)
                ).where(predicates.toArray(new Predicate[]{}));

        List<OmicsFoldChangeDetail> list=em.createQuery(criteria).getResultList();
        if(list.size()>0)
            return Optional.ofNullable(list.get(0));
        return Optional.empty();
    }

    public Optional<OmicsMacsPeakDetail>  findOmicsMacs2PeakDetail(Long id){

        long b=System.currentTimeMillis();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OmicsMacsPeakDetail> criteria=cb.createQuery(OmicsMacsPeakDetail.class);
        Root<BindingScore> fcRoot=criteria.from(BindingScore.class);
        Root<DatasetViewDTO> pathRoot =  criteria.from(DatasetViewDTO.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(fcRoot.get(BindingScore_.id), id));
        predicates.add(cb.equal(fcRoot.get(BindingScore_.experiment),pathRoot.get(DatasetViewDTO_.expid)));
        predicates.add(cb.greaterThan(fcRoot.get(BindingScore_.score), 0));

        criteria.multiselect(
                fcRoot.get(BindingScore_.id),
                fcRoot.get(BindingScore_.gene),
                fcRoot.get(BindingScore_.score),

                pathRoot.get(DatasetViewDTO_.expid),
                pathRoot.get(DatasetViewDTO_.ename),
                pathRoot.get(DatasetViewDTO_.edesc),
                pathRoot.get(DatasetViewDTO_.experimentnumber),

                pathRoot.get(DatasetViewDTO_.tissue),
                pathRoot.get(DatasetViewDTO_.ps),
                pathRoot.get(DatasetViewDTO_.species),

                pathRoot.get(DatasetViewDTO_.dname),
                pathRoot.get(DatasetViewDTO_.ddesc),
                pathRoot.get(DatasetViewDTO_.doi),
                pathRoot.get(DatasetViewDTO_.did),
                pathRoot.get(DatasetViewDTO_.repo)
        ).where(predicates.toArray(new Predicate[]{}));

        List<OmicsMacsPeakDetail> list=em.createQuery(criteria).getResultList();

        if(list.size() > 0)
            return Optional.ofNullable(list.get(0));
        return Optional.empty();
    }

    /**
     * Cache query
     * @param form
     * @param qform
     */
    public void persistQueryId(SimpleQueryForm form, QueryForm qform){
        String id=fileDownloadMemoryCache.cache(form);
        qform.setId(id);
    }

    public SimpleQueryForm getTxSimpleQueryForm(
                                                String geneSearchType,
                                                String gene,
                                                String bsm,
                                                String doi,
                                                String ps, String organ, String tissue,
                                                String species,
                                                String pathwayCategory, String signalingPathway,
                                                String significance, String direction,
                                                String fc, String fcMin, String fcMax, String findMax,
                                                String reportBy, Integer countMax)
            throws Exception {

        Optional<Double> of=this.getDoubleValue(fc);
        Optional<Double> ofMin=this.getDoubleValue(fcMin);
        Optional<Double> ofMax=this.getDoubleValue(fcMax);
        Optional<Double> opValue=this.getDoubleValue(significance);

        boolean isRange=(ofMin.isPresent() && ofMax.isPresent());

        if(isRange && of.isPresent()){
            throw new Exception("Bad Input. Provide either single FC or FC Range");

        }

        Double f=(of.isPresent())?of.get():null;
        Double fMin=(ofMin.isPresent())?ofMin.get():null;
        Double fMax=(ofMax.isPresent())?ofMax.get():null;
        Double pValue=(opValue.isPresent())?opValue.get():null;

        SimpleQueryForm sform=new SimpleQueryForm(TRANSCRIPTOMIC,geneSearchType, gene,doi,
                ps, organ,tissue,species, pathwayCategory, (signalingPathway!=null)?signalingPathway.toString():null,
                pValue,f,fMin,fMax,direction,bsm,null);

        if(findMax != null && findMax.equalsIgnoreCase("y"))
            sform.isFindMax=true;
        sform.isRange=isRange;

        return sform;
    }

    public Map prepTxSimpleQueryForm(boolean isUI,
                                     String geneSearchType,
                                     String gene,
                                     String bsm,
                                     String doi,
                                     String ps, String organ, String tissue,
                                     String species,
                                     String pathwayCategory, String signalingPathway,
                                     String significance, String direction,
                                     String fc, String fcMin, String fcMax,String findMax,
                                     String reportBy,Integer countMax){

        long b=System.currentTimeMillis();

        SimpleQueryForm sform=null;
        QueryParametersData queryParametersData=null;

            try {
                sform = getTxSimpleQueryForm(geneSearchType, gene, bsm, doi, ps, organ, tissue, species, pathwayCategory, signalingPathway
                        , significance, direction, fc, fcMin, fcMax, findMax, reportBy, countMax);

                if(isUI)
                    queryParametersData=this.formOldQueryForm(
                        geneSearchType,gene,doi,ps,organ,tissue,gene,species,
                        pathwayCategory,(signalingPathway!=null)?signalingPathway.toString():null,
                        significance,fc,fcMin,fcMax,bsm);
            }catch (Exception e){
                Map map= new HashMap();
                map.put("error",OmicsServicebean.INPUT_BOTH_FC_AND_FC_RANGE);
                return map;
            }
        //orig simple else sform=new SimpleQueryForm(TRANSCRIPTOMIC,geneSearchType,gene,doi);

        return getQueryParametersMap(isUI,sform,queryParametersData,signalingPathway,pathwayCategory);

    }

    public Map prepCisSimpleQueryForm(boolean isUI, String omics,
                                      String geneSearchType,
                                      String gene, String doi,
                                      String ps, String organ, String tissue,
                                      String species,
                                      String pathwayNodeType, String signalingPathway,
                                      Integer minScore, Integer maxScore, String bsm){

        long b=System.currentTimeMillis();
        SimpleQueryForm sform=null;
        QueryParametersData queryParametersData=null;

            sform = formSimpleQueryForm(omics, geneSearchType, gene, doi,
                    ps, organ, tissue, species, pathwayNodeType, signalingPathway,
                    minScore, maxScore, bsm, null);
        if(isUI) {
            queryParametersData=formOldQueryForm(
                    geneSearchType,gene,doi,ps,organ,tissue,gene,species,
                    pathwayNodeType,(signalingPathway!=null)?signalingPathway.toString():null,
                    null,null,null,null,bsm);

        }
        //init simple else sform=new SimpleQueryForm(omics,geneSearchType,gene,doi,minScore,maxScore);

        return getQueryParametersMap(isUI,sform,queryParametersData,signalingPathway,pathwayNodeType);
    }

    private Map prepSimpleCisReactomeQueryForm(String doi){

        Map map= new HashMap();
        map.put("sform", new SimpleQueryForm(CISTROMIC,doi));
        return map;
    }

    private Map getQueryParametersMap(boolean isUI,SimpleQueryForm sform, QueryParametersData queryParametersData, String signalingPathway, String pathwayCategory){

        long b=System.currentTimeMillis();
        String inValidMsg=null;
        if(isUI)
            inValidRequest(queryParametersData);

        Map map= new HashMap();
        if(inValidMsg != null) {
            map.put("error",inValidMsg);
            return map;
        }

        if(isUI)
            inValidMsg=validatePathwayInput(signalingPathway,pathwayCategory);

        if(inValidMsg!=null) {
            map.put("error",inValidMsg);
            return map;
        }

        //FIXME this only necessary to be backward compatible with the old TX query form format
        QueryForm queryForm= new QueryForm();
        queryForm.setQueryParameter(queryParametersData);

        if(isUI)
            if((System.currentTimeMillis()-b) > 200)
                log.log(Level.WARNING,"SRX/TX prep slow ="+(System.currentTimeMillis()-b));


        map.put("sform",sform);
        map.put("qform",queryForm);
        return map;
    }

    public Map checkCountCriteria(boolean isUI,boolean forApi,boolean hasFilters,QueryType type,SimpleQueryForm sform,QueryForm queryForm,Long startId,Integer countMax){

        long b=System.currentTimeMillis();
        int count=this.countDatapoints(sform,type,forApi,hasFilters,startId);

        long countLimitPerf=(forApi)?5000:1000;
        if((System.currentTimeMillis()-b) > countLimitPerf)
            log.log(Level.WARNING,type+ " criteria count slow : # -> ms "+count+" -> "+(System.currentTimeMillis()-b));

        int absMax=(forApi)?OmicsServicebean.MAX_API_DATAPOINTS:OmicsServicebean.MAX_ABS_NUMBER_DATAPOINTS;

        if(countMax > absMax)
            countMax=absMax;

        Map map= new HashMap();
        if(count == 0 || count > countMax){
            if(isUI) {
                TmQueryResponse<OmicsDatapoint> result =
                        new TmQueryResponse<OmicsDatapoint>(queryForm, new Long(count), null);
                Response resp = Response.ok().entity(result).build();
                map.put("error", resp);
            }
        }
        map.put("count",count);

        return map;
    }

    public String validatePathwayInput(String signalingPathway,String pathwayCategory){

        if(signalingPathway != null){
            if(pathwayCategory == null)
                return INPUT_MISSING_PATHWAY_TYPE;

            PathwayCategory category=PathwayCategory.convert(pathwayCategory);
            if(category == null)
                return INPUT_MISSING_PATHWAY_TYPE;
        }
        return null;
    }

    /**
     * Might be deprecated?
     * do aggregation on pathways, per (Uniq) datapoint
     * @param list
     * @return
     */
    private  List<OmicsDatapoint> aggregatesCisDatapointsByDatapoints(List<OmicsDatapoint> list){

        long b=System.currentTimeMillis();
        Map<Long, List<OmicsDatapoint>> listByDatapoint
                = list.stream().collect(Collectors.groupingBy(OmicsDatapoint::getId));

        log.log(Level.FINE, "# Grouped uniQ datapoints->ms ="+listByDatapoint.size()+"->"+(System.currentTimeMillis()-b));

        List<OmicsDatapoint> datalist= new ArrayList<OmicsDatapoint>();
        OmicsDatapoint data=null;

        Set<String> spns=null;
        for(Map.Entry<Long, List<OmicsDatapoint>> entry:listByDatapoint.entrySet()){
            data=entry.getValue().get(0);
            if(data == null) {
                log.log(Level.WARNING,"No OmicsDatapoint for id: "+entry.getKey());
                continue;
            }
            spns=new HashSet<String>();
            for(OmicsDatapoint cis:entry.getValue())
                spns.add(cis.getTccfTree());

            data.setPathwayNodeTree(String.join(",", spns));

            processAgsInvolvement(data);
            processBsmInvolvementInCisDatapoints(data.getFamilyid(),data);
            processOtherAgsInvolvement(data);

            datalist.add(data);
        }
        if((System.currentTimeMillis()-b)>5000)
            log.log(Level.SEVERE, "#Aggregated datapoints->ms ="+datalist.size()+"->"+(System.currentTimeMillis()-b));

        return datalist;
    }

    /**
     * Report data aggregated by pathway families
     * @param list
     * @return
     */
    private List<OmicsDatapointReport> aggregateCisDatapointsByPathways(List<OmicsDatapoint> list,boolean isUI){

        long b=System.currentTimeMillis();
        Map<Long, List<OmicsDatapoint>> listByPathway
                = list.stream()
                .filter(elem -> elem.getFamilyid() !=null)
                .collect(Collectors.groupingBy(OmicsDatapoint::getFamilyid));

        if(isUI)
            log.log(Level.INFO, "@aggregateCisDatapointsByPathways #Grouped CIS uniQ pathways ="+listByPathway.size());

        List<OmicsDatapoint> datalist= null;
        OmicsDatapoint data=null;
        OmicsDatapointReport report=null;

        List<OmicsDatapointReport> reportByPaths=new ArrayList<OmicsDatapointReport>();
        List<OmicsDatapoint> pointsInPathwayFamilyList=null;
        Long pathid=null;
        for(Map.Entry<Long, List<OmicsDatapoint>> entry:listByPathway.entrySet()) {
            pathid = entry.getKey();

            data = entry.getValue().get(0);
            report = new OmicsDatapointReport();
            report.setPathwayNode(data.getTccfTree());

            pointsInPathwayFamilyList=entry.getValue();
            datalist=new ArrayList<OmicsDatapoint>();
            for(OmicsDatapoint ddata:pointsInPathwayFamilyList){
                ddata.setDatapointType(QueryType.Cistromic);
                processBsmInvolvementInCisDatapoints(pathid,ddata);
                processOtherAgsInvolvement(ddata);
                //7.19.2018 there are no ACM for Cistromics, it seems, but if there are shoudl be treated as ipags
                processAcmInvolvement(ddata);
                datalist.add(ddata);
            }
            if(datalist.size() == 0)
                continue;
            report.getDatapoints().addAll(datalist);
            reportByPaths.add(report);
        }
        if(isUI)
            if((System.currentTimeMillis()-b) >500)
                log.log(Level.WARNING,"SRX byPathway aggregation ONLY slow  #input => ms "+list.size()+" => "+(System.currentTimeMillis()-b));
        return reportByPaths;
    }

    private  List<OmicsDatapoint> aggregatesTxDatapointsByDatapoints(List<OmicsDatapoint> list){

        long b=System.currentTimeMillis();
        Map<Long, List<OmicsDatapoint>> listByDatapoint
                = list.stream().collect(Collectors.groupingBy(OmicsDatapoint::getId));


        List<OmicsDatapoint> datalist= new ArrayList<OmicsDatapoint>();
        OmicsDatapoint data=null;

        Set<String> spns=null;
        List<OmicsDatapoint> byPathsList;
        for(Map.Entry<Long, List<OmicsDatapoint>> entry:listByDatapoint.entrySet()){
            data=entry.getValue().get(0);

            spns=new HashSet<String>();
            for(OmicsDatapoint tx:entry.getValue())
                spns.add(tx.getTccfTree());

            data.setPathwayNodeTree(String.join(",", spns));

            byPathsList=entry.getValue();
            Map<String, List<OmicsDatapoint>> listByBsm
                    = byPathsList.stream()
                    .filter(elem -> elem.getBsm()!=null)
                    .collect(Collectors.groupingBy(OmicsDatapoint::getBsm));

            OmicsDatapoint bdata=null;
            for(Map.Entry<String, List<OmicsDatapoint>> bsmEntry:listByBsm.entrySet()) {
                bdata = bsmEntry.getValue().get(0);
                bdata.setBsm(bsmEntry.getKey());
                bdata.setPathwayNodeTree(bdata.getTccfTree());
                datalist.add(bdata);
            }

            Map<String, List<OmicsDatapoint>> listByAgs
                    = byPathsList.stream()
                    .filter(elem -> elem.getAgs()!=null)
                    .collect(Collectors.groupingBy(OmicsDatapoint::getAgs));

            OmicsDatapoint adata=null;
            for(Map.Entry<String, List<OmicsDatapoint>> agsEntry:listByAgs.entrySet()) {
                adata = agsEntry.getValue().get(0);
                adata.setAgs(agsEntry.getKey());
                adata.setPathwayNodeTree(adata.getTccfTree());
                datalist.add(adata);
            }
        }
        if((System.currentTimeMillis()-b)>3000)
            log.log(Level.INFO, "#Aggregated datapoints->ms ="+datalist.size()+"->"+(System.currentTimeMillis()-b));

        return datalist;
    }

    private  List<OmicsDatapointReport> aggregatesTxDatapointsByPathways(List<OmicsDatapoint> list,boolean isUI){

        long b=System.currentTimeMillis();

        Map<Long, List<OmicsDatapoint>> listByPathway
                = list.stream()
                .filter(elem -> elem.getFamilyid() !=null)
                .collect(Collectors.groupingBy(OmicsDatapoint::getFamilyid));

        if((System.currentTimeMillis()-b) > 100)
            log.log(Level.WARNING,"TX Collectors.groupingBy.getFamilyid slow  #input => time(ms) "+list.size()+" => "+(System.currentTimeMillis()-b));

        if(isUI)
            log.log(Level.INFO, "#Grouped TX uniQ pathways ="+listByPathway.size());

        List<OmicsDatapoint> datalist= null;
        OmicsDatapoint data=null;
        OmicsDatapointReport report=null;

        List<OmicsDatapointReport> reportByPaths=new ArrayList<OmicsDatapointReport>();
        List<OmicsDatapoint> pointsInPathwayFamilyList;
        Long pathid=null;
        for(Map.Entry<Long, List<OmicsDatapoint>> entry:listByPathway.entrySet()){
            data=entry.getValue().get(0);

            log.log(Level.FINE,"pathway="+data.getType()+" source="+data.getNodeSource());

             pathid=entry.getKey();
            report = new OmicsDatapointReport();
            report.setPathwayNode(data.getTccfTree());

            pointsInPathwayFamilyList=entry.getValue();
            long bb=System.currentTimeMillis();
            Map<Long, List<OmicsDatapoint>> listByDatapoint
                    = pointsInPathwayFamilyList.stream().collect(Collectors.groupingBy(OmicsDatapoint::getId));

            if((System.currentTimeMillis()-bb) > 100)
                log.log(Level.WARNING,"  TX groupingBy.getId slow  #input => (ms) "+pointsInPathwayFamilyList.size()+" => "+(System.currentTimeMillis()-bb));

            log.log(Level.FINE,"pathway="+data.getType()+" source="+data.getNodeSource()+ " groupingBy.by datapoints, #  "+listByDatapoint.size());

            OmicsDatapoint adata=null;
            bb=System.currentTimeMillis();
            datalist=new ArrayList<OmicsDatapoint>();
            for(Map.Entry<Long, List<OmicsDatapoint>> dataPointEntry:listByDatapoint.entrySet()) {
                adata = dataPointEntry.getValue().get(0);
                adata.setPathwayNodeTree(null);
                if(!data.getNodeSource().equalsIgnoreCase("models")) {
                    addTxDatapointsBsms(pathid, adata, datalist);
                    addTxDatapointsByAgs(pathid, adata, datalist);
                }else
                    addTxDatapointsByAcm(pathid,adata,datalist);
            }

            if(isUI)
                if((System.currentTimeMillis()-bb) > 500)
                    log.log(Level.WARNING,"  TX addTxDatapoints slow  time(ms) "+(System.currentTimeMillis()-bb));

            if(datalist.size() == 0)
                continue;
            report.getDatapoints().addAll(datalist);
            reportByPaths.add(report);
         }

         if(isUI)
            if((System.currentTimeMillis()-b) > 1000)
                log.log(Level.WARNING,"TX.byPathway.aggregation.ONLY slow  #input => (ms) "+list.size()+" => "+(System.currentTimeMillis()-b));

        return reportByPaths;
    }

    /**
     * Inactive BSM-Node mappings are not meant to show up
     * There 1 BSM->many Nodes
     * How decide if only one is not active?
     * @param bsm
     * @return
     */
    private boolean isThisBsmNodeInactive(Long pathid,String bsm){

        //TODO 8.26.2019 need improve this, eg if BSM inactive, not sure why still care for pathid?
        if(PathwaysNodesMapBean.bsmToNodesInactiveMap.containsKey(bsm)) {
            List<String> pathNodesForThisPath=PathwaysNodesMapBean.familyIdNodeMap.get(pathid);

            List<String> inactives = PathwaysNodesMapBean.bsmToNodesInactiveMap.get(bsm);
            for (String deadNode : inactives) {
                if(pathNodesForThisPath!=null) {
                    if (pathNodesForThisPath.contains(deadNode)) {
                        return true;
                    }
                }else{
                    log.log(Level.FINE,"@isThisBsmNodeInactive.inactive BSM =>"+bsm+" and inactive pathid => "+pathid);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Filter to make sure the BSM( via mapped node) shoudl be appearing under this family
     * @param pathid
     * @param datapoint
     * @param datalist
     */
    private void addTxDatapointsBsms( Long pathid,OmicsDatapoint datapoint,List<OmicsDatapoint> datalist) {

        if (!PathwaysNodesMapBean.familyIdNodeMap.containsKey(pathid.longValue())) {
            log.log(Level.INFO,"    @addTxDatapointsBsms, this pathway family id not mapped, familyId=>"+pathid);
            return;
        }

        if(datapoint.getBsm() == null)
            return;

        if(!isMultipleBsmOrNode(datapoint.getBsm())){
            if(isThisBsmNodeInactive(pathid,datapoint.getBsm())) {
                log.log(Level.FINE,"Inactive BSM used: "+datapoint.getBsm());
                return;
            }
            datapoint.setAgs(null);
            datapoint.setModelNode(null);
            datalist.add(datapoint);
        }else{

            String[] bsms=datapoint.getBsm().split(",");
            //OPTMZTION note, HashSet faster than ArrayList, and removes duplicates
            Set<String> sbsms= new HashSet<String>(Arrays.asList(bsms));
            OmicsDatapoint data;
            List<String> bsmTargetNodes=null;
            for(String bsm:sbsms) {//is this bsm within the pathway?
                bsmTargetNodes= PathwaysNodesMapBean.bsmToNodesMap.get(bsm.toUpperCase());
                if (bsmTargetNodes == null || bsmTargetNodes.size() == 0)
                    continue;

                String[] bsmTargetNodesa = new String[bsmTargetNodes.size()];
                bsmTargetNodesa = bsmTargetNodes.toArray(bsmTargetNodesa);
                for(String node:bsmTargetNodesa) {
                    List<String> pathNodesList=PathwaysNodesMapBean.familyIdNodeMap.get(pathid);
                    if (!pathNodesList.contains(node))
                        continue;

                    if(isThisBsmNodeInactive(pathid,bsm))
                        break;

                    data=new OmicsDatapoint(datapoint.getId(),datapoint.getSymbol(),
                            datapoint.getProb(),datapoint.getProbType(),
                            datapoint.getFoldChangeRaw(),datapoint.getPvalue(),
                                null, null, null,null,
                            datapoint.getTissue(), datapoint.getOrgan(), datapoint.getSpeciesCommonName(),
                            datapoint.getExperimentId(),
                            datapoint.getExperimentName(),datapoint.getExperimentNumber(), datapoint.getDatasetDoi(),
                            datapoint.getType(), datapoint.getCategory(),datapoint.getCclass(), datapoint.getFamily(), datapoint.getFamilyid(),
                            datapoint.getNodeSource(),  datapoint.getPsOrgan());

                    data.setBsm(bsm);
                    datalist.add(data);
                    break;
                }
            }
        }
    }


    private boolean isMultipleBsmOrNode(String bsmOrNode){
        return bsmOrNode.contains(",");
    }

    /**
     * TODO process ACM based nodes
     * @param pathid
     * @param datapoint
     * @param datalist
     */
    private void addTxDatapointsByAcm(Long pathid,OmicsDatapoint datapoint,List<OmicsDatapoint> datalist){
        //log.log(Level.WARNING,"TODO TX AMC based family nodes additions");
        addTxDatapointsByANode(datapoint.getModelNode(),1,pathid,datapoint,datalist);
    }

    /**
     * Process normal (Ags based) nodes
     * @param pathid
     * @param datapoint
     * @param datalist
     */
    private void addTxDatapointsByAgs(Long pathid,OmicsDatapoint datapoint,List<OmicsDatapoint> datalist){
        addTxDatapointsByANode(datapoint.getAgs(),0,pathid,datapoint,datalist);
    }

    private void addTxDatapointsByANode(String snode,int nodeType,Long pathid,OmicsDatapoint datapoint,List<OmicsDatapoint> datalist) {

        if(snode == null)
            return;

        if(!isMultipleBsmOrNode(snode)){
            if(nodeType == 0) {
                datapoint.setAgs(snode);
                datapoint.setModelNode(null);
            }
            if(nodeType == 1) {
                datapoint.setModelNode(snode);
                datapoint.setAgs(null);
            }
            datapoint.setBsm(null);
            datalist.add(datapoint);
        }else{
            String[] snodes=snode.split(",");
            Set<String> nodes= new HashSet<String>(Arrays.asList(snodes));
            OmicsDatapoint data;
            for(String node:nodes) {

                List<String> pathNodesList=PathwaysNodesMapBean.familyIdNodeMap.get(pathid);
                if(pathNodesList ==null){
                    log.log(Level.SEVERE,"@addTxDatapointsByANode when processing familyid,  found that it is not mapped to a node, ignoring :" +pathid);
                    continue;
                }

                if (!pathNodesList.contains(node))
                    continue;

                data=new OmicsDatapoint(datapoint.getId(),datapoint.getSymbol(),
                        datapoint.getProb(),datapoint.getProbType(),
                        datapoint.getFoldChangeRaw(),datapoint.getPvalue(),
                        null, null, null,null,
                        datapoint.getTissue(), datapoint.getOrgan(), datapoint.getSpeciesCommonName(),
                        datapoint.getExperimentId(),
                        datapoint.getExperimentName(),datapoint.getExperimentNumber(), datapoint.getDatasetDoi(),
                        datapoint.getType(), datapoint.getCategory(),datapoint.getCclass(), datapoint.getFamily(), datapoint.getFamilyid(),
                        datapoint.getNodeSource(), datapoint.getPsOrgan());

                if(nodeType == 0)
                    data.setAgs(node);
                if(nodeType == 1)
                    data.setModelNode(node);
                datalist.add(data);
            }
        }
    }

    /**
     * Need id,macs2/fc/pvalue,biosample,type,category,class,family,node
     * @param criteria
     * @param dataPath
     * @param pathRoot
     * @param nodesRoot
     * @param predicates
     * @param queryType
     */
    private void setSelectorsForApi(CriteriaQuery<OmicsDatapoint> criteria,
                              Root<?> dataPath,
                              Root<DatasetViewDTO> pathRoot,
                              Root<DatasetNodesView> nodesRoot,
                              List<Predicate> predicates,QueryType queryType){

        if(queryType == QueryType.Cistromic) {
            Root<BindingScore> scoreRoot=(Root<BindingScore>)dataPath;
            criteria.multiselect(
                    scoreRoot.get(BindingScore_.id),
                    scoreRoot.get(BindingScore_.gene),
                    scoreRoot.get(BindingScore_.score),

                    pathRoot.get(DatasetViewDTO_.tissue),
                    nodesRoot.get(DatasetNodesView_.psorgan),

                    pathRoot.get(DatasetViewDTO_.type),
                    pathRoot.get(DatasetViewDTO_.category),
                    pathRoot.get(DatasetViewDTO_.cclass),
                    pathRoot.get(DatasetViewDTO_.family),
                    pathRoot.get(DatasetViewDTO_.familyid),

                    nodesRoot.get(DatasetNodesView_.bsm),
                    nodesRoot.get(DatasetNodesView_.ags),
                    nodesRoot.get(DatasetNodesView_.oags))
                    .where(predicates.toArray(new Predicate[]{}));
        }else{

            Root<FoldChangeView> fcRoot=(Root<FoldChangeView>)dataPath;
            criteria.multiselect(
                    fcRoot.get(FoldChangeView_.id),
                    fcRoot.get(FoldChangeView_.gene),

                    fcRoot.get(FoldChangeView_.foldChange),
                    fcRoot.get(FoldChangeView_.pValue),

                    pathRoot.get(DatasetViewDTO_.tissue),
                    nodesRoot.get(DatasetNodesView_.psorgan),

                    pathRoot.get(DatasetViewDTO_.type),
                    pathRoot.get(DatasetViewDTO_.category),
                    pathRoot.get(DatasetViewDTO_.cclass),
                    pathRoot.get(DatasetViewDTO_.family),
                    pathRoot.get(DatasetViewDTO_.familyid),

                    nodesRoot.get(DatasetNodesView_.bsm),
                    nodesRoot.get(DatasetNodesView_.ags),
                    nodesRoot.get(DatasetNodesView_.oags))
                    .where(predicates.toArray(new Predicate[]{}));
        }

    }

    private void setSelectors(CriteriaQuery<OmicsDatapoint> criteria,
                                  Root<?> dataPath,
                                  Root<DatasetViewDTO> pathRoot,
                              Root<DatasetNodesView> nodesRoot,
                                  List<Predicate> predicates,QueryType queryType,boolean hasFilters){

        if(queryType == QueryType.Cistromic) {
            Root<BindingScore> scoreRoot=(Root<BindingScore>)dataPath;
            criteria.multiselect(
                    scoreRoot.get(BindingScore_.id),
                    scoreRoot.get(BindingScore_.gene),
                    scoreRoot.get(BindingScore_.score),
                    nodesRoot.get(DatasetNodesView_.bsm),
                    nodesRoot.get(DatasetNodesView_.ags),
                    nodesRoot.get(DatasetNodesView_.oags),
                    nodesRoot.get(DatasetNodesView_.model),
                    pathRoot.get(DatasetViewDTO_.tissue),
                    pathRoot.get(DatasetViewDTO_.organ),
                    pathRoot.get(DatasetViewDTO_.species),
                    pathRoot.get(DatasetViewDTO_.internalexperimentid),
                    pathRoot.get(DatasetViewDTO_.ename),
                    pathRoot.get(DatasetViewDTO_.experimentnumber),
                    pathRoot.get(DatasetViewDTO_.doi),
                    pathRoot.get(DatasetViewDTO_.type),
                    pathRoot.get(DatasetViewDTO_.category),
                    pathRoot.get(DatasetViewDTO_.cclass),
                    pathRoot.get(DatasetViewDTO_.family),
                    pathRoot.get(DatasetViewDTO_.familyid),
                    pathRoot.get(DatasetViewDTO_.nodeSource),
                    nodesRoot.get(DatasetNodesView_.psorgan))
                    .where(predicates.toArray(new Predicate[]{}));
        }else{
            if(hasFilters) {
                Root<FoldChangeView> fcRoot = (Root<FoldChangeView>) dataPath;
                criteria.multiselect(
                        fcRoot.get(FoldChangeView_.id),
                        fcRoot.get(FoldChangeView_.gene),
                        fcRoot.get(FoldChangeView_.prob),
                        fcRoot.get(FoldChangeView_.probType),
                        fcRoot.get(FoldChangeView_.foldChange),
                        fcRoot.get(FoldChangeView_.pValue),
                        nodesRoot.get(DatasetNodesView_.bsm),
                        nodesRoot.get(DatasetNodesView_.ags),
                        nodesRoot.get(DatasetNodesView_.oags),
                        nodesRoot.get(DatasetNodesView_.model),
                        pathRoot.get(DatasetViewDTO_.tissue),
                        pathRoot.get(DatasetViewDTO_.organ),
                        pathRoot.get(DatasetViewDTO_.species),
                        pathRoot.get(DatasetViewDTO_.internalexperimentid),
                        pathRoot.get(DatasetViewDTO_.ename),
                        pathRoot.get(DatasetViewDTO_.experimentnumber),
                        pathRoot.get(DatasetViewDTO_.doi),

                        pathRoot.get(DatasetViewDTO_.type),
                        pathRoot.get(DatasetViewDTO_.category),
                        pathRoot.get(DatasetViewDTO_.cclass),
                        pathRoot.get(DatasetViewDTO_.family),

                        pathRoot.get(DatasetViewDTO_.familyid),
                        pathRoot.get(DatasetViewDTO_.nodeSource),
                        nodesRoot.get(DatasetNodesView_.psorgan))
                        .where(predicates.toArray(new Predicate[]{}));
            }else{
                Root<FoldChangeViewForApi> fcRoot = (Root<FoldChangeViewForApi>) dataPath;
                criteria.multiselect(
                        fcRoot.get(FoldChangeViewForApi_.id),
                        fcRoot.get(FoldChangeViewForApi_.gene),
                        fcRoot.get(FoldChangeViewForApi_.prob),
                        fcRoot.get(FoldChangeViewForApi_.probType),
                        fcRoot.get(FoldChangeViewForApi_.foldChange),
                        fcRoot.get(FoldChangeViewForApi_.pValue),
                        nodesRoot.get(DatasetNodesView_.bsm),
                        nodesRoot.get(DatasetNodesView_.ags),
                        nodesRoot.get(DatasetNodesView_.oags),
                        nodesRoot.get(DatasetNodesView_.model),
                        pathRoot.get(DatasetViewDTO_.tissue),
                        pathRoot.get(DatasetViewDTO_.organ),
                        pathRoot.get(DatasetViewDTO_.species),
                        pathRoot.get(DatasetViewDTO_.internalexperimentid),
                        pathRoot.get(DatasetViewDTO_.ename),
                        pathRoot.get(DatasetViewDTO_.experimentnumber),
                        pathRoot.get(DatasetViewDTO_.doi),

                        pathRoot.get(DatasetViewDTO_.type),
                        pathRoot.get(DatasetViewDTO_.category),
                        pathRoot.get(DatasetViewDTO_.cclass),
                        pathRoot.get(DatasetViewDTO_.family),

                        pathRoot.get(DatasetViewDTO_.familyid),
                        pathRoot.get(DatasetViewDTO_.nodeSource),
                        nodesRoot.get(DatasetNodesView_.psorgan))
                        .where(predicates.toArray(new Predicate[]{}));
            }
        }
    }



    /**
     * Ags field has  multiple data encoded
     * @param ags
     * @return
     */
    private Map<String, String> getAGSKeys(String ags){
        if(ags == null)
            return new HashMap<String,String>();
        return  Arrays.asList(ags.split(",")).stream()
                .filter(elem -> elem.length() > 0)
                .map(e->e).distinct()
                .collect(Collectors.toMap(e -> e, e -> e));
    }

    /**
     * Bsm from SRX Etl may have lower case (ought to have be error),
     * eg Tam, while BSm->Node maps have BSM as upper case
     * If we inferred AGS from BSM mapping, do not exclude BSM from BSM listing
     * @param data
     */
    private void processBsmInvolvementInCisDatapoints(Long pathid, OmicsDatapoint data){

        Map<String, String> agsKeys =getAGSKeys(data.getAgs());

        if(data.getBsm() == null || data.getBsm().trim().length() == 0)
            return ;

        List<String> bsmToTargetNodes;
        ArrayList<String> targetBsm=new ArrayList<String>();
        for(String bsm:data.getBsm().trim().split(",")){
            bsmToTargetNodes= PathwaysNodesMapBean.bsmToNodesMap.get(bsm.toUpperCase());
            if(bsmToTargetNodes == null || bsmToTargetNodes.size()==0){
                log.log(Level.SEVERE, "BSM with unKnown node mapping found:="+bsm);
                continue;
            }

            if(isThisBsmNodeInactive(pathid,bsm))
                continue;

            if(toIncludeBsmBasedOnNodeOrOtherAgs(bsmToTargetNodes,agsKeys,data.isAgsInferred()))
                    targetBsm.add(bsm.toUpperCase());
        }
        data.getTargetBsm().addAll(targetBsm);
    }

    /**
     * Process AGS involvement, using generic map if AGS not manually mapped
     * Case of old TX not have AGS specified
     * @param data
     */
    private void processAgsInvolvement(OmicsDatapoint data){

        if(data.getBsm() == null || data.getBsm().trim().length() == 0)
            return ;

        if(data.getAgs() != null && data.getAgs().trim().length() != 0)
            return;

       List<String> bsmToTargetNodes;
        ArrayList<String> bsmNodes=new ArrayList<String>();
        for(String bsm:data.getAgs().trim().split(",")){
            bsmToTargetNodes= PathwaysNodesMapBean.bsmToNodesMap.get(bsm.toUpperCase());
            if(bsmToTargetNodes == null || bsmToTargetNodes.size() == 0){
                log.log(Level.SEVERE, "BSM with unKnown Node mapping :="+bsm);
                continue;
            }
            bsmNodes.addAll(bsmToTargetNodes);
        }
        data.setAgs(String.join(",", bsmNodes));
        data.setAgsInferred(true);

        //data.setAgs();

    }

    private void processOtherAgsInvolvement(OmicsDatapoint data){
        Map<String, String> agsKeys =getAGSKeys(data.getAgs());

        if(data.getOags() == null || data.getOags().trim().length() == 0)
            return ;

        List<String> oagsList=Arrays.asList(data.getOags().split(",")).stream()
                .filter(elem -> elem.length()>0)
                .map(e->e).distinct()
                .collect(Collectors.toList());

        ArrayList<String> targetOtherAgs=new ArrayList<String>();
        for(String oag:oagsList){
                if(!agsKeys.containsKey(oag.trim()))
                    targetOtherAgs.add(oag.trim());
        }

        data.setTargetOags(targetOtherAgs);
    }

    /**
     * ACM, diseases mapping
     * Treat as do OtherAGS
     * @param data
     */
    private void processAcmInvolvement(OmicsDatapoint data){
        //log.log(Level.WARNING,"TODO SRX AMC based family nodes additions");
        //Seems similar to ags, so donothing?
    }

    /**
     * Rule: only include BSM if it's mapped  Node is NOT already an AGS for this experiment
     * Inferred nodes, ie pre-manual curation of old TX, is excluded
     * @param bsmTargetNodeOrOtherAGS
     * @param agsKeys
     * @return
     */
    private boolean toIncludeBsmBasedOnNodeOrOtherAgs(List<String> bsmTargetNodeOrOtherAGS,Map<String, String> agsKeys,boolean agsInferred){

        if(agsInferred)
            return true;

        for(String node:bsmTargetNodeOrOtherAGS){
            if(!agsKeys.containsKey(node))
                return true;
        }
        return false;
    }


    private <T> void setQueryCriteria(CriteriaQuery<T> criteria,
                                      Root<?> dataPath,
                                      Root<DatasetViewDTO> pathRoot,
                                      Root<DatasetNodesView> nodesRoot,
                                      List<Predicate> predicates,
                                      SimpleQueryForm form,
                                      QueryType queryType,
                                      boolean forApi,boolean hasFilters,Long startId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        if(form.doi != null && !form.doi.equalsIgnoreCase("")){
            predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.doi), form.doi));
        }

        if (queryType == QueryType.Cistromic) {
            Root<BindingScore> scoreRoot=(Root<BindingScore>)dataPath;
            if(hasFilters)
                setBindingScoreGeneLimits(criteria,scoreRoot,predicates,form);
            if(forApi && startId > 0L)
                predicates.add(cb.ge(scoreRoot.get(BindingScore_.id), startId));

            setDefaultLimits(cb, criteria, dataPath,pathRoot, nodesRoot, predicates, queryType);
        }else {
            Root<FoldChangeView> fcRoot=(Root<FoldChangeView>)dataPath;
            if(hasFilters) {
                setExpressionDatapointGeneLimits(criteria, fcRoot, predicates, form);
                setFoldChangeLimits(fcRoot, predicates, form);
            }
            setSignificanceLimits(fcRoot,predicates,form.significance);
            if(forApi && startId > 0L)
                predicates.add(cb.ge(fcRoot.get(FoldChangeView_.id), startId));

            setDefaultLimits(cb, criteria, dataPath,pathRoot, nodesRoot, predicates, queryType);
        }

        //TODO what if both < if(form.omicsType != null){
        String dtype=queryType.toString();
        predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.dtype),dtype ));

        //TODO test BSM based call, but also consider kind of identifier (pubchem,iuphar,chebi,SPP name,name?)
        if(form.bsm != null){
            predicates.add(cb.equal(nodesRoot.get(DatasetNodesView_.bsm), form.bsm));
        }

        if(form.species != null &&
                (!form.species.equalsIgnoreCase("all") && !form.species.equalsIgnoreCase("-1"))){

            Long id=PathwaysNodesMapBean.speciesMap.get(form.species);
            if(id == null)
                if(PathwaysNodesMapBean.speciesCommonTerms.containsKey(form.species.toLowerCase()))
                    id=PathwaysNodesMapBean.speciesCommonTerms.get(form.species.toLowerCase());

            id=(id != null)?id:0L;
                predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.speciesid), id));
        }

        Optional<Long> pathid=getKeylet(form.signalingPathway);
        Optional<Long> organid=getKeylet(form.organ);
        Optional<Long> tissueid=getKeylet(form.tissue);
        Optional<Long> psid=getKeylet(form.ps);

        if(form.ps != null){
            if(psid.isPresent() && psid.get() != -1L)
                predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.psid), psid.get()));
            else
                predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.ps), form.ps));
        }
        if(form.organ != null ){
            if( organid.isPresent() && organid.get() != -1L)
                predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.organid), organid.get()));
            else
                predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.organ), form.organ));
        }

        if(form.tissue != null && tissueid.isPresent() && tissueid.get() != -1L){
            predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.tissueId), tissueid.get()));
        }

        if(form.signalingPathway != null && pathid.isPresent() && pathid.get() != -1L){
            PathwayCategory category=PathwayCategory.convert(form.pathwayCategory);
            if(category != null)
                switch(category){
                    case type:
                        predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.typeid), pathid.get()));
                        break;
                    case category:
                        predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.catid), pathid.get()));
                        break;
                    case cclass:
                        predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.classid), pathid.get()));
                        break;
                    case family:
                        predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.familyid), pathid.get()));
                        break;
                }
        }
    }

    /**
     * if none, ignore
     * If !none || !any,  but we filtered out NULL, else add pvalue is NULL to query
     * @param fcRoot
     * @param predicates
     * @param significance
     * @param <T>
     */
    private <T> void setSignificanceLimits(Root<FoldChangeView> fcRoot,
                                           List<Predicate> predicates,
                                           Double significance){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        if(significance != null ){
            predicates.add(cb.le(fcRoot.get(FoldChangeView_.pValue), significance));
        }else{
            predicates.add(cb.le(fcRoot.get(FoldChangeView_.pValue), (Double)QueryDefolt.significance.getDefolt()));
        }
    }

    /**
     * Consider directions
     * @param fcRoot
     * @param predicates
     * @param form
     * @param <T>
     */
    private <T> void setFoldChangeLimits(Root<FoldChangeView> fcRoot,
                                         List<Predicate> predicates,
                                         SimpleQueryForm form){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        if(form.foldChange == null)
            form.foldChange=(Double)QueryDefolt.foldchange.getDefolt();

        if(form.direction == null){
            form.direction=(String)QueryDefolt.direction.getDefolt();
        }

        switch(form.direction){
            case "up":
                if(!form.isRange)
                    predicates.add(cb.ge(fcRoot.get(FoldChangeView_.foldChange), form.foldChange));
                else {
                    predicates.add(cb.ge(fcRoot.get(FoldChangeView_.foldChange), form.foldChangeMin));
                    predicates.add(cb.le(fcRoot.get(FoldChangeView_.foldChange), form.foldChangeMax));
                }
                break;
            case "down":
                if(!form.isRange)
                    predicates.add(cb.le(fcRoot.get(FoldChangeView_.foldChange), 1/form.foldChange));
                else{
                    predicates.add(cb.le(fcRoot.get(FoldChangeView_.foldChange), 1/form.foldChangeMin));
                    predicates.add(cb.ge(fcRoot.get(FoldChangeView_.foldChange), 1/form.foldChangeMax));
                }
                break;
            case "any":
            default:
                if(!form.isRange) {
                    Predicate dis = cb.disjunction();
                    Predicate p1=cb.ge(fcRoot.get(FoldChangeView_.foldChange), form.foldChange);
                    Predicate p2=cb.le(fcRoot.get(FoldChangeView_.foldChange), 1/form.foldChange);
                    dis=cb.or(p1,p2);
                    predicates.add(dis);
                }else{
                    Predicate con1 = cb.conjunction();
                    Predicate p1 = cb.ge(fcRoot.get(FoldChangeView_.foldChange), form.foldChangeMin);
                    Predicate p2 = cb.le(fcRoot.get(FoldChangeView_.foldChange), form.foldChangeMax);
                    con1=cb.and(p1,p2);

                    Predicate con2 = cb.conjunction();
                    Predicate p3 = cb.le(fcRoot.get(FoldChangeView_.foldChange), 1/form.foldChangeMin);
                    Predicate p4 = cb.ge(fcRoot.get(FoldChangeView_.foldChange), 1/form.foldChangeMax);
                    con2=cb.and(p3,p4);

                    Predicate dis = cb.disjunction();
                    dis=cb.or(con1,con2);
                    predicates.add(dis);
                }
        }
    }

    /**
     * In no fc, set default
     * if no pvalue, use default
     * add direction defaults
     * If has fcmin/fcmax, use those
     * respect fc limitations
     * maxPoint fc for given query
     * @param criteria
     * @param fcRoot
     * @param predicates
     * @param form
     * @param <T>
     */
    private <T> void setExpressionDatapointGeneLimits(CriteriaQuery<T> criteria,
                                                      Root<FoldChangeView> fcRoot,
                                                      List<Predicate> predicates,
                                                      SimpleQueryForm form){
        CriteriaBuilder cb = em.getCriteriaBuilder();

        if(form.geneSearchType ==null)
            return;


        boolean all=true;
        if(form.species != null &&
                (!form.species.equalsIgnoreCase("all") && !form.species.equalsIgnoreCase("-1"))){
            all=false;
        }

        if (form.geneSearchType.equals(CistromicsRestService.SEARCH_TYPE_SINGLE_GENE_EG)) {
            Long egeneid = Long.parseLong(form.gene);
            predicates.add(cb.equal(fcRoot.get(FoldChangeView_.egeneid), egeneid));
        }

        if (form.geneSearchType.equals(CistromicsRestService.SEARCH_TYPE_SINGLE_GENE)) {
            if (form.gene != null) {
                if (!isGeneId(form.gene)) {
                    if(all) {
                        //predicates.add(cb.equal(cb.lower(fcRoot.get(FoldChangeView_.gene)), form.gene.toLowerCase()));
                        Predicate dis = cb.disjunction();
                        String symbol=new StringBuilder(form.gene.substring(0, 1).toUpperCase()).append(form.gene.substring(1).toLowerCase()).toString();
                        Predicate p1=cb.equal(fcRoot.get(FoldChangeView_.gene), form.gene);
                        Predicate p2=cb.equal(fcRoot.get(FoldChangeView_.gene), symbol);
                        dis=cb.or(p1,p2);
                        predicates.add(dis);
                    }else {
                        //SYMBOL is always HGNC,
                        //?protect users from themselves, if they selected symbol/species mismatched
                        String symbol=form.gene;
                        if(isNotHuman(form.species)) {
                            symbol=symbol.toLowerCase();
                            symbol=new StringBuilder(symbol.substring(0, 1).toUpperCase()).append(symbol.substring(1).toLowerCase()).toString();
                        }
                        predicates.add(cb.equal(fcRoot.get(FoldChangeView_.gene), symbol));
                    }
                }else {
                    Long geneid = Long.parseLong(form.gene);
                    Root<GeneInfoEtl> geneRoot=criteria.from(GeneInfoEtl.class);
                    predicates.add(cb.equal(geneRoot.get(GeneInfoEtl_.geneid), geneid));
                    predicates.add(cb.equal(geneRoot.get(GeneInfoEtl_.symbol), fcRoot.get(FoldChangeView_.gene)));
                }
            }
        }

        //geneList, pick first 1000, only? TX v3.* only allowed  HGNC Human  in gene List. why?
        //this methods is more direct than going thru molsynon.id (v3.*)
        //TODO change if eGeneId is allowed in list-then needs find  symbol
        if (form.geneSearchType.equalsIgnoreCase(CistromicsRestService.SEARCH_TYPE_GENE_LIST)) {
            if (form.gene != null) {
                List<String> geneList = Arrays.asList(form.gene.split("\\s*,\\s*"));
                //log.log(Level.FINE, "list=" + geneList.toString());
                if (geneList.size() > 1000) {
                    geneList = geneList.stream()
                            .limit(1000)
                            .collect(Collectors.toList());
                }
                Expression<String> exp =null;
                if(all) {
                    Predicate dis = cb.disjunction();
                    exp = fcRoot.get(FoldChangeView_.gene);

                    List<String> genes=geneList.stream()
                            .map(String::toLowerCase)
                            .map(e->new StringBuilder(e.substring(0, 1).toUpperCase()).append(e.substring(1).toLowerCase()).toString())
                            .collect(Collectors.toList());

                    log.log(Level.FINE,"GeneList="+Arrays.toString(geneList.toArray()));

                    Predicate p1=exp.in(genes);
                    Predicate p2=exp.in(geneList);
                    dis=cb.or(p1,p2);
                    predicates.add(dis);

                }else{
                    exp = fcRoot.get(FoldChangeView_.gene);
                    predicates.add(exp.in(geneList));
                }
            }
        }

        //goTerm doing cartesian (move to  5.2, so that  join instead)  cartesian
        if (form.geneSearchType.equals(CistromicsRestService.SEARCH_TYPE_GO_TERM)) {
            if (form.gene != null) {
                /*Root<GoTermBySymbolView> goRoot = criteria.from(GoTermBySymbolView.class);
                predicates.add(cb.equal(goRoot.get(GoTermBySymbolView_.termname), form.gene));
                predicates.add(cb.equal(goRoot.get(GoTermBySymbolView_.name), fcRoot.get(FoldChangeView_.gene)));
                */
                //FIXME temp solution
                long s=System.currentTimeMillis();
                List<GoTermBySymbolView> geneList=getGoSymbols(form.gene);
                log.log(Level.WARNING,"Temp solution in play for GO queries. #GO/ms= "+geneList.size()+"/"+(System.currentTimeMillis()-s));
                Expression<String> exp = fcRoot.get(FoldChangeView_.gene);
                Predicate p1=exp.in(geneList.stream()
                        .map(GoTermBySymbolView::getName)
                        .collect(Collectors.toList()));
                predicates.add(p1);
            }
        }
    }

    private boolean isNotHuman(String species){
        return (PathwaysNodesMapBean.speciesMap.get(species) != 13L);
    }

    private List<GoTermBySymbolView> getGoSymbols(String term){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GoTermBySymbolView> criteria = cb.createQuery(GoTermBySymbolView.class);
        Root<GoTermBySymbolView> root = criteria.from(GoTermBySymbolView.class);

        criteria.select(root).where(cb.equal(root.get(GoTermBySymbolView_.termname),term));

        return em.createQuery(criteria).setMaxResults(MAX_GO_TERMS_VIA_TEMP_PRE_QUERY).getResultList();

    }
    private  <T> void setBindingScoreGeneLimits(CriteriaQuery<T> criteria,
                                                Root<BindingScore> scoreRoot,
                                                List<Predicate> predicates,
                                                SimpleQueryForm form){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        if (form.minScore != null && form.minScore > CistromicsRestService.MINIMUM_BINDING_SCORE)
            predicates.add(cb.greaterThanOrEqualTo(scoreRoot.get(BindingScore_.score), form.minScore));
        else
            predicates.add(cb.greaterThan(scoreRoot.get(BindingScore_.score), 0));

        if (form.maxScore != null && form.maxScore > 0)
            predicates.add(cb.lessThanOrEqualTo(scoreRoot.get(BindingScore_.score), form.maxScore));

        boolean all=true;
        if(form.species != null &&
                (!form.species.equalsIgnoreCase("all") && !form.species.equalsIgnoreCase("-1"))){
            all=false;
        }

        if(form.geneSearchType == null)
            return;

        if (form.geneSearchType.equals(CistromicsRestService.SEARCH_TYPE_SINGLE_GENE_EG)) {
            Long egeneid = Long.parseLong(form.gene);
            Root<GeneInfoEtl> geneInfoRoot = criteria.from(GeneInfoEtl.class);
            predicates.add(cb.equal(geneInfoRoot.get(GeneInfoEtl_.geneid), egeneid));
            predicates.add(cb.equal(geneInfoRoot.get(GeneInfoEtl_.symbol), scoreRoot.get(BindingScore_.gene)));
        }

        if (form.geneSearchType.equals(CistromicsRestService.SEARCH_TYPE_SINGLE_GENE)) {
            if (form.gene != null) {
                if (!isGeneId(form.gene)) {
                    if(all) {
                        //TODO use shadow upper_gene field
                        //predicates.add(cb.equal(cb.lower(scoreRoot.get(BindingScore_.gene)), form.gene.toLowerCase()));
                        Predicate dis = cb.disjunction();
                        String symbol= new StringBuilder(form.gene.substring(0, 1).toUpperCase()).append(form.gene.substring(1).toLowerCase()).toString();
                        Predicate p1=cb.equal(scoreRoot.get(BindingScore_.gene), form.gene);
                        Predicate p2=cb.equal(scoreRoot.get(BindingScore_.gene),  symbol );
                        dis=cb.or(p1,p2);
                        predicates.add(dis);
                    }else {
                        //SYMBOL is always HGNC
                        //?protect users from themselves, if they selected symbol/species mismatched
                        String symbol=form.gene;
                        if(isNotHuman(form.species)) {
                            symbol=symbol.toLowerCase();
                            symbol=new StringBuilder(symbol.substring(0, 1).toUpperCase()).append(symbol.substring(1).toLowerCase()).toString();
                        }
                        predicates.add(cb.equal(scoreRoot.get(BindingScore_.gene), symbol));
                    }
                }else {
                    Long geneid = Long.parseLong(form.gene);
                    Root<GeneInfoEtl> geneRoot=criteria.from(GeneInfoEtl.class);
                    predicates.add(cb.equal(geneRoot.get(GeneInfoEtl_.geneid), geneid));
                    predicates.add(cb.equal(geneRoot.get(GeneInfoEtl_.symbol), scoreRoot.get(BindingScore_.gene)));
                }
            }
        }

        //geneList, pick first 1000, only? TX v3.* only allowed  HGNC Human  in gene List. why?
        //this methods is more direct than going thru molsynon.id (v3.*)
        //TODO change if eGeneId is allowed in list THEN will needs find  symbol
        if (form.geneSearchType.equalsIgnoreCase(CistromicsRestService.SEARCH_TYPE_GENE_LIST)) {
            if (form.gene != null) {
                List<String> geneList = Arrays.asList(form.gene.split("\\s*,\\s*"));
                //log.log(Level.FINE, "list=" + geneList.toString());
                if (geneList.size() > 1000) {
                    geneList = geneList.stream()
                            .limit(1000)
                            .collect(Collectors.toList());
                }
                Expression<String> exp = null;
                if(all) {
                    //TODO test this
                    Predicate dis = cb.disjunction();
                    exp = scoreRoot.get(BindingScore_.gene);
                    Predicate p1=exp.in(geneList.stream()
                            .map(String::toLowerCase)
                            .map(e->new StringBuilder(e.substring(0, 1).toUpperCase()).append(e.substring(1).toLowerCase()).toString())
                            .collect(Collectors.toList()));
                    Predicate p2=exp.in(geneList);
                    dis=cb.or(p1,p2);
                    predicates.add(dis);

                }else{
                    exp = scoreRoot.get(BindingScore_.gene);
                    predicates.add(exp.in(geneList));
                }
            }
        }

        //goTerm doing cartesian (with 5.2, consider join) instead cartesian
        //goTerm already resolve to species specific
        if (form.geneSearchType.equals(CistromicsRestService.SEARCH_TYPE_GO_TERM)) {
            if (form.gene != null) {
                /*Root<GoTermBySymbolView> goRoot = criteria.from(GoTermBySymbolView.class);
                predicates.add(cb.equal(goRoot.get(GoTermBySymbolView_.termname), form.gene));
                predicates.add(cb.equal(goRoot.get(GoTermBySymbolView_.name), scoreRoot.get(BindingScore_.gene)));
                */

                //FIXME this is a temp solution
                long s=System.currentTimeMillis();
                List<GoTermBySymbolView> geneList=getGoSymbols(form.gene);
                log.log(Level.WARNING,"Temp solution in play for GO queries. #GO/ms= "+geneList.size()+"/"+(System.currentTimeMillis()-s));
                Expression<String> exp = scoreRoot.get(BindingScore_.gene);
                Predicate p1=exp.in(geneList.stream()
                        .map(GoTermBySymbolView::getName)
                        .collect(Collectors.toList()));
                predicates.add(p1);
            }
        }
    }

    private <T> void setDefaultLimits(CriteriaBuilder cb,CriteriaQuery<T> criteria,
                                      Root<?> dataPath,
                                      Root<DatasetViewDTO> pathRoot,
                                      Root<DatasetNodesView> nodesRoot,
                                      List<Predicate> predicates,QueryType queryType){

        //pre Hib 5.1 join unrelated entities via cartesian join (ie x join) of scoreRoot/pathroot, limit in where clause
        Root<BindingScore> scoreRoot=null;
        Root<FoldChangeView> fcRoot=null;
        if(queryType == QueryType.Cistromic) {
            scoreRoot=(Root<BindingScore>)dataPath;
            predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.expid), scoreRoot.get(BindingScore_.experiment).get(Experiment_.id)));
        }else{
            fcRoot=(Root<FoldChangeView>)dataPath;
            predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.expid), fcRoot.get(FoldChangeView_.expid)));
        }

        predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.expid), nodesRoot.get(DatasetNodesView_.id).get(NodesPkey_.experimentid)));

        if(queryType == QueryType.Cistromic) {
            predicates.add(cb.greaterThan(scoreRoot.get(BindingScore_.score), CistromicsRestService.MINIMUM_BINDING_SCORE));
            //only ipags based nodes for CIS(why??), ipags/bsm for TX.

            //v1 predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.nodeSource),CistromicsRestService.NODE_SOURCE_IPAGS));
            //TODO v2 incude ipags or ACM/D
            Predicate dis = cb.disjunction();
            Predicate p1=cb.equal(pathRoot.get(DatasetViewDTO_.nodeSource),CistromicsRestService.NODE_SOURCE_IPAGS);
            Predicate p2=cb.equal(pathRoot.get(DatasetViewDTO_.nodeSource),CistromicsRestService.NODE_SOURCE_MODEL);
            dis=cb.or(p1,p2);
            predicates.add(dis);
        }else
            predicates.add(cb.notEqual(pathRoot.get(DatasetViewDTO_.nodeSource),CistromicsRestService.NODE_SOURCE_OAGS));

        //exclude any missing pathways (possible if ETL has BSM/Node not mapped),
        //FIXME ACM/CD removed use typeid as some exp(ACM/CD) map higher than familyid??
        predicates.add(cb.isNotNull(pathRoot.get(DatasetViewDTO_.typeid)));
    }

    public List<OmicsDatapoint> filterMissingPathways(List<OmicsDatapoint> datapoints){
        return (datapoints!=null && datapoints.size()>0)? datapoints.stream()
                .filter(dat -> !dat.getPathwayNodeTree().equals(CistromicsRestService.PATHWAY_NODE_SEPARATOR))
                .collect(Collectors.toList()):null;
    }


    public String inValidRequest(QueryParametersData queryParametersData){

        if(queryParametersData == null)
            return BAD_INPUT_MISSING_PARAMS;

        if(queryParametersData.getGeneSearchType() == null)
            return BAD_INPUT_MISSING_SEARCHTYPE;

        //if((queryParametersData.getGeneSearchType().equalsIgnoreCase("consensome")))
        //    return "Bad input. Only GeneSearchType=gene,geneList,goTerm supported";

        return null;
    }

    public String getSimpleInputError(SimpleQueryForm form){

        if(form.geneSearchType == null)
            return "Bad input. Specify GeneSearchType";

        if((form.geneSearchType.equalsIgnoreCase("consensome")))
            return "Bad input. Only GeneSearchType=gene,geneList,goTerm supported";

        if(form.signalingPathway != null && !form.signalingPathway.equalsIgnoreCase("all"))
            if(form.pathwayCategory == null)
                return "Bad input. If has pathway node identifier, provide Pathway node type";

        return null;
    }

    private boolean isGeneId(String gene){
        try{
            Long.parseLong(gene);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public SimpleQueryForm formSimpleQueryForm(String omicsType,String geneSearchType, String gene, String doi,
                                                String ps, String organ,String tissue,
                                                String species,
                                                String pathwayCategory, String signalingPathway,
                                                Integer minScore,Integer maxScore,String bsm,Double percentile){

        return new SimpleQueryForm(omicsType,geneSearchType,gene,doi,
                ps,organ,tissue, species, pathwayCategory, (signalingPathway!=null)?signalingPathway.toString():null,
                minScore, maxScore,bsm,percentile);

    }

    /**
     * This is just to retain old QueryForm format for re-display
     * @param geneSearchType
     * @param gene
     * @param ps
     * @param organ
     * @param tissue
     * @param goTerm
     * @param species
     * @param pathwayCategory
     * @param signalingPathway
     * @return
     */
    public QueryParametersData formOldQueryForm(
            String geneSearchType, String gene,String doi,
            String ps, String organ,String tissue,
            String goTerm,
            String species,
            String pathwayCategory, String signalingPathway,
            String significance,String foldChange,String foldChangeMin,String foldChangeMax,
            String bsm){

        String tissueCategory=(ps != null)?ps:organ;

        return this.omineService.buildQueryParametersData(
                geneSearchType,gene,null,bsm,doi,null,
                foldChange, foldChangeMax,foldChangeMin,
                significance, null,
                tissueCategory,ps,organ,tissue,
                null, goTerm,species, signalingPathway,pathwayCategory,
                null);
    }

    private <T> void queryWithinDatasetCriteria(CriteriaQuery<T> criteria,
                                                Root<?> dataPath,
                                                Root<DatasetViewDTO> pathRoot,
                                                Root<DatasetNodesView> nodesRoot,
                                                List<Predicate> predicates,String repo,Long datasetId,QueryType queryType){


        CriteriaBuilder cb = em.getCriteriaBuilder();

        setDefaultLimits(cb,criteria,dataPath,pathRoot,nodesRoot,predicates,queryType);

        if(repo != null)
            predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.repo), repo));

        if(datasetId != null && datasetId >0)
            predicates.add(cb.equal(pathRoot.get(DatasetViewDTO_.did), datasetId));
    }

    public Optional<ConsensomeSummary> findConsensomeSummaryByDoi(String doi) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConsensomeSummary> criteria = cb.createQuery(ConsensomeSummary.class);
        Root<ConsensomeSummary> pathRoot = criteria.from(ConsensomeSummary.class);

        criteria.select(pathRoot)
                .where(cb.equal(pathRoot.get(ConsensomeSummary_.key).get(ConsensomeId_.doi), doi));

        try {
            return Optional.ofNullable(em.createQuery(criteria).getSingleResult());
        }catch (Exception e){
            return Optional.empty();
        }
    }

    public List<? super ConsensomeSummary> findConsensomeSummary(QueryType queryType,
                                                                 String doi,
                                                                 String family,String ps,String organ,String species){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConsensomeSummary> criteria = cb.createQuery(ConsensomeSummary.class);
        Root<ConsensomeSummary> pathRoot = criteria.from(ConsensomeSummary.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        String type=(queryType == QueryType.Cistromic)?CISTROMIC:TRANSCRIPTOMIC;

        predicates.add(cb.equal(pathRoot.get(ConsensomeSummary_.type), type));

        log.log(Level.INFO,"@findConsensomeSummary type/family/ps/organ/species="+type+"/"+family+"/"+ps+"/"+organ+"/"+species);

        if(doi != null)
            predicates.add(cb.equal(pathRoot.get(ConsensomeSummary_.key).get(ConsensomeId_.doi), doi));
        else{

            Optional<Long> familyid=getKeylet(family);
            Optional<Long> organid=getKeylet(organ);
            Optional<Long> psid=getKeylet(ps);

            if(family != null) {
                if (familyid.isPresent())
                    predicates.add(cb.equal(pathRoot.get(ConsensomeSummary_.key).get(ConsensomeId_.familyId), familyid.get()));
                else
                    predicates.add(cb.equal(pathRoot.get(ConsensomeSummary_.key).get(ConsensomeId_.family), family));
            }
            if(ps != null)
                if(psid.isPresent())
                    predicates.add(cb.equal(pathRoot.get(ConsensomeSummary_.key).get(ConsensomeId_.psId), psid.get()));
                else
                    predicates.add(cb.equal(pathRoot.get(ConsensomeSummary_.key).get(ConsensomeId_.physiologicalSystem), ps));

            if(organ != null)
                if(organid.isPresent())
                    predicates.add(cb.equal(pathRoot.get(ConsensomeSummary_.key).get(ConsensomeId_.organId), organid.get()));
                else
                    predicates.add(cb.equal(pathRoot.get(ConsensomeSummary_.key).get(ConsensomeId_.organ), organ));

            if(species != null)
                predicates.add(cb.equal(pathRoot.get(ConsensomeSummary_.key).get(ConsensomeId_.species), species));
        }

        criteria.select(pathRoot)
                .where(predicates.toArray(new Predicate[]{}));

        return em.createQuery(criteria).getResultList();
    }

    public  List<Consensome> findTxConsensomesByDoi(boolean forApi,boolean forFile,String doi,double percentile,int max,int page,Long startId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Consensome> criteria = cb.createQuery(Consensome.class);
        Root<Consensome> txRoot = criteria.from(Consensome.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(cb.equal(txRoot.get(Consensome_.key).get(ConsensomeId_.doi), doi));
        predicates.add(cb.ge(txRoot.get(Consensome_.percentile), percentile));

        if(forApi){
            predicates.add(cb.ge(txRoot.get(Consensome_.id), startId));
        }

        if(!forFile)
            criteria.orderBy(cb.asc(txRoot.get(Consensome_.id)));
        else
            criteria.orderBy(cb.asc(txRoot.get(Consensome_.cPValue)));

        criteria.select(txRoot).where(predicates.toArray(new Predicate[]{}));

         List<Consensome> list=em.createQuery(criteria)
                 .setFirstResult(page)
                 .setMaxResults(max)
                .getResultList();

         return list;
    }


        public  List<Consensome> findTxConsensomes(boolean forApi,boolean forFile,String family,String modelNode,String ps,String o,String s,double percentile,int max,int page,Long startId){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Consensome> criteria = cb.createQuery(Consensome.class);
        Root<Consensome> cisRoot = criteria.from(Consensome.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        Optional<Long> familyid=getKeylet(family);
        Optional<Long> modelNodeid=getKeylet(modelNode);
        Optional<Long> organid=getKeylet(o);
        Optional<Long> psid=getKeylet(ps);

        predicates.add(cb.ge(cisRoot.get(Consensome_.percentile), percentile));

        if(family != null) {
            if (familyid.isPresent())
                predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.familyId), familyid.get()));
            else
                predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.family), family));
        }

        if (modelNode != null) {
            if (modelNodeid.isPresent())
                predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.modelId), modelNodeid.get()));
            else
                predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.model), modelNode));
        }

        if(o != null)
            if(organid.isPresent())
                predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.organId), organid.get()));
            else
                predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.organ), o));

        if(ps != null)
            if(psid.isPresent())
                predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.psId), psid.get()));
            else
                predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.physiologicalSystem), ps));

        if(s != null)
            predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.species), s));

        if(forApi)
            predicates.add(cb.ge(cisRoot.get(Consensome_.id), startId));

        if(!forFile)
            criteria.orderBy(cb.asc(cisRoot.get(Consensome_.id)));
        else
            criteria.orderBy(cb.asc(cisRoot.get(Consensome_.cPValue)));

        criteria.select(cisRoot).where(predicates.toArray(new Predicate[]{}));

        return em.createQuery(criteria)
                .setFirstResult(page)
                .setMaxResults(max)
                .getResultList();
    }

    public  List<CisConsensome> findCisConsensomesByDoi(boolean forApi,boolean forFile,String doi,double percentile,int max,int page,Long startId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CisConsensome> criteria = cb.createQuery(CisConsensome.class);
        Root<CisConsensome> cisRoot = criteria.from(CisConsensome.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.doi), doi));
        predicates.add(cb.ge(cisRoot.get(CisConsensome_.percentile), percentile));

        if(forApi)
            predicates.add(cb.ge(cisRoot.get(CisConsensome_.id), startId));

        if(!forFile)
            criteria.orderBy(cb.asc(cisRoot.get(CisConsensome_.id)));
        else
            criteria.orderBy(cb.desc(cisRoot.get(CisConsensome_.averageScore)));

        criteria.select(cisRoot).where(predicates.toArray(new Predicate[]{}));

        return em.createQuery(criteria)
                .setFirstResult(page)
                .setMaxResults(max)
                .getResultList();
    }

    public  List<CisConsensome> findCisConsensomes(boolean forApi,boolean forFile,String family,String modelNode,String ps,String o,String s,double percentile,
                                                   int max,int page,Long startId){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CisConsensome> criteria = cb.createQuery(CisConsensome.class);
        Root<CisConsensome> cisRoot = criteria.from(CisConsensome.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        Optional<Long> familyid=getKeylet(family);
        Optional<Long> modelNodeid=getKeylet(modelNode);
        Optional<Long> organid=getKeylet(o);
        Optional<Long> psid=getKeylet(ps);

        predicates.add(cb.ge(cisRoot.get(CisConsensome_.percentile), percentile));

        //family,ps,o,species = key
        if(family != null) {
            if (familyid.isPresent())
                predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.familyId), familyid.get()));
            else
                predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.family), family));
        }
        if (modelNode != null) {
            if (modelNodeid.isPresent())
                predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.modelId), modelNodeid.get()));
            else
                predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.model), modelNode));
        }

        if(o != null)
            if(organid.isPresent())
                predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.organId), organid.get()));
            else
                predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.organ), o));

        if(ps != null)
            if(psid.isPresent())
                predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.psId), psid.get()));
            else
                predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.physiologicalSystem), ps));

        if(s != null)
            predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.species), s));

        if(forApi)
            predicates.add(cb.ge(cisRoot.get(CisConsensome_.id), startId));

        if(!forFile)
            criteria.orderBy(cb.asc(cisRoot.get(CisConsensome_.id)));
        else
            criteria.orderBy(cb.desc(cisRoot.get(CisConsensome_.averageScore)));

        criteria.select(cisRoot).where(predicates.toArray(new Predicate[]{}));

        return em.createQuery(criteria)
                .setFirstResult(page)
                .setMaxResults(max)
                .getResultList();
    }

    public int countConsensomes(QueryType queryType,String doi,String pw,String ps,String o,String s,double percentile){
        if(doi != null){
            List<ConsensomeSummary> list=(List<ConsensomeSummary>)this.findConsensomeSummary(queryType,doi,null,null,null,null);
            if(list == null || list.size() == 0 )//unlikely
                return 0;

            ConsensomeSummary sum=list.get(0);
            return (queryType == QueryType.Cistromic)?
                 countCisConsensomes(false,doi,null,null,
                    null, null, null,percentile,null) :
                 countTxConsensomes(false,doi,null,null,
                        null, null, null,percentile,null);
        }
        return (queryType ==QueryType.Cistromic)?
                countCisConsensomes(false,null,null,pw,ps,o,s,percentile,null):
                countTxConsensomes(false,null,null,pw,ps,o,s,percentile,null);
    }

    public int countCisConsensomes(boolean forApi,String doi,String family,String modelNode,String ps,String o,String s,double percentile,Long startId){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<CisConsensome> cisRoot = criteria.from(CisConsensome.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        Optional<Long> familyid=getKeylet(family);
        Optional<Long> modelNodeid=getKeylet(modelNode);
        Optional<Long> organid=getKeylet(o);
        Optional<Long> psid=getKeylet(ps);

        predicates.add(cb.ge(cisRoot.get(CisConsensome_.percentile), percentile));

        if(doi != null){
            predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.doi), doi));
        }else {
            if (family != null)
                if (familyid.isPresent())
                    predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.familyId), familyid.get()));
                else
                    predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.family), family));

            if (modelNode != null)
                if (modelNodeid.isPresent())
                    predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.modelId), modelNodeid.get()));
                else
                    predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.model), modelNode));

            if (o != null)
                if (organid.isPresent())
                    predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.organId), organid.get()));
                else
                    predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.organ), o));

            if (ps != null)
                if (psid.isPresent())
                    predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.psId), psid.get()));
                else
                    predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.physiologicalSystem), ps));

            if (s != null)
                predicates.add(cb.equal(cisRoot.get(CisConsensome_.key).get(ConsensomeId_.species), s));

        }


        if(forApi)
            predicates.add(cb.ge(cisRoot.get(CisConsensome_.id), startId));

        //criteria.orderBy(cb.asc(cisRoot.get(CisConsensome_.id)));

        criteria.select(cb.countDistinct(cisRoot));
        criteria.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Long> countQuery = em.createQuery( criteria );
        return countQuery.getSingleResult().intValue();
    }

    public int countTxConsensomes(boolean forApi,String doi,String family,String node,String ps,String o,String s,double percentile,Long startId){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<Consensome> cisRoot = criteria.from(Consensome.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        Optional<Long> familyid=getKeylet(family);
        Optional<Long> organid=getKeylet(o);
        Optional<Long> psid=getKeylet(ps);

        predicates.add(cb.ge(cisRoot.get(Consensome_.percentile), percentile));

        if(doi!=null){
            predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.doi), doi));
        }else {

            if (family != null)
                if (familyid.isPresent())
                    predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.familyId), familyid.get()));
                else
                    predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.family), family));

            if (o != null)
                if (organid.isPresent())
                    predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.organId), organid.get()));
                else
                    predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.organ), o));

            if (ps != null)
                if (psid.isPresent())
                    predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.psId), psid.get()));
                else
                    predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.physiologicalSystem), ps));

            if (s != null)
                predicates.add(cb.equal(cisRoot.get(Consensome_.key).get(ConsensomeId_.species), s));

        }

        if(forApi)
            predicates.add(cb.ge(cisRoot.get(Consensome_.id), startId));

        //criteria.orderBy(cb.asc(cisRoot.get(Consensome_.id)));

        criteria.select(cb.countDistinct(cisRoot));
        criteria.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Long> countQuery = em.createQuery( criteria );
        return countQuery.getSingleResult().intValue();
    }

    public Optional<Long> getKeylet(String keylet){
        try{
            return Optional.ofNullable(Long.parseLong(keylet));
        }catch(Exception e){
            return Optional.empty();
        }
    }

    public Optional<Double> getDoubleValue(String val){
        try{
            return Optional.ofNullable(Double.parseDouble(val));
        }catch(Exception e){
            return Optional.empty();
        }
    }

    public int countConsensomesForApi(QueryType queryType,String doi,String familyId,String nodeId,String psId,String organId,String species,double percentile, Long startId){

        if(queryType == QueryType.Cistromic)
            return this.countCisConsensomes(true,doi,familyId,nodeId,psId,organId,species,percentile, startId);
        else
            return this.countTxConsensomes(true,doi,familyId,nodeId,psId,organId,species,percentile, startId);

    }

    public <T> List<T> findConsensomesForApi(QueryType queryType,
                                             String doi,
                                             String family, String mnode,String ps, String organ, String species,
                                             double percentile,
                                             final int count, final int page, final Long startId){
        if(doi != null)
        {
            if(queryType == QueryType.Cistromic)
               return (List<T>)findCisConsensomesByDoi(true,false,doi,percentile,count,page,startId);
            else
               return (List<T>)findTxConsensomesByDoi(true,false,doi,percentile,count,page,startId);
        }else{

            if(queryType ==QueryType.Cistromic)
               return (List<T>) findCisConsensomes(true,false,family, mnode,ps, organ, species, percentile,count, page,startId);
            else
               return (List<T>) findTxConsensomes(true,false,family, mnode,ps, organ, species, percentile,count, page,startId);
        }
    }

    public <T> ConsensomeResult<T> findConsensomeResult(QueryType queryType,
                                                        String doi,
                                                        String family, String ps, String organ, String species,
                                                        double percentile,
                                                        final int count, final int page, String date) {

        ConsensomeResult<T> result=null;
        ConsensomeSummary summary=null;
        if(doi != null)
        {
            List<ConsensomeSummary> list=(List<ConsensomeSummary>)findConsensomeSummary(queryType,doi,null,null,null,null);
            if(list.size() == 0)
                return result;

            summary=list.get(0);

            if(queryType == QueryType.Cistromic) {
                List<CisConsensome> results = findCisConsensomesByDoi(false,false,doi,percentile,count,page,null);
                result = (ConsensomeResult<T>)new ConsensomeResult<CisConsensome>(summary, results);
            }else{
                List<Consensome> results =findTxConsensomesByDoi(false,false,doi,percentile,count,page,null);
                result = (ConsensomeResult<T>)new ConsensomeResult<Consensome>(summary, results);
            }
        }else {
            Optional<ConsensomeSummary> osummary=null;
            String adoi=null;
            if(queryType ==QueryType.Cistromic) {
                List<CisConsensome> list = findCisConsensomes(false,false,family, null,ps, organ, species, percentile,count, page,null);
                if(list!=null && list.size()>0) {
                    adoi=((CisConsensome)list.get(0)).getKey().getDoi();
                    osummary = findConsensomeSummaryByDoi(adoi);
                    if (osummary.isPresent())
                        summary = osummary.get();
                }
                result = (ConsensomeResult<T>)new ConsensomeResult<CisConsensome>(summary, list);
            }else{
                List<Consensome> list = findTxConsensomes(false,false,family, null,ps, organ, species, percentile,count, page,null);
                if(list!=null && list.size()>0) {
                    adoi=((Consensome)list.get(0)).getKey().getDoi();
                    osummary = findConsensomeSummaryByDoi(adoi);
                    if (osummary.isPresent())
                        summary = osummary.get();
                }
                result = (ConsensomeResult<T>)new ConsensomeResult<Consensome>(summary, list);
            }
        }
        return result;
    }


    private List<ExperimentBsmsView> getExperimentBsms(Long id){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExperimentBsmsView> criteria = cb.createQuery(ExperimentBsmsView.class);
        Root<BindingScore> scoreRoot = criteria.from(BindingScore.class);
        Root<ExperimentBsmsView> bsmViewRoot = criteria.from(ExperimentBsmsView.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(scoreRoot.get(BindingScore_.id), id));
        predicates.add(cb.equal(bsmViewRoot.get(ExperimentBsmsView_.expid), scoreRoot.get(BindingScore_.experiment).get(Experiment_.id)));
        criteria.select(bsmViewRoot).where(predicates.toArray(new Predicate[]{}));

        return em.createQuery(criteria).getResultList();
    }

    private List<ExperimentNodesView> getExperimentNodes(Long id){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExperimentNodesView> criteria = cb.createQuery(ExperimentNodesView.class);
        Root<BindingScore> scoreRoot = criteria.from(BindingScore.class);
        Root<ExperimentNodesView> nodeRoot = criteria.from(ExperimentNodesView.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(scoreRoot.get(BindingScore_.id), id));
        predicates.add(cb.equal(nodeRoot.get(ExperimentNodesView_.expid), scoreRoot.get(BindingScore_.experiment).get(Experiment_.id)));
        criteria.select(nodeRoot).where(predicates.toArray(new Predicate[]{}));

        return em.createQuery(criteria).getResultList();
    }

    public SignalingPathwayView getFamilyNodeHierarchy(Long familyId){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SignalingPathwayView> criteria = cb.createQuery(SignalingPathwayView.class);
        Root<SignalingPathwayView> root = criteria.from(SignalingPathwayView.class);

        criteria.select(root).where(cb.equal(root.get(SignalingPathwayView_.familyId),familyId));

        List<SignalingPathwayView> list=em.createQuery(criteria).getResultList();
        return list.get(0);
    }
}
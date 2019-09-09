package edu.bcm.dldcc.big.nursa.services.rest.pubapi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import edu.bcm.dldcc.big.nursa.model.cistromic.DatasetViewMiniDTO;
import edu.bcm.dldcc.big.nursa.model.omics.*;
import edu.bcm.dldcc.big.nursa.model.omics.dto.*;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.DatapointDTO;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util.ApiApplicationErrorMessage;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util.NoSuchIdentifierException;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util.TooManyResultsException;
import edu.bcm.dldcc.big.nursa.services.rest.omics.*;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.PathwaysNodesMapBean;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.current.APIService;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.*;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.legacy.ApiServiceLegacy;
import edu.bcm.dldcc.big.nursa.util.filter.BaseFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

/**
 * External  API(public) services  implementation
 * @author mcowiti
 *
 */
public class APIServiceRestApi implements APIService,ApiServiceLegacy {

	 @Inject private TranscriptomineService omineService;

    @Inject
    private OmicsServicebean omicsServicebean;

    @Inject
    private DatasetServiceBean datasetServiceBean;

    @Inject BsmServicebean bsmServicebean;

    @Inject
    private PathwayServiceBean pathwayServiceBean;

    private final static String MISS_REQ_QUERY_IDENTIFIERS="Missing required query identification parameter(s)";
    private final static String MISS_VALID_OMICS_TYPE="Valid OmicsType are: Cistromic, Transcriptomic";
    private final static String MISS_VALID_QUERY_TYPE="Valid queryType(s) are: entrezGeneId,goTerm,doi,biosample";
    private final static String MISS_VALID_DATASET_IDNTFIER="Missing actionable Dataset Identifier parameter(s)";
    private final static String BAD_DATASET_ADDEDSINCE_DATE_IDNTFIER="The Date needs to be yyyyMMdd format, i.e. ISO 8601 without time and zone";

    private final static String MAX_DATASET_COUNT_EXCEEDED="Exceeded the number of dataset the service can return";

    private static Logger log = Logger.getLogger("edu.bcm.dldcc.big.nursa.services.rest.transcriptomine.APIServiceRestApi");

    @Override
    public String info() {
        return "Yes, ICHY";
    }

    public List<PsOrgan> getAllPsOrgans(){
        return this.omineService.getTissueCategory();
    }

    @Override
    public Response getAllSPPBsms() {

        List<BsmView> list=bsmServicebean.getAllSPPNamedBsms();
        return Response.ok().entity(list).build();
    }

    public Response getAllPathways(){
        return  Response.ok().entity(PathwaysNodesMapBean.allPathways).build();
    }

    public Response getModelAndDiseasesNodes(){
        List<ModelsNode> models= new ArrayList<ModelsNode>();
        for(Map.Entry<Long,String> entry: PathwaysNodesMapBean.modelIdNodeMap.entrySet()){
            models.add(new ModelsNode(entry.getKey(),entry.getValue()));
        }
        return Response.ok().entity(models).build();
    }

    public  Response getNamedPathways(String apiKey,String levelType, String names){

        String[] csvnames={"Diseases"};
        if(names != null && names.trim().length() > 0)
            csvnames=names.split(",");

        PathwayCategory pathwayNodeLevelType=PathwayCategory.type;
        if(levelType != null)
            pathwayNodeLevelType=PathwayCategory.valueOf(levelType);

        if(pathwayNodeLevelType == PathwayCategory.type ){
            List<String> nameslist = Arrays.stream(csvnames).collect(Collectors.toList());
            if(nameslist.contains(PathwaysNodesMapBean.TOP_LEVEL_SIG_PATH_MODULES))
                return Response.status(400).entity(
                        new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,
                                "This query will be too slow. Instead, request the all pathways endpoint (cached), /api/2/pathways/all, and extract the hierarchy portion that you need."))
                        .build();
        }

        List<SignalingPathway> list=this.pathwayServiceBean.getNamedSignalingPathways(pathwayNodeLevelType,csvnames);
        //List<SimpleSignalingPathway> list= this.pathwayServiceBean.getShortSignalingPathway(pathwayNodeLevelType,csvnames);
        return Response.ok().entity(list).build();
    }

    private boolean isAll(String value){
        return (value.trim().equalsIgnoreCase("-1") || value.trim().equalsIgnoreCase("All"));
    }

    @Override
    public Response findConsensomes(String apiKey,
                                    String omicsType, String doi,
                                    String psId, String organId, Long familyId, Long modelNodeId,String species,
                                    Double percentile, Long startId,Integer isCount,Integer count) {


        Optional<String> response=hasRequiredConsensomeIdParameters(omicsType,psId,organId,familyId,modelNodeId,species,doi);

        if(response.isPresent())
            return Response.status(400).entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,response.get())).build();

        if(count > OmicsServicebean.MAX_API_CONSENSOMES){
            return Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"The maximum allowed count is "+ OmicsServicebean.MAX_API_CONSENSOMES+". If you need more data, consider also using the startId filter"))
                    .build();
        }

        if(doi != null){
            BaseFilter f= new BaseFilter();
            if(!f.cleanDoi(doi))
                return Response.status(400)
                        .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,OmicsServicebean.inputMessageDoi))
                        .build();
        }else{
            //validate family,ps,organ, species
            if(familyId!=null && familyId!=-1) {
                if (!PathwaysNodesMapBean.familyIdNodeMap.containsKey(familyId.longValue())) {
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST, "The supplied signaling pathway node familyId is not supported:" + familyId))
                            .build();
                }
            }
            if(modelNodeId != null && modelNodeId!=-1)
                if(!PathwaysNodesMapBean.modelIdNodeMap.containsKey(modelNodeId.longValue())){
                return Response.status(400)
                        .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"The supplied signaling pathway node model nodeId is not supported:"+modelNodeId))
                        .build();
            }

            if(!PathwaysNodesMapBean.speciesCommonTerms.containsKey(species.toLowerCase()))
                return Response.status(400)
                        .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"The  supported species are: Human, Mouse and Rat"))
                        .build();

            Optional<String> resp;
            if(!isAll(psId) && isValidInteger(psId)) {
                if (!PathwaysNodesMapBean.psIdsMap.containsKey(Long.valueOf(psId)))
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"This ps Id is unknown"))
                            .build();

            } else {
                resp=this.omicsServicebean.isValidatedBiosamplePiece(psId,OmicsServicebean.PS_INPUT);
                if(resp.isPresent())
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,resp.get()))
                            .build();
            }

            if(!isAll(organId) && isValidInteger(organId)) {
                if (!PathwaysNodesMapBean.organIdsMap.containsKey(Long.valueOf(organId)))
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"This organ Id is unknown"))
                            .build();
            }else {
                resp=this.omicsServicebean.isValidatedBiosamplePiece(organId,OmicsServicebean.ORGAN_INPUT);
                if(resp.isPresent())
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,resp.get()))
                            .build();
            }
        }

        String family=(familyId != null)?Long.toString(familyId):null;
        String mnode=(modelNodeId != null)?Long.toString(modelNodeId):null;

        QueryType queryType=(omicsType.trim().equalsIgnoreCase(QueryType.Cistromic.toString()))?QueryType.Cistromic:QueryType.Transcriptomic;
        int total=this.omicsServicebean.countConsensomesForApi(queryType,doi,family,mnode,psId,organId,species,percentile,startId);

        if(isCount == 1)
            return Response.ok().entity(total).build();

        //if total more suggests todo page by startid
        if(total > OmicsServicebean.MAX_API_CONSENSOMES){
            return Response.status(HttpStatus.SC_REQUEST_TOO_LONG)
                    .entity(new ApiErrorMessage(HttpStatus.SC_REQUEST_TOO_LONG,"Too many consensomes : "+total+". Consider using the startId filter parameter"))
                    .build();
        }

        List list=this.omicsServicebean.findConsensomesForApi(queryType,doi,
                family, mnode,psId, organId,species,percentile,count, 0, startId);

        //sort ob id for API paging
        long sort=System.currentTimeMillis();
        Collections.sort(list);
        if((System.currentTimeMillis()-sort) >2000)
            log.log(Level.WARNING,"Sorting API consensome results pre response taking over 2s");


        return Response.ok().entity(list).build();
    }


    @Override
    public Response findDatapointsForReactome(String apiKey, String omicsType, String queryType, String queryValue,
                                              Double significance, Long afterId, Integer isCount, Integer countMax) {
        if(!hasRequiredParameters(omicsType,queryType,queryValue))
            return Response.status(400).entity(
                    new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,MISS_REQ_QUERY_IDENTIFIERS)).build();

        if(!isValidOmicsType(omicsType))
            return Response.status(400).entity(
                    new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,MISS_VALID_OMICS_TYPE)).build();

        ApiQueryTargetBy queryTargetBy=ApiQueryTargetBy.doi;
        Response doiResp = getValueValidationResponse(ApiQueryTargetBy.doi,queryValue,null);
        if(doiResp != null)
            return doiResp;
        String doi = queryValue;

        QueryType omicsQueryType=QueryType.valueOf(omicsType.trim());

        if(omicsQueryType==QueryType.Transcriptomic) {
            String sig = null;
            if (significance != null) {
                sig = significance.toString();//just to use older API
                Optional<String> resp = validateSignificance(sig);
                if (resp.isPresent())
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST, resp.get())).build();
            }
        }

        return this.omicsServicebean.getDatapointsForReactome(omicsQueryType,doi,significance,(isCount == 1),afterId,countMax);
    }


    @Override
    public Response findDatasetsForReactome(String apiKey, String addedSince, String doi, Integer count,String fields) {

        long b=System.currentTimeMillis();
        if(missReactomeDatasetIdentifier(doi,addedSince))
            return Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,MISS_VALID_DATASET_IDNTFIER)).build();

        //dont be greedy
        if(count > OmicsServicebean.MAX_API_DATASETS){
            return Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,MAX_DATASET_COUNT_EXCEEDED)).build();
        }

        LocalDate ldate=null;
        if(addedSince != null) {
            try {
                ldate = LocalDate.parse(addedSince, DateTimeFormatter.BASIC_ISO_DATE);
            }catch (Exception e){
                if(ldate ==null && doi==null)
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,BAD_DATASET_ADDEDSINCE_DATE_IDNTFIER)).build();
            }
        }

        if(doi != null)
            return this.datasetServiceBean.getDatasetViewMini(DatasetQueryType.doi,doi,count,fields);

        if((System.currentTimeMillis()-b) >3000)
            log.log(Level.WARNING,"findDatasetsForReactome is slower than 3s");

        return this.datasetServiceBean.getDatasetViewMini(DatasetQueryType.datesince,addedSince,count,fields);
    }

    public Response findTestDatasetsForReactome(String addedSince,  Integer count){
        LocalDate ldate=null;
        ldate = LocalDate.parse(addedSince, DateTimeFormatter.BASIC_ISO_DATE);

        List<DatasetViewMiniDTO> datasets=this.datasetServiceBean.getDatasetViewMiniDTOByReleaseDate(addedSince,count,false);

        return Response.ok().entity(datasets).build();
    }

    private Optional<String> validateSigPath(String sigPathNodeType, Integer sigPathId) {
        if(sigPathNodeType == null || sigPathId == null)
            return Optional.of("API v2 requires that you supply both signaling pathway node type and Id");

        if(!PathwaysNodesMapBean.signalingPathwayNodeTypesMap.containsKey(sigPathNodeType.toLowerCase()))
            return Optional.of("The supported signaling pathway node types are:"+Arrays.asList(PathwaysNodesMapBean.signalingPathwayNodeTypesMap.keySet().toArray()));

        if(!PathwaysNodesMapBean.familyIdNodeMap.containsKey(sigPathId.longValue())){
            return Optional.of("The supplied signaling pathway node Id is not supported:"+sigPathId);
        }
        return Optional.empty();
    }

    private Optional<String> validateSigFcs(String fc, String fcMin, String fcMax, String sig,
                                            String direction){

        if(fc != null)
            if(!isValidDouble(fc))
                return Optional.of("Invalid foldChange Value");
        if(fcMin != null)
            if(!isValidDouble(fcMin))
                return Optional.of("Invalid foldChange Min Value");
        if(fcMax != null)
            if(!isValidDouble(fcMax))
                return Optional.of("Invalid foldChange Max Value");
        if(sig != null)
            if(!isValidDouble(sig))
                return Optional.of("Invalid significance Max Value");

        if(direction != null){
            if(!PathwaysNodesMapBean.directionsMap.containsKey(direction.toLowerCase()))
                return Optional.of("Supported directions are:"+Arrays.asList(PathwaysNodesMapBean.directionsMap.keySet().toArray()));
        }

        return Optional.empty();
    }

    private Optional<String> validateSignificance(String sig){
        if(sig != null)
            if(!isValidDouble(sig))
                return Optional.of("Invalid significance Max Value");

        return Optional.empty();

    }

    private boolean isValidDouble(String value){
        try{
            Double.parseDouble(value);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private boolean isValidInteger(String value){
        try{
            Integer.parseInt(value);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private Response getValueValidationResponse(ApiQueryTargetBy target, String value, String value2){

        Optional<Response> invalidResponse;
        switch(target) {
            case doi:
                invalidResponse = omicsServicebean.isGotInvalidDOIResponse(value);
                if (invalidResponse.isPresent())
                    return invalidResponse.get();
                break;
            case goTerm:
                invalidResponse=omicsServicebean.isGotInvalidGoTermInputResponse(value);
                if(invalidResponse.isPresent())
                    return invalidResponse.get();
                break;
            case biosample:
                if(value != null && value.trim().length() > 0) {
                    invalidResponse = inValidBiosamplePiece(value,OmicsServicebean.PS_INPUT);
                    if (invalidResponse.isPresent())
                        return invalidResponse.get();
                }
                if(value2 != null && value2.trim().length() > 0) {
                    invalidResponse = inValidBiosamplePiece(value2,OmicsServicebean.ORGAN_INPUT);
                    if (invalidResponse.isPresent())
                        return invalidResponse.get();
                }
                break;
        }
        return null;
    }

    private Optional<Response> inValidBiosamplePiece(String piece,String type){
        Optional<Response> invalidDoiResponse=omicsServicebean.isGotInvalidBiosamplePieceResponse(piece,type);
        return  (invalidDoiResponse.isPresent())?
            Optional.ofNullable(invalidDoiResponse.get()) : Optional.empty();
    }

    private boolean isValidOmicsType(String omicsType){
        return (omicsType.trim().equalsIgnoreCase(QueryType.Cistromic.toString()) || omicsType.trim().equalsIgnoreCase(QueryType.Transcriptomic.toString()));
    }
    private ApiQueryTargetBy isValidQueryType(String queryType){
        try {
            return ApiQueryTargetBy.valueOf(queryType);
        }catch (Exception e){
            return null;
        }
    }

    private boolean hasRequiredParameters(String omicsType, String queryType, String queryValue){
        return ((omicsType != null && omicsType.trim().length()>0)
                && (queryType != null && queryType.trim().length()>0)
                && (queryValue != null && queryValue.trim().length()>0));
    }

    private Optional<String> hasRequiredConsensomeIdParameters(String omicsType,String psId,String organId,Long familyId,Long modelNodeId,String species,String doi){

        if(omicsType == null || omicsType.length() == 0)
            return Optional.of("Query must specify the Omics type parameter, omicsType. This should have values of: Transcriptomic or Cistromic");

        if(doi != null && doi.length() > 0 )
            return Optional.empty();

        if(psId != null && organId != null  && species != null && (familyId != null || modelNodeId != null))
            return Optional.empty();
        else
            return Optional.of("Query must specify a doi or psId+organId+familyId/modelNodeId+species parameters to fully identify a consensome.");
    }

    private boolean isMultipleDatasetsResultQuery(String bsm,
                                                  String bsmId,
                                                  String node,
                                                  String biosample,
                                                  String addedSince){

        return (bsm != null ||
                bsmId != null ||
                node != null ||
                biosample != null ||
                addedSince != null);

    }

    private  Map getBiosampleIdentifiers(String biosample){
        String psOrgan[] =biosample.split("/");

        Long[] psOrganIds= new Long[2];
        String[] psOrgans= new String[2];

        String ps,organ=null;
        boolean isUseNames=true;
        if(!(ps=psOrgan[0].trim()).equals("")) {
            Optional<Long> psOpt = this.omicsServicebean.getKeylet(ps);
            if(psOpt.isPresent()) {
                psOrganIds[0] = psOpt.get();
                isUseNames=false;
            }
            else
                psOrgans[0]=ps;
        }
        if(!(organ=psOrgan[1].trim()).equals("")) {
            Optional<Long> organOpt = this.omicsServicebean.getKeylet(organ);
            if(organOpt.isPresent()) {
                psOrganIds[1] = organOpt.get();
                isUseNames=false;
            }
            else
                psOrgans[1]=organ;
        }

        Map map=new HashMap();
        if(isUseNames)
            map.put("names",psOrgans);
        else
            map.put("ids",psOrganIds);

        return map;
    }

    private boolean isSupplyingBsmIdentifier(String bsmId, String bsmIdType){
        return (bsmId != null || bsmIdType != null);
    }

    private boolean hasBsmIdentifier(String bsmId, String bsmIdType){
        return ((bsmId != null || bsmIdType != null) &&
           (bsmId != null && bsmIdType != null));

    }

    private boolean missDatasetIdentifier(String doi,
                                          String pmid,
                                          String bsm,
                                          String bsmId,
                                          String node,
                                          String biosample,
                                          String addedSince){
        return (doi == null &&
                pmid == null &&
                bsm == null &&
                bsmId == null &&
                node == null &&
                biosample == null &&
                addedSince == null);
    }

    private boolean missReactomeDatasetIdentifier(String doi,String addedSince){
        return (doi == null && addedSince == null);
    }

	 @Override
		public Response findDatasets(Integer geneId, String symbol, String ligandSource, String ligandSourceId) 
	  {
		 try {
				return this.queryDatasets(geneId, symbol, ligandSource, ligandSourceId);
			} catch (Exception e) {
				return Response.status(400).entity("Error:"+e.getMessage()).build();
			}
		}

		@Override
		public Response queryDatasets(Integer geneId, String symbol, String ligandSource, String ligandSourceId) 
		{
			if( geneId == null && symbol == null && ligandSource == null){
				return Response.status(400).entity("Missing parameters").build();
			}
			if((ligandSource != null && ligandSourceId == null) || (ligandSource == null && ligandSourceId != null)){
				return Response.status(400).
						entity("Usage: if querying by ligand, you must supply both  ligand parameters  ( ligand.source and ligand.sourceID )")
						.build();
			}
			
			if(symbol!=null)
				if(!new BaseFilter().cleanAPIParam(symbol))
					return Response.status(400).entity("Unacceptable symbol paramter "+symbol).build();
			if(ligandSource!=null)
				if(!new BaseFilter().cleanAPIParam(ligandSource))
					return Response.status(400).entity("Unacceptable ligandSource paramter "+ligandSource).build();
			if(ligandSourceId!=null)
				if(!new BaseFilter().cleanAPIParam(ligandSourceId))
					return Response.status(400).entity("Unacceptable ligandSourceId paramter "+ligandSourceId).build();
				
			if(symbol != null && symbol.equals("test.sample")){
				return Response.ok()
						.entity(ApiQueryUtility.getDatasetSample())
						.build();
			}
			
			DatasetApiQueryDTO query=ApiQueryUtility.getDatasetApiQueryDTO(geneId,symbol,ligandSource,ligandSourceId);
			List<DatasetMinimalDTO> l=this.omineService.findDatasets(query);
			return Response.ok()
					.entity(l)
					.build();
		}


	@Override
	public Response getDatapointsResponse(String apiKey, String includeExp,
			String geneSearchType, 
			String gene, String disease, String goTerm,String molecule,
			String ligandIdType, 
			String significance, String foldChange,String direction, 
			String species, 
			//String author, 
			String doi,
			//String pubmed, 
			String treatmenttime, 
			String ps, String organ,String tissue,
			Integer getCount,Integer count,
			String foldChangeMin,String foldChangeMax,
			String pathway,String pathwayCategory) throws Exception{
		
		QueryForm queryForm;
		try{
			queryForm=getQueryForm(apiKey, includeExp,
					geneSearchType,  gene, disease,goTerm, molecule,
				 ligandIdType,   significance,  foldChange,
				 direction,  species,  
				 null,  doi,
				 null,  
				 treatmenttime,  
				 ps,organ,tissue,
				 foldChangeMin,foldChangeMax,pathway,pathwayCategory);
		}catch (NoSuchIdentifierException e){
			ApiApplicationErrorMessage error=e.getError();
			return Response.status(400)
					.entity(error).build();
		}catch (Exception ex){
			return Response.status(400)
					.entity(new ApiApplicationErrorMessage(400,ex.getMessage())).build();
		}
		
		if(doi == null || doi.trim().length() == 0)
			if(geneSearchType == null || geneSearchType.length() == 0)
				return Response.status(400)
						.entity(new ApiApplicationErrorMessage(400,"Missing required 'geneSearchType'")).build();
		
		if(doi != null && doi.trim().length() > 0){
			BaseFilter f= new BaseFilter();
			if(!f.cleanDoi(doi))
				return Response.status(400)
						.entity(new ApiApplicationErrorMessage(400,"Invalid DOI")).build();
		}
		
		long b=System.currentTimeMillis();
		Long cnt=(omineService.findDatapointsCount(queryForm));

		if((System.currentTimeMillis()-b) > 1000)
		    log.info(" API slow datapoints countDatapoints, found # -> in ms ="+cnt+" -> "+(System.currentTimeMillis()-b));
		
		if(getCount != null && getCount == 1){
			Response.ResponseBuilder response = Response.ok(cnt);
	    	return response.build();
		}
			
		int maxDatapoints=(count !=null && (count > 0 && count < TranscriptomineService.MAX_API_DATAPOINTS.intValue()))?
				count:TranscriptomineService.MAX_API_DATAPOINTS;
		
		if(cnt.intValue() > maxDatapoints){
			log.warning("Too many result in datapoints # = "+cnt+ " for query "+queryForm.toString());
			throw new TooManyResultsException(new StringBuilder("Too many results: ").append(cnt).append(". Please narrow your query").toString());
		}
		
		List<DatapointDTO> list= this.omineService.findDataPoints(queryForm, DatapointDTO.class, maxDatapoints,false);

        if((System.currentTimeMillis()-b) > 2000)
		    log.info(" query found, but slow # -> in ms ="+list.size()+" -> "+(System.currentTimeMillis()-b));
		
		if(queryForm.getResultCount() > maxDatapoints){
			log.warning("Too many result in datapoints # = "+queryForm.getResultCount()+ " for query "+queryForm.toString());
			throw new TooManyResultsException(new StringBuilder("Too many results: ").append(queryForm.getResultCount()).append(". Please narrow your query").toString());
		}
		return  Response.ok(list).build();
    }
	
	public  QueryForm getQueryForm(String apiKey, String includeExp,
			String geneSearchType, String gene,String disease, String goTerm, String molecule,
			String ligandIdType,String significance, String foldChange,
			String direction, String species, String author, String doi,
			String pubmed, String treatmenttime, 
			String ps,String organ,String tissue,
			String foldChangeMin,String foldChangeMax,
			String pathway,String pathwayCategory) throws Exception{
		
		QueryForm form= new QueryForm();
		//FIXME removed form.setEmail(apiKey);
		Map<QueryParamName, QueryParameter> query=form.getQueryParameters();
		
		this.omineService.translateParams(query,apiKey,includeExp,
				geneSearchType, gene,disease, goTerm, molecule,
				ligandIdType,significance,foldChange,
				direction,species,author,doi,
				pubmed,treatmenttime,
				ps,organ,tissue,
				foldChangeMin,foldChangeMax,pathway,pathwayCategory);
		
		return form;
	}

    /**
     * This is the old way of getting datasets before reactome
     * @param apiKey
     * @param doi
     * @param pmid
     * @param bsm
     * @param bsmId
     * @param bsmIdType
     * @param node
     * @param biosample
     * @param addedSince
     * @param count
     * @return
     */
    @Deprecated
    private Response findNuDatasets(String apiKey,
                                    String doi,
                                    String pmid,
                                    String bsm,
                                    String bsmId, String bsmIdType,
                                    String node,
                                    String biosample, String addedSince,Integer count) {

        long b=System.currentTimeMillis();

        if(missDatasetIdentifier(doi,pmid,bsm,bsmId,node,biosample,addedSince)){
            return Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,MISS_VALID_DATASET_IDNTFIER)).build();
        }

        //if requesting more, rebuff
        if(count > OmicsServicebean.MAX_API_DATASETS){
            return Response.status(400)
                    .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,MAX_DATASET_COUNT_EXCEEDED)).build();
        }

        LocalDate ldate=null;
        if(addedSince != null) {
            ldate = LocalDate.parse(addedSince, DateTimeFormatter.BASIC_ISO_DATE);
        }

        if(doi != null)
            return this.datasetServiceBean.findDatasetByDoi(doi,ldate);

        if(pmid != null)
            return datasetServiceBean.findDatasetByPmid(pmid,ldate,count);

        //these can return multiple,  allow narrowing filter via other fields
        List<ApiQueryParameter> parameters= new ArrayList<ApiQueryParameter>();
        if(isMultipleDatasetsResultQuery(bsm,bsmId,node,biosample,addedSince)){
            if(bsm != null) {
                Optional<Response> invalidBsmResponse=omicsServicebean.isGotInvalidBsmOrNodeResponse(bsm," BSM ");
                if(invalidBsmResponse.isPresent())
                    return invalidBsmResponse.get();
                parameters.add(new ApiQueryParameter(DatasetQueryType.bsm, bsm, null, null));
            }

            if(node != null) {
                Optional<Response> invalidNodeResponse=omicsServicebean.isGotInvalidBsmOrNodeResponse(node," Node ");
                if(invalidNodeResponse.isPresent())
                    return invalidNodeResponse.get();
                parameters.add(new ApiQueryParameter(DatasetQueryType.node, node, null, null));
            }

            if(isSupplyingBsmIdentifier(bsmId,bsmIdType)){
                if(bsmId == null)
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"You supplied the BSM Identifer Type but not the  BSM Identifier. Both are required to determine the BSM.")).build();
                if(bsmIdType == null)
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"You supplied the BSM Identifer but not the  BSM Identifier Type. Both are required to determine the BSM.")).build();

                Optional<Response> invalidBsmIdResponse=omicsServicebean.isGotInvalidBsmIdResponse(bsmId);
                if(invalidBsmIdResponse.isPresent())
                    return invalidBsmIdResponse.get();
                BsmIdentifierType bsmType=null;
                try {
                    bsmType = BsmIdentifierType.valueOf(bsmIdType.toLowerCase().trim());
                }catch(Exception e){
                    return Response.status(400)
                            .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"Your supplied the BSM Identifer Type[ "+bsmIdType +" ] is not supported. Acceptable types are:"+Arrays.asList(BsmIdentifierType.values()))).build();
                }
                parameters.add(new ApiQueryParameter(DatasetQueryType.getBsmIdType(bsmType),bsmId,null,null));
            }

            if(biosample != null) {
                Map map=getBiosampleIdentifiers(biosample);
                Long[] psOrganIds=(map.containsKey("ids"))?(Long[])map.get("ids"):null;
                String[] psOrgans=(map.containsKey("names"))?(String[])map.get("names"):null;
                if(psOrgans != null)
                    parameters.add(new ApiQueryParameter(DatasetQueryType.biosample,null,psOrgans,null));
                if(psOrganIds != null)
                    parameters.add(new ApiQueryParameter(DatasetQueryType.biosampleid,null,null,psOrganIds));
            }
            if(addedSince != null)
                parameters.add(new ApiQueryParameter(DatasetQueryType.datesince,null,ldate,null,null));

            if((System.currentTimeMillis()-b)>500)
                log.log(Level.WARNING,"Slow query prep (ms) "+(System.currentTimeMillis()-b));

            return datasetServiceBean.findDatasetByBsmOrNode(parameters,count);
        }

        log.log(Level.WARNING,"Unknown dataset endpoint request");
        return Response.status(204).entity("Coming soon").build();
    }

    /**
     * This is the old wy of getting datapoints before reactome
     * @param apiKey
     * @param omicsType
     * @param queryType
     * @param queryValue
     * @param species
     * @param signalPathwayType
     * @param signalPathwayId
     * @param psParam
     * @param organParam
     * @param afterId
     * @param minScore
     * @param maxScore
     * @param fc
     * @param fcMin
     * @param fcMax
     * @param significance
     * @param direction
     * @param isCount
     * @param countMax
     * @return
     */
    @Deprecated
    private Response findDatapoints(String apiKey, String omicsType, String queryType, String queryValue,
                                    String species,String signalPathwayType,Integer  signalPathwayId,
                                    String psParam,String organParam,
                                    Long afterId,Integer minScore, Integer maxScore,
                                    String fc, String fcMin, String fcMax,String significance,String direction,
                                    Integer isCount,Integer countMax) {

        if(!hasRequiredParameters(omicsType,queryType,queryValue))
            return Response.status(400).entity(
                    new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,MISS_REQ_QUERY_IDENTIFIERS)).build();

        if(!isValidOmicsType(omicsType))
            return Response.status(400).entity(
                    new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,MISS_VALID_OMICS_TYPE)).build();

        ApiQueryTargetBy queryTargetBy=isValidQueryType(queryType);
        if(queryTargetBy == null)
            return Response.status(400).entity(
                    new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,MISS_VALID_QUERY_TYPE)).build();

        String ps=null,organ=null;
        if(queryTargetBy == ApiQueryTargetBy.biosample) {
            String[] psOrgan = queryValue.split("/");
            ps=psOrgan[0];
            organ=psOrgan[1];
            Response resp = getValueValidationResponse(ApiQueryTargetBy.biosample,ps,organ);
            if(resp != null)
                return resp;

        }else{
            ps=(psParam != null && psParam.trim().length() > 0)?psParam:null;
            organ=(organParam != null && organParam.trim().length() > 0)?organParam:null;
            Response resp = getValueValidationResponse(ApiQueryTargetBy.biosample,ps,organ);
            if(resp != null)
                return resp;

            //TODO filter by gene,doi?? Does it make sense to?
        }

        if(queryTargetBy == ApiQueryTargetBy.entrezGeneId){
            if(!StringUtils.isNumeric(queryValue))
                return Response.status(400).entity(
                        new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"For queryType=entrezGeneId,the queryValue("+queryValue +") must be numeric")).build();
        }

        String doi=null;
        if(queryTargetBy == ApiQueryTargetBy.doi) {
            Response resp = getValueValidationResponse(ApiQueryTargetBy.doi,queryValue,null);
            if(resp != null)
                return resp;
            doi = queryValue;
            //TODO does it make sense to filter by gene?
        }

        if(queryTargetBy == ApiQueryTargetBy.goTerm){
            Response resp = getValueValidationResponse(ApiQueryTargetBy.goTerm,queryValue,null);
            if(resp != null)
                return resp;

            //TODO filter by doi- does it make sense
        }

        //check optionals (species, fcs,pathway), type, size etc
        if(species != null){
            if(!PathwaysNodesMapBean.speciesCommonTerms.containsKey(species.toLowerCase()))
                return Response.status(400)
                        .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,"The only supported species are Human, Mouse and Rat")).build();
        }

        if(fc!=null || fcMax!=null || fcMin!=null || significance != null || direction!=null){
            Optional<String> resp= validateSigFcs(fc,fcMin,fcMax,significance,direction);
            if(resp.isPresent())
                return Response.status(400)
                        .entity(
                                new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,resp.get())).build();
        }

        //TODO check optionals pathway, type, size, availability
        if(signalPathwayType != null || signalPathwayId != null){
            Optional<String> resp=validateSigPath(signalPathwayType,signalPathwayId);
            if(resp.isPresent())
                return Response.status(400)
                        .entity(new ApiErrorMessage(HttpStatus.SC_BAD_REQUEST,resp.get())).build();
            if(signalPathwayType.equalsIgnoreCase("class"))
                signalPathwayType="cclass";

            if(signalPathwayType.equalsIgnoreCase("domain"))
                signalPathwayType="type";
        }

        if(omicsType.trim().equalsIgnoreCase(QueryType.Transcriptomic.toString()))
            return omicsServicebean.getFoldChangesForApi(false,queryTargetBy, queryValue, doi,
                    species, signalPathwayType, signalPathwayId,
                    ps, organ, fc, fcMin, fcMax, significance, direction, (isCount == 1), afterId, countMax);
        else
            return omicsServicebean.getAntigenBindingScoresForApi(false,queryTargetBy, queryValue, doi,
                    species, signalPathwayType, signalPathwayId,
                    ps, organ, minScore, maxScore, (isCount == 1), afterId, countMax);
    }
}
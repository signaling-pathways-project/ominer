package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.cistromic.dto.SimpleQueryForm;
import edu.bcm.dldcc.big.nursa.model.omics.Consensome;
import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeSummary;
import edu.bcm.dldcc.big.nursa.model.omics.QueryForm;
import edu.bcm.dldcc.big.nursa.model.omics.dto.*;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.QueryParametersData;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.TmQueryResponse;
import edu.bcm.dldcc.big.nursa.services.ImplicitFcRange;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.ApisCacheManager;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.FileDownloadMemoryCache;
import edu.bcm.dldcc.big.nursa.services.utils.FileHelper;
import edu.bcm.dldcc.big.nursa.services.utils.InputStreamWithFileDeletion;
import edu.bcm.dldcc.big.nursa.util.filter.BaseFilter;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * transcriptomic Service Bean.
 * Provides dataset, experiment, datapoints and Consensome target gene services
 * @author mcowiti
 */
public class TranscriptomicRestServiceBean implements TranscriptomicsRestService {

    @Inject
    private OmicsServicebean omicsServicebean;

    @Inject
    private FileHelper fileHelper;

    @Inject
    private ConsensomeFilesBean consensomeFilesBean;

    @Context
    private ServletContext context;

    @Inject private FileDownloadMemoryCache fileDownloadMemoryCache;

    private static Logger log = Logger.getLogger(TranscriptomicRestServiceBean.class.getName());


    @PersistenceContext(unitName = "NURSA")
    private EntityManager em;


    private void addParams(org.hibernate.Query query,String type,String gene,String pathtype,Long pathid,Long psid,String oid,String s){

        query.setParameter("type", type);

        if(gene!=null)
            query.setParameter("gene", gene);

        if(pathid!=null)
            query  .setParameter("familyid", pathid);

        if(s!=null)
            query .setParameter("s", s.toLowerCase());
    }

    /**
     * TODO dataset page based select requires Min/Max fc for a given query
     * @param omics Omics Search Type
     * @param geneSearchType Gene search Type
     * @param gene gene or genelist
     * @param bsm bsm
     * @param doi dataset DOI
     * @param ps physiological System
     * @param organ organ
     * @param tissue tissue
     * @param species Species
     * @param pathwayCategory pathway category/type
     * @param signalingPathway pathway id
     * @param significance significance
     * @param direction directionr
     * @param fc Foldchange
     * @param fcMin Min FoldChange
     * @param fcMax Max foldChange
     * @param fcMax findMax find Highest foldChange
     * @return Response
     */
    public Response getTranscriptomicDatapoints(String omics,
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

        if(countMax.intValue() > OmicsServicebean.MAX_ABS_NUMBER_DATAPOINTS){
            return Response.status(400).entity("The TX Service cannot return the requested number of datapoints").build();
        }

        Map map=this.omicsServicebean.prepTxSimpleQueryForm(true,
                geneSearchType,gene,bsm,doi,ps, organ, tissue,
                species,pathwayCategory,signalingPathway,significance, direction,
                fc, fcMin, fcMax,findMax,reportBy,countMax);

        if(map.containsKey("error"))
            return Response.status(400).entity(map.get("error")).build();

        SimpleQueryForm sform=(SimpleQueryForm)map.get("sform");
        QueryForm queryForm=(QueryForm)map.get("qform");

        if(ApisCacheManager.queryCache.containsKey(sform.cacheKey())){
            log.log(Level.INFO,"TX cache hit");
            this.omicsServicebean.persistQueryId(sform,queryForm);
            TmQueryResponse<OmicsDatapoint> cacheResult=ApisCacheManager.queryCache.get(sform.cacheKey());
            cacheResult.getQueryForm().setId(queryForm.getId());
            return Response.ok().entity(cacheResult).build();
        }

        Map cmap=this.omicsServicebean.checkCountCriteria(true,false,true,QueryType.Transcriptomic,sform,queryForm,0L,countMax);
        this.omicsServicebean.persistQueryId(sform,queryForm);

        if(cmap.containsKey("error"))
            return (Response)cmap.get("error");

        int count=(Integer)cmap.get("count");

        long b2=System.currentTimeMillis();
        boolean byDatapoints=(reportBy.equalsIgnoreCase("datapoints"));

        //TODO still needed?
        if(sform.isFindMax){
            log.log(Level.WARNING,"TX is using isFindMax() FCs, this may slow queries ...");
            FoldChangeMinMax minMax=this.omicsServicebean.findLargestFoldChangeInQuery(sform,QueryType.Transcriptomic);
            Double largest=this.omicsServicebean.getLargestFC(minMax);
            queryForm.setLargestFoldChange(largest);
            if((System.currentTimeMillis()-b2)>2000)
                log.log(Level.WARNING,"TX isFindMax query very.slow ms="+(System.currentTimeMillis()-b2));
        }

        List<OmicsDatapoint> datalist=null;
        List<OmicsDatapointReport> rdatalist=null;
        TmQueryResponse<OmicsDatapoint> result=null;

        //SRX has points dups. TX perhaps not???
        Integer maxCountBeforeAggregate=countMax*2;
        if(byDatapoints) {
            datalist = (List<OmicsDatapoint>) this.omicsServicebean.query(sform, true, false,true,QueryType.Transcriptomic, byDatapoints,0L,maxCountBeforeAggregate);
            result=new TmQueryResponse<OmicsDatapoint>(new Long(count),queryForm, datalist,null);
        }else {
            rdatalist = (List<OmicsDatapointReport>) this.omicsServicebean.query(sform, true, false,true,QueryType.Transcriptomic, byDatapoints,0L,maxCountBeforeAggregate);
            datalist= new ArrayList<>();
            for(OmicsDatapointReport report:rdatalist){
                datalist.addAll(report.getDatapoints());
            }
            result= new TmQueryResponse<OmicsDatapoint>(new Long(count),queryForm, null,rdatalist);
        }

        log.log(Level.INFO,"TX.query= # #datapoints => ms="+count+"/"+datalist.size()+"=>"+(System.currentTimeMillis()-b));

        if(datalist.size() > OmicsServicebean.MAX_ABS_NUMBER_DATAPOINTS){
            TmQueryResponse<OmicsDatapoint> tooMuch=
                    new TmQueryResponse<OmicsDatapoint>(queryForm, new Long(datalist.size()), null);
            if((System.currentTimeMillis()-b)>3000)
                log.log(Level.WARNING,"TX.query=data over limit.slow unique # / #datapoints => ms="+count+"/"+datalist.size()+"=>"+(System.currentTimeMillis()-b));
            ApisCacheManager.queryCache.putIfAbsent(sform.cacheKey(),tooMuch);
            return Response.status(413).entity(tooMuch).build();
        }

        if(datalist.size() > countMax.intValue()){//TranscriptomineService.MAX_NUMBER_DATAPOINTS){
            TmQueryResponse<OmicsDatapoint> tooMuch=
                    new TmQueryResponse<OmicsDatapoint>(queryForm, new Long(datalist.size()), null);
            if((System.currentTimeMillis()-b)>3000)
                log.log(Level.WARNING,"TX.query=too much data very.slow unique # / #datapoints => ms="+count+"/"+datalist.size()+"=>"+(System.currentTimeMillis()-b));
            ApisCacheManager.queryCache.putIfAbsent(sform.cacheKey(),tooMuch);
            return Response.ok().entity(tooMuch).build();
        }

        List<OmicsDatapoint> fresults= datalist;
        if(byDatapoints)
            result.setResults(fresults);

        Collections.sort(fresults);
        int num=fresults.size()-1;
        if(fresults.size() > 0){
            queryForm.setMinFoldChange(((OmicsDatapoint)fresults.get(0)).getFoldChange());
            queryForm.setMinFoldChangeRaw(((OmicsDatapoint)fresults.get(0)).getFoldChangeRaw());
            queryForm.setMaxFoldChange(((OmicsDatapoint)fresults.get(num)).getFoldChange());
            queryForm.setMaxFoldChangeRaw(((OmicsDatapoint)fresults.get(num)).getFoldChangeRaw());
        }

        result.setCount(new Long(count));
        ApisCacheManager.queryCache.putIfAbsent(sform.cacheKey(),result);

        if((System.currentTimeMillis()-b)>3000)
            log.log(Level.WARNING,"TX query data returns very.slow unique # /#datapoints => ms="+count+"/"+datalist.size()+"=>"+(System.currentTimeMillis()-b));

        return Response.ok().entity(result).build();
    }

    @Override
    public Response findBasicDatapointsByExpId(Long expId, String fc, String fcMin, String fcMax, String direction, String absoluteSort,String findMax,Integer countMax) {

        SimpleQueryForm sform=null;
        long b=System.currentTimeMillis();
        try {
            sform = omicsServicebean.getTxSimpleQueryForm( null, null, null, null, null,
                    null, null, null, null, null
                    , null, direction, fc, fcMin, fcMax, findMax, null, countMax);
        }catch (Exception e){
            return Response.status(400).entity(OmicsServicebean.INPUT_BOTH_FC_AND_FC_RANGE).build();
        }

        int absMax=TranscriptomineService.MAX_NUMBER_DATAPOINTS_ABSOLUTE;
        if(countMax > absMax)
            countMax=absMax;

        QueryForm queryForm= new QueryForm();
        queryForm.setExperimentId(expId.toString());

        List<OmicsMinimalDatapoint> list=omicsServicebean.findOmicsFoldChangesByExperimentId(expId,sform,countMax);


        String finalQueryFcMin=(sform.foldChangeMin!=null)?sform.foldChangeMin.toString():null;
        String finalQueryFcMax=(sform.foldChangeMax!=null)?sform.foldChangeMax.toString():null;

        if(list.size() == 0 && !sform.isRange){
            log.log(Level.INFO,"No datapoints byExp for going implicit ranges ");
            for(ImplicitFcRange implicitFc:ImplicitFcRange.values()){ //order implicit
                finalQueryFcMin= String.valueOf(implicitFc.getMinFc());
                finalQueryFcMax=String.valueOf(TranscriptomineService.MAX_FC_BY_DEFOLT);
                sform.foldChange=null;
                sform.foldChangeMin=implicitFc.getMinFc();
                sform.foldChangeMax=OmicsServicebean.MAX_FC_BY_DEFOLT;
                sform.isRange=true;
                list=omicsServicebean.findOmicsFoldChangesByExperimentId(expId,sform,countMax);
                if(list.size() > 0)
                    break;
                else
                    log.log(Level.INFO,"No datapoints @ImplicitFcRange "+sform.foldChangeMin+"=>"+sform.foldChangeMax);
            }
        }


        if(sform.isFindMax) {
            FoldChangeMinMax minMax = this.omicsServicebean.findLargestFoldChangeInExperiment(expId);
            Double largest = omicsServicebean.getLargestFC(minMax);
            queryForm.setLargestFoldChange(largest);
            if((System.currentTimeMillis()-b)>500)
                log.log(Level.WARNING,"datapoints.byExp  findMax slow (ms)->"+(System.currentTimeMillis()-b));
        }


        if(!sform.isRange){
            Collections.sort(list, OmicsMinimalDatapoint.Comparators.ABS);
        } else{
            if(absoluteSort == null || absoluteSort.length() == 0)
                Collections.sort(list,OmicsMinimalDatapoint.Comparators.NORMAL);
            else
                Collections.sort(list,OmicsMinimalDatapoint.Comparators.ABS);
        }

        if((System.currentTimeMillis()-b)>500)
            log.log(Level.WARNING,"datapoints.byExp  sort slow (ms)->"+(System.currentTimeMillis()-b));

        int num=list.size()-1;
        if(list.size() > 0){
            QueryParametersData queryParameter= new QueryParametersData();
            queryParameter.setFoldChangeMin(finalQueryFcMin);
            queryParameter.setFoldChangeMax(finalQueryFcMax);
            queryForm.setQueryParameter(queryParameter);
            queryForm.setMinFoldChange(((OmicsMinimalDatapoint)list.get(0)).getFoldChange());
            queryForm.setMinFoldChangeRaw(((OmicsMinimalDatapoint)list.get(0)).getFoldChangeRaw());
            queryForm.setMaxFoldChange(((OmicsMinimalDatapoint)list.get(num)).getFoldChange());
            queryForm.setMaxFoldChangeRaw(((OmicsMinimalDatapoint)list.get(num)).getFoldChangeRaw());
        }

        TmQueryResponse<OmicsMinimalDatapoint> result=null;
        result= new TmQueryResponse<OmicsMinimalDatapoint>(new Long(list.size()),queryForm, list,null);
        result.setCount(new Long(num+1));

        if((System.currentTimeMillis()-b)>1000)
            log.log(Level.WARNING,"findBasicDatapointsByExpId or Top genes Too slow (ms) "+(System.currentTimeMillis()-b));
        return Response.ok().entity(result).build();
    }

    @Override
    public Response countTranscriptomicDatapoints(String omics, String geneSearchType,
                                                  String gene,
                                                  String bsm,
                                                  String doi,
                                                  String ps, String organ, String tissue, String species,
                                                  String pathwayType, String signalingPathway,
                                                  String significance, String direction,
                                                  String foldChange, String foldChangeMin, String foldChangeMax) {

        Optional<Double> of=this.omicsServicebean.getDoubleValue(foldChange);
        Optional<Double> ofMin=this.omicsServicebean.getDoubleValue(foldChangeMin);
        Optional<Double> ofMax=this.omicsServicebean.getDoubleValue(foldChangeMax);
        Optional<Double> opValue=this.omicsServicebean.getDoubleValue(significance);

        boolean isRange=(ofMin.isPresent() && ofMax.isPresent());
        if(isRange && of.isPresent()){
            return Response.status(400).entity(OmicsServicebean.INPUT_BOTH_FC_AND_FC_RANGE).build();
        }

        Double f=(of.isPresent())?of.get():null;
        Double fMin=(ofMin.isPresent())?ofMin.get():null;
        Double fMax=(ofMax.isPresent())?ofMax.get():null;
        Double pValue=(opValue.isPresent())?opValue.get():null;

        SimpleQueryForm sform=new SimpleQueryForm(omics,geneSearchType, gene,doi,
                ps, organ,tissue,species, pathwayType, signalingPathway,
                pValue,f,fMin,fMax,direction,bsm,null);


        String errorMsg=this.omicsServicebean.getSimpleInputError(sform);

        if(errorMsg != null)
            return Response.status(400).entity(errorMsg).build();

        errorMsg=this.omicsServicebean.validatePathwayInput(signalingPathway,pathwayType);

        if(errorMsg != null)
            return Response.status(400)
                    .entity(errorMsg).build();

        int count=this.omicsServicebean.countDatapoints(sform,QueryType.Transcriptomic,false,true,0L);
        return Response.ok().entity(count).build();
    }


    @Override
    public Response findFoldchangeDetailsByDatapointID(Long datapointid) {

        if(datapointid == null)
            return Response.status(400).entity("Missing datapointId").build();

        Optional<OmicsFoldChangeDetail> datapoint=omicsServicebean.findOmicsFoldChangeDetail(datapointid);
        if(datapoint.isPresent())
            return Response.ok().entity(datapoint.get()).build();
        else
            return Response.status(400).entity("No Transcriptomic datapoint with that id").build();
    }

    public Response downloadTxExcelFile(String queryId, String fileName, String email) {
        InputStream targetStream = null;
        try {

            if(queryId == null || queryId.length() == 0)
                return Response.status(404).entity("Please provide an existing queryId to download").build();

            Optional<SimpleQueryForm> form=this.omicsServicebean.getSimpleQueryForm(queryId);
            if(!form.isPresent()){
                log.log(Level.WARNING, queryId+ " This TX file download link expired");
                return Response.status(404).entity("That file download link expired. Please re-submit the query to get a new File download link.").build();
            }

            SimpleQueryForm queryForm=form.get();
            int max=OmicsServicebean.MAX_NUMBER_XLS;
            List<OmicsDatapoint> datalist=(List<OmicsDatapoint>)omicsServicebean.query(queryForm,false,false,true,QueryType.Transcriptomic,true,0L,max);
            File file = fileHelper.generateTranscriptomicQueryExelBook(datalist, queryForm, OmicsServicebean.MAX_NUMBER_XLS);

            targetStream = new InputStreamWithFileDeletion(file);
        } catch (IOException e) {
            log.log(Level.WARNING, "Could not write results to a temp file. Sending error file", e);
            targetStream = context.getResourceAsStream("/WEB-INF/error.xls");
        }

        Response.ResponseBuilder response = Response.ok((Object) targetStream);
        response.header("Content-Disposition",
                "attachment; filename=" + fileName + ".xls");
        return response.build();
    }

    public Response getLargestFoldChange(String queryId){

        if(queryId == null || queryId.length() == 0)
            return Response.status(404).entity("Please provide an existing queryId").build();

        Optional<SimpleQueryForm> form=omicsServicebean.getSimpleQueryForm(queryId);
        if(!form.isPresent()){
            log.log(Level.WARNING, "That queryId link expired");
            return Response.status(404).entity("That queryid expired. Please re-submit the query to get a new query id.").build();
        }

        SimpleQueryForm queryForm=form.get();
        FoldChangeMinMax minMax=omicsServicebean.findLargestFoldChangeInQuery(queryForm, QueryType.Transcriptomic);
        Double max=omicsServicebean.getLargestFC(minMax);
        return Response.ok().entity(max).build();
    }

    @Override
    public Response findSummaries() {
        List<ConsensomeSummary> list=(List<ConsensomeSummary>)omicsServicebean.findConsensomeSummary(
                QueryType.Transcriptomic,null,null,null,null,null);
        return Response.ok().entity(list).build();
    }

    @Override
    public Response findSummary(String family, String ps, String organ, String species, String date) {

        log.log(Level.INFO,"@findSummary family/species ="+family+"/"+species);
        ////FR PS/O can be null 10.2018 if(family == null && ps == null && organ == null && species == null)
        if(family == null && species == null)
            return Response.status(400).entity(OmicsServicebean.inputMessage).build();

        Map<String,String> input=this.omicsServicebean.recastFamilyPsOrganOfNull(family,ps,organ);
        family=input.get("family");
        ps=input.get("ps");
        organ=input.get("organ");

        List<ConsensomeSummary> list=null;
        list = (List<ConsensomeSummary>) this.omicsServicebean.findConsensomeSummary(QueryType.Transcriptomic, null, family, ps, organ, species);

        log.log(Level.INFO,"@findSummary # list ="+list.size());
        return (list.size()>0)?Response.ok().entity(list.get(0)).build():null;
    }

    @Override
    public Response findSummaryByDoi(String doi) {
        if(doi == null )
            return Response.status(400).entity(OmicsServicebean.inputMessageDoi).build();

        if(doi.length() > 0 ){
            BaseFilter f= new BaseFilter();
            if(!f.cleanDoi(doi))
                return Response.status(400).entity(OmicsServicebean.inputMessageDoi).build();
        }
        List<ConsensomeSummary> list=(List<ConsensomeSummary>)omicsServicebean.findConsensomeSummary(QueryType.Transcriptomic,doi,null,null,null,null);
        return (list.size()>0)?Response.ok().entity(list.get(0)).build():null;
    }

    @Override
    public Response findConsensomesWithCount(String doi, String family, String ps, String organ, String species,
                                             double percentile,Integer count, Integer page, String date) {

        SimpleQueryForm sform=this.omicsServicebean.formSimpleQueryForm(QueryType.Transcriptomic.toString(),"consensome", null,doi,
                ps, organ,null,species, null, family,
                null,null,null,percentile);

        if(doi == null)
            if(family == null && ps == null && organ == null && species == null)
                return Response.status(400).entity(OmicsServicebean.inputMessage).build();

        if(page != null && count == null){
            return Response.status(400).entity(OmicsServicebean.pageNeedCountMessage).build();
        }

        if(doi != null){
            BaseFilter f= new BaseFilter();
            if(!f.cleanDoi(doi))
                return Response.status(400).entity(OmicsServicebean.inputMessageDoi).build();
        }else{
            Map<String,String> input=this.omicsServicebean.recastFamilyPsOrganOfNull(family,ps,organ);
            family=input.get("family");
            ps=input.get("ps");
            organ=input.get("organ");
        }

        QueryParametersData queryParametersData=this.omicsServicebean.formOldQueryForm(
                "consensome",null,doi,ps,organ,null,null,species,
                null,family,
                null,null,null,null,null);

        QueryForm queryForm= new QueryForm();
        queryForm.setQueryParameter(queryParametersData);
        this.omicsServicebean.persistQueryId(sform,queryForm);

        //TODO Most Consensome data will be >1000
        //there is no way to (prompt user) to filter, so will always return max 1000
        //if have ALL entries, could prompt to filer
        //if(total > UI_DATA_MAX)return Response.status(413).entity(tooManyMessage).build();

        int total=this.omicsServicebean.countConsensomes(QueryType.Transcriptomic,doi,family, ps, organ, species,percentile);
        log.log(Level.INFO, "TX consensomes count Datapoints ="+total);


        int max=CistromicsRestService.UI_DATA_MAX;
        if(count != null){
            max=(count > CistromicsRestService.UI_DATA_MAX)?max:count;
        }

        int startPage=0;
        if(page != null){
            startPage=page * max;
        }

        ConsensomeDataWrap<Consensome> data=null;
        ConsensomeResult<Consensome> result=omicsServicebean.findConsensomeResult(QueryType.Transcriptomic,doi,family, ps, organ, species,percentile,max, startPage, date);

        if(result != null) {
            data = new ConsensomeDataWrap<Consensome>(total, (ConsensomeSummary) result.getSummary(), result.getResults());
            data.setQueryForm(queryForm);
            return Response.ok().entity(data).build();
        }
        return Response.status(404).entity("No Transcriptomic Consensome with that criteria").build();
    }

    @Override
    public Response downloadConsensomeExcelFile(String family, String ps, String organ, String species, String version,
                                                String email) {
        return Response.ok().entity("Download by Consensome key not implemented yet").build();
    }

    @Override
    public Response downloadConsensomeFileByDoiOrQueryId(String queryId, String doi,double percentile) {
        InputStream targetStream = null;

        if((doi == null || doi.length() == 0) && (queryId == null || queryId.length() == 0))
            return Response.status(400).entity("Need DOI or QueryId to download a File").build();

        try {
            List<Consensome> datalist=null;
            String query=null;

            if(doi == null || doi.length() == 0) {
                if (queryId == null || queryId.length() == 0)
                    return Response.status(404).entity("Please provide an existing queryId or DOI to download data").build();

                Optional<SimpleQueryForm> form = omicsServicebean.getSimpleQueryForm(queryId);

                if (!form.isPresent()) {
                    log.log(Level.WARNING, "That file download link expired");
                    return Response.status(404).entity(queryId + " : This file download link expired. Please re-submit the query to get a new File download link.").build();
                }
                SimpleQueryForm queryForm = form.get();
                datalist = omicsServicebean.findTxConsensomes(false,true,queryForm.signalingPathway,null,
                        queryForm.ps,
                        queryForm.organ,
                        queryForm.species,queryForm.percentile,
                        OmicsServicebean.MAX_NUMBER_XLS, 0,null);

                    if(datalist.size()>0) {
                        doi = datalist.get(0).getKey().getDoi();
                        query = datalist.get(0).getKey().toString();
                    }
            }else{
                BaseFilter f= new BaseFilter();
                if(!f.cleanDoi(doi))
                    return Response.status(400).entity(OmicsServicebean.inputMessageDoi).build();

                datalist=omicsServicebean.findTxConsensomesByDoi(false,true,doi,percentile,OmicsServicebean.MAX_NUMBER_XLS, 0,null);
                query=doi;
            }

            long fdown=System.currentTimeMillis();
            File file = consensomeFilesBean.generateTranscriptomicConsensomeExcelData(query,doi,datalist, omicsServicebean.MAX_NUMBER_XLS);

            if((System.currentTimeMillis()-fdown)>10000)
                log.log(Level.WARNING,"TX file download taking too long time (ms), consider mongoDb caching="+(System.currentTimeMillis()-fdown));

            targetStream = new InputStreamWithFileDeletion(file);
        } catch (IOException e) {
            log.log(Level.WARNING, "Could not write results to a temp file. Sending error file instead", e);
            targetStream = context.getResourceAsStream("/WEB-INF/empty_or_error_file.xls");
        }

        String fileName="tX_consensome_file";
        if(doi!=null)
            fileName=doi.substring(doi.indexOf("/")+1);

        Response.ResponseBuilder response = Response.ok((Object) targetStream);
        response.header("Content-Disposition",
                "attachment; filename=" + fileName + ".xls");
        return response.build();
    }
}
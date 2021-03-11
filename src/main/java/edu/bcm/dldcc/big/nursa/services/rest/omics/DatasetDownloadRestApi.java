package edu.bcm.dldcc.big.nursa.services.rest.omics;

import io.swagger.annotations.Api;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Downloads-
 * Dataset downloads page for those missing in storage ( ie /datasetfiles/ )
 * @author amcowiti
 */
@Path("/downloads/")
@Api(value = "/downloads/", tags = "Datasets Downloads (Internal)")
public class DatasetDownloadRestApi {

    private static Logger log = Logger.getLogger(DatasetDownloadRestApi.class.getName());

    @Inject
    private DatasetDownloadBean datasetDownloadBean;
    private final static Integer PAGE_SIZE=5000;

    @GET
    @Path("dataset/count")
    @Produces("application/json")
    public Integer countDatasetsForTsv(@QueryParam("id")  Integer id,@QueryParam("type") @DefaultValue("0") Integer type){
        return datasetDownloadBean.getNumberOfDatasetRows(id,(type==0));
    }

    @GET
    @Path("dataset/tx/{id}")
    @Produces("plain/text")
    public Response downloadTxTsv(@PathParam("id") Integer id, @QueryParam("rows") @DefaultValue("200000") Integer rows){
        int count=datasetDownloadBean.getNumberOfDatasetRows(id,true);
        int pages=(count < PAGE_SIZE)?1:((count/PAGE_SIZE)+1 );
        return datasetDownloadBean.downloadPagedData(id,count,PAGE_SIZE,pages,0,true);
    }

    @GET
    @Path("dataset/cis/{id}")
    @Produces("plain/text")
    public Response downloadCisTsv(@PathParam("id") Integer id,@QueryParam("rows") @DefaultValue("200000") Integer rows){
        int count=datasetDownloadBean.getNumberOfDatasetRows(id,false);
        if(count>200000)
            log.log(Level.SEVERE,"Many rows to downoad Cistromic dataset. Dataset/#rows "+id+" / "+count);

        int pages=(count < PAGE_SIZE)?1:((count/PAGE_SIZE)+1 );
        return datasetDownloadBean.downloadPagedData(id,count,PAGE_SIZE,pages,0,false);
    }
}

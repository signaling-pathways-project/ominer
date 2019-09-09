package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.cistromic.DatasetViewDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.NursaDatasetDTO;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import java.util.List;


/**
 * Created by alexey on 4/13/16.
 *
 * @author mcowiti
 */

@Path("/dataset/")
@Api(value = "/dataset/", tags = "Datasets(Internal)")
public class DatasetService {

    @Inject
    private TranscriptomineService omineService;
    
    @Inject
    private DatasetServiceBean datasetServiceBean;

    private static Logger log = Logger.getLogger(DatasetService.class);


    /**
     * Post Cistromics mmethod
     * @param noacm
     * @return
     */
    @GET
    @Path("getAll")
    @Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Get List of TX,SRX datasets", notes = "Returns all Cis,SRX datasets.", 
	response = DatasetViewDTO.class,responseContainer = "List")
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="List of TX,SRX Datasets", response=DatasetViewDTO.class, responseContainer = "List"),
	})
    public List<DatasetViewDTO> getDatasets(
    		@ApiParam(value = "noacm",required=false) @QueryParam("noacm")  @DefaultValue("y") String  noacm,
    		@ApiParam(value = "type",required=false)  @QueryParam("type")  String  type)
    {
    	log.log(Level.INFO,"@getDatasets noacm/type="+noacm+"/"+type);
        return this.datasetServiceBean.getDatasets(noacm,type);
    }
    
    /**
     * Older  method
     * @return
     */
    @GET
    @Path("getAll0")
    @Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "(Retired)Get All Active datasets", notes = "(Retired)Returns all active datasets.", 
	response = NursaDatasetDTO.class,responseContainer = "List")
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="List of active Datasets", 
			response=NursaDatasetDTO.class, responseContainer = "List"),
	})
    @Deprecated
    public List<NursaDatasetDTO> getAllActiveDatasets()
    {
    	return omineService.getAllDatasets();
    }
}

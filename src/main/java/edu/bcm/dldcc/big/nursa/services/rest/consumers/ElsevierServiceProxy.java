package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexey on 9/28/15.
 * @author mcowiti
 */

@Path("/elsevier")
@Api(value = "/elsevier/", tags = "Elsevier,DOI Convertor")
public class ElsevierServiceProxy {
	private static Logger log = Logger.getLogger(ElsevierServiceProxy.class);
    private static final Map <String, String> notLoadedToNcbi = new HashMap<String, String>();
    static {
        notLoadedToNcbi.put("10.1016/j.toxlet.2010.10.008","20937368");
        notLoadedToNcbi.put("10.1016/j.yrtph.2010.08.002", "20801182");
        notLoadedToNcbi.put("10.1016/j.steroids.2010.06.010", "20600201");
        notLoadedToNcbi.put("10.1016/j.reprotox.2010.05.013", "20488242");
        notLoadedToNcbi.put("10.1016/j.tox.2010.02.008", "20170705");
        notLoadedToNcbi.put("10.1016/j.stem.2009.12.009", "20096661");
        notLoadedToNcbi.put("10.1016/j.pnpbp.2009.10.018","19883713");
        notLoadedToNcbi.put("10.1016/j.mce.2009.09.030", "19818377");
        notLoadedToNcbi.put("10.1016/j.mce.2009.08.030", "19744542");
        //FIXME Apollo these need to be in DB
    }

    @Context
    ServletContext context;

    @Inject
    private TranscriptomineService omineService;
    
    @Inject
    private NcbiServicesBean ncbiServicesBean;

    @GET
    @Path("/doibanner/{elsevierDOI : (.+)?}")
    @Produces("image/png")
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Get Elsevier Banner", notes = "Elsevier Banner.", response = InputStream.class)
   	@ApiResponses(value = { 
   	@ApiResponse(code = 200, message="Elsevier Banner", response=InputStream.class)
   	})
    public Response doibanner( @ApiParam(value = "elsevier DOI", required=true) @PathParam("elsevierDOI") String elsevierDOI) {
    	 
    	if(elsevierDOI == null)
    		return Response.status(400).entity("Elsevier DOI required").build();
    	
    	String pubmedId = ncbiServicesBean.elsevierDoiToPubmed(elsevierDOI);
        
        if (null == pubmedId) {
            pubmedId = notLoadedToNcbi.get(elsevierDOI);
        }
        InputStream stream;
        if (null != pubmedId && !"".equals(pubmedId)) {
            if (omineService.numberOfAssociatedDatasets(pubmedId) > 0) {
                stream = context.getResourceAsStream("/WEB-INF/banner.png");
            } else {
                stream = context.getResourceAsStream("/WEB-INF/1by1.png");
            }

        } else {
            stream = context.getResourceAsStream("/WEB-INF/1by1.png");
        }
        Response.ResponseBuilder response = Response.ok((Object) stream);
        response.header("Content-Type", "image/png");
        return response.build();
    }

    /**
     * Convert the elsevier DOI to a pubmed, and use the pubmed to locate a local dataset
     * @param response
     * @param elsevierDOI
     */
    @GET
    @Path("/getdataset/{elsevierDOI : (.+)?}")
    @ApiOperation(value = "Get Nursa Dataset page from Elsevier DOI", notes = "Nursa Dataset page from Elsevier DOI.")
   	@ApiResponses(value = { 
   	@ApiResponse(code = 302, message="Redirect to Nursa Dataset Page")
   	})
    public void getDataset(@Context HttpServletResponse response, 
    		@ApiParam(value = "Elsevier DOI", required=true)  @PathParam("elsevierDOI") String elsevierDOI) {
    	
    	if(elsevierDOI == null)
    		return ;
    	
    	 String pubmedId = ncbiServicesBean.elsevierDoiToPubmed(elsevierDOI);
        log.debug("@getDataset elsevierDOI->pubmedId="+elsevierDOI+" -> "+pubmedId);
        
        //This part is added due to the fact that not all Elsevier dois were deposited to NCBI
         if (null == pubmedId) {
            pubmedId = notLoadedToNcbi.get(elsevierDOI);
        }
        if (null != pubmedId && !"".equals(pubmedId)) {
            //TODO test the performance if we find that retract the whole list is long than swich just to doi
            List<NURSADataset> dsetlist = omineService.findDatasetByPubmedId(pubmedId);
            if (dsetlist.size() == 1) {
                String redirectUrl = "/nursa/datasets/dataset.jsf?doi="+dsetlist.get(0).getDoi().getDoi();
                try {
                    response.sendRedirect(redirectUrl);
                } catch (IOException e) {
                    log.error("Cannot redirect to: "+redirectUrl, e);
                }
            } else if (dsetlist.size() > 1) {
                String redirectUrl = "/nursa/datasets/superDataset.jsf?pubmedId="+pubmedId;
                try {
                    response.sendRedirect(redirectUrl);
                } catch (IOException e) {
                    log.error("Cannot redirect to superDataset: "+redirectUrl, e);
                }
            } else {
                log.error(String.format("Cannot find associated datasets for elsevierDOI: %s pubmedId: %s. In theory we should not end up here. Redirecting to dset page",elsevierDOI, pubmedId));
                try {
                    response.sendRedirect("/nursa/datasets/index.jsf");
                } catch (IOException e) {
                    log.error("Cannot redirect for this strange case, read previous error msg", e);
                }
            }
        } else {
            log.warn(String.format("PubmedId is null or empty for doi: %s. Redirecting to base dataset page",elsevierDOI));
            try {
                response.sendRedirect("/nursa/datasets/index.jsf");
            } catch (IOException e) {
                log.error("Cannot redirect for the strange case, read previous error msg", e);
            }
        }
    }
}

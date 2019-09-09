package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine;

import edu.bcm.dldcc.big.nursa.model.cistromic.dto.SummaryData;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.SummaryDataGraph;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.SunBurstData;
import edu.bcm.dldcc.big.nursa.services.GraphType;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import edu.bcm.dldcc.big.nursa.services.utils.Cache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * TM Graph REST API endpoint
 * @author mcowiti
 *
 */
@Path("/transcriptomine/graph")
@Api(value = "/transcriptomine/graph/",tags="Transcriptomine Graphs(SunBurst)")
public class TranscriptomineGraphRestApi {

	@Inject
	private TranscriptomineService omineService;

	@GET
	@Path("/summary")
	@Produces("application/json")
	@ApiOperation(value = "Find summary",notes = "Summary Data",response = SummaryData.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Summary Data", response=SummaryData.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	public SummaryData getSummaryData()
	{
        SummaryData result = (SummaryData) Cache.lookUp("listSummary");
        if(null == result)
        {
            result = omineService.listSummary();
            Cache.put("listSummary", result);
        }
		return result;
	}
	
	@GET
	@Path("/sunbursts")
	@Produces("application/json")
	@ApiOperation(value = "List summary graphs",notes = "List Summary Graphs",
	response = SummaryDataGraph.class,responseContainer = "List")
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="List Summary Graphs", response=SummaryDataGraph.class,responseContainer = "List"),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	public List<SummaryDataGraph<SunBurstData>> getAllSummaryDataGraphs()
	{
        List<SummaryDataGraph<SunBurstData>> result = (List<SummaryDataGraph<SunBurstData>>) Cache.lookUp("getAllSummaryDataGraphs");
        if(null == result)
        {
            result = omineService.getAllSummaryGraphData(SunBurstData.class);
            Cache.put("getAllSummaryDataGraphs", result);
        }
        return result;
	}
	
	@GET
	@Path("/sunburst/species")
	@Produces("application/json")
	@ApiOperation(value = "Summary graphs by Species",notes = "Summary graphs by Species",
	response = SummaryDataGraph.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Summary graphs by Species", response=SummaryDataGraph.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	
	public SummaryDataGraph<SunBurstData> getSummaryDataGraphBySpecies()
	{
        SummaryDataGraph<SunBurstData> result = (SummaryDataGraph<SunBurstData>) Cache.lookUp("getSummaryDataGraphBySpecies");
        if(null == result)
        {
            result = omineService.getSumamryGraph(SunBurstData.class,GraphType.expressionBySpecies);
            Cache.put("getSummaryDataGraphBySpecies",result);
        }
		return result;
	}

	@GET
	@Path("/sunburst/molecules")
	@Produces("application/json")
	@ApiOperation(value = "Summary graphs by Molecule",notes = "Summary graphs by Molecule",
	response = SummaryDataGraph.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Summary graphs by Molecule", response=SummaryDataGraph.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	public SummaryDataGraph<SunBurstData> getSummaryDataGraphByMolecules()
	{
        SummaryDataGraph<SunBurstData> result = (SummaryDataGraph<SunBurstData>) Cache.lookUp("getSummaryDataGraphByMolecules");
        if(null == result)
        {
            result = omineService.getSumamryGraph(SunBurstData.class,GraphType.expressionByMolecules);
            Cache.put("getSummaryDataGraphByMolecules",result);
        }
		return result;
	}
	
	@GET
	@Path("/sunburst/rna")
	@Produces("application/json")
	@ApiOperation(value = "Summary graphs by DNA",notes = "Summary graphs by DNA",
	response = SummaryDataGraph.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Summary graphs by DNA", response=SummaryDataGraph.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	public SummaryDataGraph<SunBurstData> getSummaryDataGraphByRna()
	{
        SummaryDataGraph<SunBurstData> result = (SummaryDataGraph<SunBurstData>) Cache.lookUp("getSummaryDataGraphByRna");
        if (null == result)
        {
            result = omineService.getSumamryGraph(SunBurstData.class,GraphType.expressionByRna);
            Cache.put("getSummaryDataGraphByRna", result);
        }
		return result;
	}
}

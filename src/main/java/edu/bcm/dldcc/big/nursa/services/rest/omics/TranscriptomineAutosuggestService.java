package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.omics.dto.AutosuggestList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;

/**
 * Auto Suggest REST service interface
 * @author mcowiti
 *
 */
@Path("/omics/auto")
@Api(value = "/omics/auto/",tags="Omics Gene,GoTerm,Disease and Tissue AutoSuggest")
public interface TranscriptomineAutosuggestService {

	public final static Integer MAX_NUMBER_AUTOSUGGEST=5;

	/**
	 * Method returns (auto suggests) {@link AutosuggestList} 
	 * If  species/taxon is specified, the Genes is filtered for species
	 * Note on symbols : See FR FR2.4.1,FR FR2.4.2
	  * @param symbol the symbol fragment to search. Uses ANY POSITION
	 * @param count the number of result to return
	  * @param taxonId the taxonId of the species to filter by
	 * @param synonym whether to search on synonyms, default, yes ( 1)
	 */
	@GET
	@Path("/gene/")
	 @Produces("application/json")
	@ApiOperation(value = "Auto complete Gene",notes = "Auto complete Gene",
	response = AutosuggestList.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Autocompleted Genes", response=AutosuggestList.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	public AutosuggestList<?> autoCompleteByGene(
			@ApiParam(value = "Symbol", required = true) @QueryParam("symbol")  String symbol,
			@ApiParam(value = "Count", required = false) @QueryParam("countDatapoints") @DefaultValue("250") Integer count,
			@ApiParam(value = "Taxon Id", required = false) @QueryParam("taxonId") @DefaultValue("0") Integer taxonId,
			@ApiParam(value = "Synonym", required = false) @QueryParam("synonym") @DefaultValue("1") Integer synonym);
	
	
	/**
	 * Auto complete Molecules
	 * @param symbol
	 * @param count
	 * @param synonym
	 * @return
	 */
	@GET
	@Path("/molecule/")
	@Produces("application/json")
	@ApiOperation(value = "Autocomplete Molecules",notes = "Autocomplete Molecules",
	response = AutosuggestList.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Autocompleted Molecules", response=AutosuggestList.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	public AutosuggestList<?> autoCompleteMolecule(
			@ApiParam(value = "Symbol", required = true) @QueryParam("symbol") String symbol,
			@ApiParam(value = "Count", required = false) @QueryParam("countDatapoints") @DefaultValue("250") String count,
			@ApiParam(value = "Synonym", required = false) @QueryParam("synonym") @DefaultValue("1") Integer synonym);
	/**
	 * Auto complete by GOTERMS
	 * @param symbol
	 * @param count
	 * @param taxonId
	 * @return
	 */
	@GET
	@Path("/goterm/")
	@Produces("application/json")
	@ApiOperation(value = "Autocomplete Go Terms",notes = "Autocomplete Go Terms",
	response = AutosuggestList.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Autocompleted Go Terms", response=AutosuggestList.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	public AutosuggestList<?> autoCompleteByGoTerm( 
		@ApiParam(value = "Symbol", required = true) @QueryParam("symbol") @DefaultValue("A") String symbol,
		@ApiParam(value = "Count", required = false) @QueryParam("countDatapoints") @DefaultValue("25") Integer count,
		@ApiParam(value = "Taxon Id", required = false) @QueryParam("taxonId") @DefaultValue("0") Integer taxonId);
	
	/**
	 * Auto complete by Omim
	 * @param symbol
	 * @param count
	 * @param synonym
	 * @return
	 */
	@GET
	   @Path("/disease/")
	   @Produces("application/json")
	@ApiOperation(value = "Autocomplete Disease Terms",notes = "Autocomplete Disease Terms",
	response = AutosuggestList.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Autocompleted Disease Terms", response=AutosuggestList.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	public AutosuggestList<?>  autoCompleteByDisease(
		@ApiParam(value = "Symbol", required = true) @QueryParam("symbol") @DefaultValue("A") String symbol,
		@ApiParam(value = "Count", required = false) @QueryParam("countDatapoints") @DefaultValue("250") Integer count,
		@ApiParam(value = "Synonym", required = false) @QueryParam("synonym") @DefaultValue("1") Integer synonym);
	
	/**
	 * Auto complete Tissue and Cell lines
	 * @param symbol
	 * @param count
	 * @param taxonId
	 * @return
	 */
	@GET
	@Path("/tissues/")
	@Produces("application/json")
	@ApiOperation(value = "Autocomplete Tissue And Cell Lines",notes = "Autocomplete Tissue And Cell Lines",
	response = AutosuggestList.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Autocompleted Tissue And Cell Lines", response=AutosuggestList.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	public AutosuggestList<?>  autoCompleteTissueAndCellLines(
			@ApiParam(value = "Symbol", required = true) @QueryParam("symbol") String symbol,
			@ApiParam(value = "Count", required = false) @QueryParam("countDatapoints") @DefaultValue("25") Integer count,
			@ApiParam(value = "taxon Id", required = false) @QueryParam("taxonId") @DefaultValue("0") Integer taxonId);

}

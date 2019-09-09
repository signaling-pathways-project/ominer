package edu.bcm.dldcc.big.nursa.services.rest.omics;


import edu.bcm.dldcc.big.nursa.controller.NursaDatasetRelatedDatasetBean;
import edu.bcm.dldcc.big.nursa.model.omics.PathwayCategory;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway;
import edu.bcm.dldcc.big.nursa.model.omics.dto.PsOrgan;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.PathwaysNodesMapBean;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Lookup service REST implementation
 * @author amcowiti
 */

public class TranscriptomineLookupServiceRestApi implements TranscriptomineLookupService {

	 private static Logger log = Logger.getLogger(TranscriptomineLookupServiceRestApi.class.getName());

    @Inject
    private TranscriptomineService omineService;

    @Inject
    private NursaDatasetRelatedDatasetBean nursaDatasetBean;

    @Inject
	private PathwayServiceBean pathwayServiceBean;

    
    //@Context ServerCache cache;
	
	public Integer verifyGeneList(String genelist)
    {
        if ( null != genelist&&!genelist.isEmpty())
        {
            List<String> list = Arrays.asList(genelist.split("\\s*,\\s*"));
            return this.omineService.validateListOfGenes(list);
        }
        else
            return -1;

	}

	@Override
	public <T> List<T> getAllTissuesAndCellLines() {
		return omineService.getAllTissuesAndCellLines();
	}

	public List<PsOrgan> getAllTissuesCategories()
	{
		return this.omineService.getTissueCategory();
	}

	
	@SuppressWarnings("unchecked")
	public  List<SignalingPathway> getAllSignalingPathways(){
		
		long b=System.currentTimeMillis();
		List<SignalingPathway> list=null;
		if(PathwaysNodesMapBean.allPathways.size() > 0)
			list= PathwaysNodesMapBean.allPathways;
		else{
			log.log(Level.WARNING, " No Pathways cache found, building one ...");
			//list= this.omineService.getSignalingPathways(PathwayCategory.type);
			list=pathwayServiceBean.getSignalingPathways(PathwayCategory.type);
			log.log(Level.INFO, " Pathways cache built in(ms)="+(System.currentTimeMillis()-b));
		}
		return list;
	}
	
	public Response getNamedPathways(String type,String names)
	{
		if((type == null || type.equals("")) && (names == null || names.equals("")))
			return Response.status(400).entity("Must supply a parameter").build();
		
		if(names == null || names.equals(""))
			return Response.status(400).entity("Must supply a pathway name parameter").build();
		
		String[] csvnames=names.split(",");
		PathwayCategory pathwayType=PathwayCategory.category;
		if(type != null)
			pathwayType=PathwayCategory.valueOf(type);
		List<SignalingPathway> list= (List<SignalingPathway>) this.omineService.getNamedPathways(pathwayType,csvnames);
		return Response.ok().entity(list).build();
	}

	@Override
	public <T> List<T> getRelatedDataset(Integer by, Long datasetId,String doi,
			Integer count) {
		return new ArrayList<>();
		//FIXME return (List<T>) this.nursaDatasetBean.findRelatedDataset(by, datasetId, doi,count);
	}
}

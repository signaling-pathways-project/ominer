package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.cache.Cache;

import edu.bcm.dldcc.big.nursa.model.common.Article;
import edu.bcm.dldcc.big.nursa.services.NcbiRestMaxConnectionsException;
import edu.bcm.dldcc.big.nursa.services.PubmedAbstractBean;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.ApisCacheManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Abstract may only be downloaded via XML extraction, 
 * since the JAXB Objects generated from XML schema, are unable to automatically
 * render an  object named Abstract from the NCBI Xml instance (ie Abstract/AbstractText)
 * @author mcowiti
 */
@Path("/pubmed")
@Api(value = "/pubmed/", tags = "Pubmed")
public class PubmedArticlesServiceProxy extends BaseEndPoint
{
	private static Logger log = Logger.getLogger("edu.bcm.dldcc.big.nursa.util.restapi.nursa.ArticleAbstractEndpoint");
	
	@PersistenceContext(unitName = "NURSA")
	private EntityManager em;
   
	@Inject 
	private PubmedAbstractBean pubmedAbstractBean;
	
	boolean persistAbstract=true;
	
   @GET
   @Path("/abstract/{id:[0-9][0-9]*}")
   @Produces({"application/json","application/xml"})
   @Cache(maxAge=86400,sMaxAge=86400)
   @ApiOperation(value = "Get Article wth Abstract", notes = "Returns Article with Abstract.", 
	response = Article.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="List of Datasets", response=Article.class),
	@ApiResponse(code = 400, message = "Invalid input parameters.", response = String.class) 
	})
   public Response findById(
		   @ApiParam(value = "PubmedId", required=true) @PathParam("id") String id)
   { 
      return getAbstractById(id,persistAbstract);
   }
   
   @GET
   @Path("/articles/{id:[0-9][0-9]*}")
   @Produces({"application/json","application/xml"})
   @Cache(maxAge=86400,sMaxAge=86400)
   @ApiOperation(value = "Get Article", notes = "Article.", response = Article.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="Article", response=Article.class),
	@ApiResponse(code = 400, message = "Invalid input parameters.", response = String.class) 
	})
   public Response findArticleById(
		   @ApiParam(value = "PubmedId", required=true) @PathParam("id") String id)
   {
	  return getArticleById(id);
   }
   
   private Response getArticleById(String pubmedId){
	   
	   if((pubmedId == null) || !isNumericPubmed(pubmedId)){
		   return Response.status(Status.BAD_REQUEST).build();
	   }
	   
	   Article article=null;
	   article=ApisCacheManager.pubmedCache.get(pubmedId);
	   if(article == null){
		   article=this.pubmedAbstractBean.findArticle(pubmedId);
		   if(article != null)
			   ApisCacheManager.pubmedCache.putIfAbsentAsync(pubmedId, article);
	   }
	   //
	   if(article == null){//!impossible since DMC does downloads
		   return Response.ok("Nursa has No such article").build();
	   }
	   article.setAuthorsCited(this.pubmedAbstractBean.getCitationAuthors(article.getAuthorsList()));
	   return Response.ok(article).build();
   }
   
   private Response getAbstractById(String pubmedId,boolean persist){
	   
	   if((pubmedId == null) || !isNumericPubmed(pubmedId)){
		   return Response.status(Status.BAD_REQUEST).build();
	   }
	   
	   Article article=null;
	   boolean cacheMiss=false;
	   article=ApisCacheManager.pubmedCache.get(pubmedId);
	   if(article == null){
		   article=this.pubmedAbstractBean.findArticle(pubmedId);
		   cacheMiss=true;
	   }
	   
	   if (article == null){//impossible, DMC already downloaded
	    	return Response.status(Status.NOT_FOUND).build();
	    }
	    //set transient citedAuthors
	   article.setAuthorsCited(this.pubmedAbstractBean.getCitationAuthors(article.getAuthorsList()));
	    if(article.getAbstractBlurb() != null && !article.getAbstractBlurb().equals(""))
	    {
	    	if(cacheMiss)
	    		 ApisCacheManager.pubmedCache.putIfAbsentAsync(pubmedId, article);
	    	return Response.ok(article).build();
	    }
	   //go download? Not possible if DMC downloaded
	   log.log(Level.INFO,"calling NCBI to download abstract for "+pubmedId);
	    String abstractText=downloadAbstract(pubmedId);
	    
	    if(abstractText != null && !abstractText.trim().equals(""))
	     {
	   	  article.setAbstractBlurb(abstractText);
	   	  if(persist)
	   		  updateArticleWithAbstract(article);
	   	  if(cacheMiss)
	   		  ApisCacheManager.pubmedCache.putIfAbsentAsync(pubmedId, article);
	   	  return Response.ok(article).build();
	     }else{
	    	 return Response.status(Status.NOT_FOUND).build();
	     }
	 }
    
   /**
    * TODO
    * 1)Could Refactor this to send an API call to DMC to update? 
    * 	-But this coubples the two apps?
    * 2) Introduces write to nursa UI
    */
   @Asynchronous
   public void updateArticleWithAbstract(Article article){
	   article=this.em.merge(article);
   }
   
   /**
    * The original Idea was that abstract should only be downloaded when requested
    * But, should DMC pre-download  abstract??
    * @param pubmedid
    * @return
    */
   private String downloadAbstract(String pubmedid){
	   
	   String abstractText=null;
		try {
			abstractText = this.pubmedAbstractBean.getAbstractText(pubmedid);
		} catch (NcbiRestMaxConnectionsException e) {
			log.log(Level.WARNING,"Reached limit for  NCBI calls, sending client status of: "+Status.SERVICE_UNAVAILABLE);
		}
		return abstractText;
   }
}
package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.deltaspike.core.util.ProjectStageProducer;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.ApisCacheManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import edu.bcm.dldcc.big.nursa.services.rest.RestApiUrlsProducer;
import edu.bcm.dldcc.big.nursa.services.rest.consumers.PubchemRecord.Information;
import edu.bcm.dldcc.big.nursa.services.rest.consumers.PubchemRecord.Record;
import edu.bcm.dldcc.big.nursa.services.rest.consumers.PubchemRecord.Section;

/**
 * NCBI Pubchem API (pug) proxy
 * @author mcowiti
 *
 */
@Path("/pubchem")
@Api(value = "/pubchem/", tags = "Pubchem Proxies")
public class PubchemApiProxyByClient {

	private static Logger log = Logger.getLogger(PubchemApiProxyByClient.class.getName());
	
	
	@Inject
	private RestApiUrlsProducer restApiUrlsProducer;
	
	@GET
    @Path("stage")
	@ApiOperation(value = "View stage ", notes = "View Project state.", response = String.class)
	 public Response getInjectedProjectStage(){
    	String json=ProjectStageProducer.getInstance().getProjectStage().toString();
		return Response.ok(json).build();
    }
	
	@GET
    @Path("/ligand/{pubchemid}")
    @Consumes("application/json")
    @Produces("application/json")
	@ApiOperation(value = "Get PubchemData", notes = "Get PubchemData ", 
	response = PubchemData.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="PubchemData for given pubchme Id"),
	@ApiResponse(code = 400, message = "Invalid input parameters.", response = String.class) 
	})
	public Response getLigandData(
			@ApiParam(value = "pubchemid Id",required=true) @PathParam("pubchemid")  String pubchemId) throws Exception{
		
		if(pubchemId==null || pubchemId.equals(""))
			return Response.status(400).entity("PubchemId required").build();
		
		PubchemData data= getPubchemRecordViaGson(pubchemId);
		return Response.ok().entity(data).build();
	}
	
	@GET
    @Path("/ligand/caches")
    @Consumes("application/json")
	 @Produces("application/json")
   public Response getLigandCaches(@QueryParam("type") String type) throws Exception{
		Collection<PubchemData> ids=null;//ApisCacheManager.pubchemCache.values();
		return Response.ok(ids).build();
	}
	
	@GET
    @Path("/ligand/caches/ids")
    @Consumes("application/json")
	 @Produces("application/json")
   public Response getLigandCachesIds() throws Exception{
		Set<String> ids=ApisCacheManager.pubchemCache.keySet();
		return Response.ok(ids).build();
	}
	
	private String jsonViaClientRequest(String url) {
		
		ClientRequest request = new ClientRequest(url);
		ClientResponse<String> response=null;
		String json=null;
		try{
			response  = request.get(String.class);
			json=response.getEntity();
			
		}catch (Exception e){
			log.log(Level.SEVERE,"Failed download "+e.getMessage());
		}finally{
			if(response!=null)
			 response.close();
		}
		return json;
	}
	
	public PubchemData getPubchemRecordViaGson(String pubchemid) throws Exception{
		
		long b=System.currentTimeMillis();
		String url= restApiUrlsProducer.getNcbiPubchemCompoundByCidAsJson()
				.replaceAll("\\{pubchemid\\}", pubchemid);
		
		String jsonResponse=null;
		jsonResponse=jsonViaClientRequest(url);
		
 	
 	PubchemRecord pubchemRecord=null;
	boolean success=true;
	int respLength=0;
		try {
			respLength=jsonResponse.length();
			JsonParser parser = new JsonParser();
	        JsonObject responseObj = parser.parse(jsonResponse).getAsJsonObject();
	        JsonObject recordObj = responseObj.getAsJsonObject(RECORD);
	        JsonElement RecordTypeEl = recordObj.get("RecordType");
	        JsonElement RecordNumberEl = recordObj.get("RecordNumber");
	       
	        pubchemRecord= new PubchemRecord();
	        if (null != RecordTypeEl) {
	    	   pubchemRecord.record.recordType = RecordTypeEl.getAsString();
	        }
	        if (null != RecordNumberEl) {
	    	   pubchemRecord.record.recordNumber = RecordNumberEl.getAsInt();
	        }
	        JsonArray sectionsArray = recordObj.getAsJsonArray(SECTION) ;
	        
	        setupSection(1,pubchemRecord,pubchemRecord.record,sectionsArray);
       		
		} catch (Exception e) {
			success=false;
			log.log(Level.SEVERE,"Error@Pubchem API url = "+url,e);
			e.printStackTrace();
		}
	
 	log.info("@ Pubchem API call id->#chars->time(ms)="+pubchemid+"->"+respLength+"->"+(System.currentTimeMillis()-b));
 	return (success)?new PubchemData(pubchemid, 
 			pubchemRecord.name, pubchemRecord.descriptions, 
 			pubchemRecord.CAS, pubchemRecord.chebi, pubchemRecord.iuphar,
			null, null, pubchemRecord.synonyms):
				null;
}
	
	private static final String RECORD="Record";
	private static final String SECTION="Section";
	private static final String INFORMATION="Information";
	private static final String TOC="TOCHeading";
	private static final String SECTION_ONE_NAMEANDIDS="Names and Identifiers";
	private boolean soughtOutSection1(String toc){
		if(toc.equals(SECTION_ONE_NAMEANDIDS))
			return true;
		return false;
	}
	
	private static final String SECTION_TWO_TITLE="Record Title";
	private static final String SECTION_TWO_DESC="Record Description";
	private static final String SECTION_TWO_OTHERIDS="Other Identifiers";
	private static final String SECTION_TWO_SYNONYMS="Synonyms";
	
	private boolean soughtOutSection2(String toc){
		if((toc.equals(SECTION_TWO_TITLE)) ||
	    		(toc.equals(SECTION_TWO_DESC)) ||
	    		(toc.equals(SECTION_TWO_OTHERIDS)) ||
	    		(toc.equals(SECTION_TWO_SYNONYMS))
	    		){
	    			return true;
	    		}
		return false;
	}
	
	
	/**
	 * CAS, Depositor-Supplied Synonyms
	 * @param toc
	 * @return
	 */
	private boolean soughtOutSection3(String toc){
		if((toc.equals(RECORD_SECTION_THREE_CAS)) || (toc.equals(RECORD_SECTION_THREE_SYNONYMS)) ){
	    	return true;
	    }
		return false;
	}
	
	private  <T> void setupSection(int level,PubchemRecord pubchemRecord, T type,JsonArray sectionArray){
		
		String toc=null;
		 List<Section> sections= new ArrayList<Section>();
		for (int i=0; i<sectionArray.size(); i++) {
			
			toc = new Gson().fromJson(sectionArray.get(i).getAsJsonObject().get(TOC), String.class);
			
			if(level == 1 && !soughtOutSection1(toc))
	    			continue;
			
			if(level == 2 && !soughtOutSection2(toc)){
				continue;
			}
			
			if(level == 3 && !soughtOutSection3(toc)){
				continue;
			}
			
			Section section= pubchemRecord.new Section();
	   		//section.tOCHeading =toc;
	   		
	   		JsonArray sectionsArray = sectionArray.get(i).getAsJsonObject().getAsJsonArray(SECTION) ;
	   		if(sectionsArray!=null){
	   			 setupSection(level+1,pubchemRecord,section,sectionsArray);
	   		}
	   		
	   		JsonArray infoList = sectionArray.get(i).getAsJsonObject().getAsJsonArray(INFORMATION) ;
	   		if(infoList!=null){
	   			//section.informations=
	   			setupInformations(pubchemRecord,toc,infoList);
	   		}
	   		sections.add(section);
		}
		
		if(level == 1)
			((Record)type).sections=sections;
		if(level > 1)
			((Section)type).sections=sections;
   		
	}
	
	private static final String RECORD_TITLE="Record Title";
	private static final String RECORD_DESC="Record Description";
	private static final String RECORD_SECTION_THREE_CAS="CAS";
	private static final String RECORD_SECTION_THREE_SYNONYMS="Depositor-Supplied Synonyms";
	
	
	private boolean infoBeingSought(String toc,String name){
		if(toc.equals(RECORD_TITLE) && name.equals(RECORD_TITLE))
			return true;
		if(toc.equals(RECORD_DESC) && name.equals(RECORD_DESC))
			return true;
		
		if(toc.equals(RECORD_SECTION_THREE_CAS) && name.equals(RECORD_SECTION_THREE_CAS))
			return true;
		if(toc.equals(RECORD_SECTION_THREE_SYNONYMS) && name.equals(RECORD_SECTION_THREE_SYNONYMS))
			return true;
		return false;
	}
	
	private static final String INFORMATION_NAME="Name";
	private static final String INFORMATION_STRINGVALUE="StringValue";
	private static final String INFORMATION_STRINGVALUELIST="StringValueList";
	private static final String INFORMATION_CHEBI_PREFIX="CHEBI:";
	private static final String INFORMATION_CHEBI_COLON=":";
	
	private List<Information> setupInformations(PubchemRecord pubchemRecord, String toc,JsonArray infoList){
		
		List<Information> informations= new ArrayList<Information>();
   		Information information=null;
   		String name=null;
   		if(infoList!=null){
	    		for (int j=0; j<infoList.size(); j++) {
	    			name = new Gson().fromJson(infoList.get(j).getAsJsonObject().get(INFORMATION_NAME), String.class);
	    			if(!infoBeingSought(toc,name))
	    				continue;
	    			
	    			information=pubchemRecord.new Information();
	   	    		
	    			information.referenceNumber = new Gson().fromJson(infoList.get(j).getAsJsonObject().get("ReferenceNumber"), Integer.class);
	    			if(infoList.get(j).getAsJsonObject().get(INFORMATION_STRINGVALUE)!=null){
	   	    			information.stringValue = new Gson().fromJson(infoList.get(j).getAsJsonObject().get(INFORMATION_STRINGVALUE), String.class);
	   	    		}
	    			if(infoList.get(j).getAsJsonObject().getAsJsonArray(INFORMATION_STRINGVALUELIST)!=null){
	    				JsonArray synoList=infoList.get(j).getAsJsonObject().getAsJsonArray(INFORMATION_STRINGVALUELIST);
	    				setSynonyms(pubchemRecord,name,synoList);
	   	    		}
	    			
	    			informations.add(information);
	    			if(toc.equals(RECORD_TITLE) && name.equals(RECORD_TITLE)){
		    			pubchemRecord.name=information.stringValue;
		    		}
	    			if(toc.equals(RECORD_SECTION_THREE_CAS) && name.equals(RECORD_SECTION_THREE_CAS)){
		    			pubchemRecord.CAS=information.stringValue;
		    		}
	    			//multiple records exist in stringValue instead of StringValueList
	    			if(toc.equals(RECORD_DESC) && name.equals(RECORD_DESC)){
		    			pubchemRecord.descriptions.add(information.stringValue);
		    		}
	    		}
	    }
   		return informations;
	}
	
	private void setSynonyms(PubchemRecord record,String name,JsonArray array){
		if(!name.equals(RECORD_SECTION_THREE_SYNONYMS))
			return ;
		List<String> synonyms= new ArrayList<String>();
		String synonym=null;
		for (int i=0; i<array.size(); i++) {
			synonym = array.get(i).getAsString();
			synonyms.add(synonym);
			if(synonym.startsWith(INFORMATION_CHEBI_PREFIX))
				record.chebi=synonym.substring(synonym.indexOf(INFORMATION_CHEBI_COLON)+1);
		}
		record.synonyms=synonyms;
	}		
}

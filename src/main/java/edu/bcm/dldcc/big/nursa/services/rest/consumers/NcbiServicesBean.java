package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.bcm.dldcc.big.nursa.model.rest.APIS;
import edu.bcm.dldcc.big.nursa.model.rest.ExternalRestApiUrl;
import edu.bcm.dldcc.big.nursa.model.rest.RestParameter;
import edu.bcm.dldcc.big.nursa.services.rest.RestApiUrlsProducer;

/**
 * These methods need to be refactored for JAX-RS 2.0 client 
 * @author mcowiti
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class NcbiServicesBean implements Serializable {

	private static final long serialVersionUID = -4457983938191847465L;

	private static Logger log = Logger.getLogger(NcbiServicesBean.class.getName());
	
	
	@Inject
	private RestApiUrlsProducer restApiUrlsProducer;
	
		private final static String UTF_ENCODING="UTF-8";
		private final static String REQ_ACCEPT_HEADER="application/xml";
		private final static String ABSTRACT_TEXT ="/PubmedArticleSet/PubmedArticle/MedlineCitation/Article/Abstract/AbstractText";
		private final static String ABSTRACT_LABEL="Label";
		
		private  ExternalRestApiUrl restUrl=null;
		private static String url=null;
		private  static String convUrl;
		   
		 
		@PostConstruct
		public void setup(){
			url=restApiUrlsProducer.getApis().get(APIS.pubmed_download).getUrl();//.getPubmedDownload();
			restUrl=restApiUrlsProducer.getApis().get(APIS.pubmed_downloadWithParams);//.getPubmedDownloadWithParams();
			convUrl=restApiUrlsProducer.getApis().get(APIS.ncbi_elsevier_idconv).getUrl();
		}
		
		public  String getAbstractFromNcbiByClient(String pubmed) throws Exception{
			/*WebTarget target=ClientBuilder.newClient()
					 .target(restUrl.getUrl());
			for(RestParameter param:restUrl.getParams()){
				if(param.getParam().equals("id")){
					target.queryParam(param.getParam(),pubmed);
					continue;
				}
				target.queryParam(param.getParam(),param.getValue());
			}*/
			
			WebTarget target=ClientBuilder.newClient()
					 .target(url)
					 .queryParam("db", "pubmed")
					 .queryParam("retmode", "xml")
					 .queryParam("email", "support@nursa.org")
					 .queryParam("id", pubmed);
			
			
			long b=System.currentTimeMillis();
			final Response response =target.request(MediaType.APPLICATION_XML_TYPE)
					 .accept(MediaType.APPLICATION_XML_TYPE)
					 .get();
			
			InputStream xmlStringResponse=null;
			if(response.getStatus() == Response.ok().build().getStatus()){
				xmlStringResponse=response.readEntity(InputStream.class);
			}else{
				throw new Exception("Abstract Quest Failed : HTTP code : "+ response.getStatus());
			}
			
			
			InputSource inputSource = new InputSource(xmlStringResponse);
			String abstractText=getAbstract(inputSource);
			log.info("NCBI abstract call time(ms)="+(System.currentTimeMillis()-b));
			
			return abstractText;
		}
		
		public  String pubmedToElsevierDoi(String pubmedId) {
	        return toElsevierID(pubmedId, "doi");
	    }

	    public  String elsevierDoiToPubmed(String elsevierDoi) {
	        return toElsevierID(elsevierDoi, "pmid");
	    }
	    
	   
	/**
	 * Call NCBI API via #HttpURLConnection
	 * Check that at the instance of making call, there are no other calls out there. If there are, wait
	  * @param pubmed
	 * @return
	 * @throws Exception
	 */
	public  String getAbstractFromNcbiByHttpConnection(String pubmed) throws Exception{
		
		String query = String.format("%s", URLEncoder.encode(pubmed, UTF_ENCODING));
		
		long b=System.currentTimeMillis();
		HttpURLConnection conn =null;
		
		try{
			conn = (HttpURLConnection) (new URL(url + query)).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept",REQ_ACCEPT_HEADER );
	 
			log.log(Level.INFO, "pubmed.abstract URL="+new URL(url + query));
			if (conn.getResponseCode() != 200) 
			{
				log.log(Level.SEVERE, "Failed : HTTP error code : "+ conn.getResponseCode());
				throw new Exception("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			InputStream xml=conn.getInputStream();
			
			InputSource inputSource = new InputSource(xml);
			String abstractText=getAbstract(inputSource);
			
			conn.disconnect();
			log.info("NCBI abstract REST API call time(ms)="+(System.currentTimeMillis()-b));
			
			return abstractText;
		}catch (Exception e){
			log.log(Level.SEVERE, "Failed  get Abstract : "+ e.getMessage());
			return null;
		}finally{
			//Wanna restrict # of connections: what if finally called after another call went in?
			//conn.disconnect();
		}
	}
	
	private String getAbstract(InputSource inputSource) throws XPathExpressionException{
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = ABSTRACT_TEXT;
		
		Node node=null;
		Element element=null;
		NodeList nodeList=null;
		StringBuilder sb= new StringBuilder();
		nodeList=(NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
		if(nodeList != null && nodeList.getLength() > 0)
		{
			for(int i = 0; i < nodeList.getLength(); i++) 
			{
				node=nodeList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) 
				{
					element=((Element)node);
					if(element.hasAttributes())
					{
						sb.append(element.getAttribute(ABSTRACT_LABEL)).append(" : ");
					}
					sb.append(element.getTextContent()).append("\n\n");
				}
			}
		}
		return sb.toString();
	}
	
	 /**
     * Calls Elsevier WS to find reference element in their system
     * @param elsevierID - id to convert
     * @param elementName - what to extract from response(doi/pmid/pmcid)
     * @return null if nothing is found or value for the provided elementName
     */
    private  String toElsevierID(String elsevierID, String elementName) {
    	
    	if (null == elsevierID || "".equals(elsevierID)) {
            return null;
        }
    	
    	WebTarget target=ClientBuilder.newClient()
				 .target(convUrl+elsevierID);
				
		final Response response =target.request(MediaType.APPLICATION_JSON_TYPE)
				 .accept(MediaType.APPLICATION_JSON_TYPE)
				 .get();
		
		String jsonStringResponse=null;
		if(response.getStatus() == Response.ok().build().getStatus()){
			jsonStringResponse=response.readEntity(String.class);
		}else{
			log.log(Level.SEVERE," elsevierID --> elementName ="+elsevierID+"->"+elementName+" code:"+response.getStatus());
        	return null;
		}
		
		JsonParser parser = new JsonParser();
		try{
            JsonObject json = parser.parse(jsonStringResponse).getAsJsonObject();

            JsonArray records = json.getAsJsonArray("records") ;
            String responseId = null;
            if (records.size() > 0) {
                JsonElement jsonElement = records.get(0).getAsJsonObject().get(elementName);
                if (null != jsonElement) {
                    responseId = jsonElement.getAsString();
                }
                if (records.size() > 1) {
                    log.log(Level.WARNING,"ToElsevierID for "+elementName+" with id: "+elsevierID+" returned more than 1 result. Getting the first one and proceed");
                }
            }
            return responseId;
		} catch (Exception e) {
        	log.log(Level.SEVERE,"Cannot get or convert elsevierID:" + elsevierID + " to ElsevierDoi", e);
        	return null;
		}
	}
}
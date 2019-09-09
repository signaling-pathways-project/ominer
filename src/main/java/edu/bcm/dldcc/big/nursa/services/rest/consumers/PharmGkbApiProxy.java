package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.bcm.dldcc.big.nursa.services.rest.RestApiUrlsProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This is a proxy for pharmGkb API data
 * @author mcowiti
 *
 */


@Path("/pharmgkb")
@Api(value = "/pharmgkb/", tags = "Pharm GKB Proxies")
public class PharmGkbApiProxy {

	private static Logger log = Logger.getLogger("edu.bcm.dldcc.big.nursa.services.rest.pharmgkb.PharmGkbApiProxy");
	
	@Inject
	private RestApiUrlsProducer restApiUrlsProducer;
	
	
	/**
	 * 
	 * @param data required. data group: clinicalPgx,researchPgx,pathways
	 * @param dtype optional. type of data: dosage, label, clinicalAnnotation, variantAnnotation. Required if data=clinicalAnnotation
	 * @param gene optional gene HGNC symbol or entrezGeneId. Required if chemical is missing
	 * @param chemical optional chemical identifier. Required is gene is missing
	 * @param chemicalIdScheme optional chemical identifier scheme, required if chemical provided
	 * @return
	 */
	@GET
    @Path("/data")
    @Consumes("application/json")
    @Produces("application/json")
	@ApiOperation(value = "Get PharmGKB data", notes = "Get PharmGKB data")
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message="List of Get PharmGKB data"),
	@ApiResponse(code = 400, message = "Invalid input parameters.", response = String.class) 
	})
    public <T> T getData(
    		@ApiParam(value = "data") @QueryParam("data") @DefaultValue("") String data,
    		@ApiParam(value = "dtype") @QueryParam("dtype") @DefaultValue("") String dtype,
    		@ApiParam(value = "gene") @QueryParam("gene") @DefaultValue("") String gene,
    		@ApiParam(value = "chemical") @QueryParam("chemical") @DefaultValue("") String chemical,
    		@ApiParam(value = "chemicalIdScheme") @QueryParam("chemicalIdScheme") @DefaultValue("pubchem") String chemicalIdScheme){
		
		if(DataGroup.getDataGroup(data) == null){
			return (T) "Usage: you must provide request parameters data, dtype, either of gene or chemical ( and chemicalIdScheme)";
		}
		
		try{
			switch(DataGroup.getDataGroup(data)){
				case clinicalPgx:
					return getClinicalPgx(dtype,gene,chemical,chemicalIdScheme);
				case pathways:
					return getPathways(gene,chemical,chemicalIdScheme);
				case researchPgx:
					return getResearchPgx(gene,chemical,chemicalIdScheme);
			}
				return respondWithError(400);
		}catch (java.lang.IllegalStateException ex){
			log.log(Level.WARNING,"No data available for your request");
			return (T) ( "No data available for your request");
		}catch (Exception e){
			return respondWithError(204);
		}
	}
	
	public enum DataGroup{
		clinicalPgx,researchPgx,pathways;
		public static DataGroup getDataGroup(String data){
			try{
				return DataGroup.valueOf(data);
			}catch (Exception e){
				return null;
			}
		}
	}
	public enum DataType{
		dosage,label,clinicalAnnotation,variantAnnotation;
		public static DataType getDataType(String dtype){
			try{
				return DataType.valueOf(dtype);
			}catch (Exception e){
				return null;
			}
		}
	}
	
	public enum QueryParamType{
		gene,chemical;
		public static QueryParamType getQueryParamType(String param){
			try{
				return QueryParamType.valueOf(param);
			}catch (Exception e){
				return null;
			}
		}
	}
	
	public enum ChemicalIdScheme{
		pubchem,chebi;//,cas, iuphar;
		public static ChemicalIdScheme getChemicalIdScheme(String param){
			try{
				return ChemicalIdScheme.valueOf(param);
			}catch (Exception e){
				return null;
			}
		}
	}
	
	/**
	 * Single API queries:
	 * 	1) clinicalAnnotation by gene
	 * 	2) Label by gene 
	 * @param type
	 * @param qtype
	 * @return
	 */
	private boolean isSingleGeneQuery(DataType type,QueryParamType qtype){
		if(type == DataType.label && qtype == QueryParamType.gene)
			return true;
		if(type == DataType.clinicalAnnotation && qtype == QueryParamType.gene)
			return true;
		return false;
	}
	
	/**
	 * Get ClinicalPgx data for gene/chemical
	 * @param dtype
	 * @param gene
	 * @param chemical
	 * @param chemicalIdScheme
	 * @return
	 * @throws Exception
	 */
	
	private <T> T getClinicalPgx(String dtype,String gene,String chemical,String chemicalIdScheme) throws Exception{
		DataType type=DataType.getDataType(dtype);
		if(type == null)
			return respondWithError(400);
		
		QueryParamType qtype=getQueryParam(gene,chemical,chemicalIdScheme);
		if(qtype == null){
			throw new RequestException(); 
		}
		
		PgxSearchId<T> pgxSearchId=null;
		String pgxId=null;
		boolean singleQuery=isSingleGeneQuery(type,qtype);
		
		//also singleQuery for ca and gene
		if(!singleQuery){
			pgxSearchId=getSearchIdentifier(qtype,gene,chemical,chemicalIdScheme);
			if(pgxSearchId == null){
				return respondWithError(204);
			}
			
			if(pgxSearchId.qtype ==QueryParamType.gene){
				pgxId=((PgxGene) pgxSearchId.identifier).id;
			}else{
				pgxId=((PgxMolecule) pgxSearchId.identifier).id;
			}
		}else{
			pgxId=(qtype == QueryParamType.gene)?gene:chemical;
		}
		
		
		switch(type){
			case dosage:
				List<Dosage> l= new ArrayList<Dosage>();
				//by PA,multiple by PA
				if(pgxSearchId.qtype == QueryParamType.gene || pgxSearchId.qtype == QueryParamType.chemical){
					l=getDosageGuidelineByPAId(pgxSearchId.qtype,pgxId);
				}else
					l.add(new Dosage("PA00","Dosage Guideline Name","Summary text","annotation present only for case of Drugs and Ligands"));
				
				return (T) new PgxDosingGuideline(l);
			case label:
				List<Label> ll= new ArrayList<Label>();
				//single by Gene, multiple by chemical PAID
				if(singleQuery || pgxSearchId.qtype == QueryParamType.gene || pgxSearchId.qtype == QueryParamType.chemical){
					ll=getDrugLabelsByGeneOrPgxId(singleQuery,qtype,pgxId);
				}else
					ll.add( new Label("Drug Label","PA Id","summary",null,null));
				return (T) new PgxDrugLabel(ll);
			case clinicalAnnotation:
				List<ClinicalAnnotation> annotations= new ArrayList<ClinicalAnnotation>();
				//multiple by gene/chemical PAID
				if(singleQuery || pgxSearchId.qtype == QueryParamType.gene || pgxSearchId.qtype == QueryParamType.chemical)
					annotations=getClinicalAnnotationByPA(singleQuery,qtype,pgxId);
				else{
					List<String> ombRaces= new ArrayList<String>();
					ombRaces.add("Asian");
					annotations.add(new ClinicalAnnotation("id",null,"raceNotes",
							 null,ombRaces,"mixedPopulation",null,null,null));
				}
				return (T) new PgxClinicalAnnotation(annotations);
			case variantAnnotation:
				//new PgxVariant();
				return respondWithError(412);
			default:
				return respondWithError(400);	
		}
		
	}
	
	
	/**
	 * Get pathways for gene (1 step) or chemical (2 steps)
	 * @param gene
	 * @param chemical
	 * @param chemicalIdScheme
	 * @return
	 * @throws Exception
	 */
	private <T> T getPathways(String gene,String chemical,String chemicalIdScheme) throws Exception{
		
		QueryParamType qtype=getQueryParam(gene,chemical,chemicalIdScheme);
		if(qtype == null){
			throw new RequestException(); 
		}
		
		List<Pathway> pathways= new ArrayList<Pathway>();
		if(qtype == QueryParamType.gene){
			pathways=getPathwaysByIdentifier(qtype,gene,null);
		}else{
			//2 step: get off pgkb chem name
			PgxSearchId<T> pgxSearchId=null;
			pgxSearchId=getSearchIdentifier(qtype,gene,chemical,chemicalIdScheme);
			if(pgxSearchId!=null)
				pathways=getPathwaysByIdentifier(qtype,null,((PgxMolecule)pgxSearchId.identifier).name);
			else
				pathways.add(new Pathway("name","URL_link_on_name"));
		}
		return (T) new PgxPathway(pathways);
	}
	
	/**
	 * Determine Query Parameter type
	 * @param gene
	 * @param chemical
	 * @param chemicalIdScheme
	 * @return
	 */
	private QueryParamType getQueryParam(String gene,String chemical,String chemicalIdScheme){
		if(gene != null && !gene.trim().equals(""))
			return QueryParamType.gene;
		if((chemical != null && !chemical.trim().equals("")) && 
				(chemicalIdScheme!=null && !chemicalIdScheme.trim().equals("")) ){
			if(ChemicalIdScheme.getChemicalIdScheme(chemicalIdScheme) !=null)
				return QueryParamType.chemical;
		}else{
			return null;
		}
		return null;
	}
	
	/**
	 * Get Pathway via gene symbol or chemical name
	 * @param type
	 * @param gene gene symbol
	 * @param chemicalIdentifier either pgkb name off name (eg warfarin) or pgkb id (ie a PA*)
	 * @return
	 * @throws Exception
	 */
	private List<Pathway> getPathwaysByIdentifier(QueryParamType type,String gene,String chemicalIdentifier) throws Exception{
		//String PATHWAY_DATA_URL="https://api.pharmgkb.org/v1/data/pathway?";
		String pathwayData=restApiUrlsProducer.getPathwayData();
		String pathwayBaseUrl=this.restApiUrlsProducer.getPharmGkbPathwyLink();
		
		StringBuilder sb=new StringBuilder(pathwayData);
		if(type==QueryParamType.gene){
			sb.append("genes.symbol=").append(gene);
		}else{
			sb.append("chemicals.name=").append(chemicalIdentifier);
		}
		
		
		ClientRequest request = new ClientRequest(sb.toString());
		ClientResponse<String> response  = request.get(String.class);
		 String json=response.getEntity();
		JsonParser parser = new JsonParser();
	    JsonArray array = parser.parse(json).getAsJsonArray();
	    Gson gson = new Gson();
	    Pathway pathway=null;
	    List<Pathway> pathways= new ArrayList<Pathway>();
	    for (int i=0; i<array.size(); i++) {
	    	pathway = gson.fromJson(array.get(i), Pathway.class);
	    	pathway.url=URLSetter.getLinkUrl(pathwayBaseUrl, pathway.id);
	    	pathway.setSource();
	    	pathways.add(pathway);
	    }
	    response.close();
	    return pathways;
		
	}
	
	/**
	 * This Pgx call finds guidelines by related Genes,  or related chemicals, using its PA number
	 * This returns fewer Guidelines and less data, unlike the v1/display/page/dosingGuidelines/ that returns an object
	 * with many other data
	 * @param paId
	 * @return
	 * @throws Exception
	 */
	private List<Dosage> getDosageGuidelineByPAId(QueryParamType type,String paId) throws Exception{
		//this does not return an array
		//String DOSAGE_URL_BY="https://api.pharmgkb.org/v1/data/guideline?";
		String dosageUrlBy=restApiUrlsProducer.getPharmGkbDosageQuery();
		
		StringBuilder sb=new StringBuilder(dosageUrlBy);
		if(type == QueryParamType.gene)
			sb.append("relatedGenes.accessionId=").append(paId);
		else
			sb.append("relatedChemicals.accessionId=").append(paId);
		
		ClientRequest request = new ClientRequest(sb.toString());
		ClientResponse<String> response  = request.get(String.class);
		 String json=response.getEntity();
		 
		 List<Dosage> dos=getDosageGuidelineFromJsonAnonArrays(json,(type == QueryParamType.gene));
	    response.close();
	    return dos;
	}
	
	private List<Dosage> getDosageGuidelineFromJsonAnonArrays(String json, boolean exclusdeText){
		
		 String dosageGuidelineUrl=this.restApiUrlsProducer.getPharmGkbGuideLink();
		
		 JsonParser parser = new JsonParser();
	    JsonArray array = parser.parse(json).getAsJsonArray();
	    Gson gson = new Gson();
	    Dosage dosage=null;
	    
	    List<Dosage> dos= null;
	    
	    dos=new ArrayList<Dosage>();
	   
	    for (int i=0; i<array.size(); i++) {
	    	 dosage = gson.fromJson(array.get(i), Dosage.class);
	    	
	    	 if(exclusdeText)
	    		 dosage.textHtml=null;
	    	 dosage.url=URLSetter.getLinkUrl(dosageGuidelineUrl,dosage.id);
	    	 dos.add(dosage);
	    }
	    return dos;
	}
	
	/**
	 * Gets Label by gene as single Query,or as multiple Q by chemical via fetched PAID  
	 * NB: TODO is maintain local chemical id/name map, can do single via relatedChemicals.name=
	 * @param singleQuery
	 * @param type
	 * @param paId
	 * @return
	 * @throws Exception
	 */
	private List<Label> getDrugLabelsByGeneOrPgxId(boolean singleQuery, QueryParamType type,String paId) throws Exception{
		
		String singleGeneDosageQueryUrl=this.restApiUrlsProducer.getPharmGkbDosageLableByGeneSingleQuery();
		
		StringBuilder sb=new StringBuilder(singleGeneDosageQueryUrl);
		if(singleQuery)
			sb.append("relatedGenes.symbol=").append(paId);
		else{
			if(type == QueryParamType.gene)
				sb.append("relatedGenes.accessionId=").append(paId);
			else
				sb.append("relatedChemicals.accessionId=").append(paId);
		}
		
		ClientRequest request = new ClientRequest(sb.toString());
		ClientResponse<String> response  = request.get(String.class);
		 String json=response.getEntity();
		 List<Label> labels=getDrugLabelsFromJsonAnonArrays(json);
	    response.close();
	    return labels;
	}
	
	private List<Label> getDrugLabelsFromJsonAnonArrays(String json){
		
		String labelUrl=this.restApiUrlsProducer.getPharmGkbLabelLink();
		
		 JsonParser parser = new JsonParser();
	    JsonArray array = parser.parse(json).getAsJsonArray();
	    Gson gson = new Gson();
	    Label label=null;
	    List<Label> labels= new ArrayList<Label>();
	    for (int i=0; i<array.size(); i++) {
	    	 label = gson.fromJson(array.get(i), Label.class);
	    	 label.setAgencyApproval();
	    	 label.setPgxLevel();
	    	 label.url=URLSetter.getLinkUrl(labelUrl,label.id);
	    	 labels.add(label);
	    }
	    return labels;
	}
	
	/**
	 * Gets annotation
	 * 	1) as single query by gene
	 * 	2) two queries via chemical PAID
	 * @param singleQuery
	 * @param type
	 * @param paId
	 * @return
	 * @throws Exception
	 */
	private List<ClinicalAnnotation> getClinicalAnnotationByPA(boolean singleQuery,QueryParamType type,String paId) throws Exception{
		//String SINGLE_Q_GENE_URL="https://api.pharmgkb.org/v1/data/clinicalAnnotation?location.genes.symbol=";
		//String Q_BY_CHEM_URL="https://api.pharmgkb.org/v1/data/clinicalAnnotation?relatedChemicals.accessionId=";
		
		String singleQueryByGeneUrl=this.restApiUrlsProducer.getPharmGkbClinAnnotationByGeneQuery();
		String QueryByChemicalUrl=this.restApiUrlsProducer.getPharmGkbClinAnnotationByChemQuery();
		
		StringBuilder sb=null;
		if(singleQuery){
			sb=new StringBuilder(singleQueryByGeneUrl).append(paId).append("&view=max");
		}else
			sb=new StringBuilder(QueryByChemicalUrl).append(paId).append("&view=max");
		
		ClientRequest request = new ClientRequest(sb.toString());
		ClientResponse<String> response  = request.get(String.class);
		 String json=response.getEntity();
		
		 List<ClinicalAnnotation> cas=getClinicalAnnotationFromJsonAnonArrays(json);
		 
	    response.close();
		
		return cas;
	}
	
	/**
	 * Result is an Unnamed array
	 * @param json
	 * @return
	 */
	private List<ClinicalAnnotation>  getClinicalAnnotationFromJsonAnonArrays(String json){
		
		String clinicalAnnotationUrl=this.restApiUrlsProducer.getPharmGkbClinAnnoLink();
		
		JsonParser parser = new JsonParser();
		
		JsonArray array = parser.parse(json).getAsJsonArray();
		Gson gson = new Gson();
		ClinicalAnnotation ca=null;
	    List<ClinicalAnnotation> cas= new ArrayList<ClinicalAnnotation>();
	    for (int i=0; i<array.size(); i++) {
	    	 ca = gson.fromJson(array.get(i), ClinicalAnnotation.class);
	    	 ca.setClinicalVariantColumn();
	    	 ca.setGenesColumns();
			 ca.url=URLSetter.getLinkUrl(clinicalAnnotationUrl,ca.id);
	    	 cas.add(ca);
	    }
	    
	    return cas;
	}
	
	public static class URLSetter{
		public static String getLinkUrl(String baseURL,String id){
			return new StringBuilder(baseURL).append(id).toString();
		}
	}
	
	/**
	 * Get Pgkb Gene via HGNC symbol
	 * TODO add to utilize entrezGeneId
	 * @param symbol
	 * @return
	 * @throws Exception
	 */
	private PgxGene getPgexGene(String symbol) throws Exception{
		
		//String GURL="https://api.pharmgkb.org/v1/data/gene?symbol=";
		String gUrl=this.restApiUrlsProducer.getPharmGkbGeneSingleQuery();
		
		ClientRequest request = new ClientRequest(gUrl+symbol);
		
		
		ClientResponse<String> response  = request.get(String.class);
		 String json=response.getEntity();
		JsonParser parser = new JsonParser();
	    JsonArray array = parser.parse(json).getAsJsonArray();
	    Gson gson = new Gson();
	    PgxGene gene = gson.fromJson(array.get(0), PgxGene.class);
		response.close();
	    return gene;
	}
	
	/**
	 * PGKB only supports pubchem for now
	 * TODO add supports for other chemical schemes
	 * @param chemical
	 * @param scheme
	 * @return
	 * @throws Exception
	 */
	private PgxMolecule getPgxMolecule(String chemical,String chemicalIdScheme) throws Exception{
		//String CHEM_URL="https://api.pharmgkb.org/v1/data/chemical?crossReferences.resource=";
		String chemicalUrl=this.restApiUrlsProducer.getPharmGkbMoleculeSingleQuery();
		
		String PUBCHEM="pubChemCompound";
		String CHEBI="ChEBI";
		ChemicalIdScheme scheme=ChemicalIdScheme.getChemicalIdScheme(chemicalIdScheme);
		if(scheme==null)
			return null;
		
		StringBuilder sb=new StringBuilder(chemicalUrl);
		switch(scheme){
		case pubchem:
			sb.append(PUBCHEM);
			break;
		case chebi:
			sb.append(CHEBI);
			break;
			default:
				return null;
		}
		
		sb.append("&crossReferences.resourceId=").append(chemical);
		ClientRequest request = new ClientRequest(sb.toString());
		
		ClientResponse<String> response  = request.get(String.class);
		 String json=response.getEntity();
		JsonParser parser = new JsonParser();
	    JsonArray array = parser.parse(json).getAsJsonArray();
	    Gson gson = new Gson();
	    PgxMolecule molecule = gson.fromJson(array.get(0), PgxMolecule.class);
		response.close();
	    return molecule;
		
	}
	
	/**
	 * Return variant annotation
	  * @param gene
	 * @param chemical
	 * @param chemicalIdScheme
	 * @return
	 * @throws Exception 
	 */
	private <T> T getResearchPgx(String gene,String chemical,String chemicalIdScheme) throws Exception{
		
		QueryParamType qtype=getQueryParam(gene,chemical,chemicalIdScheme);
		if(qtype == null){
			throw new RequestException(); 
		}
		PgxSearchId<T> pgxSearchId=null;
		try{
			pgxSearchId=getSearchIdentifier(qtype,gene,chemical,chemicalIdScheme);
		}catch (RequestException e){
			e.printStackTrace();
			respondWithError(400);
		}
		
		if(pgxSearchId == null){//that identifier returns nothing
			return respondWithError(204);
		}
		
		List<Variant> variants= new ArrayList<Variant>();
		
		List<String> chemicals= new ArrayList<String>();
		chemicals.add("letrozole");
		chemicals.add("cisplatin");
		
		if(pgxSearchId.qtype==QueryParamType.gene ){//ie for NR etc
			List<String> alleles= new ArrayList<String>();
			alleles.add("T->C");
			variants.add(new Variant("variant rsId", "url link on variant column for NR,CR,others", 
					null, chemicals, alleles,
					" aminoAcidTranslation", " function"));
		}else{
			variants.add(new Variant("variant rsId", "url link on gene column for Drugs", 
					"gene HGNC symbol present for Drugs", chemicals, null,null, null));
		}
		return (T) new PgxPharmacogenomicsVariant(variants);
		//return respondWithError(501);
	}
	
	
	
	private <T> T respondWithError(int status){
		Response.ResponseBuilder response = Response.status(status);
		
		return (T) response.build();
	}
	
	/**
	 * Determine the Search Identifier Object
	 * @param qtype
	 * @param gene
	 * @param chemical
	 * @param chemicalIdScheme
	 * @return
	 * @throws Exception
	 */
	private <T> PgxSearchId<T> getSearchIdentifier(QueryParamType qtype,String gene,String chemical,String chemicalIdScheme) throws Exception{
		
		String pgxId=null;
		if(qtype==QueryParamType.gene){
			PgxGene pgxgene=getPgexGene(gene);
			if(pgxgene == null){
				return null;//do data !found
			}
			pgxId=pgxgene.id;
			return new PgxSearchId(qtype,pgxgene,pgxId);
		}else{
			PgxMolecule pgxChemical=getPgxMolecule(chemical,chemicalIdScheme);
			if(pgxChemical == null){
				return null;//do data !found
			}
			log.fine("pgxChemical="+pgxChemical.toString());
			pgxId=pgxChemical.id;
			return new PgxSearchId(qtype,pgxChemical,pgxId);
		}
		
	}
	
	public class RequestException extends Exception {

		public RequestException() {
			super();
		}
		
	}
	
	public class PgxSearchId<T>{
		public QueryParamType qtype;
		public T identifier;
		public String pgxId;
		public PgxSearchId(QueryParamType qtype, T identifier, String pgxId) {
			super();
			this.qtype = qtype;
			this.identifier = identifier;
			this.pgxId = pgxId;
		}
	}
	
	public static class PgxMolecule{
		public String objCls;
		public String id;
		public String name;
		public PgxMolecule() {
			super();
		}
		public PgxMolecule(String objCls, String id,String name) {
			super();
			this.objCls = objCls;
			this.id = id;
			this.name=name;
		}
		@Override
		public String toString() {
			return "PgxMolecule [objCls=" + objCls + ", id=" + id + ", name=" + name + "]";
		}
		
		}
	
	public static class PgxGene{
		public String objCls;
		public String id;
		public PgxGene() {
			super();
		}
		public PgxGene(String objCls, String id) {
			super();
			this.objCls = objCls;
			this.id = id;
		}
		@Override
		public String toString() {
			return "PgxGene [objCls=" + objCls + ", id=" + id + "]";
		}
		
	}
	
	@XmlRootElement
	public class PgxPathway {
		 @XmlElement
		public List<Pathway> pathways= new ArrayList<Pathway>();
		 
		 public PgxPathway(List<Pathway> pathways){
			 this.pathways=pathways;
		 }
	}
	
	public class Pathway {
		
		private final static String SOURCE="PHARMGKB";
		 @XmlElement public String name; 
		 @XmlElement public String id;
		 @XmlElement public String url;
		 @XmlElement public String source;
		 
		public Pathway(String name,String id){
			this.name=name;
			this.id=id;
		}
		protected void setSource(){
			this.source=SOURCE;
		}
	}
	
	@XmlRootElement
	public class PgxClinicalAnnotation{
		public List<ClinicalAnnotation> annotations= new ArrayList<ClinicalAnnotation>();
		
		public PgxClinicalAnnotation(List<ClinicalAnnotation> annotations){
			this.annotations=annotations;
		}
	}
	
	public class ClinicalAnnotation{
		 
		@XmlElement public String clinicalVariant;
		@XmlTransient  public Location location;
		@XmlElement public List<String> genes= new ArrayList<String>();
		@XmlElement public String raceNotes;
		@XmlElement(name="Phenotypes") public List<RelatedDiseases> relatedDiseases= new ArrayList<RelatedDiseases>();
		@XmlElement public List<Haplotypes> haplotypes= new ArrayList<Haplotypes>();
		@XmlElement public List<RelatedChemical> relatedChemicals= new ArrayList<RelatedChemical>();
		
		
		@XmlElement public List<String> ombRaces= new ArrayList<String>();
		@XmlElement public List<String> types= new ArrayList<String>();
		@XmlElement public String mixedPopulation;
		
		@XmlElement public String id;
		@XmlElement public String url;
		 
		//!FR  @XmlElement public String levelOfEvidence;
		
		 public ClinicalAnnotation(String id,Location location,String raceNotes,
				 List<RelatedDiseases> relatedDiseases,List<String> ombRaces,String mixedPopulation,
				 List<String> types,List<Haplotypes> haplotypes,List<RelatedChemical> relatedChemicals){
			 this.id=id;
			 this.location=location;
			 this.raceNotes=raceNotes;
			 this.relatedDiseases=relatedDiseases;
			 this.ombRaces=ombRaces;
			 this.mixedPopulation=mixedPopulation;
			this.types=types;
			this.haplotypes=haplotypes;
			this.relatedChemicals=relatedChemicals;
		 } 
		 
		 /**
		  * New FR, No append, and if no rsId, look for haplotypes
		  */
		 protected void setClinicalVariantColumn(){
			 if(this.location.rsid != null){
				 this.clinicalVariant=this.location.rsid;
			 }else if(this.location.haplotypes!=null && this.location.haplotypes.size() > 0){
				 //this.clinicalVariant=getHaplotypesSymbol();
				 this.haplotypes= new ArrayList<Haplotypes>();
				 this.haplotypes.addAll(this.location.haplotypes);
			 }
		}
		 
		 protected void setGenesColumns(){
			 this.genes= new ArrayList<String>();
			 if(this.location.genes.size() > 0)
				 for(Gene gene:this.location.genes){
					  this.genes.add(gene.symbol);
				}
		 }
		 
		 private String getHaplotypesSymbol(){
			 StringBuilder sb= new StringBuilder();
			 for(Haplotypes type:this.haplotypes)
				 sb.append(type.symbol).append(",");
			 return sb.toString().substring(0, sb.toString().length()-1);
		 }
		 
		 private String getGenes(){
			 StringBuilder sb= new StringBuilder();
			 for(Gene gene:this.location.genes)
				 sb.append(gene.symbol).append(",");
			 return sb.toString().substring(0, sb.toString().length()-1);
		 }
		 

			public class Location {
				@XmlTransient public String name;
				@XmlTransient public String type;
				 @XmlElement  public String rsid;
				@XmlElement public List<Gene> genes= new ArrayList<Gene>();
				@XmlElement public List<Haplotypes> haplotypes= new ArrayList<Haplotypes>();
				public Location(String name, String type, List<Gene> genes,String rsid,List<Haplotypes> haplotypes) {
					super();
					this.name = name;
					this.type = type;
					this.genes = genes;
					this.rsid=rsid;
					this.haplotypes=haplotypes;
				}
			}
			
		public class Gene{
			 public String symbol;

				public Gene(String symbol) {
					super();
					this.symbol = symbol;
				}
		}
		
		public class RelatedChemical{
			public String name;

			public RelatedChemical(String name) {
				super();
				this.name = name;
			}
			
		}
		 private String getDiseases(){
			 StringBuilder sb= new StringBuilder();
			 for(RelatedDiseases d:this.relatedDiseases)
				 sb.append(d.name).append(",");
			 return sb.toString().substring(0, sb.toString().length()-1);
		 }
		 
		 public class Haplotypes{
			protected String id;
			public String name;
			public String symbol;
			public Haplotypes(String id, String symbol, String name) {
				super();
				this.id = id;
				this.symbol = symbol;
				this.name = name;
			}
		 }
		
		 public class RelatedDiseases{
				public String id;
				public String name;
				public RelatedDiseases(String id, String name) {
					super();
					this.id = id;
					this.name = name;
				}
			}
	}
	
	
	@XmlRootElement
	public class PgxDrugLabel{
		 @XmlElement
		public List<Label> labels= new ArrayList<Label>();
		 
		 public PgxDrugLabel(List<Label> labels){
			 this.labels=labels;
		 }
	}
	
	public class Label{
		 
		@XmlElement(name="drugLabel") public String name; 
		@XmlElement(name="summary") public String  summaryHtml; 
		 @XmlElement public String  agencyApproval;
		 @XmlElement(name="pgxLevel") public String  pgxLevel ;
		 @XmlElement public String  id; 
		 @XmlElement public String url;
		 
		 @XmlTransient public BiomarkerStatus biomarkerStatus;
		 @XmlTransient public Testing testing;
		 
		public Label(String name,String id,String summaryHtml,
				BiomarkerStatus biomarkerStatus,Testing testing){
			this.name=name;
			this.id=id;
			this.summaryHtml=summaryHtml;
			this.biomarkerStatus=biomarkerStatus;
			this.testing=testing;
			
		}
		
		protected void setPgxLevel(){
			if(this.testing!=null)
				if(this.testing.term!=null)
					this.pgxLevel=this.testing.term;
		}
		protected void setAgencyApproval(){
			StringBuilder sb= new StringBuilder();
			if(this.biomarkerStatus!=null ){
				if( this.biomarkerStatus.term!=null)
					sb.append(this.biomarkerStatus.term);
				if(this.biomarkerStatus.resource!=null)
					sb.append(" ").append(this.biomarkerStatus.resource);
			this.agencyApproval=sb.toString();
			}
		}
		
		public class Testing{
			public String term;

			public Testing(String term) {
				super();
				this.term = term;
			}
			
		}
		
		public class BiomarkerStatus{
			public String resource;
			public String term;
			public BiomarkerStatus(String resource, String term) {
				super();
				this.resource = resource;
				this.term = term;
			}
		}
	}
	
	
	public class PgxDosingGuideline{
		 @XmlElement
		public List<Dosage> guidelines= new ArrayList<Dosage>();
		 public PgxDosingGuideline(List<Dosage> guidelines){
			 this.guidelines=guidelines;
		 }
	}
	
	@XmlRootElement
	public class Dosage{
		
		 @XmlElement(name="dosingGuideline") public String name; 
		 @XmlElement(name="summary") public String  summaryHtml; 
		 @XmlElement(name="annotation") public String  textHtml; //R UI.annotation; only for Drugs
		 @XmlElement public String  id;  
		@XmlElement public String url; 
			
		 public Dosage(String pharmgkbId,String dosingGuideline,String summary,String annotation){
			 this.id=pharmgkbId;
			 this.name=dosingGuideline;
			 this.summaryHtml=summary;
			 this.textHtml=annotation;
		}
	}
	

	@XmlRootElement
	public class PgxPharmacogenomicsVariant{
		 @XmlElement
		public List<Variant> variants= new ArrayList<Variant>();
		 
		 public PgxPharmacogenomicsVariant(List<Variant> variants){
			 this.variants=variants;
		 }
	}
	
	public class Variant {
		public String variant;
		public String url;
		public List<String> chemicals= new ArrayList<String>();//R
		public String gene;
		public List<String> alleles= new ArrayList<String>();//R
		public String function;
		public String aminoAcidTranslation;
		
		public Variant(String rsId, String url, String gene, List<String> chemicals, List<String> alleles,
				String aminoAcidTranslation, String function) {
			super();
			this.variant = rsId;
			this.url = url;
			this.gene = gene;
			this.chemicals = chemicals;
			this.alleles = alleles;
			this.aminoAcidTranslation = aminoAcidTranslation;
			this.function = function;
		}
	}
}

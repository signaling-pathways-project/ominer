package edu.bcm.dldcc.big.nursa.services.rest;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.nursa.model.rest.APIS;
import edu.bcm.dldcc.big.nursa.model.rest.ExternalRestApiUrl;
import edu.bcm.dldcc.big.nursa.model.rest.ExternalRestApiUrl_;

/**
 * REST API URLs
 * @author mcowiti
 *
 */

@Singleton
@Startup
@ApplicationScoped
@Named("apiUrls")
public class RestApiUrlsProducer {

	private Logger log = Logger.getLogger(RestApiUrlsProducer.class.getName());
	
	@PersistenceContext(unitName = "NURSA")
	private EntityManager em;
	
	private  ConcurrentHashMap<APIS,ExternalRestApiUrl> apis= new ConcurrentHashMap<APIS,ExternalRestApiUrl>();
	
	public ConcurrentHashMap<APIS, ExternalRestApiUrl> getApis() {
		return apis;
	}

	@Produces
	@Named("sraLink")
	public String getSRALink(){
		return apis.get(APIS.sra).getUrl();
	}
	@Produces
	@Named("dxDoiLink")
	public String getDxDoiLink(){
		return apis.get(APIS.dx_doi).getUrl();
	}
	
	@Produces
	@Named("dmcGeo")
	public String getGeoUrl(){
		return apis.get(APIS.dmc_geo).getUrl();
	}

    @Produces
    @Named("geo")
    public String getNcbiGeoUrl(){
        return apis.get(APIS.geo).getUrl();
    }


    @Produces
	@Named("pubmedLink")
	public String getPubmedUrl(){
		return apis.get(APIS.pubmed_link_baseurl).getUrl();
	}
	
	@Produces
	@Named("pubmedPmcLink")
	public String getNcbiPubmedPmcLink(){
		return apis.get(APIS.pubmed_pmc_link_baseurl).getUrl(); 
		
	}
	@Produces
	@Named("pubmedDownload")
	public String getPubmedDownload(){
		return apis.get(APIS.pubmed_download).getUrl();
	}
	@Produces
	@Named("pubmedDownloadWithParams")
	public String getPubmedDownloadWithParams(){
		return apis.get(APIS.pubmed_downloadWithParams).getUrl();
	}
	
	@Produces
	@Named("elsevierIdConv")
	public String getElsevierIdConvertor(){
		return apis.get(APIS.ncbi_elsevier_idconv).getUrl();
	}
	
	@Produces
	@Named("chebiLink")
	public String getChebiLink(){
		return apis.get(APIS.chebi_link).getUrl();
	}
	@Produces
	@Named("pubchemLink")
	public String getNcbiPubchemLink(){
		return apis.get(APIS.ncbi_pubchem_link).getUrl();
	}

	@Produces
	@Named("pubchemCompoundLink")
	public String getNcbiPubchemCompoundLink(){
		return apis.get(APIS.ncbi_pubchem_compund_link).getUrl();
	}

	@Produces
	@Named("iupharLink")
	public String getIupharLink(){
		return apis.get(APIS.iuphar_link).getUrl();
	}
	
	@Produces
	@Named("pubchemCidJson")
	public String getNcbiPubchemCompoundByCidAsJson(){
		return apis.get(APIS.ncbi_pubchem_compound_cid_json).getUrl();
	}
	
	@Produces
	@Named("pubchemCidPng")
	public String getNcbiPubchemCompoundByCidAsPng(){
		return apis.get(APIS.ncbi_pubchem_compound_cid_png).getUrl();
	}
	
	@Produces
	@Named("pubchemCidFly")
	public String getNcbiPubchemCompoundByCidAsFly(){
		return apis.get(APIS.ncbi_pubchem_compound_cid_imagefly).getUrl();
	}
	
	@Produces
	@Named("pharmGkbPathwayLink")
	public String getPharmGkbPathwyLink(){
		return apis.get(APIS.pharmgkb_pathway_link_baseurl).getUrl();
	}
	
	public String getPathwayData(){
		return apis.get(APIS.pharmgkb_pathway).getUrl();
	}
	
	@Produces
	@Named("pharmGkbClinAnnoLink")
	public String getPharmGkbClinAnnoLink(){
		return apis.get(APIS.pharmgkb_clin_link_baseurl).getUrl();
	}
	@Produces
	@Named("pharmGkbLabelLink")
	public String getPharmGkbLabelLink(){
		return apis.get(APIS.pharmgkb_label_link_baseurl).getUrl();
	}
	@Produces
	@Named("pharmGkbGuideLink")
	public String getPharmGkbGuideLink(){
		return apis.get(APIS.pharmgkb_guidelinel_link_baseurl).getUrl();
	}
	
	@Produces
	@Named("pharmGkbVariantLink")
	public String getPharmGkbVariantLink(){
		return apis.get(APIS.pharmgkb_variant_link_baseurl).getUrl();
	}
	
	@Produces
	@Named("pharmGkbPathwayQuery")
	public String getPharmGkbPathwayQuery(){
		return apis.get(APIS.pharmgkb_pathway).getUrl();
	}
	@Produces
	@Named("pharmGkbDosageQuery")
	public String getPharmGkbDosageQuery(){
		return apis.get(APIS.pharmgkb_dosage).getUrl();
	}
	
	@Produces
	@Named("pharmGkbGeneSingleQuery")
	public String getPharmGkbGeneSingleQuery(){
		return apis.get(APIS.pharmgkb_gene_get).getUrl();
	}
	
	@Produces
	@Named("pharmGkbMoleculeSingleQuery")
	public String getPharmGkbMoleculeSingleQuery(){
		return apis.get(APIS.pharmgkb_molecule_get).getUrl();
	}
	
	@Produces 
	@Named("pharmGkbDosageLabelByGeneSingleQuery")
	public String getPharmGkbDosageLableByGeneSingleQuery(){
		return apis.get(APIS.pharmgkb_dosage_single_gene_label).getUrl();
	}
	
	@Produces 
	@Named("pharmGkbClinAnnoByGeneQuery")
	public String getPharmGkbClinAnnotationByGeneQuery(){
		return apis.get(APIS.pharmgkb_clin_single_gene_q).getUrl();
	}
	
	@Produces 
	@Named("pharmGkbClinAnnoByChemQuery")
	public String getPharmGkbClinAnnotationByChemQuery(){
		return apis.get(APIS.pharmgkb_clin_single_chem_q).getUrl();
	}
	
	public String calcLink(APIS api,String id){
		if(api.isAppendId())
			return new StringBuffer(apis.get(api).getUrl()).append(id).toString();
		else
			return  apis.get(api).getUrl().replaceAll(api.getReplacementTerm(), id);
	}
	
	@PostConstruct
	public void setup(){
		
		List<ExternalRestApiUrl> list=getRestApiUrlConfigs();
		for(ExternalRestApiUrl api:list){
			apis.putIfAbsent(api.getApi(), api);//(api.getApi(), api.getUrl());
		}
		
		if(apis.size() != APIS.values().length){
			log.log(Level.WARNING,"# urls managed misconfig, apis map/APIS: "+ apis.size()+"/"+APIS.values().length);
			updateMissingUrl();
		}
		
		if(apis.size() < 2 ){
			//initUrls(); or update those missing
			updateUrls();
		}
		log.log(Level.INFO,"# urls managed apis/APIS: "+ apis.size()+"/"+APIS.values().length);
	}
	
	/**
	 * Add URL that is configured in code, but not persisted
	 */
	private void updateMissingUrl(){
		ExternalRestApiUrl config=null;
		for(APIS api:APIS.values()){
			if(!apis.containsKey(api)){
				log.log(Level.INFO,"Adding/Persisting  URL= "+api.getUrl());
				config= new ExternalRestApiUrl(api,api.getUrl(),api.getParams());
				em.persist(config);
				apis.putIfAbsent(api, config);
			}
		}
		em.flush();
	}
	
	public void updateUrls(){
		ExternalRestApiUrl config=null;
		for(APIS api:APIS.values()){
			config=getRestApiUrlConfig(api);
			if(config == null){
				config= new ExternalRestApiUrl(api,api.getUrl(),api.getParams());
				em.persist(config);
				apis.putIfAbsent(api, config);
			}
		}
		em.flush();
	}
	
	public void initUrls(){
		ExternalRestApiUrl externalRestApiUrl;
		for(APIS api:APIS.values()){
			externalRestApiUrl= new ExternalRestApiUrl(api,api.getUrl(),api.getParams());
			em.persist(externalRestApiUrl);
			apis.putIfAbsent(api, externalRestApiUrl);
		}
		em.flush();
	}
	
	public List<ExternalRestApiUrl> getRestApiUrlConfigs(){
		
		 CriteriaBuilder builder = this.em.getCriteriaBuilder();
		  CriteriaQuery<ExternalRestApiUrl> criteria =builder.createQuery(ExternalRestApiUrl.class);
		  Root<ExternalRestApiUrl> root = criteria.from(ExternalRestApiUrl.class);
		  TypedQuery<ExternalRestApiUrl> query =
	        this.em.createQuery(criteria.select(root));
		 return query.setHint("org.hibernate.cacheable", true)
				  .getResultList();
	}
	
	public ExternalRestApiUrl getRestApiUrlConfig(APIS api){
		
		 CriteriaBuilder builder = this.em.getCriteriaBuilder();
		  CriteriaQuery<ExternalRestApiUrl> criteria =builder.createQuery(ExternalRestApiUrl.class);
		  Root<ExternalRestApiUrl> root = criteria.from(ExternalRestApiUrl.class);
		  TypedQuery<ExternalRestApiUrl> query =
	        this.em.createQuery(criteria.select(root)
	        		.where(builder.equal(root.get(ExternalRestApiUrl_.api), api)));
		 try{
			 return query.getSingleResult();
		 }catch (Exception e){
			 return null;
		 }
	}
	
}

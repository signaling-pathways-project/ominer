package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import edu.bcm.dldcc.big.nursa.services.rest.RestApiUrlsProducer;


@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class PubchemData implements Serializable{
	private static final long serialVersionUID = 6072784989396599721L;

	
	private String pubchemId;
	
	private String name;
	private List<String> descriptions= new ArrayList<String>(); 
	private String cas;
	private String chebi;
	private String iuphar;
	private String jmol;
	private String pictFile; 
	private List<String> synonyms= new ArrayList<String>();
	
	public PubchemData() {
		super();
	}

	
	@Inject
	private RestApiUrlsProducer restApiUrlsProducer;
	
	public String getPictFile() {
		String url= restApiUrlsProducer.getNcbiPubchemCompoundByCidAsJson()
				.replaceAll("\\{pubchemid\\}", this.pubchemId);
		return url;
	}


	public PubchemData(String pubchemId, String name, List<String> desc, String cas, String chebi, String iuphar,
			String jmol, String pictFile, List<String> synonyms) {
		super();
		this.pubchemId = pubchemId;
		this.name = name;
		this.descriptions = desc;
		this.cas = cas;
		this.chebi = chebi;
		this.iuphar = iuphar;
		this.jmol = jmol;
		this.pictFile = pictFile;
		this.synonyms = synonyms;
	}


	public String getPubchemId() {
		return pubchemId;
	}


	public void setPubchemId(String pubchemId) {
		this.pubchemId = pubchemId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<String> getDescriptions() {
		return descriptions;
	}


	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}


	public String getCas() {
		return cas;
	}


	public void setCas(String cas) {
		this.cas = cas;
	}


	public String getChebi() {
		return chebi;
	}


	public void setChebi(String chebi) {
		this.chebi = chebi;
	}


	public String getIuphar() {
		return iuphar;
	}


	public void setIuphar(String iuphar) {
		this.iuphar = iuphar;
	}


	public String getJmol() {
		return jmol;
	}


	public void setJmol(String jmol) {
		this.jmol = jmol;
	}


	public List<String> getSynonyms() {
		return synonyms;
	}


	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}


	public RestApiUrlsProducer getRestApiUrlsProducer() {
		return restApiUrlsProducer;
	}


	public void setRestApiUrlsProducer(RestApiUrlsProducer restApiUrlsProducer) {
		this.restApiUrlsProducer = restApiUrlsProducer;
	}


	public void setPictFile(String pictFile) {
		this.pictFile = pictFile;
	}
	

}

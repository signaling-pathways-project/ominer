package edu.bcm.dldcc.big.nursa.model.cistromic.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Molecule DTO for REST API
 * See {@link edu.bcm.dldcc.big.nursa.model.Molecule} for the the mapping 
 * @author mcowiti
 *
 */
@XmlRootElement
public class Molecule {

	private String id;
	@XmlElement
	private String name;
	@XmlElement(required=false)
	private String officialSymbol;
	@XmlElement(required=false)
	private String nursaSymbol;
	@XmlElement(required=false)
	private String type;
	@XmlElement
	private  String doi;
	
	public Molecule(String id, String name, String official,
			String nursaSymbol, String type, String doi) {
		super();
		this.id = id;
		this.name = name;
		this.officialSymbol = official;
		this.nursaSymbol = nursaSymbol;
		this.type = type;
		this.doi = doi;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOfficialSymbol() {
		return officialSymbol;
	}
	public void setOfficialSymbol(String officialSymbol) {
		this.officialSymbol = officialSymbol;
	}
	public String getNursaSymbol() {
		return nursaSymbol;
	}
	public void setNursaSymbol(String nursaSymbol) {
		this.nursaSymbol = nursaSymbol;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	
}

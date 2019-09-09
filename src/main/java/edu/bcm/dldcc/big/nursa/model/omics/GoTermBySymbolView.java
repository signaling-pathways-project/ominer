package edu.bcm.dldcc.big.nursa.model.omics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "GOTERM_NAME_SYMBOL")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name = "goTerm")
public class GoTermBySymbolView {

	@Id
	private Long id;
	@Column(name="species_id")
    private Long speciesId;
	
	@Column(name="ENTREZGENEID")
    private String entrezGeneId;
    
	 private String gotermid;
	 private String termname;
	 private String name;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSpeciesId() {
		return speciesId;
	}
	public void setSpeciesId(Long speciesId) {
		this.speciesId = speciesId;
	}
	public String getEntrezGeneId() {
		return entrezGeneId;
	}
	public void setEntrezGeneId(String entrezGeneId) {
		this.entrezGeneId = entrezGeneId;
	}
	public String getGotermid() {
		return gotermid;
	}
	public void setGotermid(String gotermid) {
		this.gotermid = gotermid;
	}
	public String getTermname() {
		return termname;
	}
	public void setTermname(String termname) {
		this.termname = termname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

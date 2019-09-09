package edu.bcm.dldcc.big.nursa.model.cistromic.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * GeneMode class represents the choices for Gene Input.
 * @author amcowiti
 *
 */
@XmlRootElement(name = "geneMode")
public class GeneSearchMode {
	
	private static final long serialVersionUID = 2548315223469827688L;
	private long id;
	private String description;
	private Integer uiorder;

	public GeneSearchMode() {
		
	}

	public GeneSearchMode(long id) {
		this.id = id;
	}
	public GeneSearchMode(long id, String description,Integer uiorder) {
		this.id = id;
		this.description = (description!=null)?description:TmNone.none.name();
		this.uiorder=uiorder;	
	}

	@XmlElement
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlElement
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = (description!=null)?description:TmNone.none.name();
	}

	public Integer getUiorder() {
		return uiorder;
	}

	public void setUiorder(Integer uiorder) {
		this.uiorder = uiorder;
	}
}

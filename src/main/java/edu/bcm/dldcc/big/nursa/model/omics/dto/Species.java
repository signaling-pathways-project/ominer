package edu.bcm.dldcc.big.nursa.model.omics.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import edu.bcm.dldcc.big.nursa.model.cistromic.dto.TmNone;


/**
 * Species POJO
 * @author amcowiti
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@XmlRootElement(name = "species")
public class Species {

	private long id;
	private String name;
	private Integer taxonId;
	
	public Species() {
	}

	public Species(long id) {
		this.id = id;
	}
	public Species(long id, String name, Integer taxonId) {
		this.id = id;
		this.name = (name!=null)?name:TmNone.none.name();
		this.taxonId = (taxonId!=null)?taxonId:TmNone.none.ordinal();
	}
	
	@XmlElement
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@XmlElement
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = (name!=null)?name:TmNone.none.name();
	}
	@XmlElement
	public Integer getTaxonId() {
		return taxonId;
	}
	public void setTaxonId(Integer taxonId) {
		this.taxonId = (taxonId!=null)?taxonId:TmNone.none.ordinal();
	}
}

package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Species implements Serializable {

	private static final long serialVersionUID = 1821332836897941648L;
	@Id
	@GeneratedValue(generator = "speciesSequencer")
	@SequenceGenerator(name = "speciesSequencer", sequenceName = "SPECIES_SEQ")
	@XmlTransient
	private Long id;

	private String commonName;
	private String scientificName;

	@Basic
	private Long taxonomyId;
	
	@Basic
	@XmlTransient
	private Integer displayOrder;
	
	@JsonIgnore
	@XmlTransient
	@Transient 
	private String identifier;

	public String getIdentifier(){
		return taxonomyId.toString();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public Long getTaxonomyId() {
		return taxonomyId;
	}

	public void setTaxonomyId(Long taxonomyId) {
		this.taxonomyId = taxonomyId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((commonName == null) ? 0 : commonName.hashCode());
		result = prime * result
				+ ((scientificName == null) ? 0 : scientificName.hashCode());
		result = prime * result
				+ ((taxonomyId == null) ? 0 : taxonomyId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Species other = (Species) obj;
		if (commonName == null) {
			if (other.commonName != null)
				return false;
		} else if (!commonName.equals(other.commonName))
			return false;
		if (scientificName == null) {
			if (other.scientificName != null)
				return false;
		} else if (!scientificName.equals(other.scientificName))
			return false;
		if (taxonomyId == null) {
			if (other.taxonomyId != null)
				return false;
		} else if (!taxonomyId.equals(other.taxonomyId))
			return false;
		return true;
	}

	/**
	 * @return the displayOrder
	 */
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	/**
	 * @param displayOrder the displayOrder to set
	 */
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

}

package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Tissue and Cell line
 * TODO consider a Tree representation of the sourceStrig
 * Tissue;cell;cell;cell-line
 * @author mcowiti
 *
 */
@Entity
@XmlRootElement
@Cacheable
public class Tissue implements Serializable {

	private static final long serialVersionUID = -882849054286626732L;

	@Id @GeneratedValue(generator = "system-uuid")
	@Column(length = 36)
	private String id;

	@Basic
	private String name; //consider Uberon?
	
	@Basic
	private String sourceString; //original sourceString

	@ManyToOne
	@ForeignKey(name = "none")
	private TissueCategory tissueCategory;
	

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

	public String getSourceString() {
		return sourceString;
	}

	public void setSourceString(String sourceString) {
		this.sourceString = sourceString;
	}

	public TissueCategory getTissueCategory() {
		return tissueCategory;
	}

	public void setTissueCategory(TissueCategory tissueCategory) {
		this.tissueCategory = tissueCategory;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((sourceString == null) ? 0 : sourceString.hashCode());
		result = prime * result
				+ ((tissueCategory == null) ? 0 : tissueCategory.hashCode());
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
		Tissue other = (Tissue) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sourceString == null) {
			if (other.sourceString != null)
				return false;
		} else if (!sourceString.equals(other.sourceString))
			return false;
		if (tissueCategory == null) {
			if (other.tissueCategory != null)
				return false;
		} else if (!tissueCategory.equals(other.tissueCategory))
			return false;
		return true;
	}
	
}

package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "synon")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public class Synonym implements Serializable {

	private static final long serialVersionUID = -6987454454866568089L;

	@Id
	@GeneratedValue(generator = "molSynonymSequencer")
	@SequenceGenerator(name = "molSynonymSequencer", sequenceName = "MOLSYNONYM_SEQ")
	@XmlTransient
	private Long id;

	@Basic
	@Column(length = 2000)
	private String name;

	@Basic
	@XmlTransient
	private Boolean display;
	
	@XmlTransient
	private Boolean autoSuggest;
	
	@XmlTransient
	private Integer rank;
	
	public Synonym() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Boolean getDisplay() {
		return display;
	}

	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public Boolean getAutoSuggest() {
		return autoSuggest;
	}

	public void setAutoSuggest(Boolean autoSuggest) {
		this.autoSuggest = autoSuggest;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((autoSuggest == null) ? 0 : autoSuggest.hashCode());
		result = prime * result + ((display == null) ? 0 : display.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Synonym other = (Synonym) obj;
		if (autoSuggest == null) {
			if (other.autoSuggest != null)
				return false;
		} else if (!autoSuggest.equals(other.autoSuggest))
			return false;
		if (display == null) {
			if (other.display != null)
				return false;
		} else if (!display.equals(other.display))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString() {
		return this.name;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

}

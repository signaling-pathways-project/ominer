package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

@Entity
public class DataResource implements Serializable, Comparable<DataResource> {

	private static final long serialVersionUID = -8042043972599253948L;

	@Id
	@GeneratedValue(generator = "dataResourceSequencer")
	@SequenceGenerator(name = "dataResourceSequencer", sequenceName = "DATARESOURCE_SEQ")
	private Long id;

	@ManyToOne
	@ForeignKey(name = "none")
	private Organization organization;

	// @since 4.15.2015 for the cases of single valued resource. Seems the use of synonyms presupposes multi-valued
	@Basic
	private String identifier;
	
	@OneToMany(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private List<Synonym> synonyms = new ArrayList<Synonym>();
	
	private String orgType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orgType == null) ? 0 : orgType.hashCode());
		result = prime * result
				+ ((organization == null) ? 0 : organization.hashCode());
		result = prime * result
				+ ((synonyms == null) ? 0 : synonyms.hashCode());
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
		DataResource other = (DataResource) obj;
		if (orgType == null) {
			if (other.orgType != null)
				return false;
		} else if (!orgType.equals(other.orgType))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (synonyms == null) {
			if (other.synonyms != null)
				return false;
		} else if (!synonyms.equals(other.synonyms))
			return false;
		return true;
	}


	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public int compareTo(DataResource dr2) {
		if(dr2.getOrganization().getName() == null) {
			return -1;
		} else if (this.getOrganization().getName() == null) {
			return 1;
		}
		return this.getOrganization().getName().compareToIgnoreCase(this.getOrganization().getName());
	}
	
}

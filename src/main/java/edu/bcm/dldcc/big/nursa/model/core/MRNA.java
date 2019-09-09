package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.DataResource;
import edu.bcm.dldcc.big.nursa.model.common.MoleculeSynonym;
import edu.bcm.dldcc.big.nursa.util.comparator.DataResourceAlphabetical;

@Entity
public class MRNA implements Serializable {

	private static final long serialVersionUID = -92255491929493577L;

	@Id
	@GeneratedValue(generator = "mrnaSequencer")
	@SequenceGenerator(name = "mrnaSequencer", sequenceName = "MRNA_SEQ")
	private Long id;

	@OneToMany(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private List<MoleculeSynonym> synonyms = new ArrayList<MoleculeSynonym>();

	@ManyToOne
	@ForeignKey(name = "none")
	private Gene encodingGene;

	@ManyToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private Polypeptide polypeptide;

	@ManyToMany(mappedBy="mrna")
	@ForeignKey(name = "none")
	private List<MiRNAInteraction> miRNAInteraction = new ArrayList<MiRNAInteraction>();

	private String status;

	@Column(name = "encodGenAccVer")
	private String encodingGenomicAccessionVersion;

	private String rnaAccessionVersion;

	@OneToMany(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	@Sort(type = SortType.COMPARATOR, comparator = DataResourceAlphabetical.class)
	private SortedSet<DataResource> dataResources = new TreeSet<DataResource>();

	private Long startPos;
	private Long endPos;
	private Long length;

	@Column(length = 3500)
	private String description;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private DOI doi;


	@Transient
	public List<DataResource> getDataResourcesAsList() {
		return new ArrayList<DataResource>(this.dataResources);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<MoleculeSynonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<MoleculeSynonym> synonyms) {
		this.synonyms = synonyms;
	}

	public Gene getEncodingGene() {
		return encodingGene;
	}

	public void setEncodingGene(Gene encodingGene) {
		this.encodingGene = encodingGene;
	}

	public Polypeptide getPolypeptide() {
		return polypeptide;
	}

	public void setPolypeptide(Polypeptide polypeptide) {
		this.polypeptide = polypeptide;
	}

	public List<MiRNAInteraction> getMiRNAInteraction() {
		return miRNAInteraction;
	}

	public void setMiRNAInteraction(List<MiRNAInteraction> miRNAInteraction) {
		this.miRNAInteraction = miRNAInteraction;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEncodingGenomicAccessionVersion() {
		return encodingGenomicAccessionVersion;
	}

	public void setEncodingGenomicAccessionVersion(
			String encodingGenomicAccessionVersion) {
		this.encodingGenomicAccessionVersion = encodingGenomicAccessionVersion;
	}

	public String getRnaAccessionVersion() {
		return rnaAccessionVersion;
	}

	public void setRnaAccessionVersion(String rnaAccessionVersion) {
		this.rnaAccessionVersion = rnaAccessionVersion;
	}

	public SortedSet<DataResource> getDataResources() {
		return dataResources;
	}

	public void setDataResources(SortedSet<DataResource> dataResources) {
		this.dataResources = dataResources;
	}

	public Long getStartPos() {
		return startPos;
	}

	public void setStartPos(Long startPos) {
		this.startPos = startPos;
	}

	public Long getEndPos() {
		return endPos;
	}

	public void setEndPos(Long endPos) {
		this.endPos = endPos;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the doi
	 */
	public DOI getDoi() {
		return doi;
	}

	/**
	 * @param doi the doi to set
	 */
	public void setDoi(DOI doi) {
		this.doi = doi;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataResources == null) ? 0 : dataResources.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((encodingGene == null) ? 0 : encodingGene.hashCode());
		result = prime
				* result
				+ ((encodingGenomicAccessionVersion == null) ? 0
						: encodingGenomicAccessionVersion.hashCode());
		result = prime * result + ((endPos == null) ? 0 : endPos.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime
				* result
				+ ((miRNAInteraction == null) ? 0 : miRNAInteraction.hashCode());
		result = prime * result
				+ ((polypeptide == null) ? 0 : polypeptide.hashCode());
		result = prime
				* result
				+ ((rnaAccessionVersion == null) ? 0 : rnaAccessionVersion
						.hashCode());
		result = prime * result
				+ ((startPos == null) ? 0 : startPos.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		MRNA other = (MRNA) obj;
		if (dataResources == null) {
			if (other.dataResources != null)
				return false;
		} else if (!dataResources.equals(other.dataResources))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (encodingGene == null) {
			if (other.encodingGene != null)
				return false;
		} else if (!encodingGene.equals(other.encodingGene))
			return false;
		if (encodingGenomicAccessionVersion == null) {
			if (other.encodingGenomicAccessionVersion != null)
				return false;
		} else if (!encodingGenomicAccessionVersion
				.equals(other.encodingGenomicAccessionVersion))
			return false;
		if (endPos == null) {
			if (other.endPos != null)
				return false;
		} else if (!endPos.equals(other.endPos))
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		if (miRNAInteraction == null) {
			if (other.miRNAInteraction != null)
				return false;
		} else if (!miRNAInteraction.equals(other.miRNAInteraction))
			return false;
		if (polypeptide == null) {
			if (other.polypeptide != null)
				return false;
		} else if (!polypeptide.equals(other.polypeptide))
			return false;
		if (rnaAccessionVersion == null) {
			if (other.rnaAccessionVersion != null)
				return false;
		} else if (!rnaAccessionVersion.equals(other.rnaAccessionVersion))
			return false;
		if (startPos == null) {
			if (other.startPos != null)
				return false;
		} else if (!startPos.equals(other.startPos))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (synonyms == null) {
			if (other.synonyms != null)
				return false;
		} else if (!synonyms.equals(other.synonyms))
			return false;
		return true;
	}
	
	


}

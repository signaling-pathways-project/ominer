package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.DataResource;
import edu.bcm.dldcc.big.nursa.model.common.MoleculeSynonym;
import edu.bcm.dldcc.big.nursa.model.common.Species;
import edu.bcm.dldcc.big.nursa.util.comparator.DataResourceAlphabetical;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Gene implements Serializable {

	private static final long serialVersionUID = -5803872719328545056L;

	@Id
	@GeneratedValue(generator = "geneSequencer")
	@SequenceGenerator(name = "geneSequencer", sequenceName = "GENE_SEQ")
	@XmlTransient
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	@XmlTransient
	@ForeignKey(name = "none")
	private MoleculeSynonym official;

	@OneToMany(cascade = CascadeType.ALL)
	@XmlTransient 
	@ForeignKey(name = "none")
	private List<MoleculeSynonym> synonyms = new ArrayList<MoleculeSynonym>();

	@OneToMany(cascade = CascadeType.ALL)
	@XmlTransient 
	@ForeignKey(name = "none")
	@Sort(type = SortType.COMPARATOR, comparator = DataResourceAlphabetical.class)
	private SortedSet<DataResource> dataResources = new TreeSet<DataResource>();

	@ManyToOne(cascade = CascadeType.ALL)
	@XmlTransient 
	@ForeignKey(name = "none")
	private Species species;

	@XmlTransient 
	private String chromosome;

	@XmlTransient 
	private String strand;

	@XmlTransient 
	private String mapLocation;

	@Basic
	private String entrezGeneId;

	@Column(length = 3500)
	private String description;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "encodingGene")
	@XmlTransient 
	@ForeignKey(name = "none")
	private List<MRNA> encodes = new ArrayList<MRNA>();

	@ManyToMany(cascade = CascadeType.ALL, mappedBy = "genes")
	@XmlTransient 
	@ForeignKey(name = "none")
	private List<GOTerm> goTerms = new ArrayList<GOTerm>();
	
	@ManyToOne(cascade = CascadeType.ALL)
	@XmlTransient 
	@ForeignKey(name = "none")
	private DOI doi;


	public Gene() {
	}

	public Long getId() {
		return id;
	}

	public MoleculeSynonym getOfficial() {
		return official;
	}

	public void setOfficial(MoleculeSynonym official) {
		this.official = official;
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

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public String getStrand() {
		return strand;
	}

	public void setStrand(String strand) {
		this.strand = strand;
	}

	public String getMapLocation() {
		return mapLocation;
	}

	public void setMapLocation(String mapLocation) {
		this.mapLocation = mapLocation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MRNA> getEncodes() {
		return encodes;
	}

	public void setEncodes(List<MRNA> encodes) {
		this.encodes = encodes;
	}

	public SortedSet<DataResource> getDataResources() {
		return dataResources;
	}

	public void setDataResources(SortedSet<DataResource> dataResources) {
		this.dataResources = dataResources;
	}

	@Transient
	public List<DataResource> getDataResourcesAsList() {
		return new ArrayList<DataResource>(this.dataResources);
	}

	public String getEntrezGeneId() {
		return entrezGeneId;
	}

	public void setEntrezGeneId(String entrezGeneId) {
		this.entrezGeneId = entrezGeneId;
	}

	public List<GOTerm> getGoTerms() {
		return goTerms;
	}

	public void setGoTerms(List<GOTerm> goTerms) {
		this.goTerms = goTerms;
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
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((entrezGeneId == null) ? 0 : entrezGeneId.hashCode());
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
		Gene other = (Gene) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (entrezGeneId == null) {
			if (other.entrezGeneId != null)
				return false;
		} else if (!entrezGeneId.equals(other.entrezGeneId))
			return false;
		return true;
	}

	
}

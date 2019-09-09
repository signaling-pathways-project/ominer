package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.DataResource;
import edu.bcm.dldcc.big.nursa.model.common.MoleculeSynonym;

@Entity
public class Polypeptide implements Serializable {

	private static final long serialVersionUID = -6100163115140948637L;

	@Id
	@GeneratedValue(generator = "polypeptideSequencer")
	@SequenceGenerator(name = "polypeptideSequencer", sequenceName = "POLYPEPTIDE_SEQ")
	private Long id;

	private String uniProtKB_AC;
	private String refseqId;

	private Integer sequenceSize;

	@OneToMany(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private List<MoleculeSynonym> synonyms = new ArrayList<MoleculeSynonym>();

	@OneToMany(mappedBy = "polypeptide")
	@ForeignKey(name = "none")
	private List<MRNA> encodingMRNA = new ArrayList<MRNA>();

	@OneToMany(mappedBy = "polypeptide")
	@ForeignKey(name = "none")
	private List<PolypeptideModification> modifications = new ArrayList<PolypeptideModification>();

	@ManyToMany(mappedBy = "polypeptides")
	@ForeignKey(name = "none")
	private List<Complex> complexes = new ArrayList<Complex>();

	@OneToMany(mappedBy = "polypeptide")
	@ForeignKey(name = "none")
	private List<Expression> expression = new ArrayList<Expression>();

	@ManyToMany(mappedBy = "polypeptides")
	@ForeignKey(name = "none")
	private List<PolypeptideInteraction> interactions = new ArrayList<PolypeptideInteraction>();

	@OneToMany(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private List<DataResource> dataResources = new ArrayList<DataResource>();

	@ManyToMany(mappedBy = "polypeptides")
	@ForeignKey(name = "none")
	private List<CrystalStructure> crystalStructure = new ArrayList<CrystalStructure>();
	
	@ManyToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private DOI doi;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSequenceSize() {
		return sequenceSize;
	}

	public void setSequenceSize(Integer sequenceSize) {
		this.sequenceSize = sequenceSize;
	}

	public List<MoleculeSynonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<MoleculeSynonym> synonyms) {
		this.synonyms = synonyms;
	}

	public List<MRNA> getEncodingMRNA() {
		return encodingMRNA;
	}

	public void setEncodingMRNA(List<MRNA> encodingMRNA) {
		this.encodingMRNA = encodingMRNA;
	}

	public List<PolypeptideModification> getModifications() {
		return modifications;
	}

	public void setModifications(List<PolypeptideModification> modifications) {
		this.modifications = modifications;
	}

	public List<Complex> getComplexes() {
		return complexes;
	}

	public void setComplexes(List<Complex> complexes) {
		this.complexes = complexes;
	}

	public List<Expression> getExpression() {
		return expression;
	}

	public void setExpression(List<Expression> expression) {
		this.expression = expression;
	}

	public List<PolypeptideInteraction> getInteractions() {
		return interactions;
	}

	public void setInteractions(List<PolypeptideInteraction> interactions) {
		this.interactions = interactions;
	}

	public List<DataResource> getDataResources() {
		return dataResources;
	}

	public void setDataResources(List<DataResource> dataResources) {
		this.dataResources = dataResources;
	}

	public List<CrystalStructure> getCrystalStructure() {
		return crystalStructure;
	}

	public void setCrystalStructure(List<CrystalStructure> crystalStructure) {
		this.crystalStructure = crystalStructure;
	}

	public String getUniProtKB_AC() {
		return uniProtKB_AC;
	}

	public void setUniProtKB_AC(String uniProtKB_AC) {
		this.uniProtKB_AC = uniProtKB_AC;
	}

	public String getRefseqId() {
		return refseqId;
	}

	public void setRefseqId(String refseqId) {
		this.refseqId = refseqId;
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
				+ ((complexes == null) ? 0 : complexes.hashCode());
		result = prime
				* result
				+ ((crystalStructure == null) ? 0 : crystalStructure.hashCode());
		result = prime * result
				+ ((dataResources == null) ? 0 : dataResources.hashCode());
		result = prime * result
				+ ((encodingMRNA == null) ? 0 : encodingMRNA.hashCode());
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		result = prime * result
				+ ((interactions == null) ? 0 : interactions.hashCode());
		result = prime * result
				+ ((modifications == null) ? 0 : modifications.hashCode());
		result = prime * result
				+ ((refseqId == null) ? 0 : refseqId.hashCode());
		result = prime * result
				+ ((sequenceSize == null) ? 0 : sequenceSize.hashCode());
		result = prime * result
				+ ((synonyms == null) ? 0 : synonyms.hashCode());
		result = prime * result
				+ ((uniProtKB_AC == null) ? 0 : uniProtKB_AC.hashCode());
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
		Polypeptide other = (Polypeptide) obj;
		if (complexes == null) {
			if (other.complexes != null)
				return false;
		} else if (!complexes.equals(other.complexes))
			return false;
		if (crystalStructure == null) {
			if (other.crystalStructure != null)
				return false;
		} else if (!crystalStructure.equals(other.crystalStructure))
			return false;
		if (dataResources == null) {
			if (other.dataResources != null)
				return false;
		} else if (!dataResources.equals(other.dataResources))
			return false;
		if (encodingMRNA == null) {
			if (other.encodingMRNA != null)
				return false;
		} else if (!encodingMRNA.equals(other.encodingMRNA))
			return false;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (interactions == null) {
			if (other.interactions != null)
				return false;
		} else if (!interactions.equals(other.interactions))
			return false;
		if (modifications == null) {
			if (other.modifications != null)
				return false;
		} else if (!modifications.equals(other.modifications))
			return false;
		if (refseqId == null) {
			if (other.refseqId != null)
				return false;
		} else if (!refseqId.equals(other.refseqId))
			return false;
		if (sequenceSize == null) {
			if (other.sequenceSize != null)
				return false;
		} else if (!sequenceSize.equals(other.sequenceSize))
			return false;
		if (synonyms == null) {
			if (other.synonyms != null)
				return false;
		} else if (!synonyms.equals(other.synonyms))
			return false;
		if (uniProtKB_AC == null) {
			if (other.uniProtKB_AC != null)
				return false;
		} else if (!uniProtKB_AC.equals(other.uniProtKB_AC))
			return false;
		return true;
	}
	
	
}

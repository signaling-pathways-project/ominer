package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.DataResource;
import edu.bcm.dldcc.big.nursa.model.common.Species;

@Entity
public class MiRNA implements Serializable {

	private static final long serialVersionUID = -6100163115140948637L;

	@Id
	@GeneratedValue(generator = "miRNASequencer")
	@SequenceGenerator(name = "miRNASequencer", sequenceName = "MIRNA_SEQ")
	private Long id;

	private String mirBaseId;
	private String matureSequence;
	
	@OneToOne(cascade=CascadeType.ALL)
	@ForeignKey(name = "none")
	private DataResource mirBaseAccession;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private MiRNAFamily family;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private Species species;
	
	@OneToMany(mappedBy = "miRNA")
	@ForeignKey(name = "none")
	private List<MiRNAInteraction> interactions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMirBaseId() {
		return mirBaseId;
	}

	public void setMirBaseId(String mirBaseId) {
		this.mirBaseId = mirBaseId;
	}

	public String getMatureSequence() {
		return matureSequence;
	}

	public void setMatureSequence(String matureSequence) {
		this.matureSequence = matureSequence;
	}

	public DataResource getMirBaseAccession() {
		return mirBaseAccession;
	}

	public void setMirBaseAccession(DataResource mirBaseAccession) {
		this.mirBaseAccession = mirBaseAccession;
	}

	public MiRNAFamily getFamily() {
		return family;
	}

	public void setFamily(MiRNAFamily family) {
		this.family = family;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public List<MiRNAInteraction> getInteractions() {
		return interactions;
	}

	public void setInteractions(List<MiRNAInteraction> interactions) {
		this.interactions = interactions;
	}

	
}

package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.core.Gene;

@Entity
public class GeneModification implements Serializable {


	private static final long serialVersionUID = 6099440744927593057L;

	@Id
	@GeneratedValue(generator = "GeneModificationSequencer")
	@SequenceGenerator(name = "GeneModificationSequencer", sequenceName = "GENEMODIFICATION_SEQ")
	private Long id;

	private Gene gene;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private GeneModificationType modificationType;
	
	private String residue;
	private String sequence;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private Polypeptide polypeptide;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Gene getGene() {
		return gene;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}

	public GeneModificationType getModificationType() {
		return modificationType;
	}

	public void setModificationType(GeneModificationType modificationType) {
		this.modificationType = modificationType;
	}

	public String getResidue() {
		return residue;
	}

	public void setResidue(String residue) {
		this.residue = residue;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

}

package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.Evidence;
import edu.bcm.dldcc.big.nursa.model.omics.Tissue;

@Entity
public class Expression implements Serializable {
	
	private static final long serialVersionUID = -5848967824909130892L;

	@Id
	@GeneratedValue(generator = "expressionSequencer")
	@SequenceGenerator(name = "expressionSequencer", sequenceName = "EXPRESSION_SEQ")
	private Long id;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private Tissue tissue;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private Polypeptide polypeptide;
	
	@OneToOne
	@ForeignKey(name = "none")
	private Evidence evidence;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tissue getTissue() {
		return tissue;
	}

	public void setTissue(Tissue tissue) {
		this.tissue = tissue;
	}

	public Evidence getEvidence() {
		return evidence;
	}

	public void setEvidence(Evidence evidence) {
		this.evidence = evidence;
	}

	public Polypeptide getPolypeptide() {
		return polypeptide;
	}

	public void setPolypeptide(Polypeptide polypeptide) {
		this.polypeptide = polypeptide;
	}
	
	
}

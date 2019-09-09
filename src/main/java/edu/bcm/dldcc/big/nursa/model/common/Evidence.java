package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

@Entity
public class Evidence implements Serializable {

	private static final long serialVersionUID = -3894807174672192676L;
	
	@Id
	@GeneratedValue(generator = "evidenceSequencer")
	@SequenceGenerator(name = "evidenceSequencer", sequenceName = "EVIDENCE_SEQ")
	private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private EvidenceCode evidenceCode;

	@ManyToMany(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private List<Reference> references = new ArrayList<Reference>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EvidenceCode getEvidenceCode() {
		return evidenceCode;
	}

	public void setEvidenceCode(EvidenceCode evidenceCode) {
		this.evidenceCode = evidenceCode;
	}

	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

}
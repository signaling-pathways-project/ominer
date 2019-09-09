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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.Reference;

@Entity
public class MiRNAInteraction  implements Serializable {

	private static final long serialVersionUID = 8101295662681940987L;

	@Id
	@GeneratedValue(generator = "miRNAInterSequencer")
	@SequenceGenerator(name = "miRNAInterSequencer", sequenceName = "MIRNAINTER_SEQ")
	private Long id;
	
	@OneToOne(cascade=CascadeType.ALL)
	@ForeignKey(name = "none")
	private Reference reference;
	
	@Transient 
	private  List<Reference> references=new ArrayList<Reference>();
	
	@ManyToOne
	@ForeignKey(name = "none")
	private MiRNA miRNA;
	
	@ManyToMany
	@ForeignKey(name = "none")
	private List<MRNA> mrna;
	
	private Long targetPosition;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public MiRNA getMiRNA() {
		return miRNA;
	}

	public void setMiRNA(MiRNA miRNA) {
		this.miRNA = miRNA;
	}

	public List<MRNA> getMrna() {
		return mrna;
	}

	public void setMrna(List<MRNA> mrna) {
		this.mrna = mrna;
	}

	public Long getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Long targetPosition) {
		this.targetPosition = targetPosition;
	}
	public List<Reference> getReferences() {
		if(this.reference!=null){
			this.references.clear();
			this.references.add(this.reference);
		}
		return references;
	}	
	
}

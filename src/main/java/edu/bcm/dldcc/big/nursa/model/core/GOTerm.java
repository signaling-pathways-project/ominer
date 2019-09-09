package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.DataResource;
import edu.bcm.dldcc.big.nursa.model.common.Reference;


@Entity
@Table(name="GOTERM")
public class GOTerm implements Serializable {

	private static final long serialVersionUID = -6523861523884879634L;

	@Id
	@GeneratedValue(generator = "goTermSequencer")
	@SequenceGenerator(name = "goTermSequencer", sequenceName = "GOTERM_SEQ")
	private Long id;

	@Basic
	private String termName;
	@Basic
	private String goTermID;
	
	@ManyToMany
	@ForeignKey(name = "none")
	private List<Gene> genes = new ArrayList<Gene>();
	
	@OneToOne
	@ForeignKey(name = "none")
	private Reference reference;
	
	@Transient
	private List<Reference> references= new ArrayList<Reference>();
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="GOTERM_Dataresource")
	@ForeignKey(name = "none")
	private List<DataResource> dataResources = new ArrayList<DataResource>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}
	
	public String getGoTermID() {
		return goTermID;
	}

	public void setGoTermID(String goTermID) {
		this.goTermID = goTermID;
	}

	public List<Gene> getGenes() {
		return genes;
	}

	public void setGenes(List<Gene> genes) {
		this.genes = genes;
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public List<DataResource> getDataResources() {
		return dataResources;
	}

	public void setDataResources(List<DataResource> dataResources) {
		this.dataResources = dataResources;
	}

	public List<Reference> getReferences() {
		if(this.reference!=null){
			this.references.clear();
			this.references.add(this.reference);
		}
		return references;
	}	
	
	
}

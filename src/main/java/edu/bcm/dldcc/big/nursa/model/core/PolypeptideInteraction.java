package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.DataResource;
import edu.bcm.dldcc.big.nursa.model.common.Reference;

@Entity
public class PolypeptideInteraction implements Serializable {
	private static final long serialVersionUID = 2104832031211193455L;

	@Id
	@GeneratedValue(generator = "polypepInterSequencer")
	@SequenceGenerator(name = "polypepInterSequencer", sequenceName = "POLYPEPINTER_SEQ")
	private Long id;

	private String name;

	private String interactionId;
	
	private String complexId;
	
	@OneToOne
	@ForeignKey(name = "none")
	private ComplexInteractionType complexType; 
	
	@OneToOne
	@ForeignKey(name = "none")
	private PolypeptideInteractionType polyinteractionType;

	@OneToMany
	@JoinTable(name = "PolyInt_Reference")
	@ForeignKey(name = "none")
	private List<Reference> references = new ArrayList<Reference>();

	@Column(length = 3500)
	private String description;

	@ManyToMany
	@JoinTable(name = "PolyInt_Poly")
	@ForeignKey(name = "none")
	private List<Polypeptide> polypeptides = new ArrayList<Polypeptide>();
	
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="PolyInt_Dataresource")
	@ForeignKey(name = "none")
	private List<DataResource> dataResources = new ArrayList<DataResource>();
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}

	public String getComplexId() {
		return complexId;
	}

	public void setComplexId(String complexId) {
		this.complexId = complexId;
	}

	public ComplexInteractionType getComplexType() {
		return complexType;
	}

	public void setComplexType(ComplexInteractionType complexType) {
		this.complexType = complexType;
	}

	public PolypeptideInteractionType getPolyinteractionType() {
		return polyinteractionType;
	}

	public void setPolyinteractionType(PolypeptideInteractionType polyinteractionType) {
		this.polyinteractionType = polyinteractionType;
	}

	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Polypeptide> getPolypeptides() {
		return polypeptides;
	}

	public void setPolypeptides(List<Polypeptide> polypeptides) {
		this.polypeptides = polypeptides;
	}

	public List<DataResource> getDataResources() {
		return dataResources;
	}

	public void setDataResources(List<DataResource> dataResources) {
		this.dataResources = dataResources;
	}
	
	

}
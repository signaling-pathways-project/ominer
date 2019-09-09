package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.MoleculeSynonym;

@Entity
public class Complex implements Serializable {

	private static final long serialVersionUID = -700735640264196283L;

	@Id
	@GeneratedValue(generator = "complexSequencer")
	@SequenceGenerator(name = "complexSequencer", sequenceName = "COMPLEX_SEQ")
	private Long id;
	
	private String name;
	
	@OneToMany(cascade=CascadeType.ALL)
	@ForeignKey(name = "none")
	private List<MoleculeSynonym> synonyms = new ArrayList<MoleculeSynonym>();
	
	@ManyToMany
	@ForeignKey(name = "none")
	private List<Polypeptide> polypeptides = new ArrayList<Polypeptide>();
	
	@OneToOne
	@ForeignKey(name = "none")
	private CrystalStructure crystalStructure;

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

	public List<Polypeptide> getPolypeptides() {
		return polypeptides;
	}

	public void setPolypeptides(List<Polypeptide> polypeptides) {
		this.polypeptides = polypeptides;
	}
	
	
}

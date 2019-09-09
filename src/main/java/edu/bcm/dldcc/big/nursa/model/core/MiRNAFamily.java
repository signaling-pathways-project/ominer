package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

@Entity
public class MiRNAFamily implements Serializable {

	private static final long serialVersionUID = 4874760841701025121L;

	@Id
	@GeneratedValue(generator = "miRNAFamSequencer")
	@SequenceGenerator(name = "miRNAFamSequencer", sequenceName = "MIRNAFAM_SEQ")
	private Long id;
	
	private String name;
	private String seed;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy = "family")
	@ForeignKey(name = "none")
	private List<MiRNA> members = new ArrayList<MiRNA>();

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

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	public List<MiRNA> getMembers() {
		return members;
	}

	public void setMembers(List<MiRNA> members) {
		this.members = members;
	}
	
	
}

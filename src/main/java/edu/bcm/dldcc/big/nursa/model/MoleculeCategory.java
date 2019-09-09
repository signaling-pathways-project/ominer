package edu.bcm.dldcc.big.nursa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

/**
 * Regulatory Molecule Category for NR and Ligands
 * @author mcowiti
 *
 */
@Entity
public class MoleculeCategory implements Serializable {

	private static final long serialVersionUID = 3211017250910425223L;

	@Id @GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(length = 36)
	private String id;
	
	@Basic
	private String name;
	
	@Basic
	private String abbreviation;
	
	@ManyToMany
	@JoinTable(name="MOLECULE_CATEGORY")
	@ForeignKey(name = "none")
	private Set<Ligand> ligands= new HashSet<Ligand> ();
	
	@OneToMany(mappedBy="moleculeCategory")
	@ForeignKey(name = "none")
	private List<NuclearReceptor> receptors= new ArrayList<NuclearReceptor>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public Set<Ligand> getLigands() {
		return ligands;
	}

	public void setLigands(Set<Ligand> ligands) {
		this.ligands = ligands;
	}

	public List<NuclearReceptor> getReceptors() {
		return receptors;
	}

	public void setReceptors(List<NuclearReceptor> receptors) {
		this.receptors = receptors;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((abbreviation == null) ? 0 : abbreviation.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MoleculeCategory other = (MoleculeCategory) obj;
		if (abbreviation == null) {
			if (other.abbreviation != null)
				return false;
		} else if (!abbreviation.equals(other.abbreviation))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}

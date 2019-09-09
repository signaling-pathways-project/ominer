package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.DataResource;

@Entity
public class CrystalStructure implements Serializable {
	private static final long serialVersionUID = -3523122479019684136L;

	@Id
	@GeneratedValue(generator = "crystalSequencer")
	@SequenceGenerator(name = "crystalSequencer", sequenceName = "CRYSTAL_SEQ")
	private Long id;

	@Column(length=1000)
	private String title;

	@OneToMany(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private List<CrystalStructureTechnique> techniques = new ArrayList<CrystalStructureTechnique>();

	@OneToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private DataResource pdbEntry;

	@ManyToMany
	@ForeignKey(name = "none")
	private List<Polypeptide> polypeptides;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<CrystalStructureTechnique> getTechniques() {
		return techniques;
	}

	public void setTechniques(List<CrystalStructureTechnique> techniques) {
		this.techniques = techniques;
	}

	public DataResource getPdbEntry() {
		return pdbEntry;
	}

	public void setPdbEntry(DataResource pdbEntry) {
		this.pdbEntry = pdbEntry;
	}

	public List<Polypeptide> getPolypeptides() {
		return polypeptides;
	}

	public void setPolypeptide(List<Polypeptide> polypeptides) {
		this.polypeptides = polypeptides;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pdbEntry == null) ? 0 : pdbEntry.hashCode());
		result = prime * result
				+ ((polypeptides == null) ? 0 : polypeptides.hashCode());
		result = prime * result
				+ ((techniques == null) ? 0 : techniques.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		CrystalStructure other = (CrystalStructure) obj;
		if (pdbEntry == null) {
			if (other.pdbEntry != null)
				return false;
		} else if (!pdbEntry.equals(other.pdbEntry))
			return false;
		if (polypeptides == null) {
			if (other.polypeptides != null)
				return false;
		} else if (!polypeptides.equals(other.polypeptides))
			return false;
		if (techniques == null) {
			if (other.techniques != null)
				return false;
		} else if (!techniques.equals(other.techniques))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	

}

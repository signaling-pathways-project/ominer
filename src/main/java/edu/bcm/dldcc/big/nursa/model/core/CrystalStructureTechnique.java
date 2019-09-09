package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="csTechnique")
public class CrystalStructureTechnique implements Serializable {
	
	private static final long serialVersionUID = -2740774954105214432L;

	@Id
	@GeneratedValue(generator = "crystalSequencerTech")
	@SequenceGenerator(name = "crystalSequencerTech", sequenceName = "CRYSTALTECH_SEQ")
	private Long id;
	
	private String resolution;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private CrystalStructureTechniqueType technique;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public CrystalStructureTechniqueType getTechnique() {
		return technique;
	}

	public void setTechnique(CrystalStructureTechniqueType technique) {
		this.technique = technique;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resolution == null) ? 0 : resolution.hashCode());
		result = prime * result
				+ ((technique == null) ? 0 : technique.hashCode());
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
		CrystalStructureTechnique other = (CrystalStructureTechnique) obj;
		if (resolution == null) {
			if (other.resolution != null)
				return false;
		} else if (!resolution.equals(other.resolution))
			return false;
		if (technique == null) {
			if (other.technique != null)
				return false;
		} else if (!technique.equals(other.technique))
			return false;
		return true;
	}
	
	
}

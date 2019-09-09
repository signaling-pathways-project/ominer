package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="csTechType")
public class CrystalStructureTechniqueType implements Serializable {
	
	
	private static final long serialVersionUID = 5989546124960312704L;

	@Id
	@GeneratedValue(generator = "crystalSequencerTechType")
	@SequenceGenerator(name = "crystalSequencerTechType", sequenceName = "CRYSTALTECHTYPE_SEQ")
	private Long id;
	
	private String technniqueTypeName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTechnniqueTypeName() {
		return technniqueTypeName;
	}

	public void setTechnniqueTypeName(String technniqueTypeName) {
		this.technniqueTypeName = technniqueTypeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((technniqueTypeName == null) ? 0 : technniqueTypeName
						.hashCode());
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
		CrystalStructureTechniqueType other = (CrystalStructureTechniqueType) obj;
		if (technniqueTypeName == null) {
			if (other.technniqueTypeName != null)
				return false;
		} else if (!technniqueTypeName.equals(other.technniqueTypeName))
			return false;
		return true;
	}
	
	

}

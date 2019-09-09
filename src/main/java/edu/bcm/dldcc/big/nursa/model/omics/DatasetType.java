package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.ForeignKey;

/**
 * Dataset Type
 * @author mcowiti
 *
 */
@Entity
@XmlRootElement
@Cacheable(true)
public class DatasetType implements Serializable {

	private static final long serialVersionUID = -8209168425931245466L;

	@Id @GeneratedValue(generator = "system-uuid")
	@Column(length = 36,unique = true)
	private String id;
	
	@ManyToOne
	@JoinColumn(name = "EXPERIMENTTYPE_ID")
	@ForeignKey(name = "none")
	private ExperimentType experimentType;

	@ManyToOne
	@ForeignKey(name = "none")
	private ExperimentTechnique experimentTechnique;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ExperimentType getExperimentType() {
		return experimentType;
	}

	public void setExperimentType(ExperimentType experimentType) {
		this.experimentType = experimentType;
	}

	public ExperimentTechnique getExperimentTechnique() {
		return experimentTechnique;
	}

	public void setExperimentTechnique(ExperimentTechnique experimentTechnique) {
		this.experimentTechnique = experimentTechnique;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((experimentTechnique == null) ? 0 : experimentTechnique
						.hashCode());
		result = prime * result
				+ ((experimentType == null) ? 0 : experimentType.hashCode());
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
		DatasetType other = (DatasetType) obj;
		if (experimentTechnique == null) {
			if (other.experimentTechnique != null)
				return false;
		} else if (!experimentTechnique.equals(other.experimentTechnique))
			return false;
		if (experimentType == null) {
			if (other.experimentType != null)
				return false;
		} else if (!experimentType.equals(other.experimentType))
			return false;
		return true;
	}
	
	
}

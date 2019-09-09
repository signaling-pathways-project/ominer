package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class ConsensomeId implements Serializable {

	private static final long serialVersionUID = 401871715417375345L;

	@JsonProperty
	@Basic
	private String doi;

	 @JsonProperty
	 @Basic
	private Long familyId; //0 for ALL
	 @JsonProperty
	 @Basic
	private Long organId; //0 for ALL
	 @JsonProperty
	 @Basic
	private Long psId; //0 for ALL
	
	 @JsonProperty
	private String family;

	@JsonProperty
	@Basic
	private Long modelId;

	@JsonProperty
	private String model;
	 
	 @JsonProperty
	 @Basic
	private String physiologicalSystem;
	 
	 @JsonProperty
	 @Basic
	private String organ;
	 
	 @JsonProperty
	 @Basic
	private String species;
	
	public ConsensomeId() {
		super();
	}
	public String getPhysiologicalSystem() {
		return physiologicalSystem;
	}
	public void setPhysiologicalSystem(String physiologicalSystem) {
		this.physiologicalSystem = physiologicalSystem;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public Long getFamilyId() {
		return familyId;
	}
	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}
	public Long getOrganId() {
		return organId;
	}
	public void setOrganId(Long organId) {
		this.organId = organId;
	}
	public Long getPsId() {
		return psId;
	}
	public void setPsId(Long psId) {
		this.psId = psId;
	}

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("ConsensomeId{");
		sb.append("doi='").append(doi).append('\'');
		sb.append(", familyId=").append(familyId);
		sb.append(", organId=").append(organId);
		sb.append(", psId=").append(psId);
		sb.append(", family='").append(family).append('\'');
		sb.append(", physiologicalSystem='").append(physiologicalSystem).append('\'');
		sb.append(", organ='").append(organ).append('\'');
		sb.append(", species='").append(species).append('\'');
		sb.append('}');
		return sb.toString();
	}
}

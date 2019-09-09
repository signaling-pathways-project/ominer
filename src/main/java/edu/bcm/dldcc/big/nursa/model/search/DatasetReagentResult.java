package edu.bcm.dldcc.big.nursa.model.search;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity represents Dataset Reagent View for lazy loading dataset reagent collections
 * @author mcowiti
 *
 */
@Entity
public class DatasetReagentResult implements Serializable {
	
	
	private static final long serialVersionUID = 7319015224095458365L;

	@Id
	private String id;
	
	private Long datasetId;
	@Basic
	private String datasetDoi;
	private String description;
	
	private String doi;
	private String reagentId;
	private String speciesCommonName;
	private String type;
	private String name;
	private String source;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getDatasetId() {
		return datasetId;
	}
	public void setDatasetId(Long datasetId) {
		this.datasetId = datasetId;
	}
	public String getDatasetDoi() {
		return datasetDoi;
	}
	public void setDatasetDoi(String datasetDoi) {
		this.datasetDoi = datasetDoi;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getReagentId() {
		return reagentId;
	}
	public void setReagentId(String reagentId) {
		this.reagentId = reagentId;
	}
	public String getSpeciesCommonName() {
		return speciesCommonName;
	}
	public void setSpeciesCommonName(String speciesCommonName) {
		this.speciesCommonName = speciesCommonName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result
				+ ((reagentId == null) ? 0 : reagentId.hashCode());
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
		DatasetReagentResult other = (DatasetReagentResult) obj;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (reagentId == null) {
			if (other.reagentId != null)
				return false;
		} else if (!reagentId.equals(other.reagentId))
			return false;
		return true;
	}
	
}

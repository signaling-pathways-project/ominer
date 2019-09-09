package edu.bcm.dldcc.big.nursa.model.search;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity represents Dataset Molecule View for lazy loading dataset molecueles collections
 * @author mcowiti
 *
 */
@Entity
public class DatasetMoleculeResult implements Serializable {
	
	
	private static final long serialVersionUID = -7080012179154328809L;

	@Id
	private String id;
	
	@Column(name="NURSADATASET_ID")
	private Long datasetId;
	
	
	@Basic
	@Column(name="DATASET_DOI")
	private String datasetDoi;
	
	@Column(name="DATASET_NAME")
	private String datasetName;
	
	private String description;
	private String doi;
	@Column(name="MOLECULES_ID")
	private String moleculeId;
	private String name;
	private String type;
	private String symbol;
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
	public String getDatasetName() {
		return datasetName;
	}
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
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
	public String getMoleculeId() {
		return moleculeId;
	}
	public void setMoleculeId(String moleculeId) {
		this.moleculeId = moleculeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result
				+ ((moleculeId == null) ? 0 : moleculeId.hashCode());
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
		DatasetMoleculeResult other = (DatasetMoleculeResult) obj;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (moleculeId == null) {
			if (other.moleculeId != null)
				return false;
		} else if (!moleculeId.equals(other.moleculeId))
			return false;
		return true;
	}

}

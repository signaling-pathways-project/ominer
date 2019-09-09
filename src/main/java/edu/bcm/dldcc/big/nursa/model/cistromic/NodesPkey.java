package edu.bcm.dldcc.big.nursa.model.cistromic;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class NodesPkey  implements Serializable {

	private static final long serialVersionUID = -7915962915226438621L;
	private Long datasetid;
	private Long experimentid;

	public Long getDatasetid() {
		return datasetid;
	}

	public void setDatasetid(Long datasetid) {
		this.datasetid = datasetid;
	}

	public Long getExperimentid() {
		return experimentid;
	}

	public void setExperimentid(Long experimentid) {
		this.experimentid = experimentid;
	}
	
	
}

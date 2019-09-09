package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class SigPathNode implements Serializable {

	private static final long serialVersionUID = 7295009458148418599L;
	
	private Long id;
	private String sigPathNodes;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSigPathNodes() {
		return sigPathNodes;
	}
	public void setSigPathNodes(String sigPathNodes) {
		this.sigPathNodes = sigPathNodes;
	}

}

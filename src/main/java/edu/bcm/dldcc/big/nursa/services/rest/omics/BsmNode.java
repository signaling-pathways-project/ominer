package edu.bcm.dldcc.big.nursa.services.rest.omics;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "bsmNode")
public class BsmNode {
	@JsonProperty
		  String bsm;
	@JsonProperty
		  String node;
	public String getBsm() {
		return bsm;
	}
	public void setBsm(String bsm) {
		this.bsm = bsm;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}

    public BsmNode() {
    }

    public BsmNode(String bsm, String node) {
        this.bsm = bsm;
        this.node = node;
    }
}

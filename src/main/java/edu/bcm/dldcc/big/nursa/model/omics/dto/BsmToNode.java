package edu.bcm.dldcc.big.nursa.model.omics.dto;

public class BsmToNode {

    private String bsm;
    private String node;

    public BsmToNode(String bsm, String node) {
        this.bsm = bsm;
        this.node = node;
    }

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
}

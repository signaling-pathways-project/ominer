package edu.bcm.dldcc.big.nursa.model.omics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table( name = "BSMS_INACTIVE_VIEW")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bsm")
public class BsmInactiveView {

    @JsonIgnore
    @Id
    private Long id;

    @JsonIgnore
    private Long bid;

    @JsonProperty(value="bsmOfficialSymbol")
    private String bsm;

    @JsonProperty
    private String name;

    @JsonIgnore
    @Basic
    private String node;

    @JsonProperty
    @Basic
    private String iuphar;
    @JsonProperty
    @Column(name="PUBCHEM")
    private Integer pubchemId;

    @Basic
    @JsonProperty
    private String evidence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public String getBsm() {
        return bsm;
    }

    public void setBsm(String bsm) {
        this.bsm = bsm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getIuphar() {
        return iuphar;
    }

    public void setIuphar(String iuphar) {
        this.iuphar = iuphar;
    }

    public Integer getPubchemId() {
        return pubchemId;
    }

    public void setPubchemId(Integer pubchemId) {
        this.pubchemId = pubchemId;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
}

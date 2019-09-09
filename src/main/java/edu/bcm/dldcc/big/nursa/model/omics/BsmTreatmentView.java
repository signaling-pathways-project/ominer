package edu.bcm.dldcc.big.nursa.model.omics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "EXPBSMTREATMENT_VIEW")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bsm")
public class BsmTreatmentView {

    @JsonIgnore
    @Id
    private Long id;

    private Long expid;
    private Long treatmentid;
    private Long bsmid;
    @JsonProperty
    private String bsm;
    @JsonProperty
    private String treatement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExpid() {
        return expid;
    }

    public void setExpid(Long expid) {
        this.expid = expid;
    }

    public Long getTreatmentid() {
        return treatmentid;
    }

    public void setTreatmentid(Long treatmentid) {
        this.treatmentid = treatmentid;
    }

    public Long getBsmid() {
        return bsmid;
    }

    public void setBsmid(Long bsmid) {
        this.bsmid = bsmid;
    }

    public String getBsm() {
        return bsm;
    }

    public void setBsm(String bsm) {
        this.bsm = bsm;
    }

    public String getTreatement() {
        return treatement;
    }

    public void setTreatement(String treatement) {
        this.treatement = treatement;
    }
}

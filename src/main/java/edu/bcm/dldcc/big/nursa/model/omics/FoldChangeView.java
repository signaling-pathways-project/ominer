package edu.bcm.dldcc.big.nursa.model.omics;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * New Fold Change Representation
 * @author mcowiti
 */

@Entity
@XmlRootElement(name="dataPoint")
@Table(name="expmicroarrayexpression")
@XmlAccessorType(XmlAccessType.FIELD)
public class FoldChangeView {

    @Id
    @JsonProperty(value="datapointid")
    private Long id;

    @JsonProperty
    @Column(name="experiment_id")
    private Long expid;

    @JsonProperty
    @Column(name="symbol")
    private String gene;

    @JsonProperty
    private Long egeneid;

    @JsonProperty
    @Column(name="probeidentifier")
    private String prob;
    @JsonProperty
    @Column(name="probeidentifiertype")
    private String probType;
    @JsonProperty
    @Basic
    private double foldChange;
    @JsonProperty
    @Basic
    private double pValue;


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

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getProb() {
        return prob;
    }

    public void setProb(String prob) {
        this.prob = prob;
    }

    public String getProbType() {
        return probType;
    }

    public void setProbType(String probType) {
        this.probType = probType;
    }


    public void setFoldChange(double foldChange) {
        this.foldChange = foldChange;
    }

    public double getpValue() {
        return pValue;
    }

    public void setpValue(double pValue) {
        this.pValue = pValue;
    }

    public Long getEgeneid() {
        return egeneid;
    }

    public void setEgeneid(Long egeneid) {
        this.egeneid = egeneid;
    }

    public Double getFoldChange() {

        if (foldChange <1D && foldChange !=0D) {
            return -1/foldChange;
        }
        return foldChange;
    }

    public Double getFoldChangeRaw() {

        return foldChange;
    }

}

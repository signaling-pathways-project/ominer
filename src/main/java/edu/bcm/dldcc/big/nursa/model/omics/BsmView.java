package edu.bcm.dldcc.big.nursa.model.omics;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@Entity
@Table(name = "BSMS_VIEW")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name = "bsm")
public class BsmView {

	public enum EvidenceType{
		iuphar,pubmed
	}
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

    @JsonProperty
    private String cas;
    @JsonProperty
    private String chebi;

	@Basic
    @JsonIgnore
    private String evidence;

	@JsonIgnore
	@Transient
	@Enumerated(EnumType.STRING)
	private EvidenceType evidenceType;
    
    @JsonProperty
    @Transient
    private String url;

    @JsonProperty
    @Transient
    private String pubchemUrl;

    @PostLoad
	public void onLoad(){
		if(this.evidence != null)
			this.evidenceType=EvidenceType.pubmed;
		else
			evidenceType=EvidenceType.iuphar;
	}

	public String getEvidence() {
		if(this.evidenceType == EvidenceType.pubmed)
			return evidence;
		return iuphar;
	}

	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}

	public EvidenceType getEvidenceType() {
		return evidenceType;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    public String getPubchemUrl() {
        return pubchemUrl;
    }

    public void setPubchemUrl(String pubchemUrl) {
        this.pubchemUrl = pubchemUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public String getChebi() {
        return chebi;
    }

    public void setChebi(String chebi) {
        this.chebi = chebi;
    }

    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BsmView bsmView = (BsmView) o;
		return Objects.equals(bid, bsmView.bid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bid);
	}
}

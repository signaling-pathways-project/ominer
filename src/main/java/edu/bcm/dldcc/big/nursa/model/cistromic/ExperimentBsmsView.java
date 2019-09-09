package edu.bcm.dldcc.big.nursa.model.cistromic;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Experiment->BSM/Node->Pathway View
 * @author mcowiti
 *
 */
@Entity
@Table(name = "DATASETEXPBSMS_VIEW")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name = "bsm")
public class ExperimentBsmsView implements Serializable {

	private static final long serialVersionUID = -524220804712844433L;

	@JsonIgnore
	@Id
	private Long id;
	@JsonIgnore
	private Long did;
	@JsonIgnore
    private Long expid;
	@JsonIgnore
    private Long lid;
    
    @JsonProperty(value="bsmOfficialSymbol")
    @Column(name="NAME")
    private String bsm;
    
    @JsonProperty(value="targetNode")
    @Basic
    private String node;
    
    @JsonProperty
    @Basic
    private String iuphar;
    @JsonProperty
    @Column(name="PUBCHEM")
    private Integer pubchemId;
    
    @JsonProperty
    @Transient
    private String url;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDid() {
		return did;
	}
	public void setDid(Long did) {
		this.did = did;
	}
	public Long getExpid() {
		return expid;
	}
	public void setExpid(Long expid) {
		this.expid = expid;
	}
	public String getBsm() {
		return bsm;
	}
	public void setBsm(String bsm) {
		this.bsm = bsm;
	}
	
	public Long getLid() {
		return lid;
	}
	public void setLid(Long lid) {
		this.lid = lid;
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

}

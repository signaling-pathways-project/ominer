package edu.bcm.dldcc.big.nursa.model.cistromic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "DATASETEXPBSMSNODES_VIEW")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name = "experimentNodes")
public class ExperimentNodesView {

	@Id
	private Long id;
    private Long did;
    private Long expid;
    private String bsm;
    private String chebi;
    private String cas;
    private String iuphar;
    private Long pubchemId;

    private String ipags;
    private String oags;


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
	public String getIpags() {
		return ipags;
	}
	public void setIpags(String ipags) {
		this.ipags = ipags;
	}
	public String getOags() {
		return oags;
	}
	public void setOags(String oags) {
		this.oags = oags;
	}

    public String getBsm() {
        return bsm;
    }

    public void setBsm(String bsm) {
        this.bsm = bsm;
    }

    public String getChebi() {
        return chebi;
    }

    public void setChebi(String chebi) {
        this.chebi = chebi;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public String getIuphar() {
        return iuphar;
    }

    public void setIuphar(String iuphar) {
        this.iuphar = iuphar;
    }

    public Long getPubchemId() {
        return pubchemId;
    }

    public void setPubchemId(Long pubchem) {
        this.pubchemId = pubchem;
    }


}

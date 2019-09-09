package edu.bcm.dldcc.big.nursa.model.cistromic;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "DATASETNODESNPSORGANS_VIEW")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name = "nodes")
public class DatasetNodesView implements Serializable {
	
	
	private static final long serialVersionUID = -5174720956483851997L;
	@EmbeddedId
	private NodesPkey id;
     private String psorgan;
     private String bsm;
     private String ags;
     private String oags;
     private String model;

	public NodesPkey getId() {
		return id;
	}
	public void setId(NodesPkey id) {
		this.id = id;
	}
	public String getPsorgan() {
		return psorgan;
	}
	public void setPsorgan(String psorgan) {
		this.psorgan = psorgan;
	}
	public String getBsm() {
		return bsm;
	}
	public void setBsm(String bsm) {
		this.bsm = bsm;
	}
	public String getAgs() {
		return ags;
	}
	public void setAgs(String ags) {
		this.ags = ags;
	}
	public String getOags() {
		return oags;
	}
	public void setOags(String oags) {
		this.oags = oags;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
}

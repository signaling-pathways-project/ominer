package edu.bcm.dldcc.big.nursa.model.cistromic;

import java.util.ArrayList;
import java.util.List;

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

@Entity
@Table(name = "signalingpathway_view")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name = "pathwayNnodes")
public class PathwayNodesView {
	
	@JsonIgnore
	@Id
	private Long id;
	
	@JsonIgnore
	private String node;
	private Long familyid;
	
	@JsonProperty
	@Basic
	private String type;
	@JsonProperty
	@Basic
	private String category;
	@JsonProperty
	@Column(name="class")
	private String cclass;
	@JsonProperty
	@Basic
	private String family;
	
	@JsonProperty
	@Transient
	private String nodes;
	
	@JsonProperty
	@Transient
	private List<String> nodesList= new ArrayList<String>();

	public String getNodes() {
		return nodes;
	}
	public void setNodes(String nodes) {
		this.nodes = nodes;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public Long getFamilyid() {
		return familyid;
	}
	public void setFamilyid(Long familyid) {
		this.familyid = familyid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCclass() {
		return cclass;
	}
	public void setCclass(String cclass) {
		this.cclass = cclass;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public List<String> getNodesList() {
		return nodesList;
	}
	public void setNodesList(List<String> nodesList) {
		this.nodesList = nodesList;
	}
	
}

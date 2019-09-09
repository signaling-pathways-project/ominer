package edu.bcm.dldcc.big.nursa.model.omics.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.bcm.dldcc.big.nursa.model.omics.PathwayCategory;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway;

@XmlRootElement(name="SignalingPathway")
@XmlAccessorType (XmlAccessType.FIELD)
public class Pathway {

	
	 @XmlElement
	 private Long id;

	@XmlElement
	private String name;
		 
		
	@XmlElement(name="displayOrder")
	private Integer displayOrder;

	@XmlElement
	private PathwayCategory type;

	@XmlElement(name="children")
	private List<Pathway> pathways = new ArrayList<Pathway>();

	public Pathway() {
		super();
	}
	
	public Pathway(SignalingPathway entity) {
		super();
		this.id=entity.getId();
		this.name=entity.getName();
		this.displayOrder=entity.getDisplayOrder();
		this.type=entity.getType();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public PathwayCategory getType() {
		return type;
	}

	public List<Pathway> getPathways() {
		return pathways;
	}

	
}

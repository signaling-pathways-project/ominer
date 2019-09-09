package edu.bcm.dldcc.big.nursa.model.omics.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import edu.bcm.dldcc.big.nursa.model.omics.PathwayCategory;

@XmlRootElement(name="PathwayNode")
@XmlAccessorType (XmlAccessType.FIELD)
public class PathwayNode {

	private PathwayCategory type;
	private String typeIds;
	private String categoryIds;
	private String classIds;
	private String familyIds;
	
	public PathwayNode() {
		super();
	}

	public PathwayNode(PathwayCategory type, String typeIds, String categoryIds, String classIds, String familyIds) {
		super();
		this.type = type;
		this.typeIds = typeIds;
		this.categoryIds = categoryIds;
		this.classIds = classIds;
		this.familyIds = familyIds;
	}


	public PathwayCategory getType() {
		return type;
	}
	public void setType(PathwayCategory type) {
		this.type = type;
	}
	public String getTypeIds() {
		return typeIds;
	}
	public void setTypeIds(String typeIds) {
		this.typeIds = typeIds;
	}
	public String getCategoryIds() {
		return categoryIds;
	}
	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}
	public String getClassIds() {
		return classIds;
	}
	public void setClassIds(String classIds) {
		this.classIds = classIds;
	}
	public String getFamilyIds() {
		return familyIds;
	}
	public void setFamilyIds(String familyIds) {
		this.familyIds = familyIds;
	}


}

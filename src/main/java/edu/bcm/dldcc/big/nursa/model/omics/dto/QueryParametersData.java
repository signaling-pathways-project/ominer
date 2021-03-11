package edu.bcm.dldcc.big.nursa.model.omics.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name = "queryParameter")
@XmlAccessorType (XmlAccessType.PROPERTY)
@JsonInclude(Include.NON_EMPTY)
public class QueryParametersData implements Serializable {

	private static final long serialVersionUID = 78777593881238360L;
	
	private String geneSearchType;
	private String gene;
	private String geneList;
	private String bsm;
	private String doi;
	private String moleculetreatmentTime;
	private String foldChange;
	private String foldChangeMax;
	private String foldChangeMin;
	private String significance;
	private String direction;
	private String ps;
	private String organ;
	private String tissue;
	private String tissueCategory;
	private String disease;
	private String goTerm;
	private String species;
	private String signalingPathway;
	private String pathwayCategory;
	private String findCount;
	private String pathwayType;
	
	public QueryParametersData() {
		super();
	}
	
	
	@XmlElement
	public String getPathwayType() {
		return pathwayType;
	}

	@XmlElement(required=true)
	public String getGeneSearchType() {
		return geneSearchType;
	}
	
	@XmlElement
	public String getGene() {
		return gene;
	}
	@XmlElement
	public String getDisease() {
		return disease;
	}
	@XmlElement
	public String getGeneList() {
		return geneList;
	}
	@XmlElement
	public String getBsm() {
		return bsm;
	}
	@XmlElement
	public String getMoleculetreatmentTime() {
		return moleculetreatmentTime;
	}
	
	@XmlElement
	public String getFoldChange() {
		return foldChange;
	}
	@XmlElement
	public String getSignificance() {
		return significance;
	}
	@XmlElement
	public String getDirection() {
		return direction;
	}
	@XmlElement
	public String getSpecies() {
		return species;
	}
	
	@XmlElement
	public String getTissue() {
		return tissue;
	}

	@XmlElement
	public String getPs() {
		return ps;
	}
	
	@XmlElement
	public String getTissueCategory() {
		return tissueCategory;
	}

	@XmlElement
	public String getOrgan() {
		return organ;
	}


	@XmlElement
	public String getGoTerm() {
		return goTerm;
	}
	
	@XmlElement
	public String getFoldChangeMin() {
		return foldChangeMin;
	}
	@XmlElement
	public String getFoldChangeMax() {
		return foldChangeMax;
	}
	@XmlElement
	public String getSignalingPathway() {
		return signalingPathway;
	}

	@XmlElement
	public String getDoi() {
		return doi;
	}

	public String getPathwayCategory() {
		return pathwayCategory;
	}

	public QueryParametersData setGoTerm(String goTerm) {
		this.goTerm = goTerm;
		return this;
	}

	public QueryParametersData setPathwayType(String pathwayType) {
		this.pathwayType = pathwayType;
		return this;
	}
	
	public QueryParametersData setGeneSearchType(String geneSearchType) {
		this.geneSearchType = geneSearchType;
		return this;
	}
	
	public QueryParametersData setGene(String gene) {
		this.gene = gene;
		return this;
	}
	
	public QueryParametersData setGeneList(String geneList) {
		this.geneList = geneList;
		return this;
	}
	
	public QueryParametersData setDisease(String disease) {
		this.disease = disease;
		return this;
	}
	
	public QueryParametersData setBsm(String bsm) {
		this.bsm = bsm;
		return this;
	}
	
	
	public QueryParametersData setMoleculetreatmentTime(String moleculetreatmentTime) {
		this.moleculetreatmentTime = moleculetreatmentTime;
		return this;
	}
	
	public QueryParametersData setFoldChange(String foldChange) {
		this.foldChange = foldChange;
		return this;
	}
	
	public QueryParametersData setFoldChangeMax(String foldChangeMax) {
		this.foldChangeMax = foldChangeMax;
		return this;
	}
	
	public QueryParametersData setFoldChangeMin(String foldChangeMin) {
		this.foldChangeMin = foldChangeMin;
		return this;
	}
	
	public QueryParametersData setSignificance(String significance) {
		this.significance = significance;
		return this;
	}
	
	public QueryParametersData setDirection(String direction) {
		this.direction = direction;
		return this;
	}
	
	public QueryParametersData setPs(String ps) {
		this.ps = ps;
		return this;
	}
	
	public QueryParametersData setTissueCategory(String cat) {
		this.tissueCategory = cat;
		return this;
	}
	
	public QueryParametersData setOrgan(String organ) {
		this.organ = organ;
		return this;
	}
	
	public QueryParametersData setSpecies(String species) {
		this.species = species;
		return this;
	}
	
	public QueryParametersData setSignalingPathway(String signalingPathway) {
		this.signalingPathway = signalingPathway;
		return this;
	}
	
	public QueryParametersData setPathwayCategory(String pathwayCategory) {
		this.pathwayCategory = pathwayCategory;
		return this;
	}

	public QueryParametersData setDoi(String doi) {
		this.doi = doi;
		return this;
	}

	public QueryParametersData setTissue(String tissue) {
		this.tissue = tissue;
		return this;
	}
	
	public String getFindCount() {
		return findCount;
	}

	public void setFindCount(String findCount) {
		this.findCount = findCount;
	}


	@Override
	public String toString() {
		return "QueryParametersData{" +
				"geneSearchType='" + geneSearchType + '\'' +
				", gene='" + gene + '\'' +
				", geneList='" + geneList + '\'' +
				", bsm='" + bsm + '\'' +
				", moleculetreatmentTime='" + moleculetreatmentTime + '\'' +
				", foldChange='" + foldChange + '\'' +
				", foldChangeMax='" + foldChangeMax + '\'' +
				", foldChangeMin='" + foldChangeMin + '\'' +
				", significance='" + significance + '\'' +
				", direction='" + direction + '\'' +
				", ps='" + ps + '\'' +
				", organ='" + organ + '\'' +
				", tissue='" + tissue + '\'' +
				", tissueCategory='" + tissueCategory + '\'' +
				", disease='" + disease + '\'' +
				", goTerm='" + goTerm + '\'' +
				", species='" + species + '\'' +
				", signalingPathway='" + signalingPathway + '\'' +
				", pathwayCategory='" + pathwayCategory + '\'' +
				", findCount='" + findCount + '\'' +
				", pathwayType='" + pathwayType + '\'' +
				'}';
	}
}

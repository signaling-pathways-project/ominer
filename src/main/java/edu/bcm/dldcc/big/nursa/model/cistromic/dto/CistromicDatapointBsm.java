package edu.bcm.dldcc.big.nursa.model.cistromic.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.bcm.dldcc.big.nursa.model.cistromic.ExperimentBsmsView;
import edu.bcm.dldcc.big.nursa.model.cistromic.PathwayNodesView;

/**
 * Cistromic datapoint BSM report DTO
 * @author mcowiti
 *
 */
@XmlRootElement(name = "bsmReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class CistromicDatapointBsm {

	@JsonIgnore
	private Long id;
	
	@JsonIgnore
	String gene;
	@JsonIgnore
	Integer score;
	@JsonProperty
	private List<ExperimentBsmsView> bsms= new ArrayList<ExperimentBsmsView>();
	
	@JsonProperty
	private List<PathwayNodesView> pathways=new ArrayList<PathwayNodesView>();

	public CistromicDatapointBsm(Long id,  List<ExperimentBsmsView> bsm,
			List<PathwayNodesView> pathway) {
		super();
		this.id = id;
		this.bsms = bsm;
		this.pathways = pathway;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public List<ExperimentBsmsView> getBsms() {
		return bsms;
	}

	public List<PathwayNodesView> getPathways() {
		return pathways;
	}
	
}

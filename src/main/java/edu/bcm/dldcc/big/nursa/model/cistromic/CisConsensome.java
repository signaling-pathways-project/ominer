package edu.bcm.dldcc.big.nursa.model.cistromic;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeDatapoint;
import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeId;

/**
 * Cistromic consensome datapoint entity
 * @author mcowiti
 *
 */

@Entity
@Table(name = "ConsensomeCis")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name = "consensome")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CisConsensome  implements ConsensomeDatapoint, Comparable,Serializable {
	
	private static final long serialVersionUID = -8149996110619968472L;
	@Id
	private Long id;

	@Override
	public int compareTo(Object o) {
		return this.id.compareTo(((CisConsensome)o).id);
	}

	@Embedded
	@JsonIgnore
	private ConsensomeId key= new ConsensomeId();

	@JsonProperty
	private String gene;

	@JsonProperty
	private String targetName;

	@JsonProperty
	@Column(name="AVRMACS2SCORE")
	private Double averageScore;
	@JsonProperty
	private Double qValue;
	@JsonProperty
	private Double percentile;

	@JsonIgnore
	private Date versionDate;

	public CisConsensome() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getDoi(){
		return this.key.getDoi();
	}

	public ConsensomeId getKey() {
		return key;
	}
	public void setKey(ConsensomeId key) {
		this.key = key;
	}
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	
	public Double getAverageScore() {
		return averageScore;
	}
	public void setAverageScore(Double averageScore) {
		this.averageScore = averageScore;
	}
	public Double getqValue() {
		return qValue;
	}
	public void setqValue(Double qValue) {
		this.qValue = qValue;
	}
	public Double getPercentile() {
		return percentile;
	}
	public void setPercentile(Double percentile) {
		this.percentile = percentile;
	}
	public Date getVersionDate() {
		return versionDate;
	}
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public String getTargetName() {
		return targetName;
	}

}

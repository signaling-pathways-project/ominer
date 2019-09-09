package edu.bcm.dldcc.big.nursa.model.cistromic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.bcm.dldcc.big.nursa.model.omics.Experiment;

@Entity
@XmlRootElement(name="bindingScore")
@Table(name="AntigenBindingdata")
@XmlAccessorType(XmlAccessType.FIELD)
public class BindingScore implements Serializable  {
	
	private static final long serialVersionUID = 4192696117517571508L;

	@JsonIgnore
	@Id 
	private Long id;
	
	@JsonProperty
	 @Basic
	 private String gene;
	 
	@JsonProperty
	@Basic
	private Integer score;
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER)
	private Experiment experiment;

	public BindingScore() {
	}

	public BindingScore(Long id, String gene, Integer score) {
		super();
		this.id = id;
		this.gene = gene;
		this.score = score;
	}

	@JsonIgnore
	 @Temporal(TemporalType.TIMESTAMP)
     private Date createdate;

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

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((experiment == null) ? 0 : experiment.hashCode());
		result = prime * result + ((gene == null) ? 0 : gene.hashCode());
		result = prime * result + ((score == null) ? 0 : score.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BindingScore other = (BindingScore) obj;
		if (experiment == null) {
			if (other.experiment != null)
				return false;
		} else if (!experiment.equals(other.experiment))
			return false;
		if (gene == null) {
			if (other.gene != null)
				return false;
		} else if (!gene.equals(other.gene))
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		return true;
	}
	
}

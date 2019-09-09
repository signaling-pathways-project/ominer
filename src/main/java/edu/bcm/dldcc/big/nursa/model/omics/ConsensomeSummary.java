package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Summary of Consensome calculation
 * @author mcowiti
 *
 */
@Entity
@Table(name = "ConsensomeSummary")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", length=50)
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement
public abstract class  ConsensomeSummary implements Serializable {

	private static final long serialVersionUID = 3554190712041499439L;

	@Id
	private Long id;
	
	@Embedded
	@JsonProperty
	private ConsensomeId key= new ConsensomeId();
	
	//this is the descriminator
	 @JsonProperty
	@Column(insertable=false, updatable=false)
	private String type;
	
	 @JsonProperty
	 @Column(name="NUMBEREXPERIMENTS")
	private Long numberOfExperiments;
	 
	 @JsonProperty
	 @Column(name="NUMBERDATASETS")
	private Long numberOfDatasets;
	
	 @JsonProperty
	private Long numberDatapoints;
	
	 @JsonProperty
	 @Transient
	private String version; //YYYYMMDD format, ie ISO 8601 without time and zone 
	
	 //@JsonPropertyprivate String doi;
	 
	 @JsonIgnore
	 private String title;
	 
	 @JsonProperty("cVersion")
	 private Integer cversion;
	 
	 @JsonIgnore
	 private Date versionDate;
	 
	 final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	 

	public ConsensomeSummary() {
		super();
	}
	
	public ConsensomeId getKey() {
		return key;
	}

	public void setKey(ConsensomeId key) {
		this.key = key;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getNumberOfExperiments() {
		return numberOfExperiments;
	}
	public void setNumberOfExperiments(Long numberOfExperiments) {
		this.numberOfExperiments = numberOfExperiments;
	}
	public Long getNumberOfDatasets() {
		return numberOfDatasets;
	}
	public void setNumberOfDatasets(Long numberOfDatasets) {
		this.numberOfDatasets = numberOfDatasets;
	}
	public Long getNumberDatapoints() {
		return numberDatapoints;
	}
	public void setNumberDatapoints(Long numberDatapoints) {
		this.numberDatapoints = numberDatapoints;
	}
	
	public String getVersion() {
		if(this.versionDate != null){
		 LocalDate date = this.versionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		 version=date.format(formatter);
		}
		return version;
	}
	public void setVersion(String date) {
		this.version = date;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getCversion() {
		return cversion;
	}

	public void setCversion(Integer cversion) {
		this.cversion = cversion;
	}

	public String getType() {
		return type;
	}


}

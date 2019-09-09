package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeId;

/**
 * Transcriptomime Consensome entity
 * @author mcowiti
 *
 */

@Entity
@Table(name = "Consensome")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Consensome  implements ConsensomeDatapoint, Serializable,Comparable {
	
	private static final long serialVersionUID = 4074322157085738121L;
	 final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		
	@Id
	private Long id;
	
	@Embedded
	@JsonIgnore
	private ConsensomeId key= new ConsensomeId();

	@JsonProperty
    private String gene;

    @JsonProperty
	private String targetName;

	@JsonProperty
	private Double percentile;
	 
	 @JsonProperty
	 private Double cPValue;
	  

	 @JsonProperty("discoveryRate")
	 private Double discrate;

	@JsonProperty
	private Double gmFc;

	@JsonProperty
	@Transient
	private String version;
	
	 @JsonIgnore
	 private Date versionDate;

	public Consensome() {
	}

	public Consensome(Long id, String doi,String pathway, String physiologicalSystem, String organ, String species,
					  String gene,String targetName,
					  Double percentile,
					  Double cPValue,
					  Double discrate,
					  Double gmFC,
					  Date versionDate) {
		super();
		setId(id);
		this.getKey().setDoi(doi);
		this.getKey().setFamily( pathway);
		this.getKey().setPhysiologicalSystem ( physiologicalSystem);
		this.getKey().setOrgan (organ);
		this.getKey().setSpecies(species);
		this.gene = gene;
		this.targetName=targetName;
		this.percentile = percentile;
		this.cPValue = cPValue;
		this.discrate = discrate;
		this.gmFc=gmFC;
		this.versionDate = versionDate;

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

	public Double getPercentile() {
		return percentile;
	}
	public void setPercentile(double percentile) {
		this.percentile = percentile;
	}
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	
	public Double getDiscrate() {
		return discrate;
	}
	public void setDiscrate(double discdate) {
		this.discrate = discdate;
	}
	public String getVersion() {
		if(this.versionDate!=null){
			 LocalDate date = this.versionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			 version=date.format(formatter);
		}
		return version;
	}
	public void setVersion(String date) {
		this.version = date;
	}
	public Double getCPValue() {
		return cPValue;
	}
	public void setCPValue(double cPValue) {
		this.cPValue = cPValue;
	}

	public Double getGmFc() {
		return gmFc;
	}
	public void setGmFc(double gmFc) {
		this.gmFc = gmFc;
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

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public ConsensomeId getKey() {
		return key;
	}


	public void setKey(ConsensomeId key) {
		this.key = key;
	}

	@Override
	public int compareTo(Object o) {
		return this.id.compareTo(((Consensome)o).id);
	}
}

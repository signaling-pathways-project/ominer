package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;
import java.util.*;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.Species;

/**
 * An experiment is derived  from  a dataset.
 * In real life, and experiment is performed first, and dataset(s) is produced as a result, 
 * but in the context of SigPathway
 * Transcriptomine/Cistromics,  an experiment is derived from a dataset that was downloaded from GEO or ChipAtlas. 
 * @author mcowiti
 *
 */
@Entity
@Table(name="TMEXPERIMENT")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Experiment implements Serializable {
	
	private static final long serialVersionUID = 4397885722636250389L;

	@Id 
	private Long id;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "TMEXPERIMENT_BSM",
			joinColumns = @JoinColumn(name = "experiment_id"))
	@Column(name="bsm")
	@JsonProperty
	private Set<String> bsms=new HashSet<String>();

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "TMEXPERIMENT_IPAGS",
			joinColumns = @JoinColumn(name = "experiment_id"))
	@Column(name="ipags")
	@JsonProperty
	private Set<String> ipags=new HashSet<String>();

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "TMEXPERIMENT_OAGS",
			joinColumns = @JoinColumn(name = "experiment_id"))
	@Column(name="oags")
	@JsonProperty
	private Set<String> oags=new HashSet<String>();

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "TMEXPERIMENT_SIGPATHNODE",
    joinColumns = @JoinColumn(name = "experiment_id"),
    inverseJoinColumns = @JoinColumn(name = "signalPathNode_id"))
	private Set<SignalingPathway> sigPath= new HashSet<SignalingPathway>();
	
	
	@Basic
	@XmlTransient
	private String internalExperimentId;
	
	@Basic
	private Integer experimentNumber;
	
	
	@Basic
//	@Audited
	private String name;
	
	
	@Column(length = 2000)
//	@Audited
	private String description;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private Tissue tissueSource; 
	
	@Basic
	@XmlTransient
	private String comments;
	
	
	@Basic
	private String platformName;
	@XmlTransient
	private Boolean active;
	private String lof; //NB: direction search affected by lof, see logic
	
	@ManyToOne
	@ForeignKey(name = "none")
	private Species species;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private NURSADataset dataset;
	

	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	
	@Transient
	private Integer numberOfDatapointsInQuery;
	

	
	@Transient
	private String doi; //doi.experimentNo
	
	@Transient
	private String queryId;

    @Column(name = "LONG_NAME")
    private String longName;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getExperimentNumber() {
		return experimentNumber;
	}
	public void setExperimentNumber(Integer experimentNumber) {
		this.experimentNumber = experimentNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Tissue getTissueSource() {
		return tissueSource;
	}
	public void setTissueSource(Tissue tissueSource) {
		this.tissueSource = tissueSource;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	public String getPlatformName() {
		return platformName;
	}
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public String getLof() {
		return lof;
	}
	public void setLof(String lof) {
		this.lof = lof;
	}
	public Species getSpecies() {
		return species;
	}
	public void setSpecies(Species species) {
		this.species = species;
	}
	
	public NURSADataset getDataset() {
		return dataset;
	}
	public void setDataset(NURSADataset dataset) {
		this.dataset = dataset;
	}
	public String getDoi() {
        if(this.dataset!=null && this.dataset.getDoi()!=null)
        {
            return new StringBuilder(this.dataset.getDoi().getDoi()).
                append(".").
                append(this.experimentNumber).toString();
        }
	    return this.doi;
	}
	 
	
	
	public Integer getNumberOfDatapointsInQuery() {
		return numberOfDatapointsInQuery;
	}

	public void setNumberOfDatapointsInQuery(Integer numberOfDatapointsInQuery) {
		this.numberOfDatapointsInQuery = numberOfDatapointsInQuery;
	}

	
	
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public String getInternalExperimentId() {
		return internalExperimentId;
	}
	public void setInternalExperimentId(String internalExperimentId) {
		this.internalExperimentId = internalExperimentId;
	}
	public String getQueryId() {
		return queryId;
	}
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }


	
	public Set<SignalingPathway> getSigPath() {
		return sigPath;
	}
	public void setSigPath(Set<SignalingPathway> sigPath) {
		this.sigPath = sigPath;
	}

	public Set<String> getBsms() {
		return bsms;
	}

	public void setBsms(Set<String> bsms) {
		this.bsms = bsms;
	}

	public Set<String> getIpags() {
		return ipags;
	}

	public void setIpags(Set<String> ipags) {
		this.ipags = ipags;
	}

	public Set<String> getOags() {
		return oags;
	}

	public void setOags(Set<String> oags) {
		this.oags = oags;
	}

	@PostUpdate
	public void updated(){
		this.updateDate= new Date();
	}


	@Override
	public int hashCode() {
		return java.util.Objects.hash(getInternalExperimentId(),getExperimentNumber(),getName());
	}

	@Override
	public boolean equals(Object obj){
		if (obj == this) {
			return true;
		}
		if (obj instanceof Experiment) {
			Experiment other = (Experiment) obj;
			return Objects.equals(internalExperimentId, other.internalExperimentId)
					&& Objects.equals(experimentNumber, other.experimentNumber)
					&& Objects.equals(name, other.name) ;
		}
		return false;
	}
	
}

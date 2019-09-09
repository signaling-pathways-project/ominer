package edu.bcm.dldcc.big.nursa.model.omics;

import edu.bcm.dldcc.big.nursa.model.Molecule;
import edu.bcm.dldcc.big.nursa.model.common.Article;
import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.DataResource;
import edu.bcm.dldcc.big.nursa.model.common.Reference;
import edu.bcm.dldcc.big.nursa.model.common.Species;
import edu.bcm.dldcc.big.nursa.services.BaseArticleBean;
import edu.bcm.dldcc.big.nursa.util.comparator.DataResourceAlphabetical;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dataset")
public class NURSADataset  implements Serializable {

	private static final long serialVersionUID = 4347561349038197693L;
	
	@Id
	@GeneratedValue(generator = "nursadatasetSequencer")
	@SequenceGenerator(name = "nursadatasetSequencer", sequenceName = "NURSADATASET_SEQ")
	private Long id;
	
	@Basic
	private String type;
	
	@Basic
	private String name;

    @Basic
    @Column(name = "RELEASE_DATE")
    private Date releaseDate;
	
    //TODO 10/26/2017 ManyToMany, with a join table
	@ManyToOne
	private Species species;

	@Column(length = 3500)
	private String description;
	
	@ManyToOne(cascade=CascadeType.ALL)
	private DOI doi;
	

	@Basic 
	private String repo;
	
	@Transient
	private String dataSource;


	@ManyToOne(cascade=CascadeType.ALL)
	@ForeignKey(name = "none")
	private Reference reference;
	
	@Transient @JsonProperty
	private List<Reference> references=new ArrayList<Reference>();

	@Transient
	private List<BsmView> referencesByBsm= new ArrayList<BsmView>();

	@JsonIgnore
	private String fileName;

	@JsonIgnore
	private String fileID;
	
	
	@Basic
	@JsonIgnore
	private Boolean active;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="dataset")
	@JsonIgnore
	@OrderBy("experimentNumber")
	private List<Experiment> experiments=new ArrayList<Experiment>();

	@ManyToOne
	@JsonIgnore
	private DatasetType datasetType;
	
	@Basic
	@JsonIgnore
	private String nursaDsId; // ds123A
	
	@Transient //these two  computed in TM v1. , might not be needed
	@JsonIgnore
	private String dataSourceData;

	@Transient
	@JsonIgnore
	private String dataSourceSource;

    //
    // Two fields for proper dataset citation, since original dataset can be published earlier than NURSA, and list of
    // authors can be wide that list of people that were listed as authors in publications
    //
	private String contributors;

    private Date published;

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	/**
	 * See rules FR26.1.3
	 * @return the dataSource
	 */
	public String getDataSource() {
		
		if(repo == null){
			dataSource="Data were retrieved from the original publication";
		}else if(repo.startsWith("GSE")){
			dataSource= "Data were retrieved from GEO dataset "+repo;
		}else if(repo.startsWith("E")){
			dataSource= "Data were retrieved from ArrayExpress"+repo;
		}else if(repo.startsWith("NURSA") || repo.contains("10.1621")){
			return "Data were retrieved from SPP.";
		}else if(repo.startsWith("SRP")){
			dataSource= "Data were retrieved from ChipAtlas "+repo;
		}
		return dataSource;
	}

	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

	@Transient
	public boolean hasUrl(){
		if(repo == null){
			return false;
		}else if(repo.startsWith("GSE")){
			return true;
		}else if(repo.startsWith("E")){
			return true;
		}else if(repo.startsWith("NURSA") || repo.contains("10.1621")){
			return true;
		}else if(repo.startsWith("SRP")){
			return true;
		}
		return false;
	}
	
	public List<Experiment> getExperiments() {
		return experiments;
	}

	public List<Experiment> getActiveExperiments() {
		List<Experiment> activeExperiments = new ArrayList<Experiment>();
		for (Experiment ex : experiments) {
			if (ex.getActive()) {
				activeExperiments.add(ex);
			}
		}
		return activeExperiments;
	}

	public void setExperiments(List<Experiment> experiments) {
		this.experiments = experiments;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


    public Date getReleaseDate() {
        return releaseDate;
    }


    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}


	public DOI getDoi() {
		return doi;
	}

	public void setDoi(DOI doi) {
		this.doi = doi;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public String getDataSourceData() {
		return dataSourceData;
	}
	public void setDataSourceData(String dataSourceData) {
		this.dataSourceData = dataSourceData;
	}
	public String getDataSourceSource() {
		return dataSourceSource;
	}
	public void setDataSourceSource(String dataSourceSource) {
		this.dataSourceSource = dataSourceSource;
	}

	public String getNursaDsId() {
		return nursaDsId;
	}
	public void setNursaDsId(String nursaDsId) {
		this.nursaDsId = nursaDsId;
	}

	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public DatasetType getDatasetType() {
		return datasetType;
	}
	public void setDatasetType(DatasetType datasetType) {
		this.datasetType = datasetType;
	}

	
	public String getRepo() {
		return repo;
	}
	public void setRepo(String repo) {
		this.repo = repo;
	}

    public String getContributors() {
        return contributors;
    }

    public void setContributors(String contributors) {
        this.contributors = contributors;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

	public List<BsmView> getReferencesByBsm() {
		return referencesByBsm;
	}

	public void setReferencesByBsm(List<BsmView> referencesByBsm) {
		this.referencesByBsm = referencesByBsm;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NURSADataset that = (NURSADataset) o;
		return Objects.equals(name, that.name) &&
				Objects.equals(doi, that.doi);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, doi);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("NURSADataset{");
		sb.append("id=").append(id);
		sb.append(", type='").append(type).append('\'');
		sb.append(", name='").append(name).append('\'');
		sb.append(", releaseDate=").append(releaseDate);
		sb.append(", species=").append(species);
		sb.append(", description='").append(description).append('\'');
		sb.append(", doi=").append(doi);
		sb.append(", repo='").append(repo).append('\'');
		sb.append(", dataSource='").append(dataSource).append('\'');
		sb.append(", fileName='").append(fileName).append('\'');
		sb.append(", active=").append(active);
		sb.append(", experiments=").append(experiments);
		sb.append(", datasetType=").append(datasetType);
		sb.append(", nursaDsId='").append(nursaDsId).append('\'');
		sb.append(", dataSourceData='").append(dataSourceData).append('\'');
		sb.append(", dataSourceSource='").append(dataSourceSource).append('\'');
		sb.append(", contributors='").append(contributors).append('\'');
		sb.append(", published=").append(published);
		sb.append('}');
		return sb.toString();
	}
}

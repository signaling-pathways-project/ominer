package edu.bcm.dldcc.big.nursa.model.cistromic;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@SqlResultSetMapping(
		name="DatasetViewMiniDTOResult",
		classes={
				@ConstructorResult(
						targetClass=DatasetViewMiniDTO.class,
						columns={
								@ColumnResult(name="id",type = Long.class),
								@ColumnResult(name="did",type = Long.class),
								@ColumnResult(name="type"),
								@ColumnResult(name="name"),
								@ColumnResult(name="doi"),
								@ColumnResult(name="description"),
								@ColumnResult(name="releaseDate",type= java.sql.Date.class),
								@ColumnResult(name="expName"),
								@ColumnResult(name="expDesc"),
								@ColumnResult(name="expId"),
								@ColumnResult(name="species"),
								@ColumnResult(name="pubmed")
						}
				)
		}
)
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "DatasetViewMiniDTOByLdate",
                query = "SELECT d.PKEY_ID id,d.did did,d.dtype type,d.dname name, d.doi, d.ddesc description, d.release_date releaseDate," +
                        "d.ename expName,d.edesc expDesc,d.internalexperimentid expId,d.species species,null as pubmed " +
                        "FROM DATASET_VIEW d WHERE d.RELEASE_DATE > TO_DATE(:ldate,'YYYYMMDD')",
                resultSetMapping = "DatasetViewMiniDTOResult"
        ),
        @NamedNativeQuery(
                name = "DatasetViewMiniDTOByLdateInclPubmed",
                query = "SELECT d.PKEY_ID id,d.did did,d.dtype type,d.dname name, d.doi, d.ddesc description, d.release_date releaseDate," +
                        "d.ename expName,d.edesc expDesc,d.internalexperimentid expId,d.species species, dp.pubmed pubmed " +
                        " FROM DATASET_VIEW d, datasetpubmed_view dp WHERE d.did=dp.did and d.RELEASE_DATE > TO_DATE(:ldate,'YYYYMMDD')",
                resultSetMapping = "DatasetViewMiniDTOResult"
        ),
        @NamedNativeQuery(
                name = "DatasetViewMiniDTOByDOI",
                query = "SELECT d.PKEY_ID id,d.did did,d.dtype type,d.dname name, d.doi, d.ddesc description, d.release_date releaseDate," +
                        "d.ename expName,d.edesc expDesc,d.internalexperimentid expId,d.species species, null as pubmed " +
                        " FROM DATASET_VIEW d WHERE d.DOI =:doi",
                resultSetMapping = "DatasetViewMiniDTOResult"
        ),
        @NamedNativeQuery(
                name = "DatasetViewMiniDTOByDOIInclPubmed",
                query = "SELECT d.PKEY_ID id,d.did did,d.dtype type,d.dname name, d.doi, d.ddesc description, d.release_date releaseDate," +
                        "d.ename expName,d.edesc expDesc,d.internalexperimentid expId,d.species species, dp.pubmed pubmed " +
                        " FROM DATASET_VIEW d, datasetpubmed_view dp WHERE d.did=dp.did and d.DOI=:doi",
                resultSetMapping = "DatasetViewMiniDTOResult"
        )
})

@Table( name = "DATASET_VIEW")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name = "dataset")
@JsonInclude(Include.NON_NULL)
public class DatasetViewDTO implements Serializable {
	
	private static final long serialVersionUID = 4267018621196414468L;

	@JsonIgnore
	@Id
	@Column(name="PKEY_ID")
	Long pkey;
	
	@JsonProperty(value="id")
	Long did;
	
	@JsonProperty(value="type")
	String dtype;
	@JsonProperty(value="name")
	String dname;
	@JsonProperty
	String repo;
	@JsonProperty
	String doi;
	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(name="RELEASE_DATE")
    Date releaseDate;
	@JsonProperty
    String contributors;
	@JsonIgnore
	@Column(name="DACTIVE")
    boolean dactive;
     @JsonIgnore
	Long speciesid;
	 @JsonIgnore
    String species;
	@Transient
	@JsonProperty
	private String speciesIds;

	@JsonIgnore //srx id
	 String internalexperimentid;
     @JsonIgnore
    Long expid;
	 @JsonIgnore
    boolean eactive;
    @JsonIgnore
    String ename;
    @JsonIgnore
    String edesc;
    @JsonIgnore
    Integer experimentnumber;
     @JsonIgnore
	String ddesc;
     
 	@Transient
    @JsonProperty
     private String expSize;
	
	@Transient
    @JsonProperty
    private String pathways;
    
	 @Transient
	 @JsonProperty
	 private String bioSamples;
	

	 
    @JsonIgnore
    Long tissueId;
    @JsonIgnore
    String tissue;
     @JsonIgnore
    Long organid;
    @JsonIgnore
    String organ;
    
    @JsonIgnore
    Long psid;
    @JsonIgnore
    String ps;
    @JsonIgnore
    Long familyid;
    @JsonIgnore
    String family;
    @JsonIgnore
    String type;
    @JsonIgnore
    Long  typeid;
    
    @JsonIgnore
    Long catid;
    @JsonIgnore
    String category;
    
    @JsonIgnore
    Long classid;
    @JsonIgnore
    @Column(name="CLASS")
    String cclass;
    
    @JsonIgnore
    String nodeSource;
    
    @Transient
    @JsonIgnore
    private Set<Long> pathIds= new HashSet<Long>();
    
    @Transient
    @JsonIgnore
    private Set<Long> bioIds= new HashSet<Long>();
    
    @Transient
    @JsonIgnore
    private Set<Long> specIds= new HashSet<Long>();
    
    
	public String getExpSize() {
		return expSize;
	}

	public void setExpSize(String expSize) {
		this.expSize = expSize;
	}

	public DatasetViewDTO() {
		super();
	}
	
	public String getPathways() {
		this.pathways=this.pathIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
		return pathways;
	}


	public String getBioSamples() {
		this.bioSamples=this.bioIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
		return bioSamples;
	}

	public String getSpeciesIds() {
		this.speciesIds=this.specIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
		return speciesIds;
	}


	public Long getDid() {
		return did;
	}
	public void setDid(Long did) {
		this.did = did;
	}
	public Long getPkey() {
		return pkey;
	}
	public void setPkey(Long pkey) {
		this.pkey = pkey;
	}
	public String getDtype() {
		return dtype;
	}
	public void setDtype(String dtype) {
		this.dtype = dtype;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	public String getDdesc() {
		return ddesc;
	}
	public void setDdesc(String ddesc) {
		this.ddesc = ddesc;
	}
	public String getRepo() {
		return repo;
	}
	public void setRepo(String repo) {
		this.repo = repo;
	}
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public boolean isDactive() {
		return dactive;
	}
	public void setDactive(boolean dACTIVE) {
		dactive = dACTIVE;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getContributors() {
		return contributors;
	}
	public void setContributors(String contributors) {
		this.contributors = contributors;
	}
	public Long getSpeciesid() {
		return speciesid;
	}
	public void setSpeciesid(Long speciesid) {
		this.speciesid = speciesid;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public Long getExpid() {
		return expid;
	}
	public void setExpid(Long expid) {
		this.expid = expid;
	}
	public boolean isEactive() {
		return eactive;
	}
	public void setEactive(boolean eactive) {
		this.eactive = eactive;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getEdesc() {
		return edesc;
	}
	public void setEdesc(String edesc) {
		this.edesc = edesc;
	}
	public String getInternalexperimentid() {
		return internalexperimentid;
	}
	public void setInternalexperimentid(String internalexperimentid) {
		this.internalexperimentid = internalexperimentid;
	}
	public Integer getExperimentnumber() {
		return experimentnumber;
	}
	public void setExperimentnumber(Integer experimentnumber) {
		this.experimentnumber = experimentnumber;
	}
	public Long getTissueId() {
		return tissueId;
	}
	public void setTissueId(Long tissueId) {
		this.tissueId = tissueId;
	}
	public String getTissue() {
		return tissue;
	}
	public void setTissue(String tissue) {
		this.tissue = tissue;
	}
	public Long getOrganid() {
		return organid;
	}
	public void setOrganid(Long organid) {
		this.organid = organid;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public Long getPsid() {
		return psid;
	}
	public void setPsid(Long psid) {
		this.psid = psid;
	}
	public String getPs() {
		return ps;
	}
	public void setPs(String ps) {
		this.ps = ps;
	}
	public Long getTypeid() {
		return typeid;
	}
	public void setTypeid(Long typeid) {
		this.typeid = typeid;
	}
	public Long getCatid() {
		return catid;
	}
	public void setCatid(Long catid) {
		this.catid = catid;
	}
	public Long getClassid() {
		return classid;
	}
	public void setClassid(Long classid) {
		this.classid = classid;
	}
	public Long getFamilyid() {
		return familyid;
	}
	public void setFamilyid(Long familyid) {
		this.familyid = familyid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCclass() {
		return cclass;
	}
	public void setCclass(String cclass) {
		this.cclass = cclass;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}

	public String getNodeSource() {
		return nodeSource;
	}

	public void setNodeSource(String nodeSource) {
		this.nodeSource = nodeSource;
	}

	public Set<Long> getPathIds() {
		return pathIds;
	}



	public void setPathIds(Set<Long> pathIds) {
		this.pathIds = pathIds;
	}

	public Set<Long> getBioIds() {
		return bioIds;
	}

	public void setBioIds(Set<Long> bioIds) {
		this.bioIds = bioIds;
	}


	public Set<Long> getSpecIds() {
		return specIds;
	}

	public void setSpecIds(Set<Long> specIds) {
		this.specIds = specIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DatasetViewDTO that = (DatasetViewDTO) o;
		return Objects.equals(doi, that.doi);
	}

	@Override
	public int hashCode() {
		return Objects.hash(doi);
	}
}

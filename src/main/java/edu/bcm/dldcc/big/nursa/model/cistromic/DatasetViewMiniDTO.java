package edu.bcm.dldcc.big.nursa.model.cistromic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dataset")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatasetViewMiniDTO {

    @JsonProperty(value="id")
    Long id;

    @JsonProperty
    Long did;

    @JsonProperty
    String type;

    @JsonProperty
    String doi;

    @JsonProperty
    String pubmed;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date releaseDate;

    @JsonProperty
    String name;

    @JsonProperty
    String description;

    @JsonProperty
    String expId;

    @JsonProperty
    String expName;
    @JsonProperty
    String expDesc;

    String species;


    public DatasetViewMiniDTO(Long id, Long did, String type, String name,String doi,  String description,Date releaseDate,
                               String expName, String expDesc, String expId,String species) {
        this.id = id;
        this.did = did;
        this.type = type;
        this.doi = doi;
        this.releaseDate = releaseDate;
        this.name = name;
        this.description = description;
        this.expId = expId;
        this.expName = expName;
        this.expDesc = expDesc;
        this.species = species;
    }

    public DatasetViewMiniDTO(Long id, Long did, String type, String name,String doi,  String description,Date releaseDate,
                              String expName, String expDesc, String expId,String species,String pubmed) {
        this.id = id;
        this.did = did;
        this.type = type;
        this.doi = doi;
        this.releaseDate = releaseDate;
        this.name = name;
        this.description = description;
        this.expId = expId;
        this.expName = expName;
        this.expDesc = expDesc;
        this.species = species;
        this.pubmed=pubmed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDid() {
        return did;
    }

    public void setDid(Long did) {
        this.did = did;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
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

    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public String getExpName() {
        return expName;
    }

    public void setExpName(String expName) {
        this.expName = expName;
    }

    public String getExpDesc() {
        return expDesc;
    }

    public void setExpDesc(String expDesc) {
        this.expDesc = expDesc;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getPubmed() {
        return pubmed;
    }

    public void setPubmed(String pubmed) {
        this.pubmed = pubmed;
    }
}

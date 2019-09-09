package edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "datasetMetadata")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatasetMetadata {

    @JsonProperty
    Long id;

    @JsonProperty
    String type;

    @JsonProperty
    String name;

    @JsonProperty
    String repo;

    @JsonProperty
    String doi;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date releaseDate;

    @JsonProperty
    String description;

    @JsonProperty
    String contributors;

    @JsonProperty
    String pubmed;

    @JsonIgnore
    boolean active;

    @JsonProperty
    Set<ExperimentMetadata> experiments= new HashSet<ExperimentMetadata>();

    public DatasetMetadata(Long id, String type, String name, String repo, String doi, Date releaseDate, String contributors, boolean active) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.repo = repo;
        this.doi = doi;
        this.releaseDate = releaseDate;
        this.contributors = contributors;
        this.active = active;
    }

    public DatasetMetadata(Long id, String type,String doi, String name, String description,Date releaseDate,String pubmed) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.doi = doi;
        this.description=description;
        this.releaseDate = releaseDate;
        this.pubmed=pubmed;
    }

    public Set<ExperimentMetadata> getExperiments() {
        return experiments;
    }
}

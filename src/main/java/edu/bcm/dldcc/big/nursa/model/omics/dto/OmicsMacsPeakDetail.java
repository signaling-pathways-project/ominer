package edu.bcm.dldcc.big.nursa.model.omics.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "datapoint")
public class OmicsMacsPeakDetail {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String symbol;

    @JsonProperty
    private Integer score;

    @JsonProperty
    private String pValue="<1E-05";

    @JsonProperty
    private Long experimentId;
    @JsonProperty
    private String experimentName;
    @JsonProperty
    private String expDescription;
    @JsonProperty
    private Integer experimentNumber;
    @JsonProperty
    private String tissueName;
    @JsonProperty
    private String tissueCategory;
    @JsonProperty
    private String speciesCommonName;

    @JsonProperty
    private String datasetName;
    @JsonProperty
    private String datasetDescription;
    @JsonProperty
    private String datasetUrl;
    @JsonProperty
    private String datasetDoi;
    @JsonProperty
    private Long datasetId;
    @JsonProperty
    private String repo;

    public OmicsMacsPeakDetail(Long id, String symbol, Integer score, Long experimentId, String experimentName, String expDescription, Integer experimentNumber, String tissueName, String tissueCategory, String speciesCommonName, String datasetName, String datasetDescription, String datasetDoi, Long datasetId, String repo) {
        this.id = id;
        this.symbol = symbol;
        this.score = score;
        this.experimentId = experimentId;
        this.experimentName = experimentName;
        this.expDescription = expDescription;
        this.experimentNumber = experimentNumber;
        this.tissueName = tissueName;
        this.tissueCategory = tissueCategory;
        this.speciesCommonName = speciesCommonName;
        this.datasetName = datasetName;
        this.datasetDescription = datasetDescription;
        this.datasetDoi = datasetDoi;
        this.datasetId = datasetId;
        this.repo = repo;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getScore() {
        return score;
    }

    public String getpValue() {
        return pValue;
    }

    public Long getExperimentId() {
        return experimentId;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public String getExpDescription() {
        return expDescription;
    }

    public Integer getExperimentNumber() {
        return experimentNumber;
    }

    public String getTissueName() {
        return tissueName;
    }

    public String getTissueCategory() {
        return tissueCategory;
    }

    public String getSpeciesCommonName() {
        return speciesCommonName;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getDatasetDescription() {
        return datasetDescription;
    }

    public String getDatasetUrl() {
        return datasetUrl;
    }

    public String getDatasetDoi() {
        return datasetDoi;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public String getRepo() {
        return repo;
    }
}

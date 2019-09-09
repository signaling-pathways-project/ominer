package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.persistence.*;
import java.util.List;


/**
 * Created by alexey on 5/29/15.
 */
@Entity
@SqlResultSetMapping(name = "FoldChangeDTO", entities = @EntityResult(entityClass = FoldChangeDTO.class,
        fields = {@FieldResult(name="id", column="id"),
                  @FieldResult(name="experimentId", column = "experimentId"),
                  @FieldResult(name="experimentName", column = "experimentName"),
                  @FieldResult(name="expDescription", column = "expDescription"),
                  @FieldResult(name = "experimentNumber", column = "experimentNumber"),
                  @FieldResult(name="tissueName", column = "tissueName"),
                  @FieldResult(name = "tissueCategory", column = "tissueCategory"),
                  @FieldResult(name="speciesCommonName", column = "speciesCommonName"),
                @FieldResult(name="datasetName", column = "datasetName"),
                @FieldResult(name="datasetDescription", column = "datasetDescription"),
                @FieldResult(name="datasetUrl", column = "datasetUrl"),
                @FieldResult(name="datasetDoi", column = "datasetDoi"),
                @FieldResult(name="datasetId", column = "datasetId"),
                @FieldResult(name="repo", column = "repo"),
                @FieldResult(name="pubmedId", column = "pubmedId"),
                @FieldResult(name="pValue", column = "pValue"),
                @FieldResult(name="foldChange", column= "foldChange"),
                @FieldResult(name="orgType", column= "orgType"),
                @FieldResult(name="symbolSynonym", column= "symbolSynonym"),
                @FieldResult(name="symbolUrl", column= "symbolUrl"),
                @FieldResult(name ="symbol", column =  "symbol"),
                @FieldResult(name="prob", column="prob"),
                @FieldResult(name = "probType", column = "probType"),}))

// Yes, I know that this class should extend DatapointDTO, but hibernated does not want to do correct
// SqlResultSetMapping with class inheritance. If you have time to try it yourself you are more than welcome
public class FoldChangeDTO  implements Datapoint<FoldChangeDTO> {

    @Id
    private String id;
    private Integer experimentId;
    private String experimentName;
    private String expDescription;
    private String experimentNumber;
    private String tissueName;
    private String tissueCategory;
    private String speciesCommonName;

    private String datasetName;
    private String datasetDescription;
    private String datasetUrl;
    private String datasetDoi;
    private String datasetId;
    private String repo;

    private String pubmedId;
    private String pValue;
    private double foldChange;
    @Transient private double foldChangeRaw;
    
    private String orgType;

    private String symbolSynonym;
    private String symbolUrl;
    private String symbol;

    private String prob;
    private String probType;

    @Transient
    private List annotations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getExpDescription() {
        return expDescription;
    }

    public void setExpDescription(String expDescription) {
        this.expDescription = expDescription;
    }

    public String getTissueName() {
        return tissueName;
    }

    public void setTissueName(String tissueName) {
        this.tissueName = tissueName;
    }

    public String getTissueCategory() {
        return tissueCategory;
    }

    public void setTissueCategory(String tissueCategory) {
        this.tissueCategory = tissueCategory;
    }

    public String getSpeciesCommonName() {
        return speciesCommonName;
    }

    public void setSpeciesCommonName(String speciesCommonName) {
        this.speciesCommonName = speciesCommonName;
    }

    public List getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List annotations) {
        this.annotations = annotations;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDatasetDescription() {
        return datasetDescription;
    }

    public void setDatasetDescription(String datasetDescription) {
        this.datasetDescription = datasetDescription;
    }

    public String getDatasetDoi() {
        return datasetDoi;
    }

    public void setDatasetDoi(String datasetDoi) {
        this.datasetDoi = datasetDoi;
    }

    public String getDatasetUrl() {
        return datasetUrl;
    }

    public void setDatasetUrl(String datasetUrl) {
        this.datasetUrl = datasetUrl;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getpValue() {
        return pValue;
    }

    public void setpValue(String pValue) {
        this.pValue = pValue;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public double getFoldChangeRaw() {
		return foldChange;
	}

	public void setFoldChangeRaw(double foldChangeRaw) {
		this.foldChangeRaw = foldChangeRaw;
	}
    public Double getFoldChange() {
        if (foldChange<1&&foldChange !=0) {
            return -1/foldChange;
        }
        return foldChange;
    }

    public void setFoldChange(double foldChange) {
        this.foldChange = foldChange;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getSymbolSynonym() {
        return symbolSynonym;
    }

    public void setSymbolSynonym(String symbolSynonym) {
        this.symbolSynonym = symbolSynonym;
    }

    public String getSymbolUrl() {
        return symbolUrl;
    }

    public void setSymbolUrl(String symbolUrl) {
        this.symbolUrl = symbolUrl;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getProb() {
        return prob;
    }

    public void setProb(String prob) {
        this.prob = prob;
    }

    public String getProbType() {
        return probType;
    }

    public void setProbType(String probType) {
        this.probType = probType;
    }

    public String getExperimentNumber() {
        return experimentNumber;
    }

    public void setExperimentNumber(String experimentInternalId) {
        this.experimentNumber = experimentInternalId;
    }

	@Override
	public int compareTo(FoldChangeDTO o) {
		//return Double.compare(this.getFoldChange(), o.getFoldChange());
		return Double.compare(Math.abs(this.getFoldChange()), Math.abs(o.getFoldChange()));
	}
	
	
}
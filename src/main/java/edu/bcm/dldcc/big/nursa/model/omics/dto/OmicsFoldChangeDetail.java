package edu.bcm.dldcc.big.nursa.model.omics.dto;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "datapoint")
public class  OmicsFoldChangeDetail {

    private Long id;
    private Long experimentId;
    private String experimentName;
    private String expDescription;
    private Integer experimentNumber;
    private String tissueName;
    private String tissueCategory;
    private String speciesCommonName;

    private String datasetName;
    private String datasetDescription;
    private String datasetUrl;
    private String datasetDoi;
    private Long datasetId;
    private String repo;

    private String pubmedId;
    private Double pValue;
    private Double foldChange;
    private Double foldChangeRaw;

    private String symbolSynonym;
    private String symbolUrl;
    private String symbol;
    private Long egeneid;
    private String prob;
    private String probType;

    public OmicsFoldChangeDetail() {
    }

    public OmicsFoldChangeDetail(Long datapointid, Double pValue, Double foldChange,
                                 String prob, String probType,String symbol,Long egeneid,
                                 Long experimentId, String experimentName, String expDescription, Integer experimentNumber,
                                 String tissueName, String tissueCategory, String speciesCommonName,
                                 String datasetName, String datasetDescription,  String datasetDoi,
                                 Long datasetId, String repo) {
        this.id = datapointid;
        this.pValue = pValue;
        this.foldChange = foldChange;
        this.symbol = symbol;
        this.prob = prob;
        this.probType = probType;
        this.egeneid=egeneid;
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

    public double getFoldChange() {
        if (foldChange <1D && foldChange !=0D) {
            return -1/foldChange;
        }
        return foldChange;
    }

    public double getFoldChangeRaw() {
        return foldChange;
    }

    public int compareTo(OmicsFoldChangeDetail o) {
        return Double.compare(Math.abs(this.getFoldChange()), Math.abs(o.getFoldChange()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Long experimentId) {
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

    public Integer getExperimentNumber() {
        return experimentNumber;
    }

    public void setExperimentNumber(Integer experimentNumber) {
        this.experimentNumber = experimentNumber;
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

    public String getDatasetUrl() {
        return "/datasets/?doi="+datasetDoi;
    }

    public void setDatasetUrl(String datasetUrl) {
        this.datasetUrl = datasetUrl;
    }

    public String getDatasetDoi() {
        return datasetDoi;
    }

    public void setDatasetDoi(String datasetDoi) {
        this.datasetDoi = datasetDoi;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public double getpValue() {
        return pValue;
    }

    public void setFoldChange(double foldChange) {
        this.foldChange = foldChange;
    }


    public void setFoldChangeRaw(double foldChangeRaw) {
        this.foldChangeRaw = foldChangeRaw;
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

    public void setpValue(double pValue) {
        this.pValue = pValue;
    }

    public Long getEgeneid() {
        return egeneid;
    }

    public void setEgeneid(Long egeneid) {
        this.egeneid = egeneid;
    }
}

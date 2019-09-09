package edu.bcm.dldcc.big.nursa.model.omics.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bcm.dldcc.big.nursa.model.cistromic.DatasetNodesView_;
import edu.bcm.dldcc.big.nursa.model.cistromic.DatasetViewDTO_;
import edu.bcm.dldcc.big.nursa.model.omics.FoldChangeView_;
import edu.bcm.dldcc.big.nursa.services.rest.omics.QueryType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@XmlRootElement(name = "datapoint")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmicsDatapoint   implements Comparable ,Serializable {

    @JsonIgnore
    private QueryType datapointType;

    @JsonIgnore
    private boolean agsInferred=false;

    @JsonProperty
    protected Long id;

    @JsonProperty
    private Integer score;

    @JsonProperty
    private String prob;

    @JsonProperty
    private String probType;

    @JsonProperty
    private Double foldChange;

    @JsonProperty
    private Double foldChangeRaw;

    @JsonProperty
    private Double pvalue;

    @JsonProperty
    protected String symbol;

    @JsonProperty
    protected String molecules;
    @JsonProperty
    protected String bsms;
    @JsonProperty
    protected String omolecules;
    @JsonProperty
    protected String models;
    @JsonProperty
    protected String tissue;
    @JsonProperty
    protected String organ;
    @JsonProperty
    protected String speciesCommonName;
    @JsonProperty
    protected String experimentId;
    @JsonProperty
    protected String experimentName;
    @JsonProperty
    protected Integer experimentNumber;
    @JsonProperty
    protected String datasetDoi;

    @JsonProperty
    protected String category;
    @JsonProperty
    protected String cclass;
    @JsonProperty
    protected String family;
    @JsonIgnore
    protected Long familyid;

    @JsonProperty
    protected String pathwayNodeTree;//multiple separated by ## and ::
    @JsonProperty
    protected String bsmNodeTarget;//SRX pre-formated as AGS|BSM|OAGS
    @JsonProperty
    protected String psOrgan;//multiple, separated by ::
    @JsonProperty
    protected String bioSample;

    @JsonProperty
    protected String type;

    @JsonIgnore
    protected String tccfTree;
    @JsonIgnore
    protected String bsm;
    @JsonIgnore
    protected String ags;
    @JsonIgnore
    protected String oags;
    @JsonIgnore
    protected String modelNode;

    @JsonProperty
    protected String nodeSource;

    @JsonIgnore
    protected List<String> targetBsm= new ArrayList<String>();

    @JsonIgnore
    protected List<String> targetOags= new ArrayList<String>();


    public OmicsDatapoint() {
    }


    public OmicsDatapoint(Long id,
                          String symbol,String bsm, String ags, String oags,String model,String tissue, String organ,String speciesCommonName,
                          String expderimentId,
                          String experimentName, Integer experimentNumber, String datasetDoi,
                          String type, String category, String cclass, String family,
                          String nodeSource, String psOrgan) {
        super();
        this.id = id;
        this.symbol = symbol;
        this.bsm = bsm;
        this.ags = ags;
        this.oags = oags;
        this.modelNode=model;
        this.tissue = tissue;
        this.organ = organ;
        this.speciesCommonName = speciesCommonName;
        this.experimentId=expderimentId;
        this.experimentName = experimentName;
        this.experimentNumber = experimentNumber;
        this.datasetDoi = datasetDoi;
        this.type = type;
        this.category=category;
        this.cclass=cclass;
        this.family=family;
        this.nodeSource=nodeSource;
        this.psOrgan = psOrgan;
    }

    public OmicsDatapoint(Long id,
                          String symbol,String prob,String probType,
                          Double foldChange,Double pvalue,
                          String bsm, String ags, String oags,String model,String tissue, String organ,String speciesCommonName,
                          String experimentId,String experimentName, Integer experimentNumber,
                          String datasetDoi,
                          String type, String category, String cclass, String family, Long familyid,
                          String nodeSource, String psOrgan) {

        this(id, symbol,bsm, ags, oags,model,tissue, organ,
                speciesCommonName,experimentId,experimentName, experimentNumber,
                datasetDoi,type,category,cclass,family,
                nodeSource,psOrgan);
        this.pvalue = pvalue;
        this.prob=prob;
        this.probType=probType;
        this.foldChange=foldChange;
        this.familyid=familyid;
        this.datapointType=QueryType.Transcriptomic;
    }

    public OmicsDatapoint(Long id, String symbol, Integer score,
                              String bsm, String ags, String oags,String model,
                              String tissue, String organ,
                              String speciesCommonName,String experimentId,
                              String experimentName, Integer experimentNumber, String datasetDoi,
                              String type, String category, String cclass, String family,Long familyId,
                              String nodeSource,String psOrgan) {

        this(id, symbol,bsm, ags, oags,model,tissue, organ,
                speciesCommonName,experimentId,experimentName, experimentNumber,
                datasetDoi,type,category,cclass,family,
                nodeSource,psOrgan);
        this.score = score;
        this.familyid=familyId;
        this.datapointType=QueryType.Cistromic;
    }

    public String getBioSample() {
        this.bioSample= new StringBuilder(this.psOrgan.substring(1).replace("/", ","))
                .append(",")
                .append(this.tissue).toString();
        return bioSample;
    }

    public String getTccfTree() {
        //make two level
        StringBuilder sb=new StringBuilder(this.category)
                .append("##").append(this.cclass);
        if(this.family != null)
            sb.append("::").append(this.family);
        this.tccfTree=sb.toString();
        return tccfTree;
    }

    public String getMolecules() {

        if(this.ags == null)
            return null;//was ""
        List<String> list= Arrays.asList(this.ags.split(",")).stream()
                .filter(elem -> elem.length()>0)
                .map(e->e)
                .distinct()
                .collect(Collectors.toList());
        this.molecules=Arrays.toString(list.stream().toArray(size -> new String[size])).replace("[", "").replace("]", "");

        return this.molecules;
    }

    public String getModels() {

        if(this.modelNode == null)
            return null;
        List<String> list= Arrays.asList(this.modelNode.split(",")).stream()
                .filter(elem -> elem.length()>0)
                .map(e->e)
                .distinct()
                .collect(Collectors.toList());
        this.models=Arrays.toString(list.stream().toArray(size -> new String[size])).replace("[", "").replace("]", "");

        return this.models;
    }

    public String getBsms() {
        if(this.datapointType == QueryType.Cistromic) {
            if (this.targetBsm.size() > 0) {
                String[] result =
                        this.targetBsm.stream().flatMap(Stream::of).toArray(String[]::new);
                this.bsms = Arrays.toString(result).replace("[", "").replace("]", "");
            }
        }else{
            this.bsms=this.bsm;
        }
        return bsms;
    }

    public String getOmolecules() {
        if(this.targetOags.size() > 0){
            String[] result=
                    this.targetOags.stream().flatMap(Stream::of).toArray(String[]::new);
            this.omolecules=Arrays.toString(result).replace("[", "").replace("]", "");
        }
        return omolecules;
    }

    public String getBsmNodeTarget() {
        if(this.datapointType == QueryType.Transcriptomic)
            return null;

        //if SRX, we use
        if(this.molecules == null)
            return "";
        StringBuilder sb= new StringBuilder(this.molecules);
        if(this.bsms != null && this.bsms.trim().length()>0)
            sb.append(" | ").append(this.bsms);
        if(this.omolecules != null && this.omolecules.trim().length()>0)
            sb.append(" | ").append(this.omolecules);
        this.bsmNodeTarget=sb.toString();
        return this.bsmNodeTarget;
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

    public String getBsm() {
        return bsm;
    }

    public void setBsm(String bsm) {
        this.bsm = bsm;
    }

    public String getAgs() {
        return ags;
    }

    public void setAgs(String ags) {
        this.ags = ags;
    }

    public String getOags() {
        return oags;
    }

    public void setOags(String oags) {
        this.oags = oags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMolecules(String molecules) {
        this.molecules = molecules;
    }

    public String getTissue() {
        return tissue;
    }

    public void setTissue(String tissue) {
        this.tissue = tissue;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getSpeciesCommonName() {
        return speciesCommonName;
    }

    public void setSpeciesCommonName(String speciesCommonName) {
        this.speciesCommonName = speciesCommonName;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public Integer getExperimentNumber() {
        return experimentNumber;
    }

    public void setExperimentNumber(Integer experimentNumber) {
        this.experimentNumber = experimentNumber;
    }

    public String getDatasetDoi() {
        return datasetDoi;
    }

    public void setDatasetDoi(String datasetDoi) {
        this.datasetDoi = datasetDoi;
    }

    public String getPathwayNodeTree() {
        return pathwayNodeTree;
    }
    public void setPathwayNodeTree(String pathwayNodeTree) {
        this.pathwayNodeTree = pathwayNodeTree;
    }

    public String getPsOrgan() {
        return psOrgan;
    }

    public String getModelNode() {
        return modelNode;
    }

    public void setModelNode(String modelNode) {
        this.modelNode = modelNode;
    }

    public void setPsOrgan(String psOrgan) {
        this.psOrgan = psOrgan;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNodeSource() {
        return nodeSource;
    }

    public void setNodeSource(String nodeSource) {
        this.nodeSource = nodeSource;
    }

    public List<String> getTargetBsm() {
        return targetBsm;
    }
    public void setTargetBsm(List<String> targetBsm) {
        this.targetBsm = targetBsm;
    }

    public List<String> getTargetOags() {
        return targetOags;
    }

    public void setTargetOags(List<String> targetOags) {
        this.targetOags = targetOags;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }

    public String getProbType() {
        return probType;
    }

    public void setProbType(String probType) {
        this.probType = probType;
    }

    public void setFoldChange(Double foldChange) {
        this.foldChange = foldChange;
    }

    public Double getPvalue() {
        return pvalue;
    }

    public void setPvalue(Double pvalue) {
        this.pvalue = pvalue;
    }

    public String getProb() {
        return prob;
    }

    public void setProb(String prob) {
        this.prob = prob;
    }

    public boolean isAgsInferred() {
        return agsInferred;
    }

    public void setAgsInferred(boolean agsInferred) {
        this.agsInferred = agsInferred;
    }

    public Long getFamilyid() {
        return familyid;
    }

    public void setFamilyid(Long familyid) {
        this.familyid = familyid;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public Double getFoldChange() {
        if(foldChange != null)
            if (foldChange < 1D && foldChange != 0D) {
                return -1/foldChange;
            }
        return foldChange;
    }

    public Double getFoldChangeRaw() {
        return foldChange;
    }

    public void setFoldChangeRaw(Double foldChangeRaw) {
        this.foldChangeRaw = foldChangeRaw;
    }

    public void setDatapointType(QueryType datapointType) {
        this.datapointType = datapointType;
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(Math.abs(this.getFoldChange()), Math.abs(((OmicsDatapoint)o).getFoldChange()));
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OmicsDatapoint{");
        sb.append("datapointType=").append(datapointType);
        sb.append(", id=").append(id);
        sb.append(", score=").append(score);
        sb.append(", prob='").append(prob).append('\'');
        sb.append(", probType='").append(probType).append('\'');
        sb.append(", foldChange=").append(foldChange);
        sb.append(", foldChangeRaw=").append(foldChangeRaw);
        sb.append(", pvalue=").append(pvalue);
        sb.append(", symbol='").append(symbol).append('\'');
        sb.append(", molecules='").append(molecules).append('\'');
        sb.append(", bsms='").append(bsms).append('\'');
        sb.append(", omolecules='").append(omolecules).append('\'');
        sb.append(", models='").append(models).append('\'');
        sb.append(", tissue='").append(tissue).append('\'');
        sb.append(", organ='").append(organ).append('\'');
        sb.append(", speciesCommonName='").append(speciesCommonName).append('\'');
        sb.append(", experimentName='").append(experimentName).append('\'');
        sb.append(", experimentNumber=").append(experimentNumber);
        sb.append(", datasetDoi='").append(datasetDoi).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append(", cclass='").append(cclass).append('\'');
        sb.append(", family='").append(family).append('\'');
        sb.append(", familyid=").append(familyid);
        sb.append(", pathwayNodeTree='").append(pathwayNodeTree).append('\'');
        sb.append(", bsmNodeTarget='").append(bsmNodeTarget).append('\'');
        sb.append(", psOrgan='").append(psOrgan).append('\'');
        sb.append(", bioSample='").append(bioSample).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", tccfTree='").append(tccfTree).append('\'');
        sb.append(", bsm='").append(bsm).append('\'');
        sb.append(", ags='").append(ags).append('\'');
        sb.append(", oags='").append(oags).append('\'');
        sb.append(", modelNode='").append(modelNode).append('\'');
        sb.append(", nodeSource='").append(nodeSource).append('\'');
        sb.append('}');
        return sb.toString();
    }

}

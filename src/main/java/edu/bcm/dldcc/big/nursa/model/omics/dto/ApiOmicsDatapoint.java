package edu.bcm.dldcc.big.nursa.model.omics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;


@XmlRootElement(name = "datapoint")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id","symbol","experimentId","score", "foldChange","pvalue",
        "biosample","type","category","clazz","family","node" } )
public class ApiOmicsDatapoint implements Comparable {

    //omicstype,biosample,macs2,[foldchange],pvalue,type,cat,class,family,node

    @JsonProperty
    protected Long id;

    @JsonProperty
    protected String symbol;

    @Override
    public int compareTo(Object o) {
        return this.id.compareTo(((ApiOmicsDatapoint)o).id);
    }

    @JsonProperty
    private Integer score;

    @JsonProperty
    private String experimentId;

    @JsonProperty
    private Double foldChange;

    @JsonProperty
    private Double pvalue;

    @JsonProperty
    private String biosample;

    @JsonProperty(value="domain")
    private String type;

    @JsonProperty
    private String category;

    @JsonProperty(value="class")
    private String clazz;

    @JsonProperty
    private String family;

    @JsonProperty
    private String node;

    public ApiOmicsDatapoint() {
    }

    /**
     * Reactome SRX datapoint
     * @param id
     * @param symbol
     * @param experimentId
     * @param score
     */
    public ApiOmicsDatapoint(Long id, String symbol,String experimentId, Integer score) {
        this.id = id;
        this.symbol = symbol;
        this.score = score;
        this.experimentId = experimentId;
    }

    /**
     * Reactome TX datapoint
     * @param id
     * @param symbol
     * @param experimentId
     * @param foldChange
     * @param pvalue
     */
    public ApiOmicsDatapoint(Long id, String symbol,String experimentId, Double foldChange, Double pvalue) {
        this.id = id;
        this.symbol = symbol;
        this.experimentId = experimentId;
        this.foldChange = foldChange;
        this.pvalue = pvalue;
    }

    public ApiOmicsDatapoint(Long id, String symbol,String experimentId,  Integer score, Double foldChange, Double pvalue) {
        this.id = id;
        this.symbol = symbol;
        this.score = score;
        this.experimentId = experimentId;
        this.foldChange = foldChange;
        this.pvalue = pvalue;
    }

    public ApiOmicsDatapoint(Long id, String symbol, String experimentId, Integer score, String biosample, String type, String category, String clazz, String family, String node) {
        this.id = id;
        this.symbol = symbol;
        this.experimentId=experimentId;
        this.score = score;
        this.biosample = biosample;
        this.type = type;
        this.category = category;
        this.clazz = clazz;
        this.family = family;
        this.node = node;
    }

    public ApiOmicsDatapoint(Long id, String symbol, String experimentId,Double foldChange, Double pvalue, String biosample, String type, String category, String clazz, String family, String node) {
        this.id = id;
        this.symbol = symbol;
        this.experimentId=experimentId;
        this.foldChange = foldChange;
        this.pvalue = pvalue;
        this.biosample = biosample;
        this.type = type;
        this.category = category;
        this.clazz = clazz;
        this.family = family;
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiOmicsDatapoint that = (ApiOmicsDatapoint) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

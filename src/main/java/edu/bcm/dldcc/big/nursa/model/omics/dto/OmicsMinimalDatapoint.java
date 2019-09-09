package edu.bcm.dldcc.big.nursa.model.omics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Comparator;
import java.util.Objects;

@XmlRootElement(name = "datapoint")
public class OmicsMinimalDatapoint implements Comparable<OmicsMinimalDatapoint> {

    @JsonProperty
    private Long id;
    @JsonProperty
    private String symbol;

    @JsonProperty
    private Double foldChange;

    @JsonProperty
    private Double pValue;
    @JsonProperty
    private Long geneOfficialId;

    @JsonProperty
    private Double foldChangeRaw;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String symbolUrl=null;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String symbolSynonym=null;


    public OmicsMinimalDatapoint(Long id, String symbol, Long geneOfficialId, Double foldChange, Double pValue ) {
        this.id = id;
        this.symbol = symbol;
        this.geneOfficialId = geneOfficialId;
        this.foldChange = foldChange;
        this.pValue = pValue;
    }

    public int compareTo(OmicsMinimalDatapoint o) {
        return OmicsMinimalDatapoint.Comparators.NORMAL.compare(this, o);
    }

    public static class Comparators {

        public static Comparator<OmicsMinimalDatapoint> NORMAL = new Comparator<OmicsMinimalDatapoint>() {
            @Override
            public int compare(OmicsMinimalDatapoint o1, OmicsMinimalDatapoint o2) {
                return Double.compare(o1.getFoldChange(), o2.getFoldChange());
            }
        };
        public static Comparator<OmicsMinimalDatapoint> ABS = new Comparator<OmicsMinimalDatapoint>() {
            @Override
            public int compare(OmicsMinimalDatapoint o1, OmicsMinimalDatapoint o2) {
                return Double.compare(Math.abs(o1.getFoldChange()), Math.abs(o2.getFoldChange()));
            }
        };
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolUrl() {
        return symbolUrl;
    }

    public void setSymbolUrl(String symbolUrl) {
        this.symbolUrl = symbolUrl;
    }

    public void setFoldChange(double foldChange) {
        this.foldChange = foldChange;
    }

    public String getSymbolSynonym() {
        return symbolSynonym;
    }

    public void setSymbolSynonym(String symbolSynonym) {
        this.symbolSynonym = symbolSynonym;
    }

    public Double getpValue() {
        return pValue;
    }

    public void setpValue(Double pValue) {
        this.pValue = pValue;
    }

    public Long getGeneOfficialId() {
        return geneOfficialId;
    }

    public void setGeneOfficialId(Long geneOfficialId) {
        this.geneOfficialId = geneOfficialId;
    }


    public void setFoldChangeRaw(Double foldChangeRaw) {
        this.foldChangeRaw = foldChangeRaw;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getFoldChange() {

        if (foldChange < 1D && foldChange !=0D) {
            return -1/foldChange;
        }
        return foldChange;
    }

    public Double getFoldChangeRaw() {

        return foldChange;//foldChangeRaw;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OmicsMinimalDatapoint that = (OmicsMinimalDatapoint) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

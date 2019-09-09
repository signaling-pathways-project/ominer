package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by alexey on 6/19/15.
 * @author mcowiti Note fc transform
 */

@Entity
@SqlResultSetMapping(name = "FoldChangeXML", entities = @EntityResult(entityClass = FoldChangeXML.class,
        fields = {@FieldResult(name="id", column="id"),
                @FieldResult(name="experimentName", column = "experimentName"),
                @FieldResult(name="speciesCommonName", column = "speciesCommonName"),
                @FieldResult(name="tissueName", column = "tissueName"),
                @FieldResult(name="prob", column="prob"),
                @FieldResult(name ="symbol", column =  "symbol"),
                @FieldResult(name="foldChange", column= "foldChange"),
                @FieldResult(name="pValue", column = "pValue"),
                @FieldResult(name="experimentId", column = "experimentId"),
                @FieldResult(name="pubmedId", column = "pubmedId")}))
public class FoldChangeXML   implements Datapoint<FoldChangeXML> {

    @Id
    private String id;
    private String experimentName;
    private Integer experimentId;
    private String speciesCommonName;
    private String tissueName;
    private String prob;
    private String symbol;
    @Transient private double foldChangeRaw;
    private Double foldChange;
    private Double pValue;

    private String pubmedId;

    @Transient
    private List annotations;


    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpeciesCommonName() {
        return speciesCommonName;
    }

    public void setSpeciesCommonName(String speciesCommonName) {
        this.speciesCommonName = speciesCommonName;
    }

    public String getTissueName() {
        return tissueName;
    }

    public void setTissueName(String tissueName) {
        this.tissueName = tissueName;
    }

    public String getProb() {
        return prob;
    }

    public void setProb(String prob) {
        this.prob = prob;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getpValue() {
        return pValue;
    }

    public void setpValue(Double pValue) {
        this.pValue = pValue;
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

    public void setFoldChange(Double foldChange) {
        this.foldChange = foldChange;
    }

    public List getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List annotations) {
        this.annotations = annotations;
    }

    public Integer getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }


    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

	@Override
	public int compareTo(FoldChangeXML o) {
		//FR needs abs return Double.compare(this.getFoldChange(), o.getFoldChange());
		return Double.compare(Math.abs(this.getFoldChange()), Math.abs(o.getFoldChange()));
	}
}

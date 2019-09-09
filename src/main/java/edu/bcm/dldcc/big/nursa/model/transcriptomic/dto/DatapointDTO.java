package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.persistence.Entity;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by alexey on 3/25/15.
 *
 * SqlResultSetMapping - is equels to the Class.getSimple name of easy mapping
 * @author mcowiti Note the foldChange Mapping case for: 0< foldchange < 1 This class shoudl not be entity
 */
@Entity
@XmlRootElement(name = "datapoint")
@XmlType(propOrder = { "id","symbol", "prob", "foldChange", "tissueName", "speciesCommonName", "experimentName", "symbolSynonym","row"} )
@SqlResultSetMapping(name="DatapointDTO",
classes = {
    @ConstructorResult(
            targetClass = DatapointDTO.class,
            columns = {
            		
            		@ColumnResult(name="id", type=String.class),
            		//@ColumnResult(name="row", type=Integer.class),
            		@ColumnResult(name ="symbol", type =  String.class),
            		@ColumnResult(name="prob", type =  String.class),
            		@ColumnResult(name = "probType", type =  String.class),
            		@ColumnResult(name="foldChange", type =  Double.class),
            		@ColumnResult(name="tissueName", type =  String.class),
            		@ColumnResult(name = "tissueCategory", type =  String.class),
            		@ColumnResult(name = "moleculePathway", type =  String.class),
            		@ColumnResult(name="speciesCommonName", type =  String.class),
            		@ColumnResult(name="experimentName", type =  String.class),
            		@ColumnResult(name = "experimentNumber", type =  String.class),
            		@ColumnResult(name = "symbolUrl", type =  String.class),
            		@ColumnResult(name = "symbolSynonym", type =  String.class),
            		@ColumnResult(name="datasetDoi", type =  String.class),
            		@ColumnResult(name="pValue", type =  BigDecimal.class),
            		@ColumnResult(name="regulatoryMoleculeSymbol", type =  String.class),
            		@ColumnResult(name="regulatoryMoleculeName", type =  String.class)
               
            })
})
public class DatapointDTO  implements Datapoint<DatapointDTO> {
	
	
	@Id
    private String id;
	private String row;
    private String symbol;
    private String prob;
    private String probType;
    private double foldChange;
    
    private String tissueName;
    private String tissueCategory;
    private String speciesCommonName;
    private String experimentName;
    private String symbolUrl;
    private String symbolSynonym;
    private String experimentNumber;
    private String datasetDoi;
    private BigDecimal pValue;
    private String regulatoryMoleculeSymbol;
    private String regulatoryMoleculeName;
    private String moleculePathway;

    @Transient
    private List<String> moleculePathwayArray;

    @Transient private double foldChangeRaw;

    @Transient
    private List<String> tissueCategoryArray;


    public DatapointDTO() {

    }

    public DatapointDTO(String id, String symbol, String prob, String probType, double foldChange, String tissueName,
			String tissueCategory, String moleculePathway, String speciesCommonName, String experimentName,
			String experimentNumber, String symbolUrl, String symbolSynonym, String datasetDoi, BigDecimal pValue,
			String regulatoryMoleculeSymbol, String regulatoryMoleculeName) {
		super();
		this.id = id;
		//this.row=row;
		this.symbol = symbol;
		this.prob = prob;
		this.probType = probType;
		this.foldChange = foldChange;
		this.tissueName = tissueName;
		this.tissueCategory = tissueCategory;
		this.moleculePathway = moleculePathway;
		this.speciesCommonName = speciesCommonName;
		this.experimentName = experimentName;
		this.experimentNumber = experimentNumber;
		this.symbolUrl = symbolUrl;
		this.symbolSynonym = symbolSynonym;
		this.datasetDoi = datasetDoi;
		this.pValue = pValue;
		this.regulatoryMoleculeSymbol = regulatoryMoleculeSymbol;
		this.regulatoryMoleculeName = regulatoryMoleculeName;
	}

    
    public DatapointDTO(String id, String symbol, String prob, String probType, 
    		double foldChange, String tissueName, String speciesCommonName, String experimentName, String symbolUrl) {
        this.id = id;
        this.symbol = symbol;
        this.prob = prob;
        this.probType = probType;
        this.foldChange = foldChange;
        this.tissueName = tissueName;
        this.speciesCommonName = speciesCommonName;
        this.experimentName = experimentName;
        this.symbolUrl = symbolUrl;
    }

   
    public String getRow() {
		return new StringBuilder(this.symbol).append("_").append(this.moleculePathway).toString();
	}

	public void setRow(String row) {
		this.row = row;
	}

	public String getProbType() {
        return probType;
    }

    public void setProbType(String probeType) {
        this.probType = probeType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTissueCategory() {
        return tissueCategory;
    }

    public void setTissueCategory(String tissueCategory) {
        this.tissueCategory = tissueCategory;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getSymbolUrl() {
        return symbolUrl;
    }

    public void setSymbolUrl(String symbolUrl) {
        this.symbolUrl = symbolUrl;
    }

    public String getSymbolSynonym() {
        return symbolSynonym;
    }

    public void setSymbolSynonym(String symbolSynonym) {
        this.symbolSynonym = symbolSynonym;
    }

    public String getExperimentNumber() {
        return experimentNumber;
    }

    public void setExperimentNumber(String experimentInternalId) {
        this.experimentNumber = experimentInternalId;
    }
    public String getDatasetDoi() {
        return datasetDoi;
    }

    public void setDatasetDoi(String datasetDoi) {
        this.datasetDoi = datasetDoi;
    }

    public BigDecimal getpValue() {
        return pValue;
    }

    public void setpValue(BigDecimal pValue) {
        this.pValue = pValue;
    }

    public String getRegulatoryMoleculeSymbol() {
        return regulatoryMoleculeSymbol;
    }

    public void setRegulatoryMoleculeSymbol(String regulatoryMoleculeSymbol) {
        this.regulatoryMoleculeSymbol = regulatoryMoleculeSymbol;
    }

    public String getRegulatoryMoleculeName() {
        return regulatoryMoleculeName;
    }

    public void setRegulatoryMoleculeName(String regulatoryMoleculeName) {
        this.regulatoryMoleculeName = regulatoryMoleculeName;
    }

    public String getMoleculePathway() {
        if (null != moleculePathway)
        {
        	if (moleculePathway.indexOf("##")<=0)
        		moleculePathway = moleculePathway.substring(2, moleculePathway.length()).replaceFirst("::", "##");
            return moleculePathway;
        }
        else
        {
            return "Others";
        }
    }

    public void setMoleculePathway(String moleculePathway) {
        this.moleculePathway = moleculePathway;
    }


    public List<String> getMoleculePathwayArray()
    {
        List<String> list = null;
        if (null != moleculePathway)
        {
            list = Arrays.asList(moleculePathway.split("\\|"));
        }
        else
        {
            list = new ArrayList<String>(1);
            list.add("Others");
        }
        return list;
    }

    public void setMoleculePathwayArray(List<String> moleculeCategories) {
        this.moleculePathwayArray = moleculeCategories;
    }


    public List<String> getTissueCategoryArray() {
        ArrayList<String> list = null;
        if ( null != tissueCategory)
        {
            list = new ArrayList<String>();
            list.add(tissueCategory);
        }
        return list;
    }

    public void setTissueCategoryArray(List<String> tissueCategoryArray) {
        this.tissueCategoryArray = tissueCategoryArray;
    }
    
    
    @Override
	  public int hashCode() {
	    return java.util.Objects.hash(this.getDatasetDoi(), getExperimentNumber()
	    		,getSymbol(),getSpeciesCommonName(), getTissueName(), getMoleculePathway());
	  }
	  @Override
	  public boolean equals(Object obj){
	    if (obj == this) {
	      return true;
	    } 
	    if (obj instanceof DatapointDTO) {
	    	DatapointDTO other = (DatapointDTO) obj; 
	      return Objects.equals(datasetDoi, other.datasetDoi) && 
	    		  Objects.equals(experimentNumber, other.experimentNumber) && 
	    		  Objects.equals(symbol, other.symbol) && 
	    		  Objects.equals(speciesCommonName, other.speciesCommonName) && 
	    		  Objects.equals(tissueName, other.tissueName) && 
	    		  Objects.equals(moleculePathway, other.moleculePathway);
	    } 
	    return false;
	  }
    

	@Override
	public int compareTo(DatapointDTO o) {
		return Double.compare(Math.abs(this.getFoldChange()), Math.abs(o.getFoldChange()));
	}
}

package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.Comparator;
import java.util.List;

/**
 * Created by alexey on 8/3/15.
 * @author mcowiti why make an entity of this?
 */

@Entity
@XmlRootElement(name = "datapoint")
@XmlType(propOrder = { "id","symbol", "symbolUrl" ,"foldChange" } )
@SqlResultSetMapping(name = "DatapointBasicDTO", entities = @EntityResult(entityClass = DatapointBasicDTO.class,
        fields = {
                @FieldResult(name="id", column="id"),
                @FieldResult(name ="symbol", column =  "symbol"),
                @FieldResult(name = "symbolUrl", column = "symbolUrl"),
                @FieldResult(name = "foldChange", column = "foldChange"),
                @FieldResult(name = "symbolSynonym", column = "symbolSynonym"),
                @FieldResult(name = "pValue", column = "pValue"),
                @FieldResult(name = "geneOfficialId", column = "geneOfficialId")}))

public class DatapointBasicDTO   implements  Datapoint<DatapointBasicDTO> {

    @Id
    private String id;
    private String symbol;
    private String symbolUrl;
    private double foldChange;
    private String symbolSynonym;
    private Double pValue;
    private String geneOfficialId;
    @Transient private double foldChangeRaw;
    

    @Transient
    private List<MetaboliteDTO> metabolites;

    public DatapointBasicDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGeneOfficialId() {
        return geneOfficialId;
    }

    public void setGeneOfficialId(String geneOfficialId) {
        this.geneOfficialId = geneOfficialId;
    }

    public List<MetaboliteDTO> getMetabolites() {
        return metabolites;
    }

    public void setMetabolites(List<MetaboliteDTO> metabolites) {
        this.metabolites = metabolites;
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(foldChange);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((geneOfficialId == null) ? 0 : geneOfficialId.hashCode());
		result = prime * result + ((pValue == null) ? 0 : pValue.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((symbolSynonym == null) ? 0 : symbolSynonym.hashCode());
		result = prime * result + ((symbolUrl == null) ? 0 : symbolUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DatapointBasicDTO other = (DatapointBasicDTO) obj;
		if (Double.doubleToLongBits(foldChange) != Double.doubleToLongBits(other.foldChange))
			return false;
		if (geneOfficialId == null) {
			if (other.geneOfficialId != null)
				return false;
		} else if (!geneOfficialId.equals(other.geneOfficialId))
			return false;
		if (pValue == null) {
			if (other.pValue != null)
				return false;
		} else if (!pValue.equals(other.pValue))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (symbolSynonym == null) {
			if (other.symbolSynonym != null)
				return false;
		} else if (!symbolSynonym.equals(other.symbolSynonym))
			return false;
		if (symbolUrl == null) {
			if (other.symbolUrl != null)
				return false;
		} else if (!symbolUrl.equals(other.symbolUrl))
			return false;
		return true;
	}

	@Override
	public int compareTo(DatapointBasicDTO o) {
		return Comparators.NORMAL.compare(this, o);
		//return Double.compare(this.getFoldChange(), o.getFoldChange());
	}

	public static class Comparators {

        public static Comparator<DatapointBasicDTO> NORMAL = new Comparator<DatapointBasicDTO>() {
            @Override
            public int compare(DatapointBasicDTO o1, DatapointBasicDTO o2) {
            	return Double.compare(o1.getFoldChange(), o2.getFoldChange());
            }
        };
        public static Comparator<DatapointBasicDTO> ABS = new Comparator<DatapointBasicDTO>() {
            @Override
            public int compare(DatapointBasicDTO o1, DatapointBasicDTO o2) {
            	return Double.compare(Math.abs(o1.getFoldChange()), Math.abs(o2.getFoldChange()));
            }
        };
	}
}

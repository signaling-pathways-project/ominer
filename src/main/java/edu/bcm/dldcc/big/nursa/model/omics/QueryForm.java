package edu.bcm.dldcc.big.nursa.model.omics;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.bcm.dldcc.big.nursa.model.cistromic.dto.QueryParametersData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Query Form. QueryForm  gathers the Query parameters being queried on.
 * @author mcowiti
 *
 */
@Entity
@XmlRootElement(name = "QueryForm")
@XmlAccessorType (XmlAccessType.PROPERTY)
@JsonInclude(Include.NON_EMPTY)
public class QueryForm implements Serializable {

	private static final long serialVersionUID = -1473005118922558561L;

	@Id @GeneratedValue(generator = "system-uuid")
	@Column(length = 36)
	private String id;
	
	private String queryName;

	@Basic
	private Long resultCount;
	
	@Transient
	private Map<QueryParamName,QueryParameter> queryParameters= new HashMap<QueryParamName,QueryParameter>();
	
	@OneToMany(mappedBy ="queryForm",fetch=FetchType.EAGER,
			cascade ={ CascadeType.ALL })
	private List<QueryParameter> parameters= new ArrayList<QueryParameter>();
		
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date queryTime;

	private Long queryDuration;
	
	@Transient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String experimentId; 
	
	@Transient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private double minFoldChange;
	@Transient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private double maxFoldChange;
	
	@Transient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private double minFoldChangeRaw;
	@Transient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private double maxFoldChangeRaw;

	@Transient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Double largestFoldChange;
	
	@Transient
	private QueryParametersData queryParameter;
	
	public QueryForm() {
		super();
	}
	
	
	 @XmlElement(required=false)
	public QueryParametersData getQueryParameter() {
		return queryParameter;
	}

	public void setQueryParameter(QueryParametersData queryParameter) {
		this.queryParameter = queryParameter;
	}

	@XmlElement
	 public double getMinFoldChange() {
		return minFoldChange;
	}

	public void setMinFoldChange(double minFoldChange) {
		this.minFoldChange = minFoldChange;
	}

	@XmlElement
	public double getMaxFoldChange() {
		return maxFoldChange;
	}

	public void setMaxFoldChange(double maxFoldChange) {
		this.maxFoldChange = maxFoldChange;
	}
	
	@XmlElement
	public double getMinFoldChangeRaw() {
		return minFoldChangeRaw;
	}

	public void setMinFoldChangeRaw(double minFoldChangeRaw) {
		this.minFoldChangeRaw = minFoldChangeRaw;
	}
	
	@XmlElement
	public double getMaxFoldChangeRaw() {
		return maxFoldChangeRaw;
	}

	public void setMaxFoldChangeRaw(double maxFoldChangeRaw) {
		this.maxFoldChangeRaw = maxFoldChangeRaw;
	}

	@JsonProperty("id")//was queryId
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	 @XmlElement(required=false)
	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}


	 @XmlElement(required=false)
	public Long getResultCount() {
		return resultCount;
	}

	public void setResultCount(Long resultCount) {
		this.resultCount = resultCount;
	}

	 @XmlElement(required=false)
	public Date getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(Date queryTime) {
		this.queryTime = queryTime;
	}

	 @XmlElement(required=false)
	public Long getQueryDuration() {
		return queryDuration;
	}

	public void setQueryDuration(Long queryDuration) {
		this.queryDuration = queryDuration;
	}

	 @XmlElement(required=false)
	public String getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}
	
	 @XmlElement(required=false)
	public Double getLargestFoldChange() {
		return largestFoldChange;
	}


	public void setLargestFoldChange(Double largestFoldChange) {
		this.largestFoldChange = largestFoldChange;
	}


	@XmlTransient
	 @JsonBackReference
	 public List<QueryParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<QueryParameter> parameters) {
		this.parameters = parameters;
	}

	
	 @JsonBackReference
	@XmlTransient
	public Map<QueryParamName, QueryParameter> getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(
			Map<QueryParamName, QueryParameter> queryParameters) {
		this.queryParameters = queryParameters;
	}
	
	

	@Override
	public String toString() {
		return "QueryForm [id=" + id + ", resultCount=" + resultCount + ", queryParameters="
				+ queryParametersToString() + ", queryTime=" + queryTime
				+ ", queryDuration=" + queryDuration + ", experimentId="
				+ experimentId + "]";
	}

	public String queryParametersToString() {

        StringBuilder stringBuilder = new StringBuilder("");
      for (Map.Entry<QueryParamName,QueryParameter> entity : queryParameters.entrySet())
        {
            stringBuilder.append(entity.getKey().name()+":");
            stringBuilder.append(Arrays.toString(entity.getValue().getParamValues().toArray()));
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((experimentId == null) ? 0 : experimentId.hashCode());
		result = prime * result
				+ ((queryDuration == null) ? 0 : queryDuration.hashCode());
		result = prime * result
				+ ((queryName == null) ? 0 : queryName.hashCode());
		result = prime * result
				+ ((queryTime == null) ? 0 : queryTime.hashCode());
		result = prime * result
				+ ((resultCount == null) ? 0 : resultCount.hashCode());
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
		QueryForm other = (QueryForm) obj;

		if (experimentId == null) {
			if (other.experimentId != null)
				return false;
		} else if (!experimentId.equals(other.experimentId))
			return false;
		if (queryDuration == null) {
			if (other.queryDuration != null)
				return false;
		} else if (!queryDuration.equals(other.queryDuration))
			return false;

		if (queryName == null) {
			if (other.queryName != null)
				return false;
		} else if (!queryName.equals(other.queryName))
			return false;
		if (queryTime == null) {
			if (other.queryTime != null)
				return false;
		} else if (!queryTime.equals(other.queryTime))
			return false;
		if (resultCount == null) {
			if (other.resultCount != null)
				return false;
		} else if (!resultCount.equals(other.resultCount))
			return false;
		return true;
	}
	
	private void setupParamQueryName(){
		String values=null;
		for (Map.Entry<QueryParamName,QueryParameter> entry : this.queryParameters.entrySet())
        {
			if(entry.getValue().getQueryParamName() == null)
				entry.getValue().setQueryParamName(entry.getKey());
			entry.getValue().setQueryForm(this);
			this.parameters.add(entry.getValue());
			if(entry.getValue().getParamValues().size()>0)
			values=StringUtils.join(entry.getValue().getParamValues(), ',');
			setupQueryParametersData(entry.getKey(),values);
        }
	}


	@PostLoad
	void setParametersFromMap(){
		for(QueryParameter parameter:this.parameters){
			this.queryParameters.put(parameter.getQueryParamName(), parameter);
		}
	}
	
	private void setupQueryParametersData(QueryParamName queryParamName,String value){
		if (this.queryParameter == null)
			this.queryParameter = new QueryParametersData();
		
		switch(queryParamName){
		case geneSearchType:
			this.queryParameter.setGeneSearchType(value);
			break;
		case gene:
			this.queryParameter.setGene(value);
			break;
		case geneList:
			this.queryParameter.setGeneList(value);
			break;
		case foldChange:
			this.queryParameter.setFoldChange(value);
			break;
		case foldChangeMin:
			this.queryParameter.setFoldChangeMin(value);
			break;
		case foldChangeMax:
			this.queryParameter.setFoldChangeMax(value);
			break;
		case direction:
			this.queryParameter.setDirection(value);
			break;
		case significance:
			this.queryParameter.setSignificance(value);
			break;
		case molecule:
			this.queryParameter.setBsm(value);
			break;
		case moleculetreatmentTime:
			this.queryParameter.setMoleculetreatmentTime(value);
			break;
		case tissue:
			this.queryParameter.setTissue(value);
			break;
		case ps:
			this.queryParameter.setPs(value);
			break;
		case organ:
			this.queryParameter.setOrgan(value);
			break;
		case species:
			this.queryParameter.setSpecies(value);
			break;
		case signalingPathway:
			this.queryParameter.setSignalingPathway(value);
			break;
			
		case disease:
			this.queryParameter.setDisease(value);
			break;
			
		case goTerm:
			this.queryParameter.setGoTerm(value);
			break;
			default:
		}
	}

	@PrePersist
    void createdAt() {
		setupParamQueryName();
        queryTime = new Date();
    }
}

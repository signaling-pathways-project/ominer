package edu.bcm.dldcc.big.nursa.model.omics.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bcm.dldcc.big.nursa.model.omics.QueryForm;

import java.io.Serializable;
import java.util.Collection;

/**
 * Response of Query. The results is either Experiment or Datapoint
 * @author mcowiti
 *
 * @param <T>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TmQueryResponse<T> implements Serializable{

	@JsonProperty
	private Long count;

	@JsonProperty
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private QueryForm queryForm;

	@JsonProperty
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Collection<T> results;


	@JsonProperty
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Collection<OmicsDatapointReport> resultsByPathways;


	public TmQueryResponse() {
		
	}

	public TmQueryResponse(QueryForm queryForm, Long count, Collection<T> results) {
		super();
		this.queryForm = queryForm;
		this.count = count;
		this.results = results;
	}

	public TmQueryResponse(Long count, QueryForm queryForm, Collection<T> results, Collection<OmicsDatapointReport> resultsByPathways) {
		this.count = count;
		this.queryForm = queryForm;
		this.results = results;
		this.resultsByPathways = resultsByPathways;
	}

	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}

    public QueryForm getQueryForm() {
		return queryForm;
	}
	public void setQueryForm(QueryForm queryForm) {
		this.queryForm = queryForm;
	}
	
	public Collection<T> getResults() {
		return results;
	}
	public void setResults(Collection<T> results) {
		this.results = results;
	}

	public Collection<OmicsDatapointReport> getResultsByPathways() {
		return resultsByPathways;
	}

	public void setResultsByPathways(Collection<OmicsDatapointReport> resultsByPathways) {
		this.resultsByPathways = resultsByPathways;
	}
}

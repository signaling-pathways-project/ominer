package edu.bcm.dldcc.big.nursa.model.omics.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeSummary;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ConsensomeResult<T> {


	@JsonProperty
	private ConsensomeSummary summary;
	@JsonProperty
	private List<T> results= new ArrayList<T>();
	
	public  ConsensomeResult(ConsensomeSummary summary, List<T> results) {
		super();
		this.summary = summary;
		this.results = results;
	}

	public void setSummary(ConsensomeSummary summary) {
		this.summary = summary;
	}


	public void setResults(List<T> results) {
		this.results = results;
	}


	public  ConsensomeSummary getSummary() {
		return summary;
	}
	public List<T> getResults() {
		return results;
	}
	
}

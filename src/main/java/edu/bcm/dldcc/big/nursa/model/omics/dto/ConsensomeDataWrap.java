package edu.bcm.dldcc.big.nursa.model.omics.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeSummary;
import edu.bcm.dldcc.big.nursa.model.omics.QueryForm;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class ConsensomeDataWrap<T> implements Serializable {

	private static final long serialVersionUID = 3941733808035051445L;

	@JsonProperty
	private int count;

	@JsonProperty
	private ConsensomeSummary consensomeSummary;

	@JsonProperty
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private QueryForm queryForm;

	@JsonProperty
	private List<T> data= new ArrayList<T>();
	
	public ConsensomeDataWrap(int count, ConsensomeSummary consensomeSummary,List<T> data) {
		super();
		this.count = count;
		this.consensomeSummary=consensomeSummary;
		this.data = data;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}

	public ConsensomeSummary getConsensomeSummary() {
		return consensomeSummary;
	}

	public void setConsensomeSummary(ConsensomeSummary consensomeSummary) {
		this.consensomeSummary = consensomeSummary;
	}

	public QueryForm getQueryForm() {
		return queryForm;
	}

	public void setQueryForm(QueryForm queryForm) {
		this.queryForm = queryForm;
	}
}

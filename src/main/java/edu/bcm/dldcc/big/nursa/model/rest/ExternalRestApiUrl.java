package edu.bcm.dldcc.big.nursa.model.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

@Entity
public class ExternalRestApiUrl {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private APIS api;
	
	private String url;

	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name = "extRestApiUrl_params",joinColumns=@JoinColumn(name="urlId"))
	private List<RestParameter> params = new ArrayList<RestParameter>();
	
	public ExternalRestApiUrl() {
		super();
	}

	public ExternalRestApiUrl(APIS api, String url,List<RestParameter> params) {
		super();
		this.api = api;
		this.url = url;
		this.params=params;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public APIS getApi() {
		return api;
	}

	public void setApi(APIS api) {
		this.api = api;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<RestParameter> getParams() {
		return params;
	}

	public void setParams(List<RestParameter> params) {
		this.params = params;
	}
	
	@Override
	  public int hashCode() {
	    return java.util.Objects.hash(getApi(),  getUrl());
	  }
	  @Override
	  public boolean equals(Object obj){
	    if (obj == this) {
	      return true;
	    } 
	    if (obj instanceof ExternalRestApiUrl) {
	    	ExternalRestApiUrl other = (ExternalRestApiUrl) obj; 
	      return Objects.equals(api, other.api) && Objects.equals(url, other.url);
	    } 
	    return false;
	  }
}

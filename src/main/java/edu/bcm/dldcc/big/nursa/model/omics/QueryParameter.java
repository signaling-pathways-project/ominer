package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


import org.hibernate.annotations.ForeignKey;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * QueryParam represents an instance of a TM domain object to search/query on, e.g. Molecule.
 * To completely specify the domain object, you specify its name and the value to search on.
 * The queryParamName property, represent the domain object (attribute) being searched on.
 * The query parameter value to be searched on may be expressed by either the a single property 
 * ,a List of paramValues  properties or geneList property.
 * 
 * If the query is being performed via a REST API, then paramValue (representing object's public identifier) may be used,
 *  otherwise, in a web framework such as JSF, it may be better to utilize the constrained list of existing Entities as
 *  parameter values, supplied  via the paramObjectValue property.
 * As an example, for a query that returns only Human Species for all genes expressed when the experiment involved treatment by
 *  a molecule identified with  CAS Registry No 53-188, we'd have 2 Object instances:
 *  1) {queryParamName:QueryParamName.species, paramValue:"9606"}
 *  2) {queryParamName:QueryParamName.molecule,paramValue:"53-188"}
 *  
 *  It is assumed that all other queryParam parameters have their default values.
 *  
 *  Since there are n possible queryParams  possible for a given domain object, all joined (by default) by the  disjunction (or), 
 *  if some other joining mechanism is available, such as,a " not in", the queryParam position property then becomes important.
 
 * @author mcowiti
 *
 */

@Entity
@XmlRootElement
@XmlAccessorType (XmlAccessType.PROPERTY)
public class QueryParameter implements Serializable{

	private static final long serialVersionUID = 2901945906755191678L;

	@Id @GeneratedValue(generator = "system-uuid")
	@Column(length = 36)
	private String id;
	
	@Basic
	private Integer position=0; 
	
	@ManyToOne
	@ForeignKey(name = "none")
	private QueryForm queryForm;
	
	@Enumerated(EnumType.STRING)
	private QueryParamName queryParamName;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name = "QueryParameter_value", 
	                     joinColumns = @JoinColumn(name = "QueryParameter_id"))
	@ForeignKey(name = "none")
	private Set<String> paramValues= new HashSet<String>();
	
	@Lob 
	private String geneList; 
	
	
	@Transient
	private String paramDescription; //eg omim,molecule desc, used by excel  or param search summary
	
	
	public QueryParameter() {
	}

	public QueryParameter(String paramValue) {
		super();
		this.paramValues.add(paramValue);
		this.geneList = null;
	}
	
	public QueryParameter(Set<String> paramValues, String geneList) {
		super();
		this.paramValues = paramValues;
		this.geneList = geneList;
	}
	
	public QueryParameter(QueryForm queryForm,QueryParamName queryParamName, Set<String> paramValues, String geneList) {
		super();
		this.queryForm=queryForm;
		this.queryParamName = queryParamName;
		this.paramValues = paramValues;
		this.geneList = geneList;
	}

	@XmlTransient 
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(required=true)
	public QueryParamName getQueryParamName() {
		return queryParamName;
	}

	public void setQueryParamName(QueryParamName queryParamName) {
		this.queryParamName = queryParamName;
	}
	
	@XmlElement(required=false)
	 public Set<String> getParamValues() {
		return paramValues;
	}

	public void setParamValues(Set<String> paramValues) {
		this.paramValues = paramValues;
	}

	@XmlTransient 
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@XmlTransient 
	public String getParamDescription() {
		return paramDescription;
	}

	public void setParamDescription(String paramDescription) {
		this.paramDescription = paramDescription;
	}

	@XmlElement(required=false)
    public String getGeneList() {
		return geneList;
	}

	public void setGeneList(String geneList) {
		this.geneList = geneList;
	}
	

	@JsonManagedReference
	@XmlTransient 
	public QueryForm getQueryForm() {
		return queryForm;
	}

	public void setQueryForm(QueryForm queryForm) {
		this.queryForm = queryForm;
	}
	
	

	@Override
	public String toString() {
		return "QueryParameter [id=" + id + ", position=" + position
				+ ", queryForm=" + queryForm + ", queryParamName="
				+ queryParamName + ", paramValues=" + paramValues
				+ ", geneList=" + geneList + ", paramDescription="
				+ paramDescription + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((geneList == null) ? 0 : geneList.hashCode());
		result = prime
				* result
				+ ((paramDescription == null) ? 0 : paramDescription.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result
				+ ((queryParamName == null) ? 0 : queryParamName.hashCode());
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
		QueryParameter other = (QueryParameter) obj;
		if (geneList == null) {
			if (other.geneList != null)
				return false;
		} else if (!geneList.equals(other.geneList))
			return false;
		if (paramDescription == null) {
			if (other.paramDescription != null)
				return false;
		} else if (!paramDescription.equals(other.paramDescription))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (queryParamName != other.queryParamName)
			return false;
		return true;
	}

	
}

package edu.bcm.dldcc.big.nursa.model.omics;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.ForeignKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@Entity
@XmlRootElement
@Cacheable
public class TissueCategory implements Serializable, Category {

	private static final long serialVersionUID = -2769101919325683906L;

	@Id
	private Long id;
	
	private String name;
	
	@OneToOne
	@XmlTransient
	@ForeignKey(name = "none")
	private TissueCategory parent;

	@Transient
	//use newer terms @JsonProperty("tissuesCategoriesID")
	private String bioSampleIds;

	public String getBioSampleIds() {
		bioSampleIds=new StringBuilder(this.getParent().getId().toString())
				.append(",").append(this.id.toString()).toString();
		return bioSampleIds;
	}
	
	@Transient
    @JsonIgnore
    public String getIdentifier(){
		return new StringBuilder(this.id.toString())
				.append("-").append(this.name).toString();
    	//1291 there is duplicate ids in Tissue
		//return this.id.toString();
    }
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TissueCategory getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = (TissueCategory) parent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		TissueCategory other = (TissueCategory) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}	
}

package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;



/**
 * 
 * @author mcowiti
 *
 */
@Entity
@Table(name = "ReferenceArticleJournal")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Journal implements Serializable  {

	
	private static final long serialVersionUID = -7838458061962025607L;

	@Id
	private String issn;
	
	private String title;
	private String ISOAbbreviation;
	@XmlTransient
	private String articleType;
	
	public String getIssn() {
		return issn;
	}
	public void setIssn(String issn) {
		this.issn = issn;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getISOAbbreviation() {
		return ISOAbbreviation;
	}
	public void setISOAbbreviation(String iSOAbbreviation) {
		ISOAbbreviation = iSOAbbreviation;
	}
	public String getArticleType() {
		return articleType;
	}
	public void setArticleType(String articleType) {
		this.articleType = articleType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((issn == null) ? 0 : issn.hashCode());
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
		Journal other = (Journal) obj;
		if (issn == null) {
			if (other.issn != null)
				return false;
		} else if (!issn.equals(other.issn))
			return false;
		return true;
	}
	
}

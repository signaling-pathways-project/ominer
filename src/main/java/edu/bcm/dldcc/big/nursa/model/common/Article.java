package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.ForeignKey;

/**
 * An article( eg Pubmed article) is separate from a Reference
 * A nursa citation reference is like
 * Ochsner SA, Watkins CM, McOwiti A, Xu X, Darlington YF, Dehart MD, Cooney AJ, Steffen DL, Becnel LB and McKenna NJ (2012) Transcriptomine, a web resource for nuclear receptor signaling transcriptomes. Physiol Genomics 44, 853-63.
 * @author mcowiti
 *
 */
@Entity
@Table(name = "ReferenceArticle")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Article implements Serializable {

	
	private static final long serialVersionUID = -7813492546585066338L;

	@Id
	private String pubmedId;

	@Transient
	private String authorsCited;
	
	@Column(length=1500)
	private String articleTitle;
	
	private String publishYear;
	
	private String volume;
	
	private String pagination;
	
	private String issue;
	private String doi;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private Journal journal;
	
	@Lob
	@Column(length=3500, columnDefinition="CLOB  NULL")
	@Basic(fetch=FetchType.EAGER)
	@XmlElement(name="abstractText")
	private String abstractBlurb;
	
	@Column(length = 2000)
	private String authorsList;
	
	@Column(length=1000)
	@XmlTransient
	private String meshHeaders;
	
	public String getPubmedId() {
		return pubmedId;
	}

	
	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}

	
	public String getAuthorsList() {
		return authorsList;
	}

	
	public void setAuthorsList(String authors) {
		this.authorsList = authors;
	}

	
	public String getArticleTitle() {
		return articleTitle;
	}

	
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	
	public String getVolume() {
		return volume;
	}

	
	public void setVolume(String volume) {
		this.volume = volume;
	}

	
	public String getPublishYear() {
		return publishYear;
	}

	
	public void setPublishYear(String publishYear) {
		this.publishYear = publishYear;
	}

	
	public String getPagination() {
		return pagination;
	}

	
	public void setPagination(String pagination) {
		this.pagination = pagination;
	}

	

	public String getIssue() {
		return issue;
	}

	
	public void setIssue(String issue) {
		this.issue = issue;
	}

	
	public String getDoi() {
		return doi;
	}

	
	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getMeshHeaders() {
		return meshHeaders;
	}


	public void setMeshHeaders(String meshHeaders) {
		this.meshHeaders = meshHeaders;
	}


	public Journal getJournal() {
		return journal;
	}


	public void setJournal(Journal journal) {
		this.journal = journal;
	}


	public String getAbstractBlurb() {
		return abstractBlurb;
	}

	/**
	 * @param abstractBlurb the abstractBlurb to set
	 */
	public void setAbstractBlurb(String abstractBlurb) {
		this.abstractBlurb = abstractBlurb;
	}


	public String getAuthorsCited() {
		return authorsCited;
	}


	public void setAuthorsCited(String authorsCited) {
		this.authorsCited = authorsCited;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pubmedId == null) ? 0 : pubmedId.hashCode());
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
		Article other = (Article) obj;
		if (pubmedId == null) {
			if (other.pubmedId != null)
				return false;
		} else if (!pubmedId.equals(other.pubmedId))
			return false;
		return true;
	}

	
	
	
}

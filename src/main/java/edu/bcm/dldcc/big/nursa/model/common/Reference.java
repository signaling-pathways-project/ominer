package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.hibernate.annotations.ForeignKey;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Reference implements Serializable {

	private static final long serialVersionUID = -6609521722907109346L;

	@Id
	@GeneratedValue(generator = "referenceSequencer")
	@SequenceGenerator(name = "referenceSequencer", sequenceName = "REFERENCE_SEQ")
	private Long id;
	
	private String pubmedId;
	
	@Column(length = 2000)
	private String nursaCitation;
	
	
	@Transient
	private String volume;
	
	@Transient
	private String pagination;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Article article;

	public Reference() {
	}

	public Reference(Article article) {
		this.article = article;
		this.pubmedId=(article!=null)?article.getPubmedId():null;
	}

	public Long getId() {
		return id;
	}

	
	public void setId(Long id) {
		this.id = id;
	}

	
	public String getPubmedId() {
		return pubmedId;
	}

	
	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}

	public String getNursaCitation() {
		return nursaCitation;
	}

	public void setNursaCitation(String nursaCitation) {
		this.nursaCitation = nursaCitation;
	}

	
	public Article getArticle() {
		return article;
	}


	public void setArticle(Article article) {
		this.article = article;
	}


	public String getVolume() {
		return (article!=null)?article.getVolume():null;
	}


	public String getPagination() {
		return (article!=null)?article.getPagination():null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Reference reference = (Reference) o;
		return Objects.equals(id, reference.id) &&
				Objects.equals(pubmedId, reference.pubmedId) &&
				Objects.equals(nursaCitation, reference.nursaCitation) &&
				Objects.equals(volume, reference.volume) &&
				Objects.equals(pagination, reference.pagination) &&
				Objects.equals(article, reference.article);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, pubmedId, nursaCitation, volume, pagination, article);
	}
}

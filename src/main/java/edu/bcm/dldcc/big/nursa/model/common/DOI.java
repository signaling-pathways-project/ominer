package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class DOI implements Serializable {
	
	private static final long serialVersionUID = 6476861257275598642L;
	@Id
	private String doi;
	private String url;
	private String type;
	
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DOI doi1 = (DOI) o;
		return Objects.equals(doi, doi1.doi) &&
				Objects.equals(type, doi1.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(doi, type);
	}
}

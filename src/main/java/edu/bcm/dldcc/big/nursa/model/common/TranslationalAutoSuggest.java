package edu.bcm.dldcc.big.nursa.model.common;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TRANSLATIONAL_AS")
public class TranslationalAutoSuggest {
	@Id
	private Long id;
	private String name;
	private String official;
	private String doi;
	private Integer rank;
	private String type;

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

	/**
	 * @return the official
	 */
	public String getOfficial() {
		return official;
	}

	/**
	 * @param official the official to set
	 */
	public void setOfficial(String official) {
		this.official = official;
	}

	/**
	 * @return the doi
	 */
	public String getDoi() {
		return doi;
	}

	/**
	 * @param doi
	 *            the doi to set
	 */
	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String toString() {
		return this.name + " (" + this.official + ")";
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}

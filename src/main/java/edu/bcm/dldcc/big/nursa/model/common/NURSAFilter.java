package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.DOI;

@Entity
public class NURSAFilter implements Serializable {

	private static final long serialVersionUID = -2768436953991112809L;

	@Id
	@GeneratedValue(generator = "nursaFilterSequencer")
	@SequenceGenerator(name = "nursaFilterSequencer", sequenceName = "NURSAFILTER_SEQ")
	private Long id;
	
	@Basic
	private String request;
	private String redirect;
	
	@Basic
	private String molType;
	@Basic
	private String identifier;
	@ManyToOne
	@ForeignKey(name = "none")
	private DOI doi;
	
	
	private boolean active;
	
	public NURSAFilter() {
		active = true;
	}

	/**
	 * @return the request
	 */
	public String getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(String request) {
		this.request = request;
	}

	/**
	 * @return the redirect
	 */
	public String getRedirect() {
		return redirect;
	}

	/**
	 * @param redirect the redirect to set
	 */
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the molType
	 */
	public String getMolType() {
		return molType;
	}

	/**
	 * @param molType the molType to set
	 */
	public void setMolType(String molType) {
		this.molType = molType;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the doi
	 */
	public DOI getDoi() {
		return doi;
	}

	/**
	 * @param doi the doi to set
	 */
	public void setDoi(DOI doi) {
		this.doi = doi;
	}
	
}

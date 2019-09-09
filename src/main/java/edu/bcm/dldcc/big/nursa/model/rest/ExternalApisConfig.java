package edu.bcm.dldcc.big.nursa.model.rest;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

/**
 * Config to access to external APIs 
 * @author mcowiti
 *
 */
@Entity
public class ExternalApisConfig {

	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(unique=true)
	@ForeignKey(name = "none")
	private ApiSource apiSource;
	
	private boolean refresh;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastRefreshDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ApiSource getApiSource() {
		return apiSource;
	}

	public void setApiSource(ApiSource apiSource) {
		this.apiSource = apiSource;
	}

	public boolean isRefresh() {
		return refresh;
	}

	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

	public Date getLastRefreshDate() {
		return lastRefreshDate;
	}

	public void setLastRefreshDate(Date lastRefreshDate) {
		this.lastRefreshDate = lastRefreshDate;
	}
	
	
	
}

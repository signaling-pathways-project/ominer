package edu.bcm.dldcc.big.nursa.model.omics;


import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Email;

/**
 * API key entity
 * @author mcowiti
 *
 */
@Entity
public class TranscriptomineApiKey  implements Serializable {

	private static final long serialVersionUID = -6086339932179713668L;

	@Id
	@Column(length = 36)
	private String id;
	
	private boolean active=true;
	
	@Email
	@Column(nullable = false,unique=true)
	private String email;
	
	private String firstName;
	
	private String lastName;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date keyIssueDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastCall;
	
	private Long numberOfCalls;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	
//	@Audited
	private String comments;
	
	public TranscriptomineApiKey() {
		this.id=generateAPIKey();
	}

	@Transient
	private String generateAPIKey(){
	  return UUID.randomUUID().toString().toUpperCase();
   }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getKeyIssueDate() {
		return keyIssueDate;
	}

	public void setKeyIssueDate(Date keyIssueDate) {
		this.keyIssueDate = keyIssueDate;
	}

	public Date getLastCall() {
		return lastCall;
	}

	public void setLastCall(Date lastCall) {
		this.lastCall = lastCall;
	}

	public Long getNumberOfCalls() {
		return numberOfCalls;
	}

	public void setNumberOfCalls(Long numberOfCalls) {
		this.numberOfCalls = numberOfCalls;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@PostUpdate
	public void update(){
		this.lastCall=new Date();
		this.setNumberOfCalls(this.numberOfCalls++);
	}
}

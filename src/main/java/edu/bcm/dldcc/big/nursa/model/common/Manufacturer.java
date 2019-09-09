package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;


@Entity
public class Manufacturer implements Serializable {

	private static final long serialVersionUID = -627993309743483180L;
	
	@Id
	@GeneratedValue(generator = "manufacturerSequencer")
	@SequenceGenerator(name = "manufacturerSequencer", sequenceName = "MANUFACTURER_SEQ")
	private long id;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private Organization organization;
	
	private String contactInformation;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}

	
}

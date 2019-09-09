package edu.bcm.dldcc.big.nursa.model.common;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class EvidenceCode implements Serializable {

	private static final long serialVersionUID = 4374986610256241384L;
	
	@Id
	@GeneratedValue(generator = "evidenceCodeSequencer")
	@SequenceGenerator(name = "evidenceCodeSequencer", sequenceName = "EVIDENCECODE_SEQ")
	private Long id;
	
	private int code;
	private String description;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class GeneModificationType implements Serializable {
	private static final long serialVersionUID = 2890436785109936509L;

	@Id
	@GeneratedValue(generator = "geneModificationTypeSequencer")
	@SequenceGenerator(name = "geneModificationTypeSequencer", sequenceName = "GENEMODIFICATIONTYPE_SEQ")
	private Long id;

	private String type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name="PolyModType")
public class PolypeptideModificationType implements Serializable {
	private static final long serialVersionUID = 2890436785109936509L;

	@Id
	@GeneratedValue(generator = "polyModificationTypeSequencer")
	@SequenceGenerator(name = "polyModificationTypeSequencer", sequenceName = "POLYMODIFICATIONTYPE_SEQ")
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

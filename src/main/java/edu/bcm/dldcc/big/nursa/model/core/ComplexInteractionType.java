package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name="ComplexInteractionType")
public class ComplexInteractionType implements Serializable {
	private static final long serialVersionUID = 2890436785109936509L;

	@Id
	@GeneratedValue(generator = "complexInterTypeSequencer")
	@SequenceGenerator(name = "complexInterTypeSequencer", sequenceName = "COMINTERTYPE_SEQ")
	private Long id;

	private String name;

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

	}

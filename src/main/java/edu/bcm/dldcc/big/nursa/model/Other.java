package edu.bcm.dldcc.big.nursa.model;

import javax.persistence.Entity;

import org.hibernate.annotations.ForeignKey;

@Entity
@ForeignKey(name = "none")
public class Other extends Molecule {

	private static final long serialVersionUID = -3247917721317216960L;

	public Other() {
		setType("Other");
	}
}

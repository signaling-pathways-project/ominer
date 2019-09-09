package edu.bcm.dldcc.big.nursa.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="NR")
@ForeignKey(name = "none")
public class NuclearReceptor extends Protein {

	private static final long serialVersionUID = 4882359692542047436L;
	
	@ManyToOne(optional=true)
	@ForeignKey(name = "none")
	private MoleculeCategory moleculeCategory;
	
	public NuclearReceptor() {
		setType("Nuclear Receptor");
	}
}

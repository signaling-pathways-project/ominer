package edu.bcm.dldcc.big.nursa.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/**
 * BSM-Node Join Entity
 * @author mcowiti
 *
 */
@Entity
@Table(name = "Ligand_Node")
public class LigandNode implements Serializable {
	
	private static final long serialVersionUID = -8973065733182847861L;

	@EmbeddedId
    private LigandNodePK id;

   @ManyToOne
    @MapsId("ligand_id") //This is the name of attr in LigandNodePK class
    @JoinColumn(name = "LIGAND_ID")
    private Ligand ligand;

    @ManyToOne
    @MapsId("molecule_id")
    @JoinColumn(name = "MOLECULE_ID")
    private Molecule node;
    
    //IUPHAR or PMID
    @Basic
    private String evidence;
    

	public LigandNodePK getId() {
		return id;
	}

	public void setId(LigandNodePK id) {
		this.id = id;
	}

	public Ligand getLigand() {
		return ligand;
	}

	public void setLigand(Ligand ligand) {
		this.ligand = ligand;
	}

	public Molecule getNode() {
		return node;
	}

	public void setNode(Molecule node) {
		this.node = node;
	}

	public String getEvidence() {
		return evidence;
	}

	public void setEvidence(String evidence) {
		this.evidence = evidence;
	} 
    
}

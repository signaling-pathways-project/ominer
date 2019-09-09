package edu.bcm.dldcc.big.nursa.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * Need for the BSM->Node M2M with extra columns
 * @author mcowiti
 *
 */
@Embeddable
public class LigandNodePK implements Serializable {

	private static final long serialVersionUID = -8696183658621441203L;

	@Column(name = "LIGAND_ID")
    private Long ligand_id;

     @Column(name = "MOLECULE_ID")
    private Long molecule_id;

	public Long getLigand_id() {
		return ligand_id;
	}

	public void setLigand_id(Long ligand_id) {
		this.ligand_id = ligand_id;
	}

	public Long getMolecule_id() {
		return molecule_id;
	}

	public void setMolecule_id(Long molecule_id) {
		this.molecule_id = molecule_id;
	}
     
	@Override
	  public int hashCode() {
	    return java.util.Objects.hash(getMolecule_id(),  getLigand_id());
	  }
	  @Override
	  public boolean equals(Object obj){
	    if (obj == this) {
	      return true;
	    } 
	    if (obj instanceof LigandNodePK) {
	    	LigandNodePK other = (LigandNodePK) obj; 
	      return Objects.equals(molecule_id, other.molecule_id) && Objects.equals(ligand_id, other.ligand_id);
	    } 
	    return false;
	  }
     
}

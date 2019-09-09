package edu.bcm.dldcc.big.nursa.model.search;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.MoleculeAutoSuggest;
import edu.bcm.dldcc.big.nursa.model.common.Species;

@Entity
@ForeignKey(name = "none")
public class NURSADatasetSearch extends SearchBase {

	private static final long serialVersionUID = 5297065328851764701L;

	@ElementCollection
	@ForeignKey(name = "none")
	private List<String> type = new ArrayList<String>();

	@ManyToMany
	@ForeignKey(name = "none")
	private List<MoleculeAutoSuggest> molecules = new ArrayList<MoleculeAutoSuggest>();

	@Transient
	private MoleculeAutoSuggest addMolecule;
	@Transient
	private MoleculeAutoSuggest removeMolecule;

	@ManyToMany
	@ForeignKey(name = "none")
	private List<Species> species = new ArrayList<Species>();

	/**
	 * @return the type
	 */
	public List<String> getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(List<String> type) {
		this.type = type;
	}

	public List<MoleculeAutoSuggest> getMolecules() {
		return molecules;
	}

	public void setMolecules(List<MoleculeAutoSuggest> molecules) {
		this.molecules = molecules;
	}

	/**
	 * @return the addMolecule
	 */
	public MoleculeAutoSuggest getAddMolecule() {
		return addMolecule;
	}

	/**
	 * @param addMolecule
	 *            the addMolecule to set
	 */
	public void setAddMolecule(MoleculeAutoSuggest addMolecule) {
		this.getMolecules().add(addMolecule);
		this.addMolecule = null;
	}

	/**
	 * @return the removeMolecule
	 */
	public MoleculeAutoSuggest getRemoveMolecule() {
		return removeMolecule;
	}

	/**
	 * @param removeMolecule
	 *            the removeMolecule to set
	 */
	public void setRemoveMolecule(MoleculeAutoSuggest removeMolecule) {
		this.getMolecules().remove(removeMolecule);
		this.removeMolecule = null;
	}

	public List<Species> getSpecies() {
		return species;
	}

	public void setSpecies(List<Species> species) {
		this.species = species;
	}
}

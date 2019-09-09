package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.Species;

/**
 * This class is represents homologene. Homologene is a way of grouping like
 * genes together using common terminology.
 * 
 * @author eastonma
 * 
 */
@Entity
public class Homologene implements Serializable {

	private static final long serialVersionUID = 4884393743736057672L;
	
	@Id
	@GeneratedValue(generator = "homologeneSequencer")
	@SequenceGenerator(name = "homologeneSequencer", sequenceName = "HOMOLOGENE_SEQ")
	private Long id;
	
	private Long homologeneId;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "HOMOLOGENE_SPECIES")
	@ForeignKey(name = "none")
	private Map<Species, Gene> genes = new HashMap<Species, Gene>();
	
	@ManyToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private DOI doi;

	
	public Homologene() {
		
	}

	/**
	 * Returns the ID of homologene
	 * @return The id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the ID of the hologene
	 * 
	 * @param id ID of the homologene
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public Long getHomologeneId() {
		return homologeneId;
	}

	public void setHomologeneId(Long homologeneId) {
		this.homologeneId = homologeneId;
	}

	/**
	 * Returns a map of all the species and genes that are associated with this homologene
	 * 
	 * @return A Map of all species and genes that are associated with this homologene
	 */
	public Map<Species, Gene> getGenes() {
		return genes;
	}

	/**
	 * Sets a map of all species and genes that are associated with this homologe.
	 * @param genes A Map of all species and genes that are associated with this homologene
	 */
	public void setGenes(Map<Species, Gene> genes) {
		this.genes = genes;
	}
	
	public void addGene(Species species, Gene gene) {
		this.genes.put(species, gene);
	}
	
	public void removeGene(Species species) {
		this.genes.remove(species);
	}

	/**
	 * @return the doi
	 */
	public DOI getDoi() {
		return doi;
	}

	/**
	 * @param doi the doi to set
	 */
	public void setDoi(DOI doi) {
		this.doi = doi;
	}
	
}

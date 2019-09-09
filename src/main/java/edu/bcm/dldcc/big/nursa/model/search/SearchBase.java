package edu.bcm.dldcc.big.nursa.model.search;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

/**
 * This is the base class for all searches in NURSA. Each individual search
 * should extend this class either directly or indirectly. By creating an entity
 * that corresponds to searches we can provide additional functionality such as
 * both public, and private saved searches.
 * 
 * @author jeremyeaston-marks
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class SearchBase implements Serializable {

	private static final long serialVersionUID = 3944173315109709585L;

	@Id
	@GeneratedValue(generator = "searchSequencer")
	@SequenceGenerator(name = "searchSequencer", sequenceName = "SEARCH_SEQ")
	private Long id;

	private String name;

	/**
	 * Returns the id corresponding to the search
	 * 
	 * @return The id of the search
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id corresponding to the search
	 * 
	 * @param id The id of the search
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the name of the search
	 * 
	 * @return The name of the search
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the search
	 * 
	 * @param name The name of the search
	 */
	public void setName(String name) {
		this.name = name;
	}

}

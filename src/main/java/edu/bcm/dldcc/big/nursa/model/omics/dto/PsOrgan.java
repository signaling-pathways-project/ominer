package edu.bcm.dldcc.big.nursa.model.omics.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Rearrange TissueCategory as PS+Organ
 * Initially Tissue Strings encoded PS and O, then split to TissueCategory (Parent,Child)
 * TissueCategory was initially part of Tissues  
 * @author mcowiti
 */

@JsonInclude(Include.NON_NULL)
public class PsOrgan implements Serializable {

	private static final long serialVersionUID = -5311018439699875891L;
	private long id;
	private String name;
	private List<PsOrgan> children= new ArrayList<PsOrgan>();
	
	public PsOrgan(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public List<PsOrgan> getChildren() {
		return children;
	}
	
}

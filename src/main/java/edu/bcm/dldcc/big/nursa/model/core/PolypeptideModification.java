package edu.bcm.dldcc.big.nursa.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import edu.bcm.dldcc.big.nursa.model.common.DataResource;
import edu.bcm.dldcc.big.nursa.model.core.Gene;

@Entity
@Table(name="PolypeptideMod")
public class PolypeptideModification implements Serializable {


	private static final long serialVersionUID = 6099440744927593057L;

	@Id
	@GeneratedValue(generator = "PolyModificationSequencer")
	@SequenceGenerator(name = "PolyModificationSequencer", sequenceName = "POLYMODIFICATION_SEQ")
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private Gene gene;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private PolypeptideModificationType modificationType;
	
	@Transient
	private String residue;
	
	private String aminoAcid;
	private Integer position;
	
	private String sequence;
	
	@ManyToOne
	@ForeignKey(name = "none")
	private Polypeptide polypeptide;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="PolypeptideMod_DataResource",  joinColumns = { @JoinColumn(name = "POLYPEPTIDEMOD_ID") })
	@ForeignKey(name = "none")
	private List<DataResource> dataResources = new ArrayList<DataResource>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Gene getGene() {
		return gene;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}

	public PolypeptideModificationType getModificationType() {
		return modificationType;
	}

	public void setModificationType(PolypeptideModificationType modificationType) {
		this.modificationType = modificationType;
	}

	public String getResidue() {
		return this.aminoAcid + this.position;
	}

	public void setResidue(String residue) {
		this.aminoAcid = residue.substring(0, 1);
		this.position = Integer.decode(residue.substring(1));
		this.residue = residue;
	}

	public String getAminoAcid() {
		return aminoAcid;
	}

	public void setAminoAcid(String aminoAcid) {
		this.aminoAcid = aminoAcid;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Polypeptide getPolypeptide() {
		return polypeptide;
	}

	public void setPolypeptide(Polypeptide polypeptide) {
		this.polypeptide = polypeptide;
	}

	public List<DataResource> getDataResources() {
		return dataResources;
	}

	public void setDataResources(List<DataResource> dataResources) {
		this.dataResources = dataResources;
	}

}

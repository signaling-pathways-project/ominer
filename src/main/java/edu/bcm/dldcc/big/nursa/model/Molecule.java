package edu.bcm.dldcc.big.nursa.model;

import java.io.Serializable;
import java.util.*;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import edu.bcm.dldcc.big.nursa.model.omics.Experiment;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Where;

import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.Reference;
import edu.bcm.dldcc.big.nursa.model.common.Species;
import edu.bcm.dldcc.big.nursa.model.common.MoleculeSynonym;


@Entity
@XmlTransient
@Inheritance(strategy = InheritanceType.JOINED)
@XmlAccessorType(XmlAccessType.FIELD)
public class Molecule implements Serializable {

	private static final long serialVersionUID = 8436035036161312729L;

	@Id
	@GeneratedValue(generator = "moleculeSequencer")
	@SequenceGenerator(name = "moleculeSequencer", sequenceName = "MOLECULE_SEQ")
	@XmlTransient
	private Long id;

	//apollo really binds only to protein
	@OneToMany(mappedBy = "ligand")
    private Set<LigandNode> ligand = new HashSet<LigandNode>();

	@Column(name="OFFICIAL_SYMBOL")
	private String officialSymbol;
	@Column(name="HGNC_SYMBOL")
	private String hgncSymbol;
	@Column(name="NAME")
	private String name;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "molecule")
	@OrderBy("name")
	@Where(clause = "display=1")
	@XmlTransient
	@ForeignKey(name = "none")
	private Set<MoleculeSynonym> synonyms = new HashSet<MoleculeSynonym>();

	@Column(length = 4000)
	@XmlTransient
	private String blurb;

	@XmlTransient
	private String moleculePictureFile;
	@XmlTransient
	private String moleculePictureId;

	/*@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "Mol_Species")
	@XmlTransient
	@ForeignKey(name = "none")
	private Map<Species, SpeciesAnnotation> speciesAnnotations = new HashMap<Species, SpeciesAnnotation>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "Mol_Annotations")
	@XmlTransient
	@ForeignKey(name = "none")
	private Map<Class<? extends MoleculeBaseAnnotation>, MoleculeBaseAnnotation> annotations = new HashMap<Class<? extends MoleculeBaseAnnotation>, MoleculeBaseAnnotation>();
*/
	@Basic
	private String type;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "Mol_Literature")
	@XmlTransient
	@ForeignKey(name = "none")
	private List<Reference> definitiveLiterature = new ArrayList<Reference>();

	@ManyToOne(cascade = CascadeType.ALL)
	@XmlTransient
	@ForeignKey(name = "none")
	private DOI doi;


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

	public String getOfficialSymbol() {
		return officialSymbol;
	}

	public void setOfficialSymbol(String officialSymbol) {
		this.officialSymbol = officialSymbol;
	}

	public String getHgncSymbol() {
		return hgncSymbol;
	}

	public void setHgncSymbol(String hgncSymbol) {
		this.hgncSymbol = hgncSymbol;
	}

	public Set<MoleculeSynonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(Set<MoleculeSynonym> synonyms) {
		this.synonyms = synonyms;
	}
	
	@Transient
	public List<MoleculeSynonym> getSynonymsAsList() {
		return new ArrayList<MoleculeSynonym>(this.synonyms);
	}

	public void addSynonym(MoleculeSynonym symbol) {
		getSynonyms().add(symbol);
	}

	public void removeSynonym(MoleculeSynonym symbol) {
		getSynonyms().remove(symbol);
	}

	public String getBlurb() {
		return (blurb.equalsIgnoreCase("N/A")) ? null:blurb ;
	}

	public void setBlurb(String blurb) {
		this.blurb = blurb;
	}


	public String getMoleculePictureFile() {
		return moleculePictureFile;
	}

	public void setMoleculePictureFile(String moleculePictureFile) {
		this.moleculePictureFile = moleculePictureFile;
	}

	public String getMoleculePictureId() {
		return moleculePictureId;
	}

	public void setMoleculePictureId(String moleculePictureId) {
		this.moleculePictureId = moleculePictureId;
	}



	/*public SpeciesAnnotation getSpeciesAnnotation(Species type) {
		return this.getSpeciesAnnotations().get(type);
	}

	public void addSpeciesAnnotation(Species species,
			SpeciesAnnotation annotation) {
		this.getSpeciesAnnotations().put(species, annotation);
	}

	public void addSpeciesAnnotations(Species species) {
		this.getSpeciesAnnotations().put(species, new SpeciesAnnotation());
	}
	
	public List<Species> retrieveSpecies() {
		return new ArrayList<Species>(getSpeciesAnnotations().keySet());
	}

	public Map<Class<? extends MoleculeBaseAnnotation>, MoleculeBaseAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(
			Map<Class<? extends MoleculeBaseAnnotation>, MoleculeBaseAnnotation> annotations) {
		this.annotations = annotations;
	}

	public Map<Species, SpeciesAnnotation> getSpeciesAnnotations() {
		return speciesAnnotations;
	}

	public void setSpeciesAnnotations(
			Map<Species, SpeciesAnnotation> annotations) {
		this.speciesAnnotations = annotations;
	}


	public <T extends MoleculeBaseAnnotation> T getAnnotation(
			Class<? extends MoleculeBaseAnnotation> type) {
		return (T) this.getAnnotations().get(type);
	}

	public <T extends MoleculeBaseAnnotation> void addAnnotation(
			Class<? extends MoleculeBaseAnnotation> class1,
			MoleculeBaseAnnotation baseCompoundAnnotation) {
		this.getAnnotations().put(class1, baseCompoundAnnotation);
	}
	

	public <T extends MoleculeBaseAnnotation> MoleculeBaseAnnotation getAnnotationFromString(
			String type) {
		try {

			// Swap the TCCL

			Thread.currentThread().setContextClassLoader(
					getClass().getClassLoader());

			// Invoke the service
			Class<? extends MoleculeBaseAnnotation> clazz = (Class<? extends MoleculeBaseAnnotation>) Class
					.forName("edu.bcm.dldcc.big.nursa.model.moleculeAnnotations."
							+ type);
			return (T) this.getAnnotation(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		}

		return null;
	}*/



	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DOI getDoi() {
		return doi;
	}

	public void setDoi(DOI doi) {
		this.doi = doi;
	}
	

	
	public Set<LigandNode> getLigand() {
		return ligand;
	}

	public void setLigand(Set<LigandNode> ligand) {
		this.ligand = ligand;
	}



	public List<Reference> getDefinitiveLiterature() {
		return definitiveLiterature;
	}

	public void setDefinitiveLiterature(List<Reference> definitiveLiterature) {
		this.definitiveLiterature = definitiveLiterature;
	}


	@Override
	public int hashCode() {
		return java.util.Objects.hash(getOfficialSymbol(),getType(),getName());
	}

	@Override
	public boolean equals(Object obj){
		if (obj == this) {
			return true;
		}
		if (obj instanceof SignalingPathway) {
			Molecule other = (Molecule) obj;
			return Objects.equals(officialSymbol, other.officialSymbol)
					&& Objects.equals(type, other.type)
					&& Objects.equals(name, other.name) ;
		}
		return false;
	}

}

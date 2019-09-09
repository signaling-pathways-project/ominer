package edu.bcm.dldcc.big.nursa.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import edu.bcm.dldcc.big.nursa.model.common.DataResource;
import edu.bcm.dldcc.big.nursa.model.common.MoleculeSynonym;
import edu.bcm.dldcc.big.nursa.services.rest.consumers.PubchemData;
import edu.bcm.dldcc.big.nursa.util.comparator.DataResourceAlphabetical;

@Entity
@ForeignKey(name = "none")
public class Ligand extends Other {

	private static final long serialVersionUID = 4882359692542047436L;
	
	//moved official symbol
	@Column(name="official_symbol")
	private String officialSymbol;
	
	//new BSM2Node
	@OneToMany(mappedBy = "node")
	private Set<LigandNode> node = new HashSet<LigandNode>();

	
	/*//TODO apollo need remove these links, have a skinny Ligand
	@OneToOne(mappedBy="ligand", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@ForeignKey(name = "none")
	private Agent compound;*/
	
	@Column(nullable=true)
	private Integer pubchemId;
	
	@Transient
	private boolean hasPubchemData;
	
	//these API gotten ids need be persisted since Queried on by /api/ Auto updated by schedule
	@Column(nullable=true)
	private String casId;
	@Column(nullable=true)
	private String chebi;
	@Column(nullable=true)
	private String iuphar;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=true)
	private Date lastPubchemUpdate;
	
	
	@Transient
	private PubchemData pubchemData;
	
	
	@OneToOne(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private MoleculeSynonym casNumber;
	
	
	@OneToMany(cascade = CascadeType.ALL)
	@Sort(type = SortType.COMPARATOR, comparator = DataResourceAlphabetical.class)
	@ForeignKey(name = "none")
	private SortedSet<DataResource> dataResources = new TreeSet<DataResource>();
	
	private String sourceType;
	
	private String jmolFile;
	
	@Transient
	private List<DataResource> datasources;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@ForeignKey(name = "none")
	private List<Protein> binds = new ArrayList<Protein>();
	
	@ManyToMany(mappedBy="ligands")
	@ForeignKey(name = "none")
	private Set<MoleculeCategory> moleculeCategory= new HashSet<MoleculeCategory>();
	
	
	@PostLoad
	public void postLoad(){
		this.hasPubchemData=(this.pubchemId != null && this.pubchemId.intValue() >0);
	}
	
	public Ligand() {
		setType("Ligand");
	}


	
	public MoleculeSynonym getCasNumber() {
		return casNumber;
	}

	public void setCasNumber(MoleculeSynonym casNumber) {
		this.casNumber = casNumber;
	}

	
	public SortedSet<DataResource> getDataResources() {
		return dataResources;
	}

	
	public void setDataResources(SortedSet<DataResource> dataResources) {
		this.dataResources = dataResources;
	}

	
	public String getSourceType() {
		return sourceType;
	}

	
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	
	public String getJmolFile() {
		return jmolFile;
	}

	
	public void setJmolFile(String jmolFile) {
		this.jmolFile = jmolFile;
	}

	
	public List<Protein> getBinds() {
		return binds;
	}

	
	public void setBinds(List<Protein> binds) {
		this.binds = binds;
	}

	public List<DataResource> getDatasources() {
		if(this.datasources== null || this.datasources.size() == 0){
			this.datasources= new ArrayList<DataResource>(this.dataResources);
		}
		return datasources;
	}

	public Set<MoleculeCategory> getMoleculeCategory() {
		return moleculeCategory;
	}

	public void setMoleculeCategory(Set<MoleculeCategory> moleculeCategory) {
		this.moleculeCategory = moleculeCategory;
	}

	public Integer getPubchemId() {
		return pubchemId;
	}

	public void setPubchemId(Integer pubchemId) {
		this.pubchemId = pubchemId;
	}

	public String getCasId() {
		return casId;
	}

	public void setCasId(String casId) {
		this.casId = casId;
	}

	public String getChebi() {
		return chebi;
	}

	public void setChebi(String chebi) {
		this.chebi = chebi;
	}

	public String getIuphar() {
		return iuphar;
	}

	public void setIuphar(String iuphar) {
		this.iuphar = iuphar;
	}

	

	public Date getLastPubchemUpdate() {
		return lastPubchemUpdate;
	}

	public void setLastPubchemUpdate(Date lastUpdate) {
		this.lastPubchemUpdate = lastUpdate;
	}
	
	public void setHasPubchemData(boolean hasPubchemData) {
		this.hasPubchemData = hasPubchemData;
	}

	public PubchemData getPubchemData() {
		return pubchemData;
	}

	public void setPubchemData(PubchemData pubchemLigand) {
		this.pubchemData = pubchemLigand;
	}
	public boolean isHasPubchemData() {
		return hasPubchemData;
	}

	public Set<LigandNode> getNode() {
		return node;
	}

	public void setNode(Set<LigandNode> node) {
		this.node = node;
	}

	public String getOfficialSymbol() {
		return officialSymbol;
	}

	public void setOfficialSymbol(String officialSymbol) {
		this.officialSymbol = officialSymbol;
	}
	
	

}

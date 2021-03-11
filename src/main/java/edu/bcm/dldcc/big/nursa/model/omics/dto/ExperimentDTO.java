package edu.bcm.dldcc.big.nursa.model.omics.dto;

import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.DatapointDTO;
import edu.bcm.dldcc.big.nursa.util.Utility;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Expriment DTO
 * @author amcowiti
 *
 */
@XmlRootElement(name = "experiment")
@XmlType(propOrder = { "id","name","experimentNumber", "dataset","lof", 
		"sourceType","tissue","species","description",
		"platformName","datasource","datapoints","molecules" } ) 
public class ExperimentDTO {

	@XmlElement(required=false)
	private String id;
	@XmlElement
	private String name;
	@XmlElement
	private String experimentNumber;
	@XmlElement(name="dataset")
	private NursaDatasetDTO dataset;
	@XmlElement
	private String sourceType;
	@XmlElement
	private String tissue;
	private String species;
	@XmlElement
	private String description;
	@XmlElement
	private String platformName;
	@XmlElement
	private Integer lof;
	
	//You may use the datapoints to represent the datapoints matching the experiment query
	//not possibility of cyclic references, so do not include this if the parent of Experiment is DatapointDTO
	@XmlElement(name="datapoints", required=false)
	private List<DatapointDTO> datapoints=new ArrayList<DatapointDTO>();


	
	public ExperimentDTO() {
		
	}
	
	public ExperimentDTO(String id) {
		this(id,null,null,null,null,null,null,null,null);
	}
	
	
	/**
	 * ExperimentDTO construction takes account of null values 
	 * @param id
	 * @param name
	 * @param experimentNumber
	 * @param dataset
	 * @param description
	 * @param species
	 * @param sourceType
	 * @param sourceString
	 * @param lof
	 */
		public ExperimentDTO(String id, String name,String experimentNumber,
				NursaDatasetDTO dataset,
			String description, String species,
			String sourceType,
			String tissue,Integer lof) {
		this.id=id;
		this.name= Utility.stringOrTnNone(name);
		this.experimentNumber = Utility.stringOrTnNone(experimentNumber);
		this.dataset=dataset;
		this.description = Utility.stringOrTnNone(description);
		this.species = Utility.stringOrTnNone(species);
		this.sourceType = Utility.stringOrTnNone(sourceType);
		this.tissue = Utility.stringOrTnNone(tissue);
		this.lof = (lof!=null)?lof:TmNone.none.ordinal();
		
	}
	

	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	
	public Integer getLof() {
		return lof;
	}

	public void setLof(Integer lof) {
		this.lof=(lof!=null)?lof:TmNone.none.ordinal();
	}

	

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType=(sourceType!=null)?sourceType:TmNone.none.name();
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String taxon) {
		this.species=(species!=null)?species:TmNone.none.name();
	}


	public String getTissue() {
		if(tissue!=null && !tissue.equals("")){
			tissue=tissue.replaceAll(SourceSeparator.SOURCE_SEP.getSep(), 
					SourceSeparator.TARGET_SEP.getSep());
		}
		return tissue;
	}

	public void setTissue(String sourceString) {
		this.tissue=(sourceString!=null)?sourceString:TmNone.none.name();
	}


	public String getPlatformName() {
		return platformName;
	}


	public void setPlatformName(String platformName) {
		this.platformName=(platformName!=null)?platformName:TmNone.none.name();
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description=(description!=null)?description:TmNone.none.name();
	}


	public List<DatapointDTO> getDatapoints() {
		return datapoints;
	}

	
	public void setDatapoints(List<DatapointDTO> datapoints) {
		this.datapoints = datapoints;
	}


	
	public String getExperimentNumber() {
		return experimentNumber;
	}

	public void setExperimentNumber(String experimentNumber) {
		this.experimentNumber = experimentNumber;
	}

	
	public NursaDatasetDTO getDataset() {
		return dataset;
	}

	public void setDataset(NursaDatasetDTO dataset) {
		this.dataset = dataset;
	}
	
}

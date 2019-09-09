package edu.bcm.dldcc.big.nursa.model.transcriptomic;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeSummary;

@Entity
@DiscriminatorValue("Transcriptomic")
public class TransSummary extends ConsensomeSummary implements Serializable{

	private static final long serialVersionUID = -2832722810667689766L;

	public TransSummary() {
	}

	public TransSummary(String pathway, String physiologicalSystem, String organ, String species,
						Long numberOfDatasets, Long numberOfExperiments, Long numberDatapoints,
						String version) {
		this.getKey().setFamily(pathway);
		this.getKey().setPhysiologicalSystem(physiologicalSystem);
		this.getKey().setOrgan(organ);
		this.getKey().setSpecies(species);
		this.setNumberOfExperiments(numberOfExperiments);
		this.setNumberOfDatasets (numberOfDatasets);
		this.setNumberDatapoints ( numberDatapoints);
		this.setVersion ( version);
	}
	
	public TransSummary(String pathway, String physiologicalSystem, String organ, String species,
			Long numberOfDatasets,Long numberOfExperiments,  Long numberDatapoints,
			Date versionDate,Integer version,String doi,String title) {
		super();
		this.getKey().setFamily(pathway);
		this.getKey().setPhysiologicalSystem(physiologicalSystem);
		this.getKey().setOrgan(organ);
		this.getKey().setSpecies(species);
		this.setNumberOfExperiments(numberOfExperiments);
		this.setNumberOfDatasets (numberOfDatasets);
		this.setNumberDatapoints ( numberDatapoints);
		//this.setVersion ( version);
		this.setVersionDate (versionDate);
		this.setCversion(version);
		//FIXME this.setDoi(doi);
		this.setTitle(title);
	}
	
}

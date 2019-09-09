package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Transcriptomine data summary pojo
 * @author amcowiti
  */
@XmlRootElement
public class SummaryData {
	
	private Map<SummaryAttribute,Long> summary= new HashMap<SummaryAttribute,Long>();
	
	public SummaryData() {
		
	}

	public SummaryData(Long numFoldChanges,
                       Long numExperiments,
                       Long numNuclearReceptors,
                       Long numCoregulators,
                       Long numLigands,
                       Long numTissueAndCells,
                       Long numSpecies) {
		this.summary.put(SummaryAttribute.numberOfDataPoints, numFoldChanges);
		this.summary.put(SummaryAttribute.numberOfExperiments, numExperiments);
		this.summary.put(SummaryAttribute.numberOfNuclearReceptors, numNuclearReceptors);
		this.summary.put(SummaryAttribute.numberOfCoregulators, numCoregulators);
		this.summary.put(SummaryAttribute.numberOfLigands, numLigands);
		this.summary.put(SummaryAttribute.numberOfTissueAndCells, numTissueAndCells);
		this.summary.put(SummaryAttribute.numberOfSpecies, numSpecies);
	}
	
	@XmlElement
	public Map<SummaryAttribute, Long> getSummary() {
		return summary;
	}

	public void setSummary(Map<SummaryAttribute, Long> summary) {
		this.summary = summary;
	}

	
	
}

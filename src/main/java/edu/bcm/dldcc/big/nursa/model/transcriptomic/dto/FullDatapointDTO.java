package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.bcm.dldcc.big.nursa.model.omics.dto.ExperimentDTO;

/**
 * Datapoint with experiment information
 * @author mcowiti
 *
 */
@XmlRootElement(name = "datapoint")
public class FullDatapointDTO extends DatapointDTO {

	private static final long serialVersionUID = -7831741093365580004L;
	
	@XmlElement(required=false)
	private ExperimentDTO experiment;
	private FoldChangeDTO detail;

	public FullDatapointDTO() {
		super();
		
	}

	public FullDatapointDTO(String id, String symbol, String prob,
			String probType, double foldChange, String tissueName,
			String speciesCommonName, String experimentName, String symbolUrl,
			ExperimentDTO experiment) {
		super(id, symbol, prob, probType, foldChange, tissueName, speciesCommonName,
				experimentName, symbolUrl);
		this.experiment=experiment;
	}
	
	

	public FoldChangeDTO getDetail() {
		return detail;
	}

	public void setDetail(FoldChangeDTO detail) {
		this.detail = detail;
	}

	public ExperimentDTO getExperiment() {
		return experiment;
	}

	public void setExperiment(ExperimentDTO experiment) {
		this.experiment = experiment;
	}
	
}

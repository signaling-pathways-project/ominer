package edu.bcm.dldcc.big.nursa.model.omics.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.bcm.dldcc.big.nursa.model.cistromic.PathwayNodesView;
import edu.bcm.dldcc.big.nursa.model.omics.BsmView;

@XmlRootElement(name = "bsmReport")
public class BsmReport {

	@JsonProperty(value="bsm")
	private List<BsmView> bsm=new ArrayList<BsmView>();
	
	@JsonProperty
	private List<PathwayNodesView> pathways=new ArrayList<PathwayNodesView>();

	public BsmReport( List<BsmView> bsm,List<PathwayNodesView> pathway) {
		super();
		this.bsm = bsm;
		this.pathways = pathway;
	}
	
}

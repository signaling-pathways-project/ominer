package edu.bcm.dldcc.big.nursa.model.cistromic.dto;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "queryForm")
public class GetQueryForm {

	@FormParam("geneMode")
	@XmlElement
	private String geneMode;
	@FormParam("gene")
	@XmlElement
	private String gene;
	
	public String getGeneMode() {
		return geneMode;
	}
	public void setGeneMode(String geneMode) {
		this.geneMode = geneMode;
	}
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	@Override
	public String toString() {
		return "GetQueryForm [geneMode=" + geneMode + ", gene=" + gene + "]";
	}
	
	
}

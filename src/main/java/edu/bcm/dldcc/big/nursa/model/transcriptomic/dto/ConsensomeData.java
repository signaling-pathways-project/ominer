package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.bcm.dldcc.big.nursa.model.omics.Consensome;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class ConsensomeData extends Consensome {

	private static final long serialVersionUID = -8114515214434417968L;
	
	 @XmlElement
	private double cPValue; 
	 @XmlElement
	 private double cbyfdrvalue;
	 @XmlElement
	private Integer nexptested;

	public ConsensomeData() {
	}

	public ConsensomeData(Long id,String doi,
						  String pathway, String physiologicalSystem, String organ, String species,
						  String gene, String targetName,
						  double rank, double lcPValue, double logcPValue,
						  double discdate, Date versionDate,
						  double gmFc, double cPValue, double cbyfdrvalue, Integer nexptested) {
		super(id, doi,pathway, physiologicalSystem, organ, species, gene, targetName,rank, cPValue, discdate,gmFc, versionDate);

		this.cPValue=cPValue;
		this.cbyfdrvalue=cbyfdrvalue;
		this.nexptested=nexptested;
	}
	
	
	public double getcPValue() {
		return cPValue;
	}
	public void setcPValue(double cPValue) {
		this.cPValue = cPValue;
	}
	public double getCbyfdrvalue() {
		return cbyfdrvalue;
	}
	public void setCbyfdrvalue(double cbyfdrvalue) {
		this.cbyfdrvalue = cbyfdrvalue;
	}
	
	
	public Integer getNexptested() {
		return nexptested;
	}
	public void setNexptested(Integer nexptested) {
		this.nexptested = nexptested;
	}
}

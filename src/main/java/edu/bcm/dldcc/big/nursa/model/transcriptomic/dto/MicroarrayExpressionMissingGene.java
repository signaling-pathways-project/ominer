package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Genes that were found in the probe, but are missing from NCBI during TM3.0 migration
 * This does not include the original (TM1.5) unidentifiable genes 
 * These would either have been retired or just not found
 * @author mcowiti
 *
 */
@Entity
@Table(name="ExpMicroExpressGeneMiss")
@XmlAccessorType(XmlAccessType.FIELD)
public class MicroarrayExpressionMissingGene implements Serializable {

	private static final long serialVersionUID = -9094973691235369272L;
	
	@Id //@GeneratedValue(generator = "system-uuid")
	//@Column(length = 36)
	private Long id;
	
	private String symbol;
	private String entrezGeneId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getEntrezGeneId() {
		return entrezGeneId;
	}
	public void setEntrezGeneId(String entrezGeneId) {
		this.entrezGeneId = entrezGeneId;
	}
	
	
}

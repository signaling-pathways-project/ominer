package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.bcm.dldcc.big.nursa.model.cistromic.dto.TmNone;


/**
 * FoldChangeView Pojo
 * @author amcowiti
 *
 */
@XmlRootElement(name = "foldChange")
public class FoldChange implements Serializable {

	
	private static final long serialVersionUID = 4631560513648479335L;
	
	private long id;
	private BigDecimal value;
	private String description;
	private String display;
	
	public FoldChange() {
	}

	public FoldChange(long id) {
		this.id = id;
	}
	public FoldChange(long id, BigDecimal value, String description,String display) {
		this.id = id;
		this.value = (value!=null)?value:BigDecimal.ZERO;
		this.description = (description!=null)?description:TmNone.none.name();
		this.display=(display!=null)?display:TmNone.none.name();
	}
	
	@XmlElement
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlElement
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = (value!=null)?value:BigDecimal.ZERO;
	}

	@XmlElement
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = (description!=null)?description:TmNone.none.name();
	}

	@XmlElement
	public String getDisplay() {
		return (display!=null)?display:TmNone.none.name();
	}

	public void setDisplay(String display) {
		this.display=(display!=null)?display:TmNone.none.name();
	}
}

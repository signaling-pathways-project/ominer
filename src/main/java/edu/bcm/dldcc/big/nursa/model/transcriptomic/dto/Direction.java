package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import edu.bcm.dldcc.big.nursa.model.omics.dto.TmNone;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Direction POJO
 * Instead of returning the entity TmLkpDirection, use this Light (DTO)
 * to make it easy  for RESTful client to create API from Service Proxy without JPA API
 * 
 * @author amcowiti
 *
 */
@XmlRootElement(name = "direction")
public class Direction {

	
	private static final long serialVersionUID = -6763743064894495196L;
	private long id;
	private String value;
	private String display;
	
	public Direction() {

	}
	
	public Direction(long id) {
		this.id = id;
	}
	
	public Direction(long id, String value, String display) {
		this.id = id;
		this.value = value;
		this.display = (display!=null)?display: TmNone.none.name();
	}
	
	@XmlElement
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@XmlElement
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@XmlElement
	public String getDisplay() {
		return (display!=null)?display:TmNone.none.name();
	}
	public void setDisplay(String display) {
		this.display = (display!=null)?display:TmNone.none.name();
	}
}

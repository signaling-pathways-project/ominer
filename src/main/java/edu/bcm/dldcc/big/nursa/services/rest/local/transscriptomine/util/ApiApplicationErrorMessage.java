package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType (XmlAccessType.PROPERTY)
public class ApiApplicationErrorMessage {

	@XmlTransient
	public static final int TOO_MANY_RESULTS=413;
	@XmlTransient
	public static final int NO_SUCH_IDENTIFIER=406;
	
	private int status;
	private String message;
	private String info;
	
	public ApiApplicationErrorMessage() {
		super();
	}

	public ApiApplicationErrorMessage(int status, String message) {
		super();
		this.status = status;
		this.message = message;
		
	}
	public ApiApplicationErrorMessage(int status, String message,String info) {
		super();
		this.status = status;
		this.message = message;
		this.info=info;
	}
	
	@XmlElement
	public int getStatus() {
		return status;
	}
	@XmlElement
	public String getMessage() {
		return message;
	}

	public String getInfo() {
		return info;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}

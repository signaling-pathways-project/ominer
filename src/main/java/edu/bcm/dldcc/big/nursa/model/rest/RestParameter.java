package edu.bcm.dldcc.big.nursa.model.rest;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;


@Embeddable
public class RestParameter implements Serializable {

	private static final long serialVersionUID = -2401827495240187394L;
	private String param;
	private String value;
	
	
	public RestParameter() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RestParameter(String param, String value) {
		super();
		this.param = param;
		this.value = value;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	  public int hashCode() {
	    return java.util.Objects.hash(getParam(),  getValue());
	  }
	  @Override
	  public boolean equals(Object obj){
	    if (obj == this) {
	      return true;
	    } 
	    if (obj instanceof RestParameter) {
	    	RestParameter other = (RestParameter) obj; 
	      return Objects.equals(param, other.param) && Objects.equals(value, other.value);
	    } 
	    return false;
	  }
}

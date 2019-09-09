package edu.bcm.dldcc.big.nursa.model.omics.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Dataset for partners, via API, eg to pharmgKB
 * @author mcowiti
 *
 */
@XmlRootElement(name="dataset")
public class DatasetMinimalDTO {

	static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	 @XmlElement public String name;
	 @XmlElement public String doi;
	 @XmlElement public String description;
	 //TODO need make this private or use JsonIgnore as RDate marshals it
	//@XmlTransient
	 @JsonIgnore  private Date releaseDate;
	

	public DatasetMinimalDTO() {
		super();
	}

	public DatasetMinimalDTO(String doi,String name, String description, Date releaseDate) {
		super();
		this.name = name;
		this.doi = doi;
		this.description=description;
		this.releaseDate=releaseDate;
	}
	
	 @XmlElement(name="releaseDate")
	public String getRDate(){
		return df.format(this.releaseDate);
	}
}

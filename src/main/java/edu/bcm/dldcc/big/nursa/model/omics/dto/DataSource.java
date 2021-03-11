package edu.bcm.dldcc.big.nursa.model.omics.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Contrast Data Source
 * @author amcowiti
 *
 */
@XmlRootElement(name = "datasource")
@XmlType(propOrder = { "source","data" } ) 
public class DataSource {

	private static final long serialVersionUID = 1711082749622515923L;

	private String source;
	private String data;
	
	public DataSource() {
		super();
	}
	public DataSource(String source, String data) {
		this.source=(source != null)?source: TmNone.none.name();
		this.data=(data != null)?data:TmNone.none.name();
	}
	@XmlElement
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	@XmlElement
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}

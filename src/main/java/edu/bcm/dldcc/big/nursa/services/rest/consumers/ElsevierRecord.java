package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Elsevier idConvertor Object
 * @author mcowiti
 *
 */
@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class ElsevierRecord {
	@XmlElement public  String status;
	@XmlElement public  String responseDate;
	@XmlElement public  String request;
	@XmlElement public  List<Record> records= new ArrayList<Record>();

	public ElsevierRecord() {
		super();
	}

	@Override
	public String toString() {
		return "ElsevierRecord [status=" + status + ", responseDate=" + responseDate + ", request=" + request + ", records="
				+ records + "]";
	}


	public class Record{
		@XmlElement public  String pmcid;
		@XmlElement public  String pmid;
		@XmlElement public  String doi;
		@XmlElement public  List<Version> versions= new ArrayList<Version>();
		
		public Record() {
			super();
		}
		@Override
		public String toString() {
			return "Record [pmcid=" + pmcid + ", pmid=" + pmid + ", doi=" + doi + "]";
		}
		
	}
	
	public class Version{
		@XmlElement public  String pmcid;
		@XmlElement public  String mid;
		@XmlElement public  String current;
		public Version() {
			super();
		}
		@Override
		public String toString() {
			return "Version [pmcid=" + pmcid + ", mid=" + mid + ", current=" + current + "]";
		}
	}
}

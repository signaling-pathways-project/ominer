package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class PubchemRecord<T> {

	private T t;
	
	@XmlElement(name="Name") public String name;
	@XmlElement(name="Description") public List<String> descriptions= new ArrayList<String>();
	@XmlElement(name="CAS") public String CAS;
	@XmlElement(name="Chebi") public String chebi;
	@XmlElement(name="iuphar") public String iuphar;
	@XmlElement(name="synonyms") public List<String> synonyms= new ArrayList<String>();
	@XmlTransient public Record record;
	
	public PubchemRecord() {
		super();
		this.record= new Record();
	}
	
	
	public PubchemRecord(Record record) {
		super();
		this.record = record;
	}


	@Override
	public String toString() {
		return "PubchemRecord [name=" + name + ", descriptions=" + descriptions + ", CAS=" + CAS + ", chebi=" + chebi
				+ ", synonyms=" + synonyms + ", record=" + record + "]";
	}


	public  class Record<T>{
		
		private T t;
		@XmlElement(name="RecordType") public String recordType;
		@XmlElement(name="RecordNumber")  public Integer recordNumber;
		@XmlElement(name="Section")  public List<Section> sections= new ArrayList<Section>();
		
		
		public Record() {
			super();
		}

		public Record(String recordType, Integer recordNumber, List<Section> section) {
			super();
			this.recordType = recordType;
			this.recordNumber = recordNumber;
			this.sections = section;
		}

		@Override
		public String toString() {
			return "Record [recordType=" + recordType + ", recordNumber=" + recordNumber + ", section=" + sections
					+ "]";
		}
	}

	public   class Section<T>{
		private T t;
		@XmlElement(name="TOCHeading")  public String tOCHeading;
		@XmlElement(name="Description")  public String description;
		@XmlElement(name="Information")  public List<Information> informations= new ArrayList<Information>();
		@XmlElement(name="Section")  public List<Section> sections= new ArrayList<Section>();
		 
		public Section() {
			super();
		}

		public Section(String tOCHeading, String description, List<Information> information, List<Section> section) {
			super();
			this.tOCHeading = tOCHeading;
			this.description = description;
			this.informations = information;
			this.sections = section;
		}

		@Override
		public String toString() {
			return "Section [tOCHeading=" + tOCHeading + ", description=" + description + ", information=" + informations
					+ ", sections=" + sections + "]";
		}
		
	 }
	
	public  class Information<T>{

		private T t;
		
		@XmlElement(name="Name") public String name;
		@XmlElement(name="ReferenceNumber")  public Integer referenceNumber;
		@XmlElement(name="StringValue") public String stringValue;
		@XmlElement(name="NumValue") public Integer numValue;
		
		public Information() {
			super();
		}
		
		
		public Information(Integer referenceNumber, String name, String description, String stringValue,
				Integer numValue) {
			super();
			this.referenceNumber = referenceNumber;
			this.name = name;
			this.stringValue = stringValue;
			this.numValue = numValue;
			
		}


		@Override
		public String toString() {
			return "Information [t=" + t + ", name=" + name + ", referenceNumber=" + referenceNumber + ", stringValue="
					+ stringValue + ", numValue=" + numValue + "]";
		}

		
	}
}

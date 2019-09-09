package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class PubchemDataProperty  {
	
	@XmlElement(name="InformationList") 
	public InformationList informationList;
	
	public PubchemDataProperty() {
		super();
	}
	public PubchemDataProperty(InformationList informationList) {
		super();
		this.informationList = informationList;
	}

	@Override
	public String toString() {
		return "PubmedLigandDesc [informationList=" + informationList + "]";
	}


	public class InformationList{
		@XmlElement(name="Information") 
		public List<Information> information= new ArrayList<Information>(); 
	 
		public InformationList() {
			super();
		}

		public InformationList(List<Information> information) {
			super();
			this.information = information;
		}

		@Override
		public String toString() {
			return "InformationList [information=" + information + "]";
		}
	}

	public class Information{
		
		 @XmlElement(name="Title")
		public String title;

		public Information() {
			super();
		}

		public Information(String title) {
			super();
			this.title = title;
		}

		@Override
		public String toString() {
			return "Information [title=" + title + "]";
		}
		
	}

}

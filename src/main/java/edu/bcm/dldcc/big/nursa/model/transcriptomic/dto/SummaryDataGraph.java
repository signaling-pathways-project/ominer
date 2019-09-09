package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data for graph, e.g. Sunburst summary graph
 * @author mcowiti
 * @param <T>
 *
 */
@XmlRootElement(name="sunburst")
public class SummaryDataGraph<T> {

	private List<SunBurstData> sunBurstData;
	
	
	public SummaryDataGraph() {
        sunBurstData = new ArrayList<SunBurstData>();
	}

	@XmlElement(name="data")
	public List<SunBurstData> getSunBurstData() {
		return sunBurstData;
	}

	public void setSunBurstData(List<SunBurstData> sunBurstData) {
		this.sunBurstData = sunBurstData;
	}

    public void addSunBurstData(List<SunBurstData> sunBurstData)
    {
        this.sunBurstData.addAll(sunBurstData);
    }

    public void addChild(SunBurstData sunBurstData)
    {
        this.sunBurstData.add(sunBurstData);
    }

}

package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * SunBurst graph data element
 * Species Graph:
 * Species-->TissueCategory-->TissueSubCategory-->TissueAndCellLines
 * Molecule Graph:
 * Ligand MoleculeCategory(ER,SR, Ligands)/NRs/CORegs -->NR Molecule Category (NR-ERs,SRs,..)-->Moleucle (NR/COR=NursaSymbol,Ligand=shortName, eg 17βE2)
 * RNA graph:
 * TissueCategory-->TissueSubCategory-->TissueAndCellLines
 * @author mcowiti
 *
 */
public  class SunBurstData {

	private String name;
	private String color;
	private Integer numberOfExperiments;
	private Integer numberOfDataPoints;
	
	private List<SunBurstData> children= new ArrayList<SunBurstData>();
	
	public SunBurstData() {
        this.numberOfDataPoints = 0;
        this.numberOfExperiments = 0;
	}
	
	
	public SunBurstData(String name, Integer numberOfExperiments,
			Integer numberOfDataPoints, List<SunBurstData> children,
			String color) {
		super();
		this.name = name;
		this.numberOfExperiments = numberOfExperiments;
		this.numberOfDataPoints = numberOfDataPoints;
		this.children = children;
		this.color = color;
	}


	@XmlElement
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	@XmlElement
	public Integer getNumberOfExperiments() {
		return numberOfExperiments;
	}
	public void setNumberOfExperiments(Integer numberOfExperiments) {
		this.numberOfExperiments = numberOfExperiments;
	}
	@XmlElement
	public Integer getNumberOfDataPoints() {
		return numberOfDataPoints;
	}
	public void setNumberOfDataPoints(Integer numberOfDataPoints) {
		this.numberOfDataPoints = numberOfDataPoints;
	}
	public List<SunBurstData> getChildren() {
		return children;
	}
	public void setChildren(List<SunBurstData> children) {
		this.children = children;
	}

    public void addChildren(SunBurstData children)
    {
        this.children.add(children);
    }

    public void addChildrenWithUpdate(SunBurstData children)
    {
        this.children.add(children);
        this.numberOfDataPoints += children.numberOfDataPoints;
        this.numberOfExperiments += children.numberOfExperiments;
    }

    public void updateNumbers(Integer numberOfExperiments, Integer numberOfDatapoints)
    {
        this.numberOfExperiments += numberOfExperiments;
        this.numberOfDataPoints += numberOfDatapoints;
    }
}

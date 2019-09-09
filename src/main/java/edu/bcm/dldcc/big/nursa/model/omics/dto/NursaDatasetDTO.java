package edu.bcm.dldcc.big.nursa.model.omics.dto;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name = "dataset")
public class NursaDatasetDTO {

	private Long id;
	
	@XmlElement
	private String name;
	@XmlElement
	private String doi;
	@XmlElement
    private Date releaseDate;

    private String type;
    
    private String tissuesCategoriesID;

    private String species;

    //from exp
    private String bioSamples;
    
    //from exp
    private String pathways;

    public String getTissuesCategoriesID() {
        if ( null != tissuesCategoriesID && tissuesCategoriesID.contains(","))
        {
            Set<String> set = new HashSet<String>();
            for (String s : tissuesCategoriesID.split(","))
            {
                if (!s.isEmpty())
                {
                    set.add(s);
                }
            }
            return StringUtils.join(set,",");
        }
        else
            return tissuesCategoriesID;
    }

    public void setTissuesCategoriesID(String tissuesCategoriesID) {
        this.tissuesCategoriesID = tissuesCategoriesID;
    }

    
    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getPathways() {
        if ( null == pathways)
        {
            // 10 maps to OTHES in signaling pathways a bit dirty solution but works for the purpose of this service
            return  "10";
        }
        return pathways;
    }

    public void setPathways(String pathways) {
        this.pathways = pathways;
    }

	
	public String getBioSamples() {
		return bioSamples;
	}

	public void setBioSamples(String bioSamples) {
		this.bioSamples = bioSamples;
	}

	public NursaDatasetDTO() {
	}


	public NursaDatasetDTO(Long id,String name, String doi) {
		super();
		this.name = name;
		this.doi = doi;
		this.id = id;
	}
	
	 public NursaDatasetDTO(Long id, String name, String doi, String type, String species,Date releaseDate) {
	        this.id = id;
	        this.name = name;
	        this.doi = doi;
	        this.releaseDate = releaseDate;
	        this.type = type;
	        this.species = species;
	    }
	 
    public NursaDatasetDTO(Long id, String name, String doi, Date releaseDate, String type, String species, String pathways) {
        this.id = id;
        this.name = name;
        this.doi = doi;
        this.releaseDate = releaseDate;
        this.type = type;
        this.species = species;
        this.pathways = pathways;
    }

    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	
}

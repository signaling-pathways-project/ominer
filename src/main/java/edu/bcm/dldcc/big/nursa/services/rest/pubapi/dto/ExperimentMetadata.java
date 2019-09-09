package edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "experiment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentMetadata {

    @JsonProperty
    Long experimentId;
    @JsonProperty
    String name;
    @JsonProperty
    String description;
    @JsonProperty
    String internalExperimentId;

    @JsonIgnore
    Long speciesid;
    @JsonProperty
    String species;

    @JsonProperty
    String tissue;
    @JsonProperty
    Long tissueId;


    @JsonProperty
    String physiologicalSystem;
    @JsonProperty
    Long physiologicalSystemId;

    @JsonProperty
    String organ;
    @JsonProperty
    Long organId;

    @JsonProperty
    String type;
    @JsonProperty
    Long typeId;
    @JsonProperty
    String category;
    @JsonProperty
    Long categoryId;
    @JsonProperty(value="class")
    String cclass;
    @JsonProperty
    Long classId;
    @JsonProperty
    String family;
    @JsonProperty
    Long familyId;

    @JsonProperty
    Set<NodeFamily> nodeFamily=null;//new HashSet<>();

    public Set<NodeFamily> getNodeFamily() {
        return nodeFamily;
    }

    public ExperimentMetadata(Long expid, String name, String description, String species, String internalexperimentid, String tissue, Long psid, String ps, Long organid, String organ) {
        this.experimentId = expid;
        this.name = name;
        this.description = description;
        this.species = species;
        this.internalExperimentId = internalexperimentid;
        this.tissue = tissue;
        this.physiologicalSystemId = psid;
        this.physiologicalSystem = ps;
        this.organId = organid;
        this.organ = organ;
    }

    public ExperimentMetadata(Long expid, String name, String desccription, String species, String internalexperimentid, String tissue, Long psid, String ps, Long organid, String organ, String type, Long typeid, Long categoryId, String category, Long classid, String cclass, Long familyid, String family) {

        this(expid,name,desccription,species,internalexperimentid,tissue,psid,ps,organid,organ);

        this.type = type;
        this.typeId = typeid;
        this.categoryId = categoryId;
        this.category = category;
        this.classId = classid;
        this.cclass = cclass;
        this.familyId = familyid;
        this.family = family;
    }

    public ExperimentMetadata(String internalExperimentId, String name, String description, String species) {
        this.name = name;
        this.description = description;
        this.internalExperimentId = internalExperimentId;
        this.species = species;
    }

    public void setNodeFamily(Set<NodeFamily> nodeFamily) {
        this.nodeFamily = nodeFamily;
    }
}

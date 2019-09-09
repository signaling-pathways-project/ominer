package edu.bcm.dldcc.big.nursa.model.omics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "datapoint")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelsNode {

    @JsonProperty
    private Long id;
    @JsonProperty
    private String name;

    public ModelsNode(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

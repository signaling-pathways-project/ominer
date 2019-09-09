package edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "experiment")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type","category","class","family","typeId","categoryId","classId","familyId"})
public class NodeFamily {

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

    public NodeFamily(String type, Long typeId, String category, Long categoryId, String cclass, Long classId, String family, Long familyId) {
        this.type = type;
        this.typeId = typeId;
        this.category = category;
        this.categoryId = categoryId;
        this.cclass = cclass;
        this.classId = classId;
        this.family = family;
        this.familyId = familyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeFamily that = (NodeFamily) o;
        return Objects.equals(familyId, that.familyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(familyId);
    }
}

package edu.bcm.dldcc.big.nursa.model.omics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "SIGNALINGPATHWAY_VIEW")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "pathwayNode")
public class SignalingPathwayView {

    @JsonIgnore
    @Id
    private Long id;

    @JsonProperty
    @Column(name="CATEGORYID")
    private Long categoryId;
    @JsonProperty
    @Column(name="TYPEID")
    private Long typeId;
    @JsonProperty
    @Column(name="CLASSID")
    private Long classId;
    @JsonProperty
    @Column(name="FAMILYID")
    private Long familyId;

    public SignalingPathwayView() {
    }

    public SignalingPathwayView(Long typeId, Long categoryId, Long classId, Long familyId) {
        this.typeId = typeId;
        this.categoryId = categoryId;
        this.classId = classId;
        this.familyId = familyId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public Long getClassId() {
        return classId;
    }

    public Long getFamilyId() {
        return familyId;
    }
}

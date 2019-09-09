package edu.bcm.dldcc.big.nursa.model.omics;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@Entity
@Table(name = "DATASET_EXPS_NODES")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "datasetPathwayNode")
public class ExperimentNodesHierarchyView {


    @JsonIgnore
    @Id
    private Long id;

    private Long did;
    private Long expid;
    private Long familyId;
    private String family;
    private Long classId;
    @Column(name="CLASS")
    private String clazz;
    @Column(name="CATID")
    private Long categoryId;
    private String category;
    private Long typeId;
    private String type;

    private String nodesource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDid() {
        return did;
    }

    public void setDid(Long did) {
        this.did = did;
    }

    public Long getExpid() {
        return expid;
    }

    public void setExpid(Long expid) {
        this.expid = expid;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classid) {
        this.classId = classid;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNodesource() {
        return nodesource;
    }

    public void setNodesource(String nodesource) {
        this.nodesource = nodesource;
    }
}

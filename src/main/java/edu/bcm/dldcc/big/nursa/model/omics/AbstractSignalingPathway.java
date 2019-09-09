package edu.bcm.dldcc.big.nursa.model.omics;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractSignalingPathway {

    @Id
    @GeneratedValue(generator = "singalingPathwaySeqR")
    @SequenceGenerator(name = "singalingPathwaySeqR", sequenceName = "SIGNAL_PATHWAY_SEQ")
    @Column(updatable = false, nullable = false)
    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;

    @JsonProperty
    @Enumerated(EnumType.STRING)
    private PathwayCategory type;

    @JsonProperty
    private Integer active;

    @XmlTransient
    @JsonBackReference
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="PATHWAY_CATEGORY")
    private List<SignalingPathway> category = new ArrayList<SignalingPathway>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonManagedReference(value="children")
    @ManyToMany(mappedBy="category",fetch=FetchType.EAGER)
    private List<SignalingPathway> pathways = new ArrayList<SignalingPathway>();


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


    public PathwayCategory getType() {
        return type;
    }

    public void setType(PathwayCategory type) {
        this.type = type;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }


    public List<SignalingPathway> getCategory() {
        return category;
    }
    public void setCategory(List<SignalingPathway> category) {
        this.category = category;
    }

    public List<SignalingPathway> getPathways() {
        return pathways;
    }

    public void setPathways(List<SignalingPathway> pathways) {
        this.pathways = pathways;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id,name,  type);
    }
    @Override
    public boolean equals(Object obj){
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractSignalingPathway) {
            AbstractSignalingPathway other = (AbstractSignalingPathway) obj;
            return Objects.equals(this.id, other.id) && Objects.equals(name, other.name) && Objects.equals(type, other.type);
        }
        return false;
    }
}

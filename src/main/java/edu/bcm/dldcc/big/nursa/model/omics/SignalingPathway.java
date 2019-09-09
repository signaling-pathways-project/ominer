package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.*;
import edu.bcm.dldcc.big.nursa.model.omics.dto.NodeHierarchy;

import com.fasterxml.jackson.annotation.JsonInclude.Include;


@Entity
@Table(name = "SIGNALING_PATHWAY")
@XmlRootElement
@JsonPropertyOrder({ "id", "name", "type","parentId","displayOrder", "transExpCount", "cisExpCount","pathways","active" })
@XmlAccessorType (XmlAccessType.FIELD)
@Cacheable(true)
@JsonInclude(JsonInclude.Include.NON_NULL)
//@Immutable
public class SignalingPathway implements Serializable {

	private static final long serialVersionUID = 7465298106728989531L;

	@Id
    @GeneratedValue(generator = "singalingPathwaySeqR")
   	@SequenceGenerator(name = "singalingPathwaySeqR", sequenceName = "SIGNAL_PATHWAY_SEQ")
    @JsonProperty
	private Long id;

	@JsonProperty
    private String name;
	
	private Integer autoSuggest;
	
	 @JsonProperty("displayOrder")
	 @Column(unique=true)
	private Integer displayOrder;

	 @JsonProperty
	 private Integer active;

   @JsonProperty
   @Enumerated(EnumType.STRING)
   private PathwayCategory type;
   
    
   @XmlTransient
   @JsonBackReference
   @ManyToMany(fetch=FetchType.EAGER)
   @JoinTable(name="PATHWAY_CATEGORY")
   private List<SignalingPathway> category = new ArrayList<SignalingPathway>();
   
   @JsonInclude(Include.NON_NULL)
   @JsonManagedReference(value="children")
   @ManyToMany(mappedBy="category",fetch=FetchType.EAGER)
	private List<SignalingPathway> pathways = new ArrayList<SignalingPathway>();
  
   @Transient
   @JsonProperty
   private Integer transExpCount=null;
   
   @Transient
   @JsonProperty
   private Integer cisExpCount=null;
   
   @Transient
    @JsonIgnore
   private Optional<SignalingPathway> parent;
   
   @Transient
   @JsonProperty
   private Long parentId;
   
   
   public Integer getTransExpCount() {
       return (transExpCount!=null)?transExpCount:0;
   }

   public void setTransExpCount(Integer transCount) { this.transExpCount = transCount; }

   public Integer getCisExpCount() {

       return (cisExpCount!=null)?cisExpCount:0;
   }

   public void setCisExpCount(Integer cisCount) {
	this.cisExpCount = cisCount;
   }

   @Transient
   @JsonIgnore
   public String getIdentifier(){
   	return this.id.toString();
   }

   public Long getParentId() {
	   parentId=null;
	   if(getParent().isPresent())
	    parentId=getParent().get().getId();
	   return parentId;
   }

    @Transient
    @JsonIgnore
    @XmlTransient
   public String getUniqueName(){
   	if(this.type == PathwayCategory.type)
   		return this.getName();
   	
   	StringBuilder sb=new StringBuilder();
   	
   	if(this.type == PathwayCategory.category){
   		SignalingPathway type=getParent().get();//sure it's not null
   		return sb.append(type.getName())
   				.append("::")
   				.append(this.getName()).toString();
   	}
   	
   	if(this.type == PathwayCategory.cclass){
   		SignalingPathway category=getParent().get();
   		return sb.append(category.getParent().get().getName())
   				.append("::")
   				.append(category.getName())
   				.append("::")
   				.append(this.getName()).toString();
   	}
   	
   	SignalingPathway spnClass=getParent().get();
   	return sb.append(spnClass.getParent().get().getParent().get().getName())
				.append("::")
				.append(spnClass.getParent().get().getName())
				.append("::")
				.append(spnClass.getName())
				.append("::")
				.append(this.getName()).toString();
   	
   }


    @Transient
   @JsonIgnore
   public NodeHierarchy getNode(){
       NodeHierarchy node= new NodeHierarchy();
       SignalingPathway clazz=this.getParent().get();
       SignalingPathway cat=clazz.getParent().get();
       SignalingPathway type=cat.getParent().get();

       node.family=this.getName();
       node.familyid=this.getId();
       node.category=cat.getName();
       node.categoryid=cat.getId();
       node.type=type.getName();
       node.typeid=type.getId();
       node.classid=clazz.getId();
       node.clazz=clazz.getName();
       return node;
   }
   
   public Optional<SignalingPathway> getParent() {
	  if(this.type == PathwayCategory.type)
   			parent= Optional.empty();
   		else
   			parent=Optional.ofNullable(this.getCategory().get(0));
   		
	  return parent;	
	}
	

   public Long getId() {
       return id;
   }

   public void setId(Long id) {
       this.id = id;
   }

   public Integer getAutoSuggest() {
		return autoSuggest;
	}

	public void setAutoSuggest(Integer autoSuggest) {
		this.autoSuggest = autoSuggest;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
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

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	@Override
	  public int hashCode() {
	    return java.util.Objects.hash(getId(),getName(),  getType());
	  }
	  @Override
	  public boolean equals(Object obj){
	    if (obj == this) {
	      return true;
	    } 
	    if (obj instanceof SignalingPathway) {
	    	SignalingPathway other = (SignalingPathway) obj; 
	      return Objects.equals(id, other.id) && Objects.equals(name, other.name) && Objects.equals(type, other.type);
	    } 
	    return false;
	  }
}

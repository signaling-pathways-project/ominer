package edu.bcm.dldcc.big.nursa.model.omics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

/**
 * Experiment Annotation
 * @author mcowiti
 *
 */
@Entity
@XmlTransient
@Table(name="EXPANNOTATION")
@Inheritance(strategy=InheritanceType.JOINED)
@XmlAccessorType(XmlAccessType.FIELD)
public class ExperimentBaseAnnotation implements Serializable {

	
	private static final long serialVersionUID = -5445435578500529073L;
	@Id @GeneratedValue(generator = "system-uuid")
	@Column(length = 36)
	@XmlTransient
	private String id;
	
	@ManyToOne 
	@XmlTransient
	@ForeignKey(name = "none")
	private Experiment experiment;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	
}

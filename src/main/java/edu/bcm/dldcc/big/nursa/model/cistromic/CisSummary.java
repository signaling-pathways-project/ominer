package edu.bcm.dldcc.big.nursa.model.cistromic;

import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeSummary;


/**
 * Summary of Cis Consensome calculation
 * @author mcowiti
 *
 */
@Entity
@DiscriminatorValue("Cistromic")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement
public class CisSummary extends ConsensomeSummary implements Serializable {

	
	private static final long serialVersionUID = 7425246990058569874L;

	public CisSummary() {
		super();
	}

}

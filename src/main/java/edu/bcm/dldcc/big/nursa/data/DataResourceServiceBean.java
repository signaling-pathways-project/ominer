package edu.bcm.dldcc.big.nursa.data;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.nursa.model.Ligand;
import edu.bcm.dldcc.big.nursa.util.qualifier.NoConversationDatabase;

/**
 * Returns Nursa Data (Molecule,Ligand, Protein, etc) 
 * Service Facade for RESTful quests
 * @author mcowiti
 *
 */
@Stateless
@Named
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class DataResourceServiceBean implements Serializable{

	private static final long serialVersionUID = 2817805356087126018L;
	
	@Inject
	@NoConversationDatabase
	private EntityManager noConvoEntityManager;
	
	/**
	 * More useful to have by doi
	 * @param id
	 * @return
	 */
	public Ligand findLigand(Long id){
		return noConvoEntityManager.find(Ligand.class, id);
	}
}

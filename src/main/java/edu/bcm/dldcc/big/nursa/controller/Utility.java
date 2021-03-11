package edu.bcm.dldcc.big.nursa.controller;

import java.io.Serializable;

import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import edu.bcm.dldcc.big.nursa.model.Molecule;
import edu.bcm.dldcc.big.nursa.model.common.Synonym;

@RequestScoped
@Named("jsfUtilities")
public class Utility implements Serializable {

	private static final long serialVersionUID = -6041995464703811263L;

	public String createSearchTerms(Molecule molecule) {
		String returns = "(" + molecule.getOfficialSymbol() + ")";
		
		for(Synonym synonym : molecule.getSynonymsAsList()) {
			if (synonym != null && synonym.getName() != null && !synonym.equals(""))
			returns = "(" + returns + " OR " + synonym.getName() + ")";
		}
		
		return returns;
	}

}

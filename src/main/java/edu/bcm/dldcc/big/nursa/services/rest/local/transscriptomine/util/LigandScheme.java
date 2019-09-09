package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util;


/**
 * Ligand Identification scheme
 * @author mcowiti
 *
 */
public enum LigandScheme {
CAS,CHEBI,IUPHAR,PUBCHEM,DRUG;
	public static boolean isConvertible(String val){
		try{
			LigandScheme.valueOf(val);
			return true;
		}catch (Exception e){
			return false;
		}
	}
}

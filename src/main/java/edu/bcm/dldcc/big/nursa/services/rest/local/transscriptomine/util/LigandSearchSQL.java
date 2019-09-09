package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util;

/**
 * Ligand search SQl
 * @author mcowiti
 *
 */
public enum LigandSearchSQL {

	CAS_SQL("select lig.ID as id from LIGAND lig inner join molsynon mols on mols.id=lig.CASNUMBER_ID "+
				"where name in (:id)"),
	CHEIUPPUB_SQL("select lig.id as id from ligand lig "+
		"inner join ligand_dataresource ldat on lig.id=ldat.ligand_id "+
		"inner join dataresource dat on ldat.dataresources_id=dat.id "+
		" inner join organization org on org.id=dat.organization_id "+
		" inner join molsynon mol on mol.molecule_id=lig.id "+
		" inner join dataresource_synon dats on dats.dataresource_id=dat.id and dats.synonyms_id=mol.id "+
		" where mol.name in (:id) and lower(org.name)=:scheme");
	
	private String sql;

	public String getSql() {
		return sql;
	}

	private LigandSearchSQL(String sql) {
		this.sql = sql;
	}
	
}

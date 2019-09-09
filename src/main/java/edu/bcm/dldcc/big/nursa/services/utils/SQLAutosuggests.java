package edu.bcm.dldcc.big.nursa.services.utils;

public class SQLAutosuggests {

	public final static String MOLECULE="select NAME, OFFICIAL, type, mlid  from (select NAME, OFFICIAL, ml.type as type, ml.id as mlid, mas.DOI, RANK, ROW_NUMBER() OVER " +
            "(PARTITION BY OFFICIAL ORDER BY OFFICIAL) AS rn from molecule_as mas INNER JOIN DOI d on mas.DOI = d.DOI " +
            "INNER JOIN MOLECULE ml ON d.DOI = ml.DOI_DOI where UPPER(NAME) LIKE :query " +
            "order by RANK asc) where rn <=1 order by RANK asc, NAME asc";
	public final static String DISEASE="select NAME, OFFICIAL from (select NAME, OFFICIAL, RANK, ROW_NUMBER() OVER " +
            "(PARTITION BY OFFICIAL ORDER BY OFFICIAL) AS rn from translational_as mas where UPPER(NAME) LIKE :query " +
            "order by RANK asc) where rn <=1 order by RANK asc, NAME asc";
	public final static String TISSUES="SELECT t.name, t.id FROM Tissue t where upper(t.name) like :query";
	public final static String ALLTISSUES="select t.name, t.ID from TISSUE t";

	public final static String GENE="select distinct(ms_name.NAME), ms_official.NAME as official, ml.TYPE, ms_official.id from " +
            "molecule ml inner join molsynon ms_name on ml.ID = ms_name.MOLECULE_ID" +
            " inner join molsynon ms_official on ml.OFFICIAL_ID = ms_official.ID " +
            "where UPPER(ms_name.NAME) like :query " +
            "and ml.TYPE in ('Nuclear Receptor', 'Coregulator', 'Protein') " +
            "and ms_name.DISPLAY = 1 and ms_name.NAME IS not NULL and ms_official.NAME IS not NULL";

	public final static String GENE_VIAGENEINFO="select symbol as name, upper_symbol as official,'Protein' as type,geneid from geneinfo_reetl\n" +
            "        where upper_symbol like :query";


	public final static String GOTERM="select distinct(g.termName) from edu.bcm.dldcc.big.nursa.model.core.GOTerm g where UPPER(g.termName) like :expr or g.goTermID like :expr";
}

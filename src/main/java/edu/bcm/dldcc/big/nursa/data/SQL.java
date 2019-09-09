package edu.bcm.dldcc.big.nursa.data;

import edu.bcm.dldcc.big.nursa.model.omics.PathwayCategory;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService.Direction;

/**
 * Created by alexey on 4/15/16.
 *
 * The main reason for this class it to store all hardcoded SQL in a single spot for better maintainability/lookability and so on
 */
public class SQL {
	
	public static final int TOPTEN=10;
	public static final int MAX_ROWS_FOR_TOPTEN=20;
	public static final int MAX_ROWS_FOR_RANGE=1000;
	
	 public static final String SELECT_DATASETS_BY_HGNC_SYMBOL=
			 "select  distinct d.name as name, d.description as description, d.doi_doi as doi, d.RELEASE_DATE as releaseDate "+
			 " from NURSADATASET d,nursadataset_molecule dm, molecule m, molsynon sy "+
			 " where d.id=dm.nursadataset_id and dm.molecules_id=m.id and "+
			 " d.ACTIVE=1 and "+
			 " m.official_id=sy.id and "+
			 " sy.name=:term";
	 
	 public static final String SELECT_DATASETS_BY_EGENEID=
			 "select  distinct d.name as name, d.description as description, d.doi_doi as doi, d.RELEASE_DATE as releaseDate "+
			 " from NURSADATASET d,nursadataset_molecule dm, molecule m, molsynon sy "+
			 " where d.id=dm.nursadataset_id and dm.molecules_id=m.id and "+
			 " d.ACTIVE=1 and "+
			 " m.official_id=sy.id and "+
			 " sy.name in ( "+
			 " select s.name from gene g, molsynon s "+
			 " where g.official_id=s.id and "+
			 " g.entrezgeneid=:term) ";
	
	 public static final String SELECT_DATASETS_BY_LIGAND_CAS=
			 "select distinct d.name as name,d.doi_doi as doi,d.description as description, d.RELEASE_DATE as releaseDate "
			 + " from NURSADATASET d, nursadataset_molecule dm, molecule m, OTHER o, ligand l, molsynon sy "
			 +" where d.id=dm.nursadataset_id and dm.molecules_id=m.id and "
			 +" d.ACTIVE=1 and "
			 +" m.id=o.id and l.id=o.id and l.casnumber_id=sy.id and "
			 +" sy.name =:term";
	 
	 public static final String SELECT_DATASETS_BY_LIGAND_PIC=
			 "select distinct d.name as name,d.doi_doi as doi,d.description as description, d.RELEASE_DATE as releaseDate "
			 +" from NURSADATASET d,nursadataset_molecule dm, molecule mol, other o, ligand l, LIGAND_DATARESOURCE lds, dataresource ds, organization org, molsynon sy "
			 +" where d.id=dm.nursadataset_id and dm.molecules_id=mol.id  "
			 +" and d.ACTIVE=1 "
			 +" and mol.id=o.id and o.id=l.id  "
			 + "and l.id=lds.ligand_id and lds.dataresources_id=ds.id "
			 + "and ds.organization_id=org.id "
			 +" and sy.molecule_id=l.id "
			 +" and org.orgtype='LIGAND' and org.name=:scheme  and sy.name=:term";
	 
    //next comment does not have any value only tip for Intellij for popper syntax highlighting
    //language=SQL
    public static final String SELECT_ALL_DATASETS_WITH_METADATA =
                                         "WITH " +
                                        "    tissue_aggreegate AS " +
                                        "(SELECT " +
                                        "   dataset_id, " +
                                        "   listagg(subca, ',') " +
                                        "   WITHIN GROUP ( ORDER BY subca) AS tissuesCategoriesID " +
                                        " FROM (SELECT DISTINCT\n" +
                                        "         (t.TISSUECATEGORY_ID),\n" +
                                        "         exp.DATASET_ID,\n" +
                                        "         tcat.subca\n" +
                                        "       FROM TMEXPERIMENT exp INNER JOIN TISSUE t ON t.ID = exp.TISSUESOURCE_ID " +
                                        "       INNER JOIN  (SELECT tc.id, sys_connect_by_path(tc.ID,',') subca\n" +
                                        "                    FROM tissuecategory tc CONNECT BY PRIOR tc.id = tc.parent_id\n" +
                                        "                    START WITH parent_id IS NULL)  tcat ON tcat.id = t.TISSUECATEGORY_ID)\n" +
                                        " GROUP BY DATASET_ID)\n" +
                                        "SELECT\n" +
                                        "  id,\n" +
                                        "  TYPE,\n" +
                                        "  NAME,\n" +
                                        "  doi,\n" +
                                        "  releaseDate,\n" +
                                        "  species,\n" +
                                        "  tissuesCategoriesID,\n" +
                                        "  listagg(PATHWAY_ID, ',')\n" +
                                        "  WITHIN GROUP (\n" +
                                        "    ORDER BY PATHWAY_ID) AS pathways\n" +
                                        "FROM\n" +
                                        "  (SELECT DISTINCT\n" +
                                        "     (molpay.PATHWAY_ID),\n" +
                                        "     dset.id,\n" +
                                        "     dset.TYPE,\n" +
                                        "     dset.NAME,\n" +
                                        "     dset.DOI_DOI AS doi,\n" +
                                        "     dset.RELEASE_DATE AS releaseDate,\n" +
                                        "     sp.COMMONNAME AS species,\n" +
                                        "     taggree.tissuesCategoriesID " +
                                        "   FROM NURSADATASET dset\n" +
                                        "     LEFT JOIN NURSADATASET_MOLECULE dsetmol ON dset.ID = dsetmol.NURSADATASET_ID\n" +
                                        "     LEFT JOIN MOLECULE_SPATHWAY molpay ON molpay.MOLECULE_ID = dsetmol.MOLECULES_ID\n" +
                                        "     LEFT JOIN tissue_aggreegate taggree ON dset.id = taggree.DATASET_ID\n" +
                                        "     INNER JOIN SPECIES sp ON dset.SPECIES_ID = sp.ID\n" +
                                        "   WHERE dset.ACTIVE = 1)" +
                                        "GROUP BY id, TYPE, NAME, doi, releaseDate, species,tissuesCategoriesID ";

    //language=SQL
    public static final String SELECT_PATHWAY = "select pway from SignalingPathway  pway where pway.type=:type order by pway.name";
    public static final String SELECT_PATHWAY_CONFIGED = "select pway from SignalingPathway  pway where pway.type=:type and pway.autoSuggest=:suggest order by pway.name";

    public static final String SELECT_NAMED_PATHWAY = "select pway from SignalingPathway  pway where pway.type=:type and pway.name=:name order by pway.name";
    public static final String SELECT_NAMED_PATHWAYS = "select pway from SignalingPathway  pway where pway.type=:type and pway.name in (:names) order by pway.name";

    //language=SQL
    public static final String SELECT_TISSUECATEGORY= "select tcat from TissueCategory tcat";

    //language=SQL
    public static final String COUNT_ASSOSIATED_DPOINTS = "select count(*) from NURSADATASET dset " +
                                                          " INNER JOIN REFERENCE r on r.ID = dset.REFERENCE_ID" +
                                                          " where r.PUBMEDID = :pubmedId";

    //language=SQL
    public static final String SELECT_DSET_BY_PMID = "select dset from NURSADataset dset " +
                                                      " where dset.reference.pubmedId = :pubmedId";

    //language=SQL
    public static final String SELECT_DSET_BY_ID = "select ds from edu.bcm.dldcc.big.nursa.model.dataset.NURSADataset ds where ds.id = :id and ds.active = true";

    //language=SQL
    public static final String COUNT_GENES_BY_ENTREZGENEID = "select count(distinct m.NAME) from gene g INNER JOIN MOLSYNON m on m.ID = g.OFFICIAL_ID where g.ENTREZGENEID in (:list)";

    //language=SQL
    public static final String COUNT_GENES_BY_NAME = "select count(distinct m.NAME) from gene g INNER JOIN MOLSYNON m on m.ID = g.OFFICIAL_ID where m.NAME in (:list)";

    //language=SQL
    public static final String SELECT_ALL_TISSUES = "select t from Tissue t ";

    //language=SQL
    public static final String SELECT_QF_BY_ID = "select qf from QueryForm qf where qf.id = :id";

    //language=SQL
    public static final String COUNT_SPECIES = "SELECT count(sp) FROM edu.bcm.dldcc.big.nursa.model.common.Species sp";

    //language=SQL
    public static final String COUNT_TISSUES = "SELECT count(distinct t.ID ) FROM EXPMICROARRAYEXPRESSION expr INNER JOIN TMEXPERIMENT exp on expr.EXPERIMENT_ID = exp.ID INNER JOIN TISSUE t on exp.TISSUESOURCE_ID = t.ID WHERE exp.ACTIVE = 1";

    //language=SQL
    public static final String COUNT_COREGULATORS = "SELECT count(distinct m.id ) from EXPANNOTATION_VIEW ev INNER JOIN MOLECULE m on ev.MOLECULE_OFFICIAL_ID = m.id INNER JOIN TMEXPERIMENT exp on ev.EXPERIMENT_ID = exp.ID where m.type = 'Coregulator' and exp.ACTIVE = 1";

    //language=SQL
    public static final String COUNT_LIGANDS = "SELECT count(distinct m.id ) from EXPANNOTATION_VIEW ev INNER JOIN MOLECULE m on ev.MOLECULE_OFFICIAL_ID = m.id INNER JOIN TMEXPERIMENT exp on ev.EXPERIMENT_ID = exp.ID where m.type = 'Ligand' and exp.ACTIVE = 1";

    //language=SQL
    public static final String COUNT_NR = "SELECT count(distinct m.id ) from EXPANNOTATION_VIEW ev INNER JOIN MOLECULE m on ev.MOLECULE_OFFICIAL_ID = m.id INNER JOIN TMEXPERIMENT exp on ev.EXPERIMENT_ID = exp.ID where m.type = 'Nuclear Receptor' and exp.ACTIVE = 1";

    //language=SQL
    public static final String COUNT_ACTIVE_EXPERIMENTS = "SELECT count(ex) FROM edu.bcm.dldcc.big.nursa.model.transcriptomine.Experiment ex where ex.active = true ";

    //language=SQL
    public static final String COUNT_DPOINTS = "SELECT count(expr.id) FROM EXPMICROARRAYEXPRESSION expr INNER JOIN TMEXPERIMENT exp on expr.EXPERIMENT_ID = exp.ID where exp.ACTIVE = 1 AND expr.GENE_ID is not null";

    //language=SQL
    public static final String SELECT_DPOINTS_BY_EXP_ID = "SELECT  e.id as id, ms.name as symbol, d.url as symbolUrl, e.FOLDCHANGE as foldChange, " +
                                                        " m.name as symbolSynonym, e.PVALUE as pValue, ms.ID as geneOfficialId," +
                                                        " listagg(mtb_dpoint.METABOLITE_ID,',') WITHIN GROUP (ORDER BY mtb_dpoint.METABOLITE_ID) as mtbids" +
                                                        " FROM EXPMICROARRAYEXPRESSION e" +
                                                        "  LEFT JOIN GENE g on e.gene_id=g.id" +
                                                        "  LEFT JOIN MOLSYNON m on g.official_id = m.id" +
                                                        "  LEFT JOIN MOLECULE ml on ml.ID = m.MOLECULE_ID" +
                                                        "  LEFT JOIN MOLSYNON ms on ml.OFFICIAL_ID = ms.id" +
                                                        "  LEFT JOIN DOI d on ml.DOI_DOI = d.DOI" +
                                                        " LEFT JOIN METABOLITE_DATAPOINT mtb_dpoint on e.ID = mtb_dpoint.DATAPOINT_ID" +
                                                        "  where e.EXPERIMENT_ID = :experiment_id" +
                                                        "  and (e.PVALUE <= 0.05 or e.PVALUE is NULL)" +
                                                        "  and (e.FOLDCHANGE >= :foldChange or e.FOLDCHANGE <= 1/:foldChange)" +
                                                        " GROUP BY e.id, ms.name, d.url , e.FOLDCHANGE, m.name, e.pvalue,ms.ID";

  //language=SQL
    public static final String SELECT_METABOLITES_BY_EXP_ID = "SELECT DISTINCT mtb.id, mtb.name " +
                                                            "FROM EXPMICROARRAYEXPRESSION e " +
                                                            "  LEFT JOIN GENE g on e.gene_id=g.id " +
                                                            "  LEFT JOIN MOLSYNON m on g.official_id = m.id " +
                                                            "  LEFT JOIN MOLECULE ml on ml.ID = m.MOLECULE_ID " +
                                                            "  LEFT JOIN MOLSYNON ms on ml.OFFICIAL_ID = ms.id " +
                                                            "  LEFT JOIN DOI d on ml.DOI_DOI = d.DOI " +
                                                            "  LEFT JOIN METABOLITE_DATAPOINT mtb_dpoint on e.ID = mtb_dpoint.DATAPOINT_ID " +
                                                            "  LEFT JOIN METABOLITE mtb on mtb_dpoint.METABOLITE_ID = mtb.ID " +
                                                            "where e.EXPERIMENT_ID = :experiment_id " +
                                                            "      and (e.PVALUE <= 0.05 or e.PVALUE is NULL) " +
                                                            "      and (e.FOLDCHANGE >= :foldChange or e.FOLDCHANGE <= 1/:foldChange)";
    
    //apollo listagg effect minimal if well indexed 
    public static final String SELECT_TOPTEN_DPOINTS_BY_EXP_ID ="Select * from ( "+
    														" SELECT  e.id as id, ms.name as symbol, d.url as symbolUrl, e.FOLDCHANGE as foldChange, " +
    		                                                        " m.name as symbolSynonym, e.PVALUE as pValue, ms.ID as geneOfficialId," +
    		                                                        " listagg(mtb_dpoint.METABOLITE_ID,',') WITHIN GROUP (ORDER BY mtb_dpoint.METABOLITE_ID) as mtbids" +
    		                                                        " FROM EXPMICROARRAYEXPRESSION e" +
    		                                                        "  LEFT JOIN GENE g on e.gene_id=g.id" +
    		                                                        "  LEFT JOIN MOLSYNON m on g.official_id = m.id" +
    		                                                        "  LEFT JOIN MOLECULE ml on ml.ID = m.MOLECULE_ID" +
    		                                                        "  LEFT JOIN MOLSYNON ms on ml.OFFICIAL_ID = ms.id" +
    		                                                        "  LEFT JOIN DOI d on ml.DOI_DOI = d.DOI" +
    		                                                        " LEFT JOIN METABOLITE_DATAPOINT mtb_dpoint on e.ID = mtb_dpoint.DATAPOINT_ID" +
    		                                                        "  where e.EXPERIMENT_ID = :experiment_id" +
    		                                                        "  and (e.PVALUE <= 0.05 or e.PVALUE is NULL)" +
    		                                                        "  and (e.FOLDCHANGE >= :foldChange)" +
    		                                                        " GROUP BY e.id, ms.name, d.url , e.FOLDCHANGE, m.name, e.pvalue,ms.ID " +
    		                                                 " Order by e.foldchange desc ) "+
    		                                                 " Where rownum<=10 "+
    		                                                 " Union "+
    		                                                 " Select * from ( "+
    		                                                 		" SELECT  e.id as id, ms.name as symbol, d.url as symbolUrl, e.FOLDCHANGE as foldChange, " +
    		                                                        " m.name as symbolSynonym, e.PVALUE as pValue, ms.ID as geneOfficialId," +
    		                                                        " listagg(mtb_dpoint.METABOLITE_ID,',') WITHIN GROUP (ORDER BY mtb_dpoint.METABOLITE_ID) as mtbids" +
    		                                                        " FROM EXPMICROARRAYEXPRESSION e" +
    		                                                        "  LEFT JOIN GENE g on e.gene_id=g.id" +
    		                                                        "  LEFT JOIN MOLSYNON m on g.official_id = m.id" +
    		                                                        "  LEFT JOIN MOLECULE ml on ml.ID = m.MOLECULE_ID" +
    		                                                        "  LEFT JOIN MOLSYNON ms on ml.OFFICIAL_ID = ms.id" +
    		                                                        "  LEFT JOIN DOI d on ml.DOI_DOI = d.DOI" +
    		                                                        " LEFT JOIN METABOLITE_DATAPOINT mtb_dpoint on e.ID = mtb_dpoint.DATAPOINT_ID" +
    		                                                        "  where e.EXPERIMENT_ID = :experiment_id" +
    		                                                        "  and (e.PVALUE <= 0.05 or e.PVALUE is NULL)" +
    		                                                        "  and (e.FOLDCHANGE <= 1/:foldChange)" +
    		                                                        " GROUP BY e.id, ms.name, d.url , e.FOLDCHANGE, m.name, e.pvalue,ms.ID" +
    		                                                   " Order by e.foldchange ) "+
    		                                                   " Where rownum<=10 ";
    
    //Query metabolite  , 
    public static final String SELECT_METABOLITES_BY_EXP_ID_RANGE_FRAG="SELECT DISTINCT mtb.id, mtb.name " +
            "FROM EXPMICROARRAYEXPRESSION e " +
            "  INNER JOIN METABOLITE_DATAPOINT mtb_dpoint on e.ID = mtb_dpoint.DATAPOINT_ID " +
            "  INNER JOIN METABOLITE mtb on mtb_dpoint.METABOLITE_ID = mtb.ID " +
            "where e.EXPERIMENT_ID = :experiment_id " +
            "  and (e.PVALUE <= 0.05 or e.PVALUE is NULL) " ;
          
   public static final String SELECT_DPOINTS_BY_EXP_ID_RANGE_FRAG = "SELECT  e.id as id, "
   		+ "CASE ds.type WHEN 'Metabolomic' THEN METABOLITE.name" + 
        " WHEN 'Transcriptomic' THEN ms.name " + 
        " END AS symbol, " + 
        " d.url as symbolUrl, e.FOLDCHANGE as foldChange, " +
            " m.name as symbolSynonym, e.PVALUE as pValue, ms.ID as geneOfficialId," +
            " listagg(mtb_dpoint.METABOLITE_ID,',') WITHIN GROUP (ORDER BY mtb_dpoint.METABOLITE_ID) as mtbids" +
            " FROM EXPMICROARRAYEXPRESSION e" +
            "  LEFT JOIN GENE g on e.gene_id=g.id" +
            "  LEFT JOIN MOLSYNON m on g.official_id = m.id" +
            "  LEFT JOIN MOLECULE ml on ml.ID = m.MOLECULE_ID" +
            "  LEFT JOIN MOLSYNON ms on ml.OFFICIAL_ID = ms.id" +
            "  LEFT JOIN DOI d on ml.DOI_DOI = d.DOI" +
            " LEFT JOIN METABOLITE_DATAPOINT mtb_dpoint on e.ID = mtb_dpoint.DATAPOINT_ID" +
            " LEFT JOIN  METABOLITE METABOLITE ON mtb_dpoint.METABOLITE_ID = METABOLITE.ID" +
            "  INNER JOIN TMEXPERIMENT tm on tm.id = e.EXPERIMENT_ID" +
            "  LEFT JOIN nursadataset ds on ds.id = tm.dataset_id" +
            "  where e.EXPERIMENT_ID = :experiment_id" +
            "  and (e.PVALUE <= 0.05 or e.PVALUE is NULL) AND (ms.name is not null OR METABOLITE.name is not null)";
   
   
   public static String getMetabolitesRangeSQL(boolean isRange,Direction direction){
	   StringBuilder sb= new StringBuilder(SELECT_METABOLITES_BY_EXP_ID_RANGE_FRAG);
	   
	   switch(direction){
	   	case any:
		default:
			if(isRange)
				sb.append(" and ((e.FOLDCHANGE >= :foldChangeMin and e.FOLDCHANGE <= :foldChangeMax) or (e.FOLDCHANGE <= 1/:foldChangeMin and e.FOLDCHANGE >= 1/:foldChangeMax))"); 
			else
				sb.append(" and ((e.FOLDCHANGE >= :foldChange) or (e.FOLDCHANGE <= 1/:foldChange))"); 
		break;
		case up:
			if(isRange)
				sb.append(" and ((e.FOLDCHANGE >= :foldChangeMin and e.FOLDCHANGE <= :foldChangeMax))");
			else
				sb.append(" and ((e.FOLDCHANGE >= :foldChange))");
			break;
		case down:
			if(isRange)
			  sb.append(" and ((e.FOLDCHANGE <= 1/:foldChangeMin and e.FOLDCHANGE >= 1/:foldChangeMax))") ;
			else
				sb.append(" and ((e.FOLDCHANGE <= 1/:foldChange ))") ;
	   }
	   return sb.toString();
   }
   
   public static String getDatapointsRangeSQL(boolean isRange,Direction direction){
	   StringBuilder sb= new StringBuilder(SELECT_DPOINTS_BY_EXP_ID_RANGE_FRAG);
	   switch(direction){
	   	case any:
		default:
			if(isRange)
				sb.append(" and ((e.FOLDCHANGE >= :foldChangeMin and e.FOLDCHANGE <= :foldChangeMax) or (e.FOLDCHANGE <= 1/:foldChangeMin and e.FOLDCHANGE >= 1/:foldChangeMax))");   
			else
				sb.append(" and ((e.FOLDCHANGE >= :foldChange ) or (e.FOLDCHANGE <= 1/:foldChange ))");   
		break;
		case up:
			if(isRange)
				sb.append("  and ( (e.FOLDCHANGE >= :foldChangeMin and e.FOLDCHANGE <= :foldChangeMax))");
			else
				sb.append("  and ( (e.FOLDCHANGE >= :foldChange ))");
		break;
		case down:
			if(isRange)
			  sb.append("  and ( (e.FOLDCHANGE <= 1/:foldChangeMin and e.FOLDCHANGE >= 1/:foldChangeMax))") ;
			else
				 sb.append("  and ( (e.FOLDCHANGE <= 1/:foldChange ))") ;
	   }
	   sb.append(" GROUP BY e.id, METABOLITE.name, d.url , e.FOLDCHANGE, m.name, e.pvalue,ms.ID, "
	   		+ " CASE ds.type WHEN 'Metabolomic' THEN METABOLITE.name WHEN 'Transcriptomic'then ms.name END");
	   return sb.toString();
   }
   
    //language=SQL
    public static final String RELATED_DSET_BY_MOL_OLD = "select distinct(D.ID) AS ID,D.NAME AS NAME,D.DOI_DOI as DOI " +
            "from Molecule M,EXPMOLECULETREATMENT T,EXPANNOTATION A, TMEXPERIMENT EXP,NURSADATASET D " +
            "where M.ID=T.MOLECULE_ID " +
            "and A.ID=T.ID " +
            "and A.EXPERIMENT_ID=EXP.ID " +
            "and D.ID=EXP.DATASET_ID " +
            "and D.ACTIVE=1 " +
            "and M.ID in (:valCollection) and D.ID!=:datasetID " +
            "order by D.NAME ASC";

	public static final String RELATED_DSET_BY_MOL = "select distinct(D.ID) AS ID,D.NAME AS NAME, D.DOI_DOI as DOI " +
			"            from NURSADATASET D  " +
			"            inner join TMEXPERIMENT E on D.ID=e.DATASET_ID " +
			"            inner join TMEXPERIMENT_IPAGS A on e.id=a.experiment_id " +
			"            inner join Molecule M on a.ipags=m.official_symbol " +
			"            where   D.ACTIVE=1 and e.active=1 and d.type='Transcriptomic'" +
			"           and M.ID in (:valCollection) " +
			"           and D.ID!=:datasetID " +
			"           order by D.name ASC";

    //language=SQL
    public static final String RELATED_DATASET_BY_RNA_SQL = "select distinct(D.ID),D.NAME,D.DOI_DOI AS DOI" +
            " from Tissue T, TMEXPERIMENT EXP,NURSADATASET D" +
            " where EXP.TISSUESOURCE_ID=T.ID" +
            " and D.ID=EXP.DATASET_ID" +
            " and D.ACTIVE=1" +
            " and T.NAME in (:valCollection) and D.ID!=:datasetID" +
            " order by D.NAME ASC";

    //language=SQL
    public static final String COMPLETE_TRANSLATIONAL_SYMBOL_WITHOUT_TYPE = "select ID, NAME, OFFICIAL, DOI, RANK, TYPE from (select ID, NAME, OFFICIAL, DOI, RANK, TYPE, ROW_NUMBER() OVER (PARTITION BY OFFICIAL ORDER BY OFFICIAL) AS rn from translational_as mas where UPPER(NAME) LIKE :query order by RANK asc) where rn <=1 order by RANK asc, NAME asc";
    //language=SQL
    public static final String COMPLETE_TRANSLATION_SYMBOL_WITH_TYPE = "select ID, NAME, OFFICIAL, DOI, RANK, TYPE from (select ID, NAME, OFFICIAL, DOI, RANK, TYPE, ROW_NUMBER() OVER (PARTITION BY OFFICIAL ORDER BY OFFICIAL) AS rn from translational_as mas where UPPER(NAME) LIKE :query AND type = :tranType order by RANK asc) where rn <=1 order by RANK asc, NAME asc";

    
}

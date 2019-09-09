package edu.bcm.dldcc.big.nursa.services.utils;

import edu.bcm.dldcc.big.nursa.model.omics.QueryForm;
import edu.bcm.dldcc.big.nursa.model.omics.QueryParamName;
import edu.bcm.dldcc.big.nursa.model.omics.QueryParameter;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.Datapoint;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.DatapointBasicDTO;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.DatapointDTO;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.FoldChangeDTO;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.FoldChangeXML;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.FullDatapointDTO;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.ExpAnnotationViewEntity;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alexey on 4/15/15.
 * @author mcowiti needs to be a server component 1/18/2017
 */

@Stateless
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class QueryHelper implements Serializable
{

	private static final long serialVersionUID = -5471037132817267546L;

	@PersistenceContext(unitName = "NURSA")
    private EntityManager entityManager;

	private static Logger log = Logger.getLogger(QueryHelper.class.getName());

    public  Long countDatapoints(QueryForm queryForm,  Class type)
    {
    	Query query = buildSelect(queryForm, true,false, type);
    	Long result = ((Number)query.getSingleResult()).longValue();
        this.entityManager.clear();
        return  result; 
    }
    
    public  Double findLargestFoldChange(QueryForm queryForm,  Class type)
    {
        Query query = buildSelect(queryForm, false,true, type);
        List<Object[]> result =query.getResultList();
        entityManager.clear();
        return  getLargestFC(result);
    }

    public  Double getLargestFC(List<Object[]> list){
		
    	if(list == null || list.size() == 0)
    		return 0D;
    	
		double min=0d,max=0d;
		if(list.size() != 1)
			log.log(Level.WARNING,"min/max query # list="+list.size());
		
		for(Object[] obj:list){
			if(obj[0] ==null || obj[1] ==null)
				return null;
    		min=((BigDecimal)obj[0]).doubleValue();
    		max=((BigDecimal)obj[1]).doubleValue();
    	}
    	if(min >0 && min <= 2d)
    		min=(1d/min);
    	if(max >0 && max <= 2d)
    		max=(1d/max);
    	
		return (max>min)?max:min;
	}

    /**
     * Bulding native query for Datapoints.
     * @param queryForm - parameters to search on
     * @param isCount - if this is true returns the countDatapoints of the Datapoints else returns actual datapoints
     * @param entityManager - EntityManager used to createNativeQuery later on
     * @return
     */
    private  Query buildSelect(QueryForm queryForm, boolean isCount, boolean isLargestFoldChange, Class type) {

    	 QueryBuilder builder = new QueryBuilder();
    	 
    	 //FIXME apollo we need to fix this
        if (isCount) {
            builder.setIfEmptyColumns(" /*+ LEADING(tcg) parallel(g, 4) */ countDatapoints(g.id)");
        }else if(isLargestFoldChange){
        	builder.setIfEmptyColumns("/*+ LEADING(tcg) parallel(g, 4) */  min(distinct e.FOLDCHANGE), max(distinct e.FOLDCHANGE)");
        } else {
            /***************************
             * picking up columns based on what objects that was passed
             *****************************/
            if (DatapointDTO.class.equals(type)) {
                builder.column(" /*+ LEADING(tcg) parallel(g, 4) */ ann.MOLSYNON_NAME as regulatoryMoleculeSymbol")
                		//.column("rownum as row") //FIXME for WK
                        .column("ann.molecule_name as regulatoryMoleculeName")
//                        .column(" '/' ||sp1.name || '/' || sp2.name AS moleculePathway ");
                        .column("    ann.pathway_names AS moleculePathway ");
                columnsDatapoints(builder);
            } else if (FoldChangeDTO.class.equals(type)) {
                columnsDatapoints(builder);
                columnsFoldchange(builder);
            } else if (FoldChangeXML.class.equals(type)) {
                columnsFoldchangeXML(builder);
            }
            else if (ExpAnnotationViewEntity.class.equals(type)) {
                columnsAnnotations(builder);
            }
        }
        builder.from("EXPMICROARRAYEXPRESSION e");
        builder.leftJoin("EXPANNOTATION_VIEW ann on exp.ID = ann.EXPERIMENT_ID");
        builder.leftJoin("MOLECULE_SPATHWAY mss on mss.MOLECULE_ID = ann.molecule_official_id");
        builder.leftJoin("PATHWAY_CATEGORY pc ON mss.PATHWAY_ID = pc.pathways_ID");
        builder.leftJoin("SIGNALING_PATHWAY sp1 ON pc.CATEGORY_ID = sp1.id");
        builder.leftJoin("SIGNALING_PATHWAY sp2 ON sp2.id = pc.pathways_ID");

        joinsDatapoints(builder, queryForm);
        //Added if condition: FR API by DOI miss due to spathway condition, eg DOI=10.1621/gTqItVnDEP 
        if (hasValidParameter(queryForm.getQueryParameters().get(QueryParamName.signalingPathway))) {
        	builder.where(" (sp1.name IS NOT NULL or sp2.name IS NOT NULL) ");
        }
        
        if (FoldChangeDTO.class.equals(type)||FoldChangeXML.class.equals(type)||ExpAnnotationViewEntity.class.equals(type)) {
            joinsForFolchange(builder);
            if (ExpAnnotationViewEntity.class.equals(type)) {
                builder.leftJoin("EXPANNOTATION_VIEW ann on exp.ID = ann.EXPERIMENT_ID");
            }
        }
        buildWhere(queryForm, builder,isLargestFoldChange);
  
        if (isCount) {
            return builder.buildQuery(entityManager,null);
        } else if (isLargestFoldChange){
        	 return builder.buildQuery(entityManager,null);
        }else{
            return builder.buildQuery( entityManager,type.getSimpleName());
        }
    }
    
    private  void columnsFoldchangeXML(QueryBuilder builder) {
        builder.column("e.id as id")
                .column("exp.name as experimentName")
                .column("sp.COMMONNAME as speciesCommonName")
                .column("t.name as tissueName")
                .column("e.PROBEIDENTIFIER as prob")
                .column("ms.name as symbol")
                .column("e.FOLDCHANGE as foldChange")
                .column("e.PVALUE as pValue")
                .column("exp.ID as experimentId")
                .column("ref.PUBMEDID as pubmedId");
    }

    private  void columnsAnnotations(QueryBuilder builder) {
        builder.column("ann.id as id")
                .column("ann.quantity as quantity")
                .column("ann.timehours as timehours")
                .column("ann.TIMEUNIT as timeunit")
                .column("ann.quantityunit_unit as quantityunit_unit")
                .column("ann.EXPERIMENT_ID as experiment_id")

                .column("ann.MOLSYNON_NAME as MOLSYNON_NAME")

                .column("ann.DOI_URL as DOI_URL");

        builder.groupBy("ann.id")
                .groupBy("ann.QUANTITY")
                .groupBy("ann.EXPERIMENT_ID")
                .groupBy("ann.MOLSYNON_NAME")

                .groupBy("ann.QUANTITYUNIT_UNIT")
                .groupBy("ann.TIMEHOURS")
                .groupBy("ann.TIMEUNIT")
                .groupBy("ann.DOI_URL");
    }

    private  void columnsFoldchange(QueryBuilder builder) {

        builder.column("exp.ID as experimentId")
                .column("exp.DESCRIPTION as expDescription")
                .column("dset.NAME as datasetName")
                .column("dset.DESCRIPTION as datasetDescription")
                .column("dset.id as datasetId")
                .column("dset_doi.url as datasetUrl")
                .column("dset_doi.doi as datasetDoi")
                .column("dset.repo as repo")
                .column("e.FOLDCHANGE as foldChange")
                .column("ref.PUBMEDID as pubmedId")
                .column("org.ORGTYPE as orgType");
    }

    private  void columnsDatapoints(QueryBuilder builder) {
        builder.column("e.id as id")
        //.column("rownum as row") //FIXME for WK
                .column("dset.doi_doi as datasetDoi")
                .column("exp.name as experimentName")
                .column("exp.experimentNumber as experimentNumber")
                .column("t.name as tissueName")
                .column("e.FOLDCHANGE as foldChange")
                .column("e.PROBEIDENTIFIER as prob")
                .column("e.PROBEIDENTIFIERTYPE as probType")
                .column("sp.COMMONNAME as speciesCommonName")
                .column("d.url as symbolUrl")
                .column("ms.name as symbol")
                .column("m.name as symbolSynonym")
                .column("e.PVALUE as pValue")
                .column("tcg.subca as tissueCategory");
    }

    private  void joinsForFolchange(QueryBuilder builder) {
        builder.join("DOI g_doi on g.DOI_DOI = g_doi.DOI")
                .join("DOI dset_doi on dset.DOI_DOI = dset_doi.DOI")
                .leftJoin("REFERENCE ref on dset.REFERENCE_ID = ref.ID")
                .leftJoin("NURSADATASET_DATARESOURCE ndst_ds on dset.ID = ndst_ds.NURSADATASET_ID")
                .leftJoin("dataresource dresource on ndst_ds.DATARESOURCES_ID = dresource.ID")
                .leftJoin("organization org on org.ID = dresource.ORGANIZATION_ID");
    }

    private  void joinsDatapoints(QueryBuilder builder, QueryForm queryForm) {

    	builder.join("GENE g on e.gene_id=g.id");
    	
        if (queryForm != null && hasValidParameter(queryForm.getQueryParameters().get(QueryParamName.goTerm)))
        	builder.join("GOTERMNAME_GENEID gg ON gg.genes_id=g.id");
        
        if (queryForm != null && hasValidParameter(queryForm.getQueryParameters().get(QueryParamName.disease)))
        	builder.join("DISEASETERMS_VIEW dtv ON g.id = dtv.GENE_ID");
        
    	builder.join("MOLSYNON m on g.official_id = m.id")
    		.join("TMEXPERIMENT exp on e.experiment_ID=exp.id")
            .join("NURSADATASET dset on  exp.DATASET_ID = dset.ID")
            .join("SPECIES sp on g.SPECIES_ID = sp.ID")
            .join("TISSUE t on exp.TISSUESOURCE_ID = t.ID")
            .join("MOLECULE ml on ml.ID = m.MOLECULE_ID")
            .join("MOLSYNON ms on ml.OFFICIAL_ID = ms.id")
            .join("DOI d on ml.DOI_DOI = d.DOI")
            .leftJoin("(SELECT tc.id, sys_connect_by_path(name,'/') subca " +
                    " from tissuecategory tc connect by prior tc.id = tc.parent_id " +
                    " start with parent_id is null)  tcg on t.TISSUECATEGORY_ID = tcg.id")
            .where("exp.active = 1 and dset.active = 1 "); // and ann.pathway_names is not null 
    }

    private  boolean isNegativeFoldChangeEndPoint(String fc){
    	return fc.startsWith("-");
    }
    
    private  String getRecip(String fc){
    	double s =Double.parseDouble(fc);
    	double dfc=(1d/s);
    	return String.valueOf(dfc);
    }
    
    private  boolean  isLessOrEqualPointFive(String fc){
    	BigDecimal ref = new BigDecimal("0.5");
    	BigDecimal b1 = new BigDecimal(fc);
    	return (b1.compareTo(ref) == -1 || b1.compareTo(ref) == 0) ;
    }
    
    
    private  void buildWhere(QueryForm queryForm,  QueryBuilder builder, boolean isLargestFoldChange) {
        String value;
        QueryParameter parameter;

        /////////////////
        // SINGLE GENE
        ////////////////
        setAndGetWhere(queryForm, QueryParamName.gene, "ms.id = :symbol_id", "symbol_id", builder);

        /////////////////
        // GENE LIST
        /////////////////
        parameter = queryForm.getQueryParameters().get(QueryParamName.geneList);
        if (hasValidParameter(parameter)) {
            value = (String)parameter.getParamValues().toArray()[0];
            List<String> geneList = Arrays.asList(value.toUpperCase().split("\\s*,\\s*"));

            builder.where(" ms.id in (SELECT DISTINCT (ms_official.id) FROM molecule ml " +
            " INNER JOIN molsynon ms_name ON ml.ID = ms_name.MOLECULE_ID " +
            " INNER JOIN molsynon ms_official ON ml.OFFICIAL_ID = ms_official.ID "
            + " WHERE UPPER(ms_name.NAME) IN ( :list) " 
            + "  AND ml.TYPE IN ( 'Nuclear Receptor',           'Coregulator',               'Protein' ) "
            + "  AND ms_name.DISPLAY = 1  AND ms_name.NAME IS NOT NULL  AND ms_official.NAME IS NOT NULL)")
            .addNamedParameter("list",geneList);
        }

        if(!isLargestFoldChange){
        //////////////
        // Direction
        //////////////
        parameter = queryForm.getQueryParameters().get(QueryParamName.direction);
        if (hasValidParameter(parameter)) {
            value = (String) parameter.getParamValues().toArray()[0];

            ////////////////
            // FC
            ///////////////
            String fcValue = "2.0";
            String fcValueMax = null;
            String fcValueMin = null;
            
            parameter = queryForm.getQueryParameters().get(QueryParamName.foldChange);
            if (hasValidParameter(parameter)) {
                fcValue = (String) parameter.getParamValues().toArray()[0];
            }
            
            parameter = queryForm.getQueryParameters().get(QueryParamName.foldChangeMax);
            if (hasValidParameter(parameter)) {
            	fcValueMax = (String) parameter.getParamValues().toArray()[0];
            }
            parameter = queryForm.getQueryParameters().get(QueryParamName.foldChangeMin);
            if (hasValidParameter(parameter)) {
            	fcValueMin = (String) parameter.getParamValues().toArray()[0];
            }
            boolean isRange=(fcValueMin != null && fcValueMax != null);
            
            if ("up".equals(value)) {
                builder.where("(e.FOLDCHANGEOPERATOR = 'eq' or e.FOLDCHANGEOPERATOR = 'gt')");
                if (!value.equals("0") && !value.equals("0.0")) {
                	if(!isRange)
                		builder.where("e.FOLDCHANGE >= :foldChange")
                           .addNamedParameter("foldChange", fcValue);
                	else{
                		builder.where("e.FOLDCHANGE >= :foldChangeMin and e.FOLDCHANGE <= :foldChangeMax")
                        .addNamedParameter("foldChangeMin", fcValueMin)
                        .addNamedParameter("foldChangeMax", fcValueMax);
                	}
                }

            } else if ("down".equals(value)) {//TODO deal with down
                builder.where("(e.FOLDCHANGEOPERATOR = 'eq' or e.pValueOperator = 'lt')");
                if (!value.equals("0") && !value.equals("0.0")) {
                	if(!isRange)
                		builder.where("e.FOLDCHANGE <= 1/:foldChange")
                            .addNamedParameter("foldChange", fcValue);
                	else{
                		 builder.where("e.FOLDCHANGE <= 1/:foldChangeMin and e.FOLDCHANGE >= 1/:foldChangeMax")
                		 .addNamedParameter("foldChangeMin", fcValueMin)
                         .addNamedParameter("foldChangeMax", fcValueMax);
                	}
                }

            } else if ("any".equals(value)) {//APollo why these value check?
                if (!value.equals("0") && !value.equals("0.0")) {
                	if(!isRange){
                		builder.where("(e.FOLDCHANGE >= :foldChange or e.FOLDCHANGE <= 1/:foldChange)");
                		 builder.addNamedParameter("foldChange", fcValue);
                	}else{
                		builder.where("((e.FOLDCHANGE >= :foldChangeMin and e.FOLDCHANGE <= :foldChangeMax) or (e.FOLDCHANGE <= 1/:foldChangeMin and e.FOLDCHANGE >= 1/:foldChangeMax))")
                        .addNamedParameter("foldChangeMin", fcValueMin)
                        .addNamedParameter("foldChangeMax", fcValueMax);
                	}
                }else {
                	if(!isRange){
                		builder.where("e.FOLDCHANGE >= :foldChange");
                		 builder.addNamedParameter("foldChange", fcValue);
                	}else{
                		builder.where("e.FOLDCHANGE >= :foldChangeMin and e.FOLDCHANGE <= :foldChangeMax")
                        .addNamedParameter("foldChangeMin", fcValueMin)
                        .addNamedParameter("foldChangeMax", fcValueMax);
                	}
                }
            }
        }
        }

        /////////////////
        // Significance/ P-VALUE
        /////////////////
        parameter = queryForm.getQueryParameters().get(QueryParamName.significance);
        if (hasValidParameter(parameter)) {
            value = (String) parameter.getParamValues().toArray()[0];
            if (!"any".equals(value)) {
                BigDecimal pvalue = new BigDecimal(value);
                builder.where("(e.pvalue <= :significance or e.pvalue is NULL)")
                        .addNamedParameter("significance", pvalue);
            }
        }

        /////////////////
        //    Species
        /////////////////
        parameter = queryForm.getQueryParameters().get(QueryParamName.species);
        if (hasValidParameter(parameter)) {
            value = (String) parameter.getParamValues().toArray()[0];
            if (!"all".equals(value)) {
                builder.where("sp.COMMONNAME = :speciesName")
                        .addNamedParameter("speciesName",value);
            }
        }
        
        /////// 6/9/2017 FR API
        //TODO DOI
        //////
        parameter = queryForm.getQueryParameters().get(QueryParamName.doi);
        if (hasValidParameter(parameter)) {
            value = (String) parameter.getParamValues().toArray()[0];
            if (!"".equals(value)) {
                builder.where("dset.doi_doi = :doi")
                        .addNamedParameter("doi",value);
            }
        }

        //////////////////
        //    Tissues
        /////////////////
        parameter = queryForm.getQueryParameters().get(QueryParamName.tissue);
        if (hasValidParameter(parameter)) {
            value = (String) parameter.getParamValues().toArray()[0];
            if (null != value && !"".equals(value)) {

                // checking if this tissue id is the category
                Query tissueQuery = entityManager.createNativeQuery("select t.SOURCESTRING from TISSUE t where t.ID = :id");
                tissueQuery.setParameter("id",value);
                String name = (String) tissueQuery.getResultList().get(0);
                if (name.contains("%")) {
                    //TODO refactor inner double select to the single subselect if it is possible
                    builder.where("t.id in (SELECT t.id from TISSUE t where t.TISSUECATEGORY_ID = (select tt.TISSUECATEGORY_ID from TISSUE tt where tt.id = :tissue_id)  )");
                } else {
                    builder.where("t.id = :tissue_id");
                }
                builder.addNamedParameter("tissue_id", value);
            }
        }

        ////////////////////////////////
        // Tissue Category(RNA source)
        ////////////////////////////////
        parameter =  queryForm.getQueryParameters().get(QueryParamName.tissueCategory);
        if (hasValidParameter(parameter)) {
            value = (String) parameter.getParamValues().toArray()[0];
            if (null != value && !"".equals(value))
            {
                builder.join("( select id from tissuecategory " +
                        "    where parent_id = :tissueCategory_id or id = :tissueCategory_id " +
                        "    connect by prior id = parent_id " +
                        "    start with parent_id is null ) tissuecat on tissuecat.id = t.tissuecategory_id")
                .addNamedParameter("tissueCategory_id", value);
            }
        }

        /////////////////////////////////
        // Molecule Pathway
        ////////////////////////////////
        parameter = queryForm.getQueryParameters().get(QueryParamName.signalingPathway);
        if (hasValidParameter(parameter)) {
            value = (String) parameter.getParamValues().toArray()[0];
            if (null != value && !"".equals(value)) {
        		String subSql = "";
        		parameter = queryForm.getQueryParameters().get(QueryParamName.pathwayType); 
        		String typeValue = (String) parameter.getParamValues().toArray()[0];

        		if (typeValue.equalsIgnoreCase("category")) {
        			subSql = " and ss.category =:signaling_pway )";
        		} else if (typeValue.equalsIgnoreCase("cclass")) {
                	subSql = " and ss.cclass =:signaling_pway )";
        		} else if (typeValue.equalsIgnoreCase("family")) {
                	subSql = " and ss.id =:signaling_pway )";
        		}
                //10 is constant for OTHER category in signaling pathway. TODO think about do one extra query to map "OTHERS" to id - if we will have to migrate
                //FIXME amcowiti this need to be fixed
            	String sql = " ann.molecule_official_id in (SELECT sp.MOLECULE_ID"
              		  + " FROM MOLECULE_SPATHWAY sp,  ( SELECT a.pathways_id id,"
              		  + " a.type, a.category_id cclass, CONNECT_BY_ROOT a.category_id category  FROM "
              		  + " ( SELECT p.*, s.name, s.type FROM pathway_category p, signaling_pathway s WHERE s.id = p.pathways_id(+)) a "
              		  + "  WHERE LEVEL >1  START WITH TYPE = 'cclass' CONNECT BY  PRIOR pathways_id = category_id) ss WHERE ss.id = sp.PATHWAY_ID";
            	if ("10".equals(value)) {
//                    builder.where("ann.molecule_official_id in (select sp.MOLECULE_ID from  MOLECULE_SPATHWAY sp where sp.PATHWAY_ID = :signaling_pway)");
            		builder.where(sql + subSql);
                }
                else {
//                    builder.where("ann.molecule_official_id in (select sp.MOLECULE_ID from  MOLECULE_SPATHWAY sp where sp.PATHWAY_ID = :signaling_pway or sp.PATHWAY_ID is NULL)");\
            		builder.where(sql + subSql);
                }
                builder.addNamedParameter("signaling_pway", value);
            }
        }


        /////////////////
        //    GoTerm
        // TODO 2/15/2017 redo this as will fail for 1000+
        /////////////////
        setAndGetWhere(queryForm, QueryParamName.goTerm, 
        		"gg.TERMNAME = :go_term_name", "go_term_name", builder);
        ///////////////////
        // Disease Term
        ///////////////////
        setAndGetWhere(queryForm, QueryParamName.disease, 
        		"dtv.NAME = :disease_name", "disease_name", builder);

        /////////////////
        //  Molecule
        /////////////////
        setAndGetWhere(queryForm, QueryParamName.molecule,
        		"exp.id in (select DISTINCT(exp_an.EXPERIMENT_ID) from EXPANNOTATION_VIEW exp_an where exp_an.MOLECULE_OFFICIAL_ID = :molecule_official_id)",
                "molecule_official_id", builder);


        ///////////////////////////
        // Moleculetreatment Time
        ///////////////////////////
        parameter = queryForm.getQueryParameters().get(QueryParamName.moleculetreatmentTime);
        if (hasValidParameter(parameter)) {
            value = (String) parameter.getParamValues().toArray()[0];
            if ("1".equals(value)) {
                builder.where("exp.id in (SELECT DISTINCT(exp_an.EXPERIMENT_ID) from EXPANNOTATION_VIEW exp_an where exp_an.TIME_HOURS <= 1)");
            } else if ("1-4".equals(value)) {
                builder.where("exp.id in (SELECT DISTINCT(exp_an.EXPERIMENT_ID) from EXPANNOTATION_VIEW exp_an where exp_an.TIME_HOURS > 1 and exp_an.TIME_HOURS <= 4)");
            } else if ("4-24".equals(value)) {
                builder.where("exp.id in (SELECT DISTINCT(exp_an.EXPERIMENT_ID) from EXPANNOTATION_VIEW exp_an where exp_an.TIME_HOURS > 4 and exp_an.TIME_HOURS <= 24)");
            } else if ("24".equals(value)) {
                builder.where("exp.id in (SELECT DISTINCT(exp_an.EXPERIMENT_ID) from EXPANNOTATION_VIEW exp_an where exp_an.TIME_HOURS > 24)");
            }
        }
    }

    private  void setAndGetWhere(QueryForm queryForm, QueryParamName paramName ,String query, String namedParameter, QueryBuilder builder)
    {
        QueryParameter parameter = queryForm.getQueryParameters().get(paramName);
        if (hasValidParameter(parameter)) {
            String value = (String) parameter.getParamValues().toArray()[0];
            if (null != value && !"".equals(value)) {
                builder.where(query)
                       .addNamedParameter(namedParameter, value);
            }
        }
    }

    public  List executeQuery(QueryForm queryForm,  Integer max_number, Class type) {

    	Long endTime, startTime = System.currentTimeMillis();
        Long count;
        Double largestFoldChange;
        long b=System.currentTimeMillis();
        if ( null != queryForm.getResultCount() && 0 != queryForm.getResultCount()) {
        	count = queryForm.getResultCount();
        } 
            count = countDatapoints(queryForm, type);
           
        queryForm.setResultCount(count);
        
        List results = new ArrayList();

        if (count > 0 && count <= max_number) {
        	largestFoldChange=findLargestFoldChange(queryForm, type);
        	queryForm.setLargestFoldChange(largestFoldChange);
        	
            Query query = buildSelect(queryForm, false, false, type);
            //cannot cache why?? return is DatapointDTO  
            //query.setHint("org.hibernate.cacheable", true);
            results = query.getResultList();
            
            // if it is a FC we need to load annotations
            List annotationsList;
            if (FoldChangeXML.class.equals(type)) {
            	log.log(Level.FINE,"@executeQuery is FoldChangeXML, loading annotation. Extra Query, TODO Collapse to one Query ");
                Query query1 = buildSelect(queryForm, false, false, ExpAnnotationViewEntity.class);
                annotationsList = query1.getResultList();

                Map<Integer,List<ExpAnnotationViewEntity>> map = annotationsToMap(annotationsList);

                for (Object foldChange : results) {
                    FoldChangeXML tmp = (FoldChangeXML) foldChange;
                    tmp.setAnnotations(map.get(tmp.getExperimentId()));
                }
            }
            
            if(isADatapointQuery(type)){
            	Collections.sort(results);
                int num=results.size()-1;
                if(results.size() > 0){
	                queryForm.setMinFoldChange(((Datapoint)results.get(0)).getFoldChange());
	                queryForm.setMinFoldChangeRaw(((Datapoint)results.get(0)).getFoldChangeRaw());
	                queryForm.setMaxFoldChange(((Datapoint)results.get(num)).getFoldChange());
	                queryForm.setMaxFoldChangeRaw(((Datapoint)results.get(num)).getFoldChangeRaw());
                }
            }
        }
        
        queryForm.setQueryDuration(System.currentTimeMillis()-startTime);
        queryForm.setResultCount(count);
        
        log.log(Level.FINE,"@executeQuery done (ms)  "+(System.currentTimeMillis()-b));
        return results;
    }

    /**
     * Refactor this, use instanceof Datapoint<T>
     * @param type
     * @return
     */
    private  boolean isADatapointQuery(Class type){
    	if (FoldChangeXML.class.equals(type))
    		return true;
    	if (DatapointDTO.class.equals(type))
    		return true;
    	if (DatapointBasicDTO.class.equals(type))
    		return true;
    	if (FoldChangeDTO.class.equals(type))
    		return true;
    	if (FullDatapointDTO.class.equals(type))
    		return true;
    	
    	return false;
    }

    public  FoldChangeDTO getSingleFcById(Integer id) {
        //TODO finish merge with buildSelect
        QueryBuilder builder = new QueryBuilder();
        builder.from("EXPMICROARRAYEXPRESSION e");
        joinsDatapoints(builder, null);
        joinsForFolchange(builder);
        columnsDatapoints(builder);
        columnsFoldchange(builder);

        builder.where("e.ID = :id").addNamedParameter("id", id);

        Query query = builder.buildQuery(entityManager,FoldChangeDTO.class.getSimpleName());

        FoldChangeDTO result = (FoldChangeDTO) query.getSingleResult();
        loadAnnotations(result);

        return result;
    }

    private  void loadAnnotations(FoldChangeDTO result) {

        if (null == result|| null == result.getExperimentId()) {
            return;
        }

        List<ExpAnnotationViewEntity> annotations = getAnnotations(result.getExperimentId());

        if (null != annotations && annotations.size() >0)
            result.setAnnotations(annotations);
    }

    private  List<ExpAnnotationViewEntity> getAnnotations(Integer experementId) {
        Query getAnnotationtQuery = entityManager.createQuery("SELECT ann from edu.bcm.dldcc.big.nursa.model.transcriptomine.view.ExpAnnotationViewEntity ann where ann.experimentId = :experiment_id");
        getAnnotationtQuery.setParameter("experiment_id", experementId);
        List<ExpAnnotationViewEntity> annotations = getAnnotationtQuery.getResultList();
        return annotations;
    }

    private  boolean hasValidParameter(QueryParameter parameter)
    {
        return null != parameter&&
                null !=parameter.getParamValues()&&
                parameter.getParamValues().size() >0;
    }

    public  long bigDecimalToLong(Object num) {
        return ((BigDecimal)num).longValue();
    }

    private  Map<Integer,List<ExpAnnotationViewEntity>> annotationsToMap(List<ExpAnnotationViewEntity> annotations) {
        Map<Integer, List<ExpAnnotationViewEntity>> result = new HashMap<Integer, List<ExpAnnotationViewEntity>>(annotations.size());
        for (ExpAnnotationViewEntity annotation : annotations) {
            if (null == annotation) {
                continue;
            }

            List<ExpAnnotationViewEntity> record = result.get(annotation.getExperimentId());

            if (null != record) {
                record.add(annotation);
            } else {
                List<ExpAnnotationViewEntity> newRecord = new ArrayList<ExpAnnotationViewEntity>();
                newRecord.add(annotation);
                result.put(annotation.getExperimentId(), newRecord);
            }
        }

        return result;
    }
}
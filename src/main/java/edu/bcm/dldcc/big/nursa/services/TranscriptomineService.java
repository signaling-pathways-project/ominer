package edu.bcm.dldcc.big.nursa.services;

import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.model.omics.PathwayCategory;
import edu.bcm.dldcc.big.nursa.model.omics.QueryForm;
import edu.bcm.dldcc.big.nursa.model.omics.QueryParamName;
import edu.bcm.dldcc.big.nursa.model.omics.QueryParameter;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway;
import edu.bcm.dldcc.big.nursa.model.omics.Tissue;
import edu.bcm.dldcc.big.nursa.model.omics.TissueCategory;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetApiQueryDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetMinimalDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.NursaDatasetDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.PsOrgan;
import edu.bcm.dldcc.big.nursa.model.omics.dto.AutosuggestList;
import edu.bcm.dldcc.big.nursa.model.omics.dto.QueryParametersData;
import edu.bcm.dldcc.big.nursa.model.omics.dto.SummaryData;
import edu.bcm.dldcc.big.nursa.model.omics.dto.TmQueryResponse;

import javax.ejb.Local;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The main service for Transcriptomine
 * @author mcowiti
 *
 */
@Local
public interface TranscriptomineService {

	public final static String  DEFAULT_PATHWAY_CAT_NR_NAME="NR Pathways";
	public final static String  DEFAULT_PATHWAY_CAT_CR_NAME="Coregulators";
	public final static Integer  DEFAULT_PATHWAY_AUTOSUGGEST=1;
	public final static Integer MAX_NUMBER_DATAPOINTS = 2000;
	public final static Integer MAX_NUMBER_DATAPOINTS_ABSOLUTE = 10000;
	public final static Integer MAX_NUMBER_AUTOSUGGEST=5;
    public final static Integer MAX_NUMBER_GENELIST = 5000;
    // Yes, recent version of EXCEL supports up to 1,048,576 records, but we want to have that number
    public static final Integer MAX_NUMBER_XLS = 65535;
    public static final Integer MAX_API_DATAPOINTS = 2000;
    public static final Integer MAX_API_DATASETS = 2000;
    public static final double MAX_FC_BY_DEFOLT = 6666.6666D;

    public enum Direction{
		any,up,down;
	}
    
    public List<NursaDatasetDTO> getAllActiveDatasets();
    
    public QueryParametersData buildQueryParametersData(String geneSearchType, String gene, String geneList, String bsm,
			String doi,String moleculetreatmentTime, String foldChange, String foldChangeMax, String foldChangeMin,
			String significance, String direction, 
			String tissueCategory,String ps,String organ,String tissue, 
			String disease, String goTerm,
			String species, String signalingPathway,String pathwayCategory,String findCount) ;
    
	 public <T> T findBasicDatapointsByExpId(int experimentId, Double foldChange,
			 Double foldChangeMin,Double foldChangeMax,String direction,String absSort);


	/**
	 * Return # of important Data summary, {@link SummaryData}
	 * @return
	 */
	public SummaryData listSummary();
	
	/**
	 * Return data for summary graph data, {@link SummaryDataGraph}
	 * @param <T>
	 * @return
	 */
	//public <T> SummaryDataGraph<T> getSumamryGraph(Class<T> type,GraphType graphType);
	
	
	/**
	 * Return list of Datapoints matching query request. 
	 * If there is an existing query, and no parameters have changed, use the method
	 * If this is a first time query, persist the QueryForm provided, if end client IP is absent,
	 * determine the IP address
	 * find datapoints matching query.
     *
	**/
	public <T> List<T> findDataPoints(QueryForm queryForm, Class type, Integer maxNumber,boolean isPersist);
	public Double findLargestFoldChange(QueryForm queryForm,Class type);

	public Long findDatapointsCount(QueryForm queryForm);
	
    /**
     * Return list of Datapoints matching query that was submitted before and persisted with the provided id
     * @param id
     * @return
     */
    public <T>List<T> findDatapointsByQueryFormId(String id);

    /**
     * Search for saved QueryForm if nothing is found returns null;
     * @param queryId query form id
     * @return QueryForm/null
     */
    public QueryForm findQueryForm(String queryId);

    
	
	/**Ó
	 * Query  returns a Datapoint DTO or entity, expected to be called by REST API client
	* Always first determine the countDatapoints of datapoints, T, the query will return before getting a
	 * result set. If the countDatapoints exceed {@link #MAX_NUMBER_DATAPOINTS}, set the
	 * @see TmQueryResponse#count, but
	 * do not return any results. 
	 *  If this is a first time query, persist the QueryForm provided. If the end client IP is absent in
	 *  the queryForm, determine the client IP address
	 * @param <T>
	 * @param queryForm
	 * @return
	 */
	public <T> TmQueryResponse<T> findDatapointSearchResults(QueryForm queryForm);
	
	
	
	/**
	 * List Constrained parameters ( so called dropdown or lookup lists)
	 * @param type
	 * @return
	 */
	public <T> Collection<T> listDropDowns(Class<T> type);
	
	
	/**
	 * Perform autoComplete on given attribute(Molecule,Gene,Disease,GoTerm,Tissue)
	 * @param type
	 * @param inputValue
	 * @param max maximum number to show. Default 5
	 * @return
	 */
	public <T> AutosuggestList<T> autoSuggestList(Class<T> type,String inputValue,Integer max);
	
	public <T> Collection<T> autoSuggest(Class<T> type,String inputValue,Integer max);
	
	/**
	 * List all tissues and cell lines curated within TM
	 * @return
	 */
	public Collection<Tissue> listTissuesAndCellLines();

    /**
     * Verifies the validity of the Genelist uploaded.
     * @param list
     * @return number of entries that does not mach. -1 if list is null or has more than 5k entries
     */
    public Integer validateListOfGenes(List<String> list);
    
    /**
     * Return a list of all Tissue and Celllines in curated by the system
     * @return
     */
    public <T> List<T> getAllTissuesAndCellLines();
    public List<TissueCategory> getAllTissueCategories();

    public <T> T getFoldchangeDetails(Integer id);

    /**
     * Search NURSADataset by id
     * @param id
     * @return
     */
    public NURSADataset findDataSetById(Long id);

    /**
     * Searched for NURSADataset based on pubmedId. Since we ca have multiple datasets associated with the single
     * pubmedId the list can be returned;
     * @param pubmedId
     * @return
     */
    public List<NURSADataset> findDatasetByPubmedId(String pubmedId);

    /**
     * Returns true if we have a dataset associated with
     * @param pubmedId
     * @return
     */
    public int numberOfAssociatedDatasets(String pubmedId);

	
	public List<PsOrgan> getTissueCategory();

	/**
	 * Returns all SignalingPathways organised as tree
	 * @return CategoryNode
     */
	//public CategoryNode getSignalingPathways();

	public List<SignalingPathway> getAllSignalingPathways(PathwayCategory pathwayCategory,String name);
	 public List<SignalingPathway> getSignalingPathways(PathwayCategory type);

	public List<SignalingPathway> getNamedPathways(PathwayCategory pathwayCategory,String[] names);
	
	public List<NursaDatasetDTO> getAllDatasets();
	
	public List<DatasetMinimalDTO> findDatasets(DatasetApiQueryDTO query);

	public void translateParams(Map<QueryParamName, QueryParameter> query, String apiKey, String includeExp,
			String geneSearchType, String gene, String disease, String goTerm, String molecule, String ligandIdType,
			String significance, String foldChange, String direction, String species, String author, String doi,
			String pubmed, String treatmenttime, 
			String ps,String organ,String tissue,
			String foldChangeMin, String foldChangeMax,
			String signalingPathway,String pathwayCategory)
	 throws Exception;
}

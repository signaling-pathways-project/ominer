package edu.bcm.dldcc.big.nursa.data;

import edu.bcm.dldcc.big.nursa.model.Molecule;
import edu.bcm.dldcc.big.nursa.model.Molecule_;
import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.DOI_;
import edu.bcm.dldcc.big.nursa.model.common.MoleculeAutoSuggest;
import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset_;
import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.model.search.NURSADatasetSearch;
import org.omnifaces.util.Ajax;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LazyNURSADatasetDataLoader extends LazyDataModel<NURSADataset> {

	//TODO Apollo we need to remove this
	private static final long serialVersionUID = 7625203008398806277L;

	private NURSADatasetSearch ndsSearch;

	private EntityManager objectEntityManager;

	private List<SortMeta> storedSort;
	
	private Map<String, Integer> datasetBreakdown = new HashMap<String, Integer>();
	
	@Inject
	private MoleculeSynonymListProducer mslp;
	

	public LazyNURSADatasetDataLoader() {
		super();
	}

	public LazyNURSADatasetDataLoader(EntityManager objectEntityManager,
			NURSADatasetSearch ndsSearch) {
		super();
		this.objectEntityManager = objectEntityManager;
		this.ndsSearch = ndsSearch;
	}

	@Override
	public List<NURSADataset> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		SortMeta sm = new SortMeta();
		sm.setSortField(sortField);
		sm.setSortOrder(sortOrder);
		List<SortMeta> multiSortMeta = new ArrayList<SortMeta>();
		multiSortMeta.add(sm);
		return load(first, pageSize, multiSortMeta, filters);
	}

	public List<NURSADataset> load(int first, int pageSize,
			List<SortMeta> multiSortMeta, Map<String, Object> filters) {

		// Setup
		CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();

		// Count Query
		CriteriaQuery<Tuple> cqCount = cb.createTupleQuery();

		Root<NURSADataset> datasetRootCount = cqCount.from(NURSADataset.class);
		cqCount.multiselect(datasetRootCount.get(NURSADataset_.type),
				cb.countDistinct(datasetRootCount));
		cqCount.where(createRestrictions(cb, datasetRootCount));
		cqCount.groupBy(datasetRootCount.get(NURSADataset_.type));

		int total = 0;
		datasetBreakdown = new HashMap<String, Integer>();
		for (Tuple tuple : objectEntityManager.createQuery(cqCount)
				.getResultList()) {
			datasetBreakdown.put((String) tuple.get(0),
					((Long) tuple.get(1)).intValue());
			total = total + ((Long) tuple.get(1)).intValue();
		}
		this.setRowCount(total);

		// Result Page Query with order

		CriteriaQuery<NURSADataset> cQuery = cb
				.createQuery(NURSADataset.class);
		Root<NURSADataset> datasetRoot = cQuery.from(NURSADataset.class);

		// Order
		List<Order> sortOrder = new ArrayList<Order>();

		// Defaults to initial sort
		if ((multiSortMeta == null) && (this.storedSort == null)) {
			multiSortMeta = createInitialSort();
		} else if ((multiSortMeta == null) && (this.storedSort != null)) {
			multiSortMeta = this.storedSort;
		}
		this.storedSort = multiSortMeta;

		boolean makeDistinct = true;
		for (SortMeta sm : multiSortMeta) {
			Order newOrder = createOrder(sm, datasetRoot, cb);
			if ((makeDistinct) && (sm.getSortField() != null) && (sm.getSortField().contains("."))) {
				makeDistinct = false;
			}
			if (newOrder != null) {
				sortOrder.add(newOrder);
			}
		}

		if (makeDistinct) {
			cQuery.distinct(true);
		}
		

		if (sortOrder.size() > 0) {
			cQuery.orderBy(sortOrder);
		}

		cQuery.where(createRestrictions(cb, datasetRoot));
		
		cQuery.select(datasetRoot);

		// Restrict returns
		TypedQuery<NURSADataset> returnQuery = objectEntityManager
				.createQuery(cQuery);
		returnQuery.setFirstResult(first);
		returnQuery.setMaxResults(pageSize);

		/*
		 * omnifaces to set the rowcount in the molFilter since it winds up
		 * being a step behind otherwise
		 */
		String qPCRTotal = "0";
		String affinityPurification = "0";
		String transcriptomicTotal = "0";
		String cHIPCHIP = "0";

		if (datasetBreakdown.containsKey("Q-PCR")) {
			qPCRTotal = datasetBreakdown.get("Q-PCR").toString();
		}
		if (datasetBreakdown.containsKey("Affinity Purification")) {
			affinityPurification = datasetBreakdown.get("Affinity Purification").toString();
		}
		if (datasetBreakdown.containsKey("ChIP-ChIP")) {
			cHIPCHIP = datasetBreakdown.get("ChIP-ChIP").toString();
		}
		if (datasetBreakdown.containsKey("Transcriptomic")) {
            transcriptomicTotal = datasetBreakdown.get("Transcriptomic").toString();
		}
		
		
		Ajax.oncomplete("$('#datasetTotal').text(' " + this.getRowCount() + "');");
		
		Ajax.oncomplete("$('#qPCRTotal').text(' " + qPCRTotal + "')");
		Ajax.oncomplete("$('#affinityPurificationTotal').text(' " + affinityPurification + "')");
		Ajax.oncomplete("$('#transcriptomicTotal').text(' " + transcriptomicTotal + "')");
		Ajax.oncomplete("$('#cHIPCHIPTotal').text(' " + cHIPCHIP + "')");
		
		return returnQuery.getResultList();

	}

	private List<SortMeta> createInitialSort() {
		List<SortMeta> initialSort = new ArrayList<SortMeta>();

		// Type
		SortMeta typeSort = new SortMeta();
		typeSort.setSortField("type");
		typeSort.setSortOrder(SortOrder.ASCENDING);
		initialSort.add(typeSort);


		// Name
		SortMeta nameSort = new SortMeta();
		nameSort.setSortField("name");
		nameSort.setSortOrder(SortOrder.ASCENDING);
		initialSort.add(nameSort);

		return initialSort;
	}
	
	private Predicate createRestrictions(CriteriaBuilder cb, Root<NURSADataset> datasetRoot) {
		Predicate restrictions = cb.conjunction();

		// Filter
		if (!this.ndsSearch.getType().isEmpty()) {
			restrictions = cb.and(
					restrictions,
					datasetRoot.get(NURSADataset_.type).in(
							this.ndsSearch.getType()));

		}
		if (!this.ndsSearch.getSpecies().isEmpty()) {
			restrictions = cb.and(restrictions,
					datasetRoot.get(NURSADataset_.species).in(this.ndsSearch.getSpecies()));
		}

		/* No more Molecules
		if (!this.ndsSearch.getMolecules().isEmpty()) {
			List<String> molDOI = new ArrayList<String>();
			
			for(MoleculeAutoSuggest mas : this.ndsSearch.getMolecules()) {
				
				if(mas != null) {
					molDOI.add(mas.getDoi());
				}
			}
			if(!molDOI.isEmpty()) {
				Join<NURSADataset, Molecule> molecule = datasetRoot.join(NURSADataset_.molecules);
				Join<Molecule, DOI> doi = molecule.join(Molecule_.doi);
				
				restrictions = cb.and(restrictions, doi.get(DOI_.doi).in(molDOI));
			}
		}*/

        restrictions = cb.and(restrictions, cb.equal(datasetRoot.get(NURSADataset_.active), true));
		
		
		return restrictions;
	}
	
	@SuppressWarnings("unchecked")
	private Order createOrder(SortMeta sm, Root<NURSADataset> datasetRoot, CriteriaBuilder cb) {
		String sortField = sm.getSortField();
		SortOrder sortOrder = sm.getSortOrder();

		if (sortField != null) {
			Path<?> path = null;
			if (!sortField.contains(".")) {
				path = datasetRoot.get(sortField);
			} else {
				path = createPath(datasetRoot, sortField);
			}
			
			@SuppressWarnings("rawtypes")
			Expression exp;
			if (path.getJavaType().equals(Integer.class)) {
				exp = (Expression<Integer>) path;
			}
			else if (path.getJavaType().equals(Date.class))
			{
				exp = (Expression<Date>) path;
			}
			else {
				exp = cb.upper((Expression<String>) path);
			}

			if (sortOrder == SortOrder.ASCENDING) {
				return cb.asc(exp);
			} else if (sortOrder == SortOrder.DESCENDING) {
				return cb.desc(exp);
			} else if (sortOrder == SortOrder.UNSORTED) {
				return null;
			}
		}
		return null;
	}
	

	private Path<?> createPath(Root<NURSADataset> root, String stringPath) {
		Path<?> finalPath = root;
		
		// generate a JPA path from the string key
		Join<?, ?> path = null;// = root.get;

		if (stringPath.contains(".")) {
			String[] parts = stringPath.split("\\.");
			path = root.join(parts[0]);
			for (int i = 1; i <= parts.length - 2; i++) {
				path = path.join(parts[i]);
			}
			finalPath = path.get(parts[parts.length - 1]);
		} else {
			finalPath = root.join(stringPath);
		}

		return finalPath;
	}

	


	@Override
	public NURSADataset getRowData(String rowKey) {
		// TODO: Add Return
		return null;

	}

	public Object getRowKey(NURSADataset object) {
		// TODO: Add Return
		return null;

	}

	public NURSADatasetSearch getNURSADatasetSearch() {
		return ndsSearch;
	}

	public void setNURSADatasetSearch(NURSADatasetSearch ndsSearch) {
		this.ndsSearch = ndsSearch;
	}

	/**
	 * @return the datasetBreakdown
	 */
	public Map<String, Integer> getDatasetBreakdown() {
		return datasetBreakdown;
	}

	/**
	 * @param datasetBreakdown the datasetBreakdown to set
	 */
	public void setDatasetBreakdown(Map<String, Integer> datasetBreakdown) {
		this.datasetBreakdown = datasetBreakdown;
	}

}

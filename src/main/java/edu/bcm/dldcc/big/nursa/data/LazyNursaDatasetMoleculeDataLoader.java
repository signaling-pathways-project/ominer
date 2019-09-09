package edu.bcm.dldcc.big.nursa.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import edu.bcm.dldcc.big.nursa.model.Molecule;
import edu.bcm.dldcc.big.nursa.model.search.DatasetMoleculeResult;
import edu.bcm.dldcc.big.nursa.model.search.DatasetMoleculeResult_;

/**
 * Lazy fetch the dataset molecules collections
 * Simply setting fetch to lazy  on Dataset Entity is not good enough for large collections such as these
 * @author mcowiti
 *
 */
public class LazyNursaDatasetMoleculeDataLoader extends LazyDataModel<DatasetMoleculeResult> {

	
	private static final long serialVersionUID = 7625203008398806177L;

	
	
	private EntityManager objectEntityManager;

	private List<SortMeta> storedSort;
	
	private Map<String, Integer> moleculeBreakdown = new HashMap<String, Integer>();
	
	private String datasetDoi;

	public LazyNursaDatasetMoleculeDataLoader() {
		super();
	}

	public LazyNursaDatasetMoleculeDataLoader(EntityManager objectEntityManager,
			String datasetDoi) {
		super();
		this.objectEntityManager = objectEntityManager;
		this.datasetDoi=datasetDoi;
	}

	@Override
	public List<DatasetMoleculeResult> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		SortMeta sm = new SortMeta();
		sm.setSortField(sortField);
		sm.setSortOrder(sortOrder);
		List<SortMeta> multiSortMeta = new ArrayList<SortMeta>();
		multiSortMeta.add(sm);
		return load(first, pageSize, multiSortMeta, filters);
	}

	public List<DatasetMoleculeResult> load(int first, int pageSize,
			List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		
		// Setup
		CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();

		//JPQL below that works from non-view entities. More difficult with Criteria API
		// return (List<Molecule>)em.createQuery("SELECT mol from NURSADataset ds JOIN ds.molecules mol where ds.name=?1").setParameter(1, dsName).getResultList();
	
		CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);
		Root<DatasetMoleculeResult> molRootCount = cqCount.from(DatasetMoleculeResult.class);
		cqCount.where(createRestrictions(cb, molRootCount));
		cqCount.select(cb.countDistinct(molRootCount));
		this.setRowCount(objectEntityManager.createQuery(cqCount).getSingleResult().intValue());
		
	
		// Result Page Query with order
		
		CriteriaQuery<DatasetMoleculeResult> cQuery = cb.createQuery(DatasetMoleculeResult.class);
		Root<DatasetMoleculeResult> molRoot = cQuery.from(DatasetMoleculeResult.class);
		
		// Order
		List<Order> sortOrder = new ArrayList<Order>();
		
		// Defaults to initial sort
		if ((multiSortMeta == null) && (this.storedSort == null)) {
			multiSortMeta = createInitialSort();
		} else if ((multiSortMeta == null) && (this.storedSort != null)) {
			multiSortMeta = this.storedSort;
		}
		this.storedSort = multiSortMeta;

		for (SortMeta sm : multiSortMeta) {
			Order newOrder = createOrder(sm, molRoot, cb);
			if (newOrder != null) {
				sortOrder.add(newOrder);
			}
		}

		if (sortOrder.size() > 0) {
			cQuery.orderBy(sortOrder);
		}
		
		cQuery.where(createRestrictions(cb, molRoot));
		cQuery.distinct(true);
		cQuery.select(molRoot);

		// Restrict returns
		Query returnQuery = objectEntityManager.createQuery(cQuery);
		returnQuery.setFirstResult(first);
		returnQuery.setMaxResults(pageSize);
		
		return returnQuery.getResultList();

	}
	
	private Predicate createRestrictions(CriteriaBuilder cb, Root<DatasetMoleculeResult> molRoot) {
		
		Predicate restrictions = cb.equal(molRoot.get(DatasetMoleculeResult_.datasetDoi), this.datasetDoi); 

		return restrictions;
	}

	private List<SortMeta> createInitialSort() {
		List<SortMeta> initialSort = new ArrayList<SortMeta>();

		// Name
		SortMeta nameSort = new SortMeta();
		nameSort.setSortField("name");
		nameSort.setSortOrder(SortOrder.ASCENDING);
		initialSort.add(nameSort);

		return initialSort;
	}

	@SuppressWarnings("unchecked")
	private Order createOrder(SortMeta sm, Root<?> root, CriteriaBuilder cb) {
		String sortField = sm.getSortField();
		SortOrder sortOrder = sm.getSortOrder();

		if (sortField != null) {
			Path<?> path = null;
			if (!sortField.contains(".")) {
				path = root.get(sortField);
			} else {
				path = root;
				for (String part : sortField.split("\\.")) {
					path = path.get(part);
				}
			}
			if (sortOrder == SortOrder.ASCENDING) {
				return cb.asc(cb.upper((Expression<String>) path));
			} else if (sortOrder == SortOrder.DESCENDING) {
				return cb.desc(cb.upper((Expression<String>) path));
			} else if (sortOrder == SortOrder.UNSORTED) {
				return null;
			}
		}
		return null;
	}

	@Override
	public DatasetMoleculeResult getRowData(String rowKey) {
		return null;

	}

	public Object getRowKey(Molecule object) {
		return null;

	}


	public Map<String, Integer> getMoleculeBreakdown() {
		return moleculeBreakdown;
	}

	public void setMoleculeBreakdown(Map<String, Integer> moleculeBreakdown) {
		this.moleculeBreakdown = moleculeBreakdown;
	}

}

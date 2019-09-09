package edu.bcm.dldcc.big.nursa.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import edu.bcm.dldcc.big.nursa.model.common.BaseAnnotation;

public abstract class AnnotationLazyLoader<T, A extends BaseAnnotation>
		extends LazyDataModel<T> {

	private static final long serialVersionUID = -4863324975145132743L;

	private EntityManager objectEntityManager;
	private A annotation;

	private List<SortMeta> storedSort;

	private Class<A> mbaClass;

	private Map<String, String> baseFilter = new HashMap<String, String>();

	private String distinctColumn;

	public AnnotationLazyLoader() {
		super();
	}

	// constructor for non-specific data
	public AnnotationLazyLoader(EntityManager objectEntityManager, A annotation) {
		super();
		this.setDistinctColumn(null);
		this.setObjectEntityManager(objectEntityManager);
		this.setAnnotation(annotation);
		mbaClass = (Class<A>) annotation.getClass();

	}

	// constructor for data with globally filtered results (not from the
	// datatable; e.g.,
	// results for a particular organization) and/or unique resultset
	// constraints
	public AnnotationLazyLoader(EntityManager objectEntityManager,
			A annotation, Map<String, String> baseFilter) {
		this(objectEntityManager, annotation);
		this.setBaseFilter(baseFilter);
	}

	public AnnotationLazyLoader(EntityManager objectEntityManager,
			A annotation, Map<String, String> baseFilter, String distinctColumn) {
		this(objectEntityManager, annotation, baseFilter);
		this.setDistinctColumn(distinctColumn);
	}

	public AnnotationLazyLoader(EntityManager objectEntityManager,
			A annotation, String distinctColumn) {
		this(objectEntityManager, annotation);
		this.setDistinctColumn(distinctColumn);
	}

	@Override
	public List<T> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		SortMeta sm = new SortMeta();
		sm.setSortField(sortField);
		sm.setSortOrder(sortOrder);
		List<SortMeta> multiSortMeta = new ArrayList<SortMeta>();
		multiSortMeta.add(sm);
		return load(first, pageSize, multiSortMeta, filters);
	}

	@Override
	public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta,
			Map<String, Object> filters) {
		return null;
	}

	protected List<T> createDistinctColumnQuery(int first, int pageSize,
			List<SortMeta> multiSortMeta, Map<String, Object> filters,
			String joinField) {
		filters.putAll(this.getBaseFilter());
		List<Long> ids = getIds(filters, joinField);

		if (!ids.isEmpty()) {
			CriteriaBuilder cb = getObjectEntityManager().getCriteriaBuilder();
			CriteriaQuery<T> criteria = (CriteriaQuery<T>) cb.createQuery();

			// Run the query to get the correct Annotations
			Root<A> annotationRoot = criteria.from(mbaClass);
			ListJoin<A, T> joinRoot = annotationRoot.joinList(joinField);

			criteria.where(joinRoot.get("id").in(ids));
			criteria.select(joinRoot);

			// Order on Query
			List<Order> sortOrder = new ArrayList<Order>();

			// Defaults to former sort
			if ((multiSortMeta == null) && (getStoredSort() != null)) {
				multiSortMeta = getStoredSort();
			}
			setStoredSort(multiSortMeta);

			boolean makeDistinct = true;
			for (SortMeta sm : multiSortMeta) {
				Order newOrder = createOrder(sm, joinRoot, cb);
				if ((makeDistinct) && (sm.getSortField() != null) && (sm.getSortField().contains("."))) {
					makeDistinct = false;
				}
				if (newOrder != null) {
					sortOrder.add(newOrder);
				}
			}

			if (makeDistinct) {
				criteria.distinct(true);
			}

			if (sortOrder.size() > 0) {
				criteria.orderBy(sortOrder);
			}

			TypedQuery<T> returnQuery = getObjectEntityManager().createQuery(
					criteria);
			returnQuery.setFirstResult(first);
			returnQuery.setMaxResults(pageSize);

			List<T> results = returnQuery.getResultList();

			this.setRowCount(ids.size());

			// Run Query for total result numbers
			return results;
		} else {
			this.setRowCount(0);
			return new ArrayList<T>();
		}

	}

	private List<Long> getIds(Map<String, Object> filters, String joinField) {

		CriteriaBuilder cb = getObjectEntityManager().getCriteriaBuilder();

		CriteriaQuery<Long> ids = cb.createQuery(Long.class);
		Root<A> annotationRoot = ids.from(mbaClass);
		ListJoin<A, T> joinRoot = annotationRoot.joinList(joinField);

		ids.select(cb.greatest(joinRoot.get("id").as(Long.class)));

		Predicate restrictions = cb.conjunction();
		restrictions = cb.and(restrictions,
				createGlobalFilter(filters, joinRoot, cb));
		restrictions = cb.and(restrictions,
				cb.equal(annotationRoot, annotation));

		ids.where(restrictions);

		ids.groupBy(createPath(joinRoot, this.getDistinctColumn()));

		return getObjectEntityManager().createQuery(ids).getResultList();
	}

	protected List<T> createQuery(int first, int pageSize,
			List<SortMeta> multiSortMeta, Map<String, Object> filters,
			String joinField) {
		// Setup
		CriteriaBuilder cb = getObjectEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> criteria = (CriteriaQuery<T>) cb.createQuery();
		Root<A> annotationRoot = criteria.from(mbaClass);
		ListJoin<A, T> joinRoot = annotationRoot.joinList(joinField);
		Predicate restrictions = cb.conjunction();

		// Filter
		filters.putAll(this.getBaseFilter());
		restrictions = cb.and(restrictions,
				createGlobalFilter(filters, joinRoot, cb));

		restrictions = cb.and(restrictions,
				cb.equal(annotationRoot, annotation));

		// Order
		List<Order> sortOrder = new ArrayList<Order>();

		// Defaults to former sort
		if ((multiSortMeta == null) && (getStoredSort() != null)) {
			multiSortMeta = getStoredSort();
		}
		setStoredSort(multiSortMeta);

		for (SortMeta sm : multiSortMeta) {
			Order newOrder = createOrder(sm, joinRoot, cb);
			if (newOrder != null) {
				sortOrder.add(newOrder);
			}
		}

		if (sortOrder.size() > 0) {
			criteria.orderBy(sortOrder);
		}

		// Query creation
		criteria.where(restrictions);
		criteria.select(joinRoot);

		// Restrict Returns
		TypedQuery<T> returnQuery = getObjectEntityManager().createQuery(
				criteria);
		returnQuery.setFirstResult(first);
		returnQuery.setMaxResults(pageSize);

		List<T> results = returnQuery.getResultList();

		// Result Size
		CriteriaQuery<Long> cqSize = cb.createQuery(Long.class);
		Root<A> returnSizeRoot = cqSize.from(mbaClass);
		ListJoin<A, T> joinSizeRoot = returnSizeRoot.joinList(joinField);
		Predicate restrictions2 = cb.conjunction();

		filters.putAll(this.getBaseFilter());
		restrictions2 = cb.and(restrictions,
				createGlobalFilter(filters, joinSizeRoot, cb));

		restrictions2 = cb.and(restrictions2,
				cb.equal(annotationRoot, annotation));
		cqSize.where(restrictions2);
		cqSize.select(cb.count(joinSizeRoot));
		this.setRowCount(objectEntityManager.createQuery(cqSize)
				.getSingleResult().intValue());

		// Return results
		return results;
	}

	private Order createOrder(SortMeta sm, ListJoin<A, T> root,
			CriteriaBuilder cb) {
		String sortField = sm.getSortField();
		SortOrder sortOrder = sm.getSortOrder();

		if (sortField != null) {

			Path<?> path = null;

			if (!sortField.contains(".")) {
				path = root.get(sortField);
			} else {
				path = createPath(root, sortField);
			}
			Expression exp;
			if (path.getJavaType().equals(Integer.class)) {
				exp = (Expression<Integer>) path;
			} else {
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

	private Path<?> createPath(ListJoin<A, T> root, String stringPath) {
		Path<?> finalPath = root;
		// generate a JPA path from the string key
		Join<?, ?> path = root;

		if (stringPath.contains(".")) {
			String[] parts = stringPath.split("\\.");
			for (int i = 0; i <= parts.length - 2; i++) {
				path = path.join(parts[i]);
			}
			finalPath = path.get(parts[parts.length - 1]);
		} else {
			finalPath = path.get(stringPath);
		}

		return finalPath;
	}

	/**
	 * @author CW
	 * 
	 *         makes a list of Predicates that will be applied to every query
	 *         run in order to filter results globally, e.g., a list of mRNAs
	 *         for a particular Organization
	 * 
	 * @param filters
	 *            K = String path to the field in question V = the value we're
	 *            looking for
	 * @param root
	 *            the root of each JPA predicate
	 * @return the restrictions
	 */
	private Predicate createGlobalFilter(Map<String, Object> filters,
			ListJoin<A, T> root, CriteriaBuilder cb) {

		Predicate restrictions = cb.conjunction();

		// make sure we have filters
		if (!filters.isEmpty()) {
			Path<?> finalPath = root;

			// iterate through the keys of the map and create predicates
			for (String stringPath : filters.keySet()) {

				finalPath = createPath(root, stringPath);

				// now make a predicate with that path and associated theFilters
				// value
				restrictions = cb.and(restrictions,
						cb.equal(finalPath, filters.get(stringPath)));
			}

		}

		return restrictions;

	}

	// create any default

	@Override
	public T getRowData(String rowKey) {
		// TODO: Add Return
		return null;

	}

	public Object getRowKey(T object) {
		// TODO: Add Return
		return null;

	}

	public EntityManager getObjectEntityManager() {
		return objectEntityManager;
	}

	public void setObjectEntityManager(EntityManager objectEntityManager) {
		this.objectEntityManager = objectEntityManager;
	}

	public A getAnnotation() {
		return annotation;
	}

	public void setAnnotation(A annotation) {
		this.annotation = annotation;
	}

	public List<SortMeta> getStoredSort() {
		return storedSort;
	}

	public void setStoredSort(List<SortMeta> storedSort) {
		this.storedSort = storedSort;
	}

	public Map<String, String> getBaseFilter() {
		return baseFilter;
	}

	public void setBaseFilter(Map<String, String> baseFilter) {
		this.baseFilter = baseFilter;
	}

	public String getDistinctColumn() {
		return distinctColumn;
	}

	public void setDistinctColumn(String distinctColumn) {
		this.distinctColumn = distinctColumn;
	}
}

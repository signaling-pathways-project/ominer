package edu.bcm.dldcc.big.nursa.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.primefaces.model.chart.PieChartModel;

import edu.bcm.dldcc.big.nursa.model.Molecule;
import edu.bcm.dldcc.big.nursa.model.Molecule_;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;

@ApplicationScoped
@Named("NURSAInformationService")
public class NursaInformation {

	//Pie Chart Models
	private PieChartModel moleculeBreakdown;
	private PieChartModel reagentBreakdown;
	private PieChartModel translationalBreakdown;
	
	// map of mol breakdowns
	private Map<String, Long> moleculeBreakdownMap;
	private Map<String, Long> reagentBreakdownMap;
	private Map<String, Long> translationalBreakdownMap;

	// total of mols
	private Long moleculeTotal;
	private Long reagentTotal;
	private Long translationalTotal;
	
	// Types
	
	private List<String> moleculeTypes = new ArrayList<String>();
	private List<String> reagentTypes = new ArrayList<String>();
	private List<String> translationalTypes = new ArrayList<String>();

	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;

	@PostConstruct
	public void updateAll() {
		updateMolecules();
		//updateTranslational();
	}

	public void updateMolecules() {
		// init vars
		setMoleculeBreakdown(new PieChartModel());
		setMoleculeBreakdownMap(new HashMap<String, Long>());
		setMoleculeTotal(0L);

		CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();

		CriteriaQuery<Tuple> cq = cb.createTupleQuery();

		// get data
		Root<Molecule> moleculeRoot = cq.from(Molecule.class);
		cq.multiselect(moleculeRoot.get(Molecule_.type), cb.count(moleculeRoot));
		cq.groupBy(moleculeRoot.get(Molecule_.type));

		// assign it to our return variables
		// getMoleculeBreakdown is called instead of setXXX because we need to
		// call
		// a method on the underlying object to set the value on it
		for (Tuple tuple : objectEntityManager.createQuery(cq).getResultList()) {
			getMoleculeBreakdown().set((String) tuple.get(0),
					(Long) tuple.get(1));
			
			getMoleculeBreakdownMap().put((String) tuple.get(0),
					(Long) tuple.get(1));
			
			setMoleculeTotal(getMoleculeTotal() + (Long) tuple.get(1));
			getMoleculeTypes().add((String) tuple.get(0));
		}
		
		Collections.sort(this.moleculeTypes);
	}





	public PieChartModel getMoleculeBreakdown() {
		return moleculeBreakdown;
	}

	public void setMoleculeBreakdown(PieChartModel moleculeBreakdown) {
		this.moleculeBreakdown = moleculeBreakdown;
	}

	public PieChartModel getReagentBreakdown() {
		return reagentBreakdown;
	}

	public void setReagentBreakdown(PieChartModel reagentBreakdown) {
		this.reagentBreakdown = reagentBreakdown;
	}

	public PieChartModel getTranslationalBreakdown() {
		return translationalBreakdown;
	}

	public void setTranslationalBreakdown(PieChartModel translationalBreakdown) {
		this.translationalBreakdown = translationalBreakdown;
	}

	public Map<String, Long> getMoleculeBreakdownMap() {
		return moleculeBreakdownMap;
	}

	public void setMoleculeBreakdownMap(Map<String, Long> moleculeBreakdownMap) {
		this.moleculeBreakdownMap = moleculeBreakdownMap;
	}

	public Map<String, Long> getReagentBreakdownMap() {
		return reagentBreakdownMap;
	}

	public void setReagentBreakdownMap(Map<String, Long> reagentBreakdownMap) {
		this.reagentBreakdownMap = reagentBreakdownMap;
	}

	public Long getMoleculeTotal() {
		return moleculeTotal;
	}

	public void setMoleculeTotal(Long moleculeTotal) {
		this.moleculeTotal = moleculeTotal;
	}

	public Long getReagentTotal() {
		return reagentTotal;
	}

	public void setReagentTotal(Long reagentTotal) {
		this.reagentTotal = reagentTotal;
	}

	

	/**
	 * @return the translationalTotal
	 */
	public Long getTranslationalTotal() {
		return translationalTotal;
	}

	/**
	 * @param translationalTotal the translationalTotal to set
	 */
	public void setTranslationalTotal(Long translationalTotal) {
		this.translationalTotal = translationalTotal;
	}

	/**
	 * @return the moleculeTypes
	 */
	public List<String> getMoleculeTypes() {
		return moleculeTypes;
	}

	/**
	 * @param moleculeTypes the moleculeTypes to set
	 */
	public void setMoleculeTypes(List<String> moleculeTypes) {
		this.moleculeTypes = moleculeTypes;
	}

	/**
	 * @return the reagentTypes
	 */
	public List<String> getReagentTypes() {
		return reagentTypes;
	}

	/**
	 * @param reagentTypes the reagentTypes to set
	 */
	public void setReagentTypes(List<String> reagentTypes) {
		this.reagentTypes = reagentTypes;
	}

	/**
	 * @return the translationalTypes
	 */
	public List<String> getTranslationalTypes() {
		return translationalTypes;
	}

	/**
	 * @param translationalTypes the translationalTypes to set
	 */
	public void setTranslationalTypes(List<String> translationalTypes) {
		this.translationalTypes = translationalTypes;
	}

	public Map<String, Long> getTranslationalBreakdownMap() {
		return translationalBreakdownMap;
	}

	public void setTranslationalBreakdownMap(
			Map<String, Long> translationalBreakdownMap) {
		this.translationalBreakdownMap = translationalBreakdownMap;
	}

}

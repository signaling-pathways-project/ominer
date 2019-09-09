package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author mcowiti
 *
 */
public  class AutosuggestListImpl<T> implements AutosuggestList<T>{
	
	private List<T> exactList= new ArrayList<T>(0);
	private List<T> otherList= new ArrayList<T>(0);
	
	public AutosuggestListImpl(){
		
	}

	
	/**
	 * May be deprecated for later version of JAX-RS 
	 * We do a hack here
	 * Since default Jettison 
	 * (i)does not marshal out an empty List,
	 * (ii) marshals a single list as object
	 * If an empty list/ single list, add an empty AutoTerm Object
	 * @param exactList
	 * @param otherList
	 */
	public AutosuggestListImpl(List<T> exactList, List<T> otherList) {
		super();

		this.exactList.addAll(exactList);

		this.otherList.addAll(otherList);
		
	}

	@XmlElement(name="exact")
	public List<T> getExactList() {
		return exactList;
	}
	
	public void setExactList(List<T> exactList) {
		this.exactList = exactList;
	}
	
	@XmlElement(name="other")
	public List<T> getOtherList() {
		return otherList;
	}
	public void setOtherList(List<T> otherList) {
		this.otherList = otherList;
	}
	
}

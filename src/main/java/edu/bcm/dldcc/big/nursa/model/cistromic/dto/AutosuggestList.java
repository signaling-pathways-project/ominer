package edu.bcm.dldcc.big.nursa.model.cistromic.dto;

import java.util.List;

/**
 * Auto suggest terms DTO
 * @author mcowiti
 *
 * @param <T>
 */
public interface AutosuggestList<T> {

	public List<T> getExactList();
	public List<T> getOtherList();
}

package edu.bcm.dldcc.big.nursa.controller;


import javax.ejb.Local;

import org.primefaces.model.LazyDataModel;

import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.model.search.NURSADatasetSearch;

/**
 * The dataset search interface bean provides a framework for the
 * implementation layer and allows for further decoupleing later on.
 * 
 * 
 * @author jeremyeaston-marks
 * 
 */
@Local
public interface NURSADatasetSearchInterfaceBean {

	/**
	 * Runs a search against the database using the information in the
	 * datasetSearch object. It stores that information in the searchResults
	 * object.
	 * 
	 */
	void search();

	/**
	 * Resets the dataset search and returns a full list for search
	 * 
	 */
	void reset();

	/**
	 * Sends a redirect to the browser that will bring them to the correct page.
	 * 
	 * @param dataset The dataset to be directed to.
	 */
	void redirect(NURSADataset dataset);


	/**
	 * Returns a list of datasets that are the result of the search
	 * 
	 * @return A list of search results as datasets.
	 */
	LazyDataModel<NURSADataset> getSearchResults();

	/**
	 * Sets the list of datasets that are the results of a search
	 * 
	 * @param searchResults A list of search results as datasets
	 */
	void setSearchResults(LazyDataModel<NURSADataset> searchResults);

	/**
	 * Gets the dataset search object which contains all the search parameters.
	 * 
	 * @return The dataset search object.
	 */
	NURSADatasetSearch getNURSADatasetSearch();

	/**
	 * Sets the dataset search object which contains all the search parameters.
	 * 
	 * @param datasetSearch The dataset search object.
	 */
	void setNURSADatasetSearch(NURSADatasetSearch datasetSearch);

	NURSADatasetSearch getDatasetSearch();

	void setDatasetSearch(NURSADatasetSearch datasetSearch);
}

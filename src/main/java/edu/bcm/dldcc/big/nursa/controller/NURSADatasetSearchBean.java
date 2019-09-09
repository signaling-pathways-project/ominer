package edu.bcm.dldcc.big.nursa.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.nursa.data.LazyNURSADatasetDataLoader;
import org.omnifaces.cdi.Param;
import org.omnifaces.cdi.param.ParamValue;
import org.primefaces.model.LazyDataModel;

import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.model.search.NURSADatasetSearch;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;

@Stateful
@ViewScoped
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
@Named("datasetSearchService")
public class NURSADatasetSearchBean implements NURSADatasetSearchInterfaceBean {

	@Inject
	@Param(required = false)
	private ParamValue<String> datasetType;
	
	private NURSADatasetSearch datasetSearch = new NURSADatasetSearch();
	
	private LazyDataModel<NURSADataset> searchResults; 
	
	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;
	
	@PostConstruct
	public void init() {
		if (this.datasetType.getValue() != null) {
			this.getDatasetSearch().getType().add(this.datasetType.getValue());
		}
		searchResults = new LazyNURSADatasetDataLoader(objectEntityManager,
				this.getDatasetSearch());
	}
	
	@Override
	public void search() {

	}

	@Override
	public void reset() {
		setNURSADatasetSearch(new NURSADatasetSearch());
		init();
	}

	@Override
	public void redirect(NURSADataset dataset) {
		try {

			// doi.url should be absolute
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							FacesContext.getCurrentInstance()
									.getExternalContext()
									.getRequestContextPath()
									+ (dataset.getDoi().getUrl().charAt(0) == '/' ? dataset
											.getDoi().getUrl() : "/"
											+ dataset.getDoi().getUrl()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	@Override
	public LazyDataModel<NURSADataset> getSearchResults() {
		return searchResults;
	}

	@Override
	public void setSearchResults(LazyDataModel<NURSADataset> searchResults) {
		this.searchResults = searchResults;
	}

	@Override
	public NURSADatasetSearch getNURSADatasetSearch() {
		return this.getDatasetSearch();
	}

	@Override
	public void setNURSADatasetSearch(NURSADatasetSearch datasetSearch) {
		this.setDatasetSearch(datasetSearch);

	}

	/**
	 * @return the datasetSearch
	 */
	@Override
	public NURSADatasetSearch getDatasetSearch() {
		return datasetSearch;
	}

	/**
	 * @param datasetSearch the datasetSearch to set
	 */
	@Override
	public void setDatasetSearch(NURSADatasetSearch datasetSearch) {
		this.datasetSearch = datasetSearch;
	}
}

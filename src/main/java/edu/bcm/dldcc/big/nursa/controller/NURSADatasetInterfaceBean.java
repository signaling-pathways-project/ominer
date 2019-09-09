package edu.bcm.dldcc.big.nursa.controller;

import edu.bcm.dldcc.big.nursa.model.omics.Experiment;
import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.model.omics.RelatedDatasetBy;
import edu.bcm.dldcc.big.nursa.model.omics.dto.NursaDatasetDTO;
import edu.bcm.dldcc.big.nursa.model.search.DatasetMoleculeResult;
import edu.bcm.dldcc.big.nursa.model.search.DatasetReagentResult;

import org.primefaces.model.LazyDataModel;

import javax.ejb.Local;
import java.util.List;

@Local
public interface NURSADatasetInterfaceBean {

	void updateSelectedDataset();
	
	NURSADataset getSelectedDataset();
	
	void setSelectedDataset(NURSADataset selectedDataset);
	
	//LazyDataModel<DatasetMoleculeResult> getMoleculeSearchResults();
	//void setMoleculeSearchResults(LazyDataModel<DatasetMoleculeResult> searchResults);

	//LazyDataModel<DatasetReagentResult> getReagentResults();
	//void setReagentResults(LazyDataModel<DatasetReagentResult> searchResults);

	public Experiment getSelectedExperiment();
	public void setSelectedExperiment(Experiment experiment);
	
	public RelatedDatasetBy getSelectedRelatedBy();
	public void setSelectedRelatedBy(RelatedDatasetBy selectedRelatedBy);
	public void searchRelatedDatasets();
	public List<NursaDatasetDTO> getRelatedDatasets();
	public RelatedDatasetBy[] getRelatedDatasetBy();

    public String getElsevierUrl();
}

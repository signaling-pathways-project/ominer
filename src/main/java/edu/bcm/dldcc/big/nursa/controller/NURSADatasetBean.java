package edu.bcm.dldcc.big.nursa.controller;

import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.model.common.DOI_;
import edu.bcm.dldcc.big.nursa.model.omics.*;
import edu.bcm.dldcc.big.nursa.model.omics.dto.NursaDatasetDTO;
import edu.bcm.dldcc.big.nursa.services.PubmedAbstractBean;
import edu.bcm.dldcc.big.nursa.services.rest.consumers.NcbiServicesBean;
import edu.bcm.dldcc.big.nursa.services.rest.omics.DatasetServiceBean;
import edu.bcm.dldcc.big.nursa.util.ReferenceUtil;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;
import org.omnifaces.cdi.Param;
import org.omnifaces.cdi.param.ParamValue;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateful
@ViewScoped
@Named("datasetService")
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class NURSADatasetBean implements NURSADatasetInterfaceBean {

	private static Logger log = Logger.getLogger(NURSADatasetBean.class.getName());

	@Inject
	@Param(required = true)
	private ParamValue<String> doi;

	private NURSADataset selectedDataset;
	
	private Experiment selectedExperiment;
	
	private static final String DX_DOI_URL="http://dx.doi.org/";


	@Inject
	@UserDatabase
	private EntityManager objectEntityManager;
	
	@Inject 
	private NursaDatasetRelatedDatasetBean nursaDatasetBean;

	@Inject
	private NcbiServicesBean ncbiServicesBean;

	@Inject
    private DatasetServiceBean datasetServiceBean;

    @Inject 
    private ReferenceUtil referenceUtil;
    
	private static final String DATASET_TYPE_TRANSCRIPTOMIC="Transcriptomic";

	@Inject private PubmedAbstractBean pubmedAbstractBean;

	private RelatedDatasetBy selectedRelatedBy;
	private List<NursaDatasetDTO> relatedDatasets= new ArrayList<NursaDatasetDTO>();

	@Override
	@PostConstruct
	public void updateSelectedDataset() {
		//!Turn off conversations. org.jboss.seam.faces.conversion.ObjectConverter need them (and idle monitor??)

		long b=System.currentTimeMillis();
		//List<NURSADataset> results=findInitialDataset();
		NURSADataset results=datasetServiceBean.findInitialDataset(objectEntityManager,this.doi.getValue());

		// no result found
		if (results == null) {//
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"No datasets was found with that DOI", "");
			FacesContext.getCurrentInstance().addMessage(null, message);
			log.log(Level.SEVERE,"Should not happen: No datasets was found with that DOI="+this.doi.getValue());
			setSelectedDataset(null);
		}else {
			setSelectedDataset(results);
			referenceUtil.setReference(results.getReference());
			initializeFirstExperiment();

			if (results.getReference()!= null) {
				this.selectedDataset.getReferences().add(results.getReference());
			}
		}
	}

	public void initializeFirstExperiment(){
		//FIXME TEMP solution
		List<Experiment> activeExperiments=this.selectedDataset.getActiveExperiments();
		if(activeExperiments.size() > 0) {
			this.selectedExperiment=activeExperiments.get(0);
		}
	}

	public RelatedDatasetBy[] getRelatedDatasetBy(){
		return RelatedDatasetBy.values();
	}


    @Override
    public String getElsevierUrl() {
        //log.log(Level.INFO,"@getElsevierUrl for 1)pmid= "+selectedDataset.getReference().getPubmedId());

        String pubmed = (selectedDataset.getReferences()!=null && selectedDataset.getReferences().size()!=0)?selectedDataset.getReferences().get(0).getPubmedId():null;
		//log.log(Level.INFO,"@getElsevierUrl for 2) pmid= "+pubmed);
		if (pubmed != null) {
           String result = ncbiServicesBean.pubmedToElsevierDoi(pubmed);
           return null != result ? DX_DOI_URL+result  : null ;
        }
        return null;
    }

    public void searchRelatedDatasets(){
		if(this.selectedDataset !=null)
		{
				if(this.selectedDataset.getDatasetType().getExperimentType().getName().equalsIgnoreCase(DATASET_TYPE_TRANSCRIPTOMIC))
				{
					relatedDatasets= this.nursaDatasetBean.findRelatedNursaDataset
						(this.selectedRelatedBy,
								this.selectedDataset.getId(),
								new ArrayList<Long>(nursaDatasetBean.extractMoleculeIds(this.selectedDataset)),
								new ArrayList<String>(nursaDatasetBean.extractRnaSources(this.selectedDataset)),
								NursaDatasetRelatedDatasetBean.MAX_RELATED);
				}

    	}
	}
	
	public List<NursaDatasetDTO> getRelatedDatasets(){
		return relatedDatasets;
	}
	
	public RelatedDatasetBy getSelectedRelatedBy() {
		return selectedRelatedBy;
	}

	public void setSelectedRelatedBy(RelatedDatasetBy selectedRelatedBy) {
		this.selectedRelatedBy = selectedRelatedBy;
		//FIXME 2B replaced by related by family  searchRelatedDatasets();
	}


	@Override
	public NURSADataset getSelectedDataset() {
		return selectedDataset;
	}

	@Override
	public void setSelectedDataset(NURSADataset selectedDataset) {
		this.selectedDataset = selectedDataset;
		setSelectedRelatedBy(RelatedDatasetBy.REGUALTORY_MOLECULE);
	}

	public Experiment getSelectedExperiment() {
		return selectedExperiment;
	}

	public void setSelectedExperiment(Experiment selectedExperiment) {
		this.selectedExperiment = selectedExperiment;
	}

}
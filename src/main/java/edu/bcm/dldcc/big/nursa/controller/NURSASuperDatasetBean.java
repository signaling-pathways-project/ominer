package edu.bcm.dldcc.big.nursa.controller;

import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import org.omnifaces.cdi.Param;
import org.omnifaces.cdi.param.ParamValue;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alexey on 9/29/15.
 *
 * Simple controller for page in case when we have multiple Datasets assosiated with a single pubmed;
 */

@Stateful
@ViewScoped
@Named("superDatasetService")
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class NURSASuperDatasetBean  {

	private static Logger log = Logger.getLogger(NURSASuperDatasetBean.class.getName());

    @Inject
    private TranscriptomineService omineService;

    private List<NURSADataset> datasets;

    @Inject
    @Param(required = true)
    private ParamValue<String> pubmedId; // passed pubmedId

    @PostConstruct
    public void updateSelectedDataset() {
    	log.info("superDatasets pubmedId = "+pubmedId);
        datasets = omineService.findDatasetByPubmedId(pubmedId.getValue());
    }

    public List<NURSADataset> getDatasets() {
        return datasets;
    }

}

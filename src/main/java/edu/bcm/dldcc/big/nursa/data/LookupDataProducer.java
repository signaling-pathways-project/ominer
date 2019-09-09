package edu.bcm.dldcc.big.nursa.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import edu.bcm.dldcc.big.nursa.model.omics.Tissue;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;

/**
 * Producer for standard(lookup) values 
 * @author mcowiti
 *
 */
@Named("lookup")
@ApplicationScoped
public class LookupDataProducer {

	@Inject
	private TranscriptomineService omineService;

	@Produces
	@Named("allTissues")
	public List<Tissue> listAllTissues(){
		return (List<Tissue>) omineService.listTissuesAndCellLines();
	}
}

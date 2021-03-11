package edu.bcm.dldcc.big.nursa.data;

import edu.bcm.dldcc.big.nursa.model.common.MoleculeAutoSuggest;
import edu.bcm.dldcc.big.nursa.model.common.TranslationalAutoSuggest;
import edu.bcm.dldcc.big.nursa.model.core.GOTerm;
import edu.bcm.dldcc.big.nursa.model.core.Gene;
import edu.bcm.dldcc.big.nursa.model.omics.Tissue;
import edu.bcm.dldcc.big.nursa.model.omics.dto.AutosuggestList;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * TM Query Parameter auto suggester.
 * Currently only Gene, Molecule and Tissue  auto suggest is provided. 
 * @author mcowiti
 *
 */
@RequestScoped
@Named("tmAutoSuggest")
public class QueryTermAutoSuggest {

	@Inject
	private TranscriptomineService omineService;
	
	public AutosuggestList autoSuggestTissues(String inputValue){
		return  omineService.autoSuggestList(Tissue.class, inputValue, TranscriptomineService.MAX_NUMBER_AUTOSUGGEST);
	}
	
	/**
	 * The Gene auto suggest behaves  similar to existing Molecule auto suggest 
	 * @see {edu.bcm.dldcc.big.nursa.data.common.MoleculeSynonymListProducer#completeSymbol}
	 * The  auto suggested term is SYNONYM(HUGO SYMBOL)
	 * @param inputValue
	 * @return
	 */
	public AutosuggestList autoSuggestGenes(String inputValue)
    {
		return omineService.autoSuggestList(Gene.class, inputValue, TranscriptomineService.MAX_NUMBER_AUTOSUGGEST);
	}
	
	/**
	 * The Molecule auto complete @see {edu.bcm.dldcc.big.nursa.data.common.MoleculeSynonymListProducer#completeSymbol}
	 * The auto suggested term is SYNONYM(OFFICIAL NURSA SYMBOL)
	 * @param inputValue
	 * @return
	 */
	public AutosuggestList autoSuggestMolecules(String inputValue)
    {
		return omineService.autoSuggestList(MoleculeAutoSuggest.class, inputValue, TranscriptomineService.MAX_NUMBER_AUTOSUGGEST);
	}

    public AutosuggestList autoSugestGoTerm(String query)
    {
        return omineService.autoSuggestList(GOTerm.class, query, TranscriptomineService.MAX_NUMBER_AUTOSUGGEST);
    }

    public AutosuggestList autoSuggestDisease(String query)
    {
        return omineService.autoSuggestList(TranslationalAutoSuggest.class, query, TranscriptomineService.MAX_NUMBER_AUTOSUGGEST);
    }
}

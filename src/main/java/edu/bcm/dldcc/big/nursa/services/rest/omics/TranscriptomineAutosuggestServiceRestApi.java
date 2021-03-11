package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.common.MoleculeAutoSuggest;
import edu.bcm.dldcc.big.nursa.model.common.TranslationalAutoSuggest;
import edu.bcm.dldcc.big.nursa.model.core.GOTerm;
import edu.bcm.dldcc.big.nursa.model.core.Gene;
import edu.bcm.dldcc.big.nursa.model.omics.Tissue;
import edu.bcm.dldcc.big.nursa.model.omics.dto.AutosuggestList;
import edu.bcm.dldcc.big.nursa.services.utils.AutosuggestHelperBean;

import javax.inject.Inject;

/**
 * Auto suggest REST implementation class
 * @author mcowiti
 *
 */
public class TranscriptomineAutosuggestServiceRestApi implements TranscriptomineAutosuggestService {


    //@Inject private TranscriptomineService omineService;
    @Inject
    private AutosuggestHelperBean autosuggestHelperBean;

	public AutosuggestList<?> autoCompleteByGene(String symbol, Integer count, Integer taxonId, Integer synonym)
    {
        //was omineService.
		return autoSuggestList(Gene.class, symbol, MAX_NUMBER_AUTOSUGGEST);
	}

	public AutosuggestList<?> autoCompleteMolecule(String symbol, String count, Integer synonym)
    {
        return autoSuggestList(MoleculeAutoSuggest.class, symbol, MAX_NUMBER_AUTOSUGGEST);
	}

	public AutosuggestList<?> autoCompleteByGoTerm(String symbol, Integer count, Integer taxonId)
    {
        return autoSuggestList(GOTerm.class, symbol, MAX_NUMBER_AUTOSUGGEST);
	}

	public AutosuggestList<?> autoCompleteByDisease(String symbol, Integer count, Integer synonym)
    {
        return autoSuggestList(TranslationalAutoSuggest.class, symbol, MAX_NUMBER_AUTOSUGGEST);
	}

	public AutosuggestList<?> autoCompleteTissueAndCellLines(String symbol, Integer count, Integer taxonId)
    {
        return autoSuggestList(Tissue.class, symbol, MAX_NUMBER_AUTOSUGGEST);
	}


    public <T> AutosuggestList autoSuggestList(Class<T> type, String value, Integer max) {

        if (type.equals(GOTerm.class))
            return autosuggestHelperBean.autoSuggestGOTerm(value, max);
        else if (type.equals(MoleculeAutoSuggest.class))
            return autosuggestHelperBean.autoSuggestMolecule(value,max);
        else if (type.equals(Gene.class))
            return autosuggestHelperBean.autoSuggestGene(value, max);
        else if (type.equals(TranslationalAutoSuggest.class))
            return autosuggestHelperBean.autoSuggestDisease(value, max);
        else if (type.equals(Tissue.class))
            return autosuggestHelperBean.autoSuggesTissue(value, max);

        return null;
    }

}

package edu.bcm.dldcc.big.nursa.services;

import java.io.Serializable;

import javax.ejb.Local;

import edu.bcm.dldcc.big.nursa.model.omics.TranscriptomineApiKey;

/**
 * API Access service
 * @author mcowiti
 *
 */
@Local
public interface ApiAccessService extends Serializable {

	public static final String API_KEY_PARAM="apiKey";
	
	 public TranscriptomineApiKey isValidApiKey(String key);
	public void updateAccountAccess(TranscriptomineApiKey transcriptomineApiKey);
	public boolean mayMakeNewAPICall(TranscriptomineApiKey apiKey);
	
}

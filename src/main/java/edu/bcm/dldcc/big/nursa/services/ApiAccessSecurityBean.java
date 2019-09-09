package edu.bcm.dldcc.big.nursa.services;

import java.io.Serializable;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import edu.bcm.dldcc.big.nursa.model.omics.TranscriptomineApiKey;

/**
 * Authenticate API access, keep statistics
 * @author mcowiti
 *
 */

@Stateless
public class ApiAccessSecurityBean implements ApiAccessService, Serializable {

	private static final long serialVersionUID = 2441267842532542969L;
	
	@PersistenceContext(unitName = "NURSA")
	private EntityManager em;
	
	private static Logger log = Logger.getLogger("edu.bcm.dldcc.big.nursa.services.ApiAccessSecurityBean");
	 
	
	private final static int ONE_SEC=1000; //can't make more calls in shorter than 1 second
	private final static int MAX_CALLS_PER_SEC_ALLOWED=3;
	
	 public TranscriptomineApiKey isValidApiKey(String key)
	  {
		 
		 final TranscriptomineApiKey account= em.find(TranscriptomineApiKey.class, key);
		 if(account !=null && account.isActive())
			 return account;
		 return null;
		 
		}
	 
	 /**
	  * Policy:allow max 3 calls per second
	  */
	 public boolean mayMakeNewAPICall(TranscriptomineApiKey apiKey){
			
			DateTime lastCall=new DateTime(apiKey.getLastCall());
			Duration duration = new Duration(lastCall,DateTime.now());
			log.log(Level.FINE,"Last API call at "+lastCall.toString() +" duration delta so far(s)="+duration.toStandardSeconds()+" #calls="+apiKey.getNumberOfCalls());
			
			apiKey.setLastCall(DateTime.now().toDate());
			if( duration.isShorterThan(new Duration(ONE_SEC)))
			{ 
				if(apiKey.getNumberOfCalls().intValue() <= MAX_CALLS_PER_SEC_ALLOWED)
				{
					apiKey.setNumberOfCalls(new Long(apiKey.getNumberOfCalls().intValue()+1));
				}else
				{
					apiKey.setNumberOfCalls(new Long(apiKey.getNumberOfCalls().intValue()+1));
					return false;
				}
			}else{
				apiKey.setNumberOfCalls(new Long(1));
			}
			return true;
		}
	 
	 /**
	  * Update API Key statistics
	  * @param transcriptomineApiKey
	  */
	 public void updateAccountAccess(TranscriptomineApiKey transcriptomineApiKey){
		 TranscriptomineApiKey acct=em.find(TranscriptomineApiKey.class, transcriptomineApiKey.getId());
		 em.merge(acct);
	 }
	 
}

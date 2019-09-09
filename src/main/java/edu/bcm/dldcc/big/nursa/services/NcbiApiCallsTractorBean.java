package edu.bcm.dldcc.big.nursa.services;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Singleton Bean manages data rate for All NCBI API calls
 * NCBI limits concurrent calls to a max of 3 calls per second
 * May need have external server do this, eg ngx
 * @author mcowiti
 *
 */
@Singleton
@ApplicationScoped
@Startup
@Named
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class NcbiApiCallsTractorBean implements Serializable{

	private static final long serialVersionUID = -5766285082067152233L;
	
	private static Logger log = Logger.getLogger("edu.bcm.dldcc.big.nursa.util.restapi.client.NcbiApiCallsTractorBean");
	 
	
	private final static int PER_SEC=1000;
	private final static int MAX_CALLS_PER_SEC=3;
	
	private Integer count=0;
	private DateTime lastCallTime= new DateTime();
	
	public Integer getCount() {
		return count;
	}
	@Lock(LockType.WRITE)
	public void setCount(Integer count) {
		this.count = count;
	}
	public DateTime getLastCallTime() {
		return lastCallTime;
	}
	@Lock(LockType.WRITE)
	public void setLastCallTime(DateTime lastCallTime) {
		this.lastCallTime = lastCallTime;
	}
	
	public boolean mayMakeNewNCPIAPICall(){
		
		DateTime lastCall=this.getLastCallTime();
		Duration duration = new Duration(lastCall,DateTime.now());
		log.log(Level.FINE,"Last NCBP pubmed API call at "+lastCall.toString() +" duration delata so far(s)="+duration.toStandardSeconds()+" #calls="+this.count);
		
		if( duration.isShorterThan(new Duration(PER_SEC)))
		{ 
			if(this.count < MAX_CALLS_PER_SEC)
			{
				this.count=this.count+1;
			}else
			{
				return false;
			}
		}else{
			this.setLastCallTime(DateTime.now());
			this.setCount(1);
		}
		return true;
	}
	
}

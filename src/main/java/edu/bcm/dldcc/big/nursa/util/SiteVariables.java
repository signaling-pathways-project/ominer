package edu.bcm.dldcc.big.nursa.util;

import java.util.Date;

import org.joda.time.DateTime;


/**
 * 
 * @author cw
 *
 *class to hold variables that will be used across the application
 *
 */
public class SiteVariables {
	
	/**
	 * sorted list of species taxon ids for displaying orthologs
	 * human, mouse, rat, fly
	 */
	public static final Long[] ORTHOLOGS = {9606L,10090L,10116L,7227L};	
	
	/* apollo minus fly */
	public static final Long[] SOME_ORTHOLOGS = {9606L,10090L,10116L};	
	
	
	/**
	 * last updated
	 */
	public static final Date LAST_UPDATED = new DateTime(2014,1,31,0,0).toCalendar(null).getTime();
}

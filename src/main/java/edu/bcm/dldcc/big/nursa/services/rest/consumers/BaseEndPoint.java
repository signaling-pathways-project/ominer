package edu.bcm.dldcc.big.nursa.services.rest.consumers;

/**
 * 
 * @author mcowiti
 *
 */
public abstract class BaseEndPoint {

	
	/**
	 * The REST URL ensures id is string in 0-9 range 
	 *  Pro-actively protect against too large a value
	 * @param id
	 * @return
	 */
	  boolean isNumericPubmed(String id){
		   
		   try{
			   Integer.parseInt(id);
		   }catch (Exception e){
			   return false;
		   }
		   return true;
	   }
	  
	   
}

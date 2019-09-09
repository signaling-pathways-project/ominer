package edu.bcm.dldcc.big.nursa.util.filter;

/**
 * Base Filter
 * NURSA DOI filter rules
 * NB: Modern DOI pattern is: /^10.\d{4,9}/[-._;()/:A-Z0-9]+$/i
 * Nursa own Pattern :/^10.1621/[-./:a-zA-Z0-9]+$/
 * Nursa cfm (molType,molid,gene) Pattern: /^[-a-zA-Z0-9]+$/
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseFilter {

	private static final int MAX_PARAM_SIZE=128; 
	private static final int MAX_API_PARAM_SIZE=256;
	private static final int MAX_HALF_DOI_PARAM_SIZE=12;
    private static final int MAX_BSMID_PARAM_SIZE=24;

	//we limit Pattern  to our own set 
	Pattern p = Pattern.compile("^10.1621/[-./:a-zA-Z0-9]+$");
    Pattern pHalf = Pattern.compile("^[-./:a-zA-Z0-9]+$");
	//old cfm molId, molType and gene params, allow only alpha numeric 
	Pattern cfmP = Pattern.compile("^[-a-zA-Z0-9]+$");
	
	Pattern apiAllow=Pattern.compile("^[ A-Za-z0-9_@./#&+-]*$");
	Pattern pmidPattern = Pattern.compile("^[0-9]+$");
    Pattern bsmPattern = Pattern.compile("^[a-zA-Z0-9]+$");
	Pattern biosamplePattern = Pattern.compile("^[a-zA-Z ]+$");

    Pattern goIdPattern = Pattern.compile("^GO:[0-9]+$");
    Pattern goTermPattern = Pattern.compile("^[a-zA-Z0-9: ]+$");


    public boolean isGoIDPattern(final String goId){

        Matcher m = goIdPattern.matcher(goId);
        return (m.find());
    }

    public boolean cleanGoTerm(final String goTerm){
        if (goTerm == null)
            return true;
        if(goTerm.length() > MAX_PARAM_SIZE)
            return false;

        Matcher m = goTermPattern.matcher(goTerm);
        return (m.find());
    }

	public boolean cleanDoi(final String doi){
			if (doi == null)
				return true;
			if(doi.length() > MAX_PARAM_SIZE)
				return false;
			
			Matcher m = p.matcher(doi);
			 return (m.find());
    }

	public boolean cleanPmid(final String pmid){
		if (pmid == null)
			return true;
		if(pmid.length() > MAX_HALF_DOI_PARAM_SIZE)
			return false;

		Matcher m = pmidPattern.matcher(pmid);
		return (m.find());
	}

    public boolean cleanBsmId(final String id){
        if (id == null)
            return true;
        if(id.length() > MAX_BSMID_PARAM_SIZE)
            return false;

        Matcher m = cfmP.matcher(id);
        return (m.find());
    }

    public boolean cleanBsmOrNode(final String bsmOrNode){
        if (bsmOrNode == null)
            return true;
        if(bsmOrNode.length() > MAX_HALF_DOI_PARAM_SIZE)
            return false;

        Matcher m = bsmPattern.matcher(bsmOrNode);
        return (m.find());
    }

    public boolean cleanBiosamplePieceId(final String id){
        if (id == null)
            return true;
        if(id.length() > MAX_PARAM_SIZE)
            return false;

        Matcher m = biosamplePattern.matcher(id);
        return (m.find());
    }

	public boolean cleanHalfDoi(final String doi){
		if (doi == null)
			return true;
		if(doi.length() > MAX_HALF_DOI_PARAM_SIZE)
			return false;

		Matcher m = pHalf.matcher(doi);
		return (m.find());
	}
		
		protected boolean cleanCfmParam(final String param){
			if (param == null)
				return true;
			if(param.length() > MAX_PARAM_SIZE)
				return false;
			Matcher m = cfmP.matcher(param);
			 return (m.find());
		}
		
		public boolean cleanAPIParam(final String param){
			if (param == null)
				return true;
			if(param.length() > MAX_API_PARAM_SIZE)
				return false;
			Matcher m = apiAllow.matcher(param);
			 return (m.find());
		}
}

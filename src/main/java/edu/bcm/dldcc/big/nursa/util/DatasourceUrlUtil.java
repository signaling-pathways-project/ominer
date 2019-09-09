package edu.bcm.dldcc.big.nursa.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import edu.bcm.dldcc.big.nursa.services.rest.RestApiUrlsProducer;

/**
 * Util to determine URLs
 * TODO Need to move this to RestApiUrlsProducer
 * @author mcowiti
 *
 */
@Named("urlUtil")
@ApplicationScoped
public class DatasourceUrlUtil {

	@Inject
	private RestApiUrlsProducer restApiUrlsProducer;

	String patternString = "(\\$\\{ID\\}|\\$\\{GENE.OFFICIAL.NAME\\})";
	Pattern pattern = Pattern.compile(patternString);

	
	@Produces
	public String getPubmedBaseUrl() {
		return restApiUrlsProducer.getPubmedUrl();
	}

	@Produces
	public String getPubmedAbstractBaseUrl() {
		return restApiUrlsProducer.getPubmedUrl();
	}
	
	@Produces
	public String getSraBaseUrl(){
		return restApiUrlsProducer.getSRALink();
	}

	public  String calculatedUrl(String baseUrl,String id,String name){
		Map<String,String> tokens = new HashMap<String,String>();
		tokens.put("${ID}", id);
		tokens.put("${GENE.OFFICIAL.NAME}", name);

		Matcher matcher = pattern.matcher(baseUrl);

		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
		    matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	public String calcPubmedUrl(String baseUrl,String pubmedId){
		return new StringBuffer(baseUrl).append(pubmedId).toString();
	}
}

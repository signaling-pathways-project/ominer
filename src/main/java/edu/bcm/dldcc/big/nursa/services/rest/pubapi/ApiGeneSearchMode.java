package edu.bcm.dldcc.big.nursa.services.rest.pubapi;

public enum ApiGeneSearchMode {
gene,goterm,disease;
	public static boolean isConvertible(String val){
		try{
			ApiGeneSearchMode.valueOf(val);
			return true;
		}catch (Exception e){
			return false;
		}
	}
}

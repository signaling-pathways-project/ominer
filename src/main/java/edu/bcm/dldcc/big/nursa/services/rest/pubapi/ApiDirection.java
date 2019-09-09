package edu.bcm.dldcc.big.nursa.services.rest.pubapi;

public enum ApiDirection {
any,up,down;
	public static boolean isConvertible(String val){
		try{
			ApiDirection.valueOf(val);
			return true;
		}catch (Exception e){
			return false;
		}
	}
}

package edu.bcm.dldcc.big.nursa.model.omics;

public enum PathwayCategory {

	type,category,cclass,family ;

	public static PathwayCategory convert(String value){
		try {
			return PathwayCategory.valueOf(value);
		}catch (IllegalArgumentException e){
			return null;
		}
	}

	public static PathwayCategory convertToCClass(String value){
		if(value != null && value.trim().equalsIgnoreCase("class"))
			return cclass;
		return null;
	}
}

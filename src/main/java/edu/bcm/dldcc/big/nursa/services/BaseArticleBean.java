package edu.bcm.dldcc.big.nursa.services;

import java.io.Serializable;

public class BaseArticleBean implements Serializable {

	private static final long serialVersionUID = -8293149329538315644L;

	/**
	* Method to implement (Nursa) citation Authors standard
	* TODO if make changes here, need also make them in model.Dataset. Need DRY this 
	* @param authorsList
	* @return
	*/
	// On 10/15/15 Per NM request, changed the author list to full, across the board - WK
	public  String getCitationAuthors(String authorsList) {
		if (null == authorsList) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		String[] auths = authorsList.split(",");
		String sep = "";

		for (int i = 0; i < auths.length; i++) {
			sb.append(sep);
			sb.append(auths[i]);
			if ((i + 2) == auths.length) {
				sep = " and ";
			} else {
				sep = ", ";
			}
		}
		return sb.toString();
	}
}
package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

/**
 * must be comma space as Neils 09.12.2011 email
 * @author amcowiti
 *
 */
public enum SourceSeparator {
	SOURCE_SEP(";"),
	TARGET_SEP(" , ");
	private String sep;

	public String getSep() {
		return sep;
	}

	private SourceSeparator(String sep) {
		this.sep = sep;
	}
	
	
}

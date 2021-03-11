package edu.bcm.dldcc.big.nursa.model.omics.dto;

/**
 * Sci Notation Format
 * As Per Mantis 2414, PValue format is 0.##E0
 * @author amcowiti
 *
 */
public enum ScienceFormPattern {
	PVALUE("0.##E0"),
	PVALUE_ZERO(" < 1E-10"),
	FC("0.##E0");
	private String pattern;
	private ScienceFormPattern(String pattern){
		this.pattern=pattern;
	}
	public String getPattern() {
		return pattern;
	}
	
}

package edu.bcm.dldcc.big.nursa.model.transcriptomic;

/**
 * Comparison Operators for no standard values
 * @author mcowiti
 *
 */
public enum FoldChangeSignificanceOtherOperator {

	foldChange("ge"),significance("lt");
	
	private String operator;

	private FoldChangeSignificanceOtherOperator(String operator) {
		this.operator = operator;
	}
	
}

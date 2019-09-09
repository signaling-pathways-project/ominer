package edu.bcm.dldcc.big.nursa.util.qualifier;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

/**
 * @author pew
 * 
 */
public class NamedLiteral extends AnnotationLiteral<Named> implements Named {

	private static final long serialVersionUID = 5354942660223513782L;
	/**
   * 
   */
	private final String value;

	/**
	 * @param value
	 */
	public NamedLiteral(String value) {
		this.value = value;
	}

	@Override
	public String value() {
		return value;
	}

}
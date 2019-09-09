package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util;

public class NoSuchIdentifierException extends Exception {

	private ApiApplicationErrorMessage error;
	public NoSuchIdentifierException(String message) {
        super(message);
       
    }
	public NoSuchIdentifierException(String message,ApiApplicationErrorMessage error) {
        super(message);
        this.error=error;
    }
	public ApiApplicationErrorMessage getError() {
		return error;
	}
	public void setError(ApiApplicationErrorMessage error) {
		this.error = error;
	}
	
}

package edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.http.HttpStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ApiErrorMessage {
    @JsonProperty
    private int httpStatus;
    @JsonProperty
    private String message;

    public ApiErrorMessage(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

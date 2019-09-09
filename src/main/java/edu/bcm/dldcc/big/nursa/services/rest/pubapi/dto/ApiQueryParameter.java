package edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto;

import edu.bcm.dldcc.big.nursa.services.rest.omics.DatasetQueryType;

import java.time.LocalDate;
import java.util.Arrays;

public class ApiQueryParameter {
    public DatasetQueryType type;
    public String value;
    public LocalDate date;
    public String[] biosampleNameValues;
    public Long[] biosampleIdValues;

    public ApiQueryParameter(DatasetQueryType type, String value, String[] biosampleNameValues, Long[] biosampleIdValues) {
        this.type = type;
        this.value = value;
        this.biosampleNameValues = biosampleNameValues;
        this.biosampleIdValues = biosampleIdValues;
    }

    public ApiQueryParameter(DatasetQueryType type, String value, LocalDate date,String[] biosampleNameValues, Long[] biosampleIdValues) {
        this(type,value,biosampleNameValues,biosampleIdValues);
        this.date=date;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ApiQueryParameter{");
        sb.append("type=").append(type);
        sb.append(", value='").append(value).append('\'');
        sb.append(", date=").append(date);
        sb.append(", biosampleNameValues=").append(biosampleNameValues == null ? "null" : Arrays.asList(biosampleNameValues).toString());
        sb.append(", biosampleIdValues=").append(biosampleIdValues == null ? "null" : Arrays.asList(biosampleIdValues).toString());
        sb.append('}');
        return sb.toString();
    }
}

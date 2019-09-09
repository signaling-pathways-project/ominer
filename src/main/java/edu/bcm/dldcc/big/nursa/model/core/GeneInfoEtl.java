package edu.bcm.dldcc.big.nursa.model.core;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@Entity(name="GENEINFO_REETL")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneInfoEtl {

    @Id
    private Long geneid;

    private String symbol;

    public Long getGeneid() {
        return geneid;
    }

    public void setGeneid(Long geneid) {
        this.geneid = geneid;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public GeneInfoEtl() {
    }
}

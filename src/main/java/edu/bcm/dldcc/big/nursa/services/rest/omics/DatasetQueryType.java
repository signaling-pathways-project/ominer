package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.BsmIdentifierType;

public enum DatasetQueryType {
    doi,pmid,bsm,bsmidcas,bsmidiuphar,bsmidpubchem,bsmidchebi,node,biosample,biosampleid,datesince;

    public static DatasetQueryType getBsmIdType(BsmIdentifierType type){
        switch (type){
            case cas:
                return bsmidcas;
            case iuphar:
            default:
                return bsmidiuphar;
            case chebi:
                return bsmidchebi;
            case pubchem:
                return bsmidpubchem;
        }
    }
}

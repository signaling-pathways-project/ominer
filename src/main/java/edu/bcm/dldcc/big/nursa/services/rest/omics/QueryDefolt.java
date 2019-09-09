package edu.bcm.dldcc.big.nursa.services.rest.omics;

public enum QueryDefolt {

    direction("any"),
    significance(0.05D),
    foldchange(2.0D);
    private Object defolt;

    public Object getDefolt() {
        return defolt;
    }

    private QueryDefolt(Object defolt) {
        this.defolt = defolt;
    }
}

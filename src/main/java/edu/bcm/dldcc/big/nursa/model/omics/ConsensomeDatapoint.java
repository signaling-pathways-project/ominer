package edu.bcm.dldcc.big.nursa.model.omics;

import java.util.Date;

public interface ConsensomeDatapoint {
    Long getId();
    ConsensomeId getKey();
    String getDoi();
    String getGene();
    String getTargetName();
    Double getPercentile();
    Date getVersionDate();

    default Double getAverageScore(){ return null;}
    default Double getQValue(){ return null;}

    default  Double getCPValue(){ return null;}
    default  Double getDiscrate(){ return null;}
    default  Double getGmFc(){ return null;}

    default String getVersion(){ return null;}
}

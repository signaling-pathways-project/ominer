package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

public interface Datapoint<T> extends Comparable<T>{

	public String getId();
	public  Double getFoldChange();
	public double getFoldChangeRaw();
}

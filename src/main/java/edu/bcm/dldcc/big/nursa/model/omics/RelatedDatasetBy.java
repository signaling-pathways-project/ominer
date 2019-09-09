package edu.bcm.dldcc.big.nursa.model.omics;

public enum RelatedDatasetBy {

	REGUALTORY_MOLECULE("Regulatory Molecule"),RNA_SOURCE("RNA Source");
	String desc;

	private RelatedDatasetBy(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}

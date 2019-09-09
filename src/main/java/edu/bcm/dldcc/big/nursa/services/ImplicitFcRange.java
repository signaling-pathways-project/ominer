package edu.bcm.dldcc.big.nursa.services;

public enum ImplicitFcRange {

	two(2.0),onePoinFive(1.5);
	
	private double minFc;

	public double getMinFc() {
		return minFc;
	}

	private ImplicitFcRange(double min) {
		this.minFc = min;
	}
	
}

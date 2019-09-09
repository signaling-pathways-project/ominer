package edu.bcm.dldcc.big.nursa.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import edu.bcm.dldcc.big.nursa.model.common.DataResource;

public class DataResourceAlphabetical implements Comparator<DataResource>, Serializable{

	private static final long serialVersionUID = -5433390441125060137L;

	@Override
	public int compare(DataResource dr1, DataResource dr2) {
		if((dr2 == null) || (dr2.getOrganization() == null) || (dr2.getOrganization().getName() == null)) {
			return -1;
		} else if ((dr1 == null) || (dr1.getOrganization() == null) || (dr1.getOrganization().getName() == null)) {
			return 1;
		}
		return dr1.getOrganization().getName().compareToIgnoreCase(dr2.getOrganization().getName());
	}

}

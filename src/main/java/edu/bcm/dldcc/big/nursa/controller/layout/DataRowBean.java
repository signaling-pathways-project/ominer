package edu.bcm.dldcc.big.nursa.controller.layout;

import java.util.List;

import javax.inject.Named;

@Named
public class DataRowBean {

	// truncate a list to an arbitrary length and avoid IndexOutOfBound exceptions
	 public static <T> List<T> truncateList(List<T> theList, Integer maxItems) {
		if (theList instanceof java.util.List && theList.size() > maxItems) {
		
			return theList.subList(0,maxItems);
			
		} else {
			 return theList;
		}
	}
	 
	 //1.08.2015 hack, since primer.mrna is M2M, but to be 121 
	 public static <T> T pickOneElement(List<T> theList){
		 return theList.get(0);
	 }

}

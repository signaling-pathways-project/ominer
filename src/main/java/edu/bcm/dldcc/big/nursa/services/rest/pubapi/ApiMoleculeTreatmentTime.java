package edu.bcm.dldcc.big.nursa.services.rest.pubapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Molecule Treatment times 1,1-4,4-24,24
 * @author mcowiti
 *
 */
public enum ApiMoleculeTreatmentTime {
	
	le1("1"){
		public String getAmount(){
			return "1";
		}
	},
	gt1lt4("1-4"){
		public String getAmount(){
			return "1-4";
		}
	},
	gt4lt24("4-24"){
		public String getAmount(){
			return "4-24";
		}
	},
	gt24("24"){
		public String getAmount(){
			return "24";
		}
	};
	
	private String time;
	private static Map<String, ApiMoleculeTreatmentTime> map = new HashMap<String, ApiMoleculeTreatmentTime>();
	static {
        for (ApiMoleculeTreatmentTime time : ApiMoleculeTreatmentTime.values()) {
            map.put(time.time, time);
        }
    }
	
	public static String[] getTimes(){
		return map.keySet().toArray(new String[map.size()]);
	}
	
	private ApiMoleculeTreatmentTime(final String time) {
		this.time = time;
	}
	
	public static boolean isConvertible(String val){
		return map.containsKey(val);
	}
	public abstract String getAmount();
}

package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.bcm.dldcc.big.nursa.services.rest.consumers.PubchemDataProperty.Information;

public class Recorder {

	private static Logger log = Logger.getLogger(Recorder.class.getName());
	
	public static <T> T getRecordsViaGson(String json,Class<T> valueType){
		JsonParser parser = new JsonParser();
		JsonObject jobject = parser.parse(json).getAsJsonObject();
		log.info("jobject "+jobject);
		//JsonArray array = parser.parse(jobject.toString()).getAsJsonArray("Information");
		//List<Information> infos= new ArrayList<Information>();
	    Gson gson = new Gson();
	   return  gson.fromJson(jobject, valueType);   
	}
}

package util;

import org.json.JSONObject;

import enums.Direction;
import enums.Floor;

public class Validator {
	
	public static boolean validateElevInput(JSONObject input){
		try {
			Floor currFloor = Floor.valueOf(input.get("Floor").toString().toUpperCase());
			Floor destinationFloor = Floor.valueOf(input.get("Car Button").toString().toUpperCase());
			Direction.valueOf(input.getString("Floor Button").toString().toUpperCase());
			
			if(currFloor == Floor.ONE && destinationFloor == Floor.THREE ||
			   currFloor == Floor.ONE && destinationFloor == Floor.THREE) {
				return false;
			}
			
		} catch (Exception ex) {
			return false;
		}
		
		return true;
	}
	
	public static boolean objectExists(JSONObject jsonObject, String key) {
		Object o;
		try {
			o = jsonObject.get(key);
		} catch(Exception e) {
			return false;
		}
		
		return o != null;
	}
	
	

}

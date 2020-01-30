package elevatorsim;

import java.util.ArrayList;
import java.util.HashMap;

import elevatorsim.common.MessageRequest;
import elevatorsim.floor.Floor;
import elevatorsim.util.FileParser;
import elevatorsim.util.MessageRequestUtil;

public class Main {
	
	//Change to user input/ config file later
	private static final int numOfFloors = 4;
	//Change to user input/ config file later
	private static final String path = "placeholder";
	
	public static void main(String[] args) {
		
		ArrayList<MessageRequest> requests = (ArrayList<MessageRequest>) FileParser.parseInputFile(path).values();
		
		//Initialize Floors
		HashMap<Integer, Floor> floors = new HashMap<Integer, Floor>();
		for(int i = 0; i < numOfFloors; i++ ) {
			floors.put(i, new Floor(i));
		}
		
		FloorController floorController = new FloorController("floorController", floors, requests);
	}

}

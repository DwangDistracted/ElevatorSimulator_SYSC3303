package elevatorsim;

import java.io.File;
import java.util.HashMap;

import elevatorsim.common.MessageRequest;
import elevatorsim.floor.Floor;
import elevatorsim.util.FileParser;
import elevatorsim.util.MessageRequestUtil;

public class Main {
	//Change to user input/ config file later
	private static final int numOfFloors = 4;
	//Change to user input/ config file later
	private static final String testFilePath = "resources/test.txt";
	
	public static void main(String[] args) {
		String path = new File(testFilePath).getAbsolutePath();
		HashMap<Integer, MessageRequest> requestMap = FileParser.parseInputFile(path);
		ArrayList<MessageRequest> requests = (ArrayList<MessageRequest>) requestMap.values();
		
		//Initialize Floors
		HashMap<Integer, Floor> floors = new HashMap<Integer, Floor>();
		for(int i = 0; i < numOfFloors; i++ ) {
			floors.put(i, new Floor(i));
		}
		FloorController floorController = new FloorController("floorController", floors, requests);
	}
}

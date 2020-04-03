package elevatorsim;

import java.io.File;
import java.util.HashMap;

import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.util.FileParser;
import elevatorsim.floor.FloorController;

public class FloorSystemProcess {
	//Change to user input/ config file later
	private static final int numOfFloors = 22;
	//Change to user input/ config file later
	private static final String inputFilePath = "resources/test.txt";
	
	public static void main(String[] args) {
		String path = new File(inputFilePath).getAbsolutePath();
		HashMap<Integer, ElevatorRequest> requestMap = FileParser.parseInputFile(path);
		
		FloorController floorController = new FloorController("floorController", numOfFloors, requestMap);
		floorController.start();
	}
}

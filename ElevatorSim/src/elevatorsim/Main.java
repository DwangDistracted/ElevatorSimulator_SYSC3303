package elevatorsim;

import java.io.File;
import java.util.HashMap;

import elevatorsim.common.MessageRequest;
import elevatorsim.elevator.Elevator;
import elevatorsim.floor.FloorController;
import elevatorsim.scheduler.Scheduler;
import elevatorsim.util.FileParser;

public class Main {
	//Change to user input/ config file later
	private static final int numOfFloors = 10;
	//Change to user input/ config file later
	private static final String inputFilePath = "resources/test.txt";
	
	public static void main(String[] args) {
		String path = new File(inputFilePath).getAbsolutePath();
		HashMap<Integer, MessageRequest> requestMap = FileParser.parseInputFile(path);
		
		FloorController floorController = new FloorController("floorController", numOfFloors, requestMap);
		Elevator elevator = new Elevator(numOfFloors);
		Scheduler scheduler = Scheduler.getInstance();
		scheduler.setFloorController(floorController);
		scheduler.addElevator(elevator);
		
		scheduler.start();
		elevator.start();
		floorController.start();
	}
}

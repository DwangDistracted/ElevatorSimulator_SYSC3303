package elevatorsim;

import java.io.File;
import java.util.HashMap;

import elevatorsim.common.requests.ElevatorRequest;
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
		HashMap<Integer, ElevatorRequest> requestMap = FileParser.parseInputFile(path);
		
		FloorController floorController = new FloorController("floorController", numOfFloors, requestMap);
		Elevator elevator = new Elevator(numOfFloors, "ElevatorServer1");
		Elevator elevator2 = new Elevator(numOfFloors, "ElevatorServer2");
		Scheduler scheduler = Scheduler.getInstance();

		scheduler.start();
		elevator.start();
		elevator2.start();
		floorController.start();
	}
}

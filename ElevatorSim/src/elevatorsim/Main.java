package elevatorsim;

import java.io.File;
import java.util.HashMap;

import elevatorsim.common.ElevatorRequest;
import elevatorsim.elevator.Elevator;
import elevatorsim.floor.FloorController;
import elevatorsim.scheduler.Scheduler;
import elevatorsim.util.FileParser;

/**
 * The Entry Point for the elevator simulator
 * @author David Wang
 */
public class Main {
	//Change to user input/ config file later
	private static final int numOfFloors = 10;
	//Change to user input/ config file later
	private static final String inputFilePath = "resources/test.txt";
	
	public static void main(String[] args) {
		String path = new File(inputFilePath).getAbsolutePath();
		HashMap<Integer, ElevatorRequest> requestMap = FileParser.parseInputFile(path);
		
		FloorController floorController = new FloorController("floorController", numOfFloors, requestMap);
		Elevator elevator = new Elevator(numOfFloors);
		Scheduler scheduler = Scheduler.getInstance();
		scheduler.start();
		elevator.start();
		floorController.start();
	}
	
/* Please Remove after verification... This is simply for showing that deserialization works */	
//	public static void main(String[] args) {
//		ElevatorRequest request;
//		request = new ElevatorRequest("14:05:15.0", "2", "Up", "5");
//
//		ByteArrayOutputStream message = new ByteArrayOutputStream();
//		request.serialize(message);
//	
//		DatagramPacket p = new DatagramPacket(message.toByteArray(), message.size());
//		
//		System.out.println(ElevatorRequest.deserialize(p.getData()).toString());
//		
//		// Data returned looks like this as a string "RequestId: 0 | Timestamp: 14:05:15 | StartFloor: 2 | Direction: UP | DestinationFloor: 5"
//	}
}

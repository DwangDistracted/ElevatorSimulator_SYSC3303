package elevatorsim;

import elevatorsim.elevator.Elevator;

public class ElevatorProcess {
	//Change to user input/ config file later
	private static final int numOfFloors = 22;
	
	public static void main(String[] args) {
		Elevator elevator = new Elevator(numOfFloors, "ElevatorServer1");
		Elevator elevator2 = new Elevator(numOfFloors, "ElevatorServer2");
		Elevator elevator3 = new Elevator(numOfFloors, "ElevatorServer3");
		Elevator elevator4 = new Elevator(numOfFloors, "ElevatorServer4");
		elevator.start();
		elevator2.start();
		elevator3.start();
		elevator4.start();
	}
}

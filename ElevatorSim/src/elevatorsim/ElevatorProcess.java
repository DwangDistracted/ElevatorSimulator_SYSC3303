package elevatorsim;

import elevatorsim.elevator.Elevator;

public class ElevatorProcess {
	//Change to user input/ config file later
	private static final int numOfFloors = 10;
	
	public static void main(String[] args) {
		Elevator elevator = new Elevator(numOfFloors, "Elevator Server 1 ");
		Elevator elevator2 = new Elevator(11, "Elevator Server 2 ");
		elevator.start();
		elevator2.start();
	}
}

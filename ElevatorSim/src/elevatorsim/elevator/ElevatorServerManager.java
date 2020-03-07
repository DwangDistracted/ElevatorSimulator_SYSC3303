package elevatorsim.elevator;

import java.net.SocketException;
import java.util.ArrayList;

import elevatorsim.constants.ConfigConstants;

public class ElevatorServerManager {
	
	public static void main(String[] args) {
		ArrayList<ElevatorServer> elevatorServers = new ArrayList<ElevatorServer>();
		
		for (int i = 0; i < 1; i++) {
			Elevator elevator = new Elevator(ConfigConstants.elevatorFloors);
			try {
				ElevatorServer elevatorServer = new ElevatorServer(elevator);
				elevatorServers.add(elevatorServer);
				elevator.start();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

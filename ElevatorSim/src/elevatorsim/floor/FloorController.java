package elevatorsim.floor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import elevatorsim.common.ElevatorRequest;
import elevatorsim.common.MessageReciever;
import elevatorsim.constants.NetworkConstants;

/**
 * Takes in arrival sensor signals, and client requests
 * and then distributes them wherever necessary. 
 * 
 * @author Michael Patsula, David Wang
 */
public class FloorController extends Thread implements MessageReciever {
	private HashMap<Integer, Floor> floors;
	private Map<Integer, ElevatorRequest> requests;
	
	public FloorController(String name, int numOfFloors, Map<Integer, ElevatorRequest> requests) {
		super(name);
		
		this.floors = new HashMap<>();
		//Initialize Floors
		for(int i = 0; i < numOfFloors; i++ ) {
			floors.put(i, new Floor(i));
		}

		this.requests = requests;
	}
	
	/**
	 * Send elevator requests to the scheduler, and
	 * the appropriate floors for processing
	 */
	public void run() {
		FloorServer server = null;
		try {
			server = new FloorServer(this);
			server.startServer();
			Thread.sleep(NetworkConstants.DELAY_SERVER_START_MS);
			
			for(ElevatorRequest request : requests.values()) {
				floors.get(request.getStartFloor()).readRequest(request);
				server.sendElevatorRequest(request);
				Thread.sleep(500);
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				server.sendExitRequest();

				System.out.println(this.getName() + " - INFO : Exiting");
				server.stopServer();
			}
		}
	}
	
	/**
	 * This method Receives the arrival signal sent from the scheduler
	 * indicating an elevator has arrived at a particular floor and then 
	 * notifies the correct floor.
	 */
	@Override
	public void receive(ElevatorRequest message) {
		floors.get(message.getDestFloor()).loadPassengers(message.getDirection());
	}	
}

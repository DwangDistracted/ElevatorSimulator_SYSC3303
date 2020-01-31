package elevatorsim.floor;

import java.util.HashMap;
import java.util.Map;

import elevatorsim.common.MessageReciever;
import elevatorsim.common.MessageRequest;
import elevatorsim.enums.MessageDestination;
import elevatorsim.scheduler.Scheduler;

/**
 * Takes in arrival sensor singles, and client requests
 * and then distributes them wherever necessary. 
 * 
 * @author Michael Patsula, David Wang
 */
public class FloorController extends Thread implements MessageReciever {
	private HashMap<Integer, Floor> floors;
	private Map<Integer, MessageRequest> requests;
	
	public FloorController(String name, int numOfFloors, Map<Integer, MessageRequest> requests) {
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
		Scheduler scheduler = Scheduler.getInstance();
		
		for(MessageRequest request : requests.values()) {
			System.out.println("Floor Sending Request to Scheduler");
			floors.get(request.getStartFloor()).readRequest(request);
			scheduler.sendMessage(MessageDestination.ELEVATORS, request);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method Receives the arrival signal sent from the scheduler
	 * indicating an elevator has arrived at a particular floor and then 
	 * notifies the correct floor.
	 */
	@Override
	public void recieve(MessageRequest message) {
		floors.get(message.getDestFloor()).loadPassengers(message.getDirection());
	}
	
}

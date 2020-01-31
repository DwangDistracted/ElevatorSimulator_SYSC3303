package elevatorsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import elevatorsim.common.MessageReciever;
import elevatorsim.common.MessageRequest;
import elevatorsim.enums.Direction;
import elevatorsim.enums.MessageDestination;
import elevatorsim.floor.ArrivalSignal;
import elevatorsim.floor.Floor;
import elevatorsim.scheduler.Scheduler;
import elevatorsim.util.FileParser;

/**
 * Takes in arrival sensor singles, and client requests
 * and then distributes them wherever necessary. 
 */

public class FloorController extends Thread implements MessageReciever {
	private HashMap<Integer, Floor> floors;
	private List<MessageRequest> requests;
	
	public FloorController(String name, HashMap<Integer, Floor> floors, List<MessageRequest> requests) {
		super(name);
		this.floors = floors;
		this.requests = requests;
	}
	
	/**
	 * Send elevator requests to the scheduler, and
	 * the appropriate floors for processing
	 */
	public void run() {
		Scheduler scheduler = Scheduler.getInstance();
		for(MessageRequest request : requests) {
			floors.get(request.getStartFloor()).readRequest(request);
			scheduler.sendMessage(MessageDestination.ELEVATORS, request);
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

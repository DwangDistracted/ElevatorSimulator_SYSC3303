package elevatorsim.floor;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import elevatorsim.common.requests.ElevatorArrivalRequest;
import elevatorsim.common.requests.ElevatorDestinationRequest;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.requests.MessageReciever;
import elevatorsim.common.requests.Request;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.TimeConstants;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Takes in arrival sensor signals, and client requests
 * and then distributes them wherever necessary. 
 * 
 * @author Michael Patsula, David Wang
 */
public class FloorController extends Thread implements MessageReciever {
	private HashMap<Integer, Floor> floors;
	private Map<Integer, ElevatorRequest> requests;
	private boolean running;
	private Queue<Request> eventQueue;
	FloorServer server = null;
	private LocalTime lastRequestTime;
	
	public FloorController(String name, int numOfFloors, Map<Integer, ElevatorRequest> requests) {
		super(name);
		
		this.floors = new HashMap<>();
		//Initialize Floors
		for(int i = 0; i < numOfFloors; i++ ) {
			floors.put(i, new Floor(i, numOfFloors));
		}

		this.requests = requests;
	}
	
	/**
	 * Determines how long to wait before sending the next elevatorRequest to the scheduler to simulate the time in between floor button presses
	 * @param newRequestTime the time of the next floor button press
	 * @return long - the time inbetween presses in milliseconds (or the maximum request delay to speed up execution)
	 */
	public long getRequestDelay(LocalTime newRequestTime) {
		long delayTime = lastRequestTime == null ? 0 : SECONDS.between(lastRequestTime,newRequestTime);
		lastRequestTime = newRequestTime;
		return Long.min(delayTime * 1000, TimeConstants.maxFloorRequestDelay);
	}
	
	/**
	 * Send elevator requests to the scheduler, and
	 * the appropriate floors for processing
	 */
	public void run() {
		try {
			server = new FloorServer(this);
			server.startServer();
			Thread.sleep(NetworkConstants.DELAY_SERVER_START_MS);
			
			for(ElevatorRequest request : requests.values()) {
				floors.get(request.getStartFloor()).readRequest(request);
				server.sendElevatorRequest(request, getRequestDelay(request.getTimeStamp()));
				Thread.sleep(500);
			}
			
			while(running) {
				processEvents();
				wait();
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
	
	public void processEvents() {
		while(!eventQueue.isEmpty()) {
			Request request = eventQueue.poll();
			
			if(request instanceof ElevatorArrivalRequest) {
				Floor floor = floors.get(((ElevatorArrivalRequest) request).getArrivalFloor());
				ElevatorDestinationRequest buttonRequests = floor.loadPassengers((ElevatorArrivalRequest) request);
				
				server.sendDestinationRequest(buttonRequests);
			} 
		}
	}
	
	
	/**
	 * This method Receives the arrival signal sent from the scheduler
	 * indicating an elevator has arrived at a particular floor and then 
	 * notifies the correct floor.
	 */
	@Override
	public void receive(Request request) {
		eventQueue.add(request);
		notifyAll();
	}	
}

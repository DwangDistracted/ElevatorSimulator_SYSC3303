package elevatorsim.floor;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.constants.TimeConstants;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 *Stores all the floor instances, and simulates people making
 * elevator requests 
 *  
 * @author Michael Patsula, David Wang
 */
public class FloorController extends Thread {
	private HashMap<Integer, Floor> floors;
	private Map<Integer, ElevatorRequest> requests;
	private LocalTime lastRequestTime;
	private FloorEvents floorEvents;
	
	public FloorController(String name, int numOfFloors, Map<Integer, ElevatorRequest> requests) {
		super(name);
		
		this.floors = new HashMap<>();
		//Initialize Floors
		for(int i = 0; i < numOfFloors; i++ ) {
			floors.put(i, new Floor(i, numOfFloors));
		}

		this.requests = requests;
		floorEvents = new FloorEvents(this);
	}
	
	/**
	 * Determines how long to wait before sending the next elevatorRequest to the scheduler to simulate the time in between floor button presses
	 * @param newRequestTime the time of the next floor button press
	 * @return long - the time in between presses in milliseconds (or the maximum request delay to speed up execution)
	 */
	public long getRequestDelay(LocalTime newRequestTime) {
		long delayTime = lastRequestTime == null ? 0 : SECONDS.between(lastRequestTime,newRequestTime);
		lastRequestTime = newRequestTime;
		return Long.min(delayTime * 1000, TimeConstants.maxFloorRequestDelay);
	}
	
	/**
	 * Simulates people entering the lobby and making an elevator request
	 */
	public void run() {
		floorEvents.start();
		try {
			for(ElevatorRequest request : requests.values()) {
				Thread.sleep(getRequestDelay(request.getTimeStamp()));
				floorEvents.receive(request);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
//			floorEvents.stopEventPolling();
		}
	}
	
	/**
	 * Fetches the Floor instance 
	 * @param floor number
	 * @return
	 */
	public Floor getFloor(int floor) {
		return floors.get(floor);
	}
}

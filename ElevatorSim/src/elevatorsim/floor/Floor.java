package elevatorsim.floor;

import java.util.HashMap;
import java.util.List;

import elevatorsim.common.MessageRequest;
import elevatorsim.enums.Direction;

/**
 * 
 *
 */
public class Floor {
	private Integer floorNumber;
	private FloorButton dirButtons;
	private HashMap<Direction, List<MessageRequest>> activeRequests; 

	public Floor(int floorNumber) {
		this.floorNumber = floorNumber;
		this.dirButtons = new FloorButton();
		activeRequests = new HashMap<Direction, List<MessageRequest>>();
	}
	
	/**
	 * Adds the elevator request to the list of requests for the floor and
	 * if necessary turns on the direction button lamps
	 * 
	 * @param request the request made by the user/elevator requester
	 */
	public void readRequest(MessageRequest request) {
		List<MessageRequest> requests = activeRequests.get(request.getDirection());
		requests.add(request);
		activeRequests.replace(request.getDirection(), requests);
		
		if(request.getDirection() == Direction.UP) {
			dirButtons.setUpFloorButton(true);
		} else {
			dirButtons.setDownFloorButton(true);
		}
	}

	/**
	 * Turns off the direction lamps when an elevator arrives
	 * and loads passengers onto the elevator
	 * 
	 * @param directionLamp
	 */
	public void loadPassengers(Direction directionLamp) {
		if (directionLamp == Direction.UP) {
			dirButtons.setUpFloorButton(false);
			System.out.println("Elevator has arrived going up");
			activeRequests.get(Direction.UP).clear();
		} else {
			dirButtons.setDownFloorButton(false);
			System.out.println("Elevator has arrived going down");
			activeRequests.get(Direction.DOWN).clear();
		}
	}

	/**
	 * Get the floor number for the current floor
	 * 
	 * @return an integer with the number of the current floor
	 */
	public Integer getFloorNumber() {
		return floorNumber;
	}
}

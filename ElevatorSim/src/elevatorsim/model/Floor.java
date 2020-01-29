package elevatorsim.model;

import java.util.HashMap;
import java.util.Set;

import elevatorsim.enums.Direction;

public class Floor {
	private Integer floorNumber;
	private FloorButton dirButtons;
	private HashMap<Integer, Set<Integer>> activeRequests; // A map of requestId and floorNumber

	public Floor(int floorNumber) {
		this.floorNumber = floorNumber;
		this.dirButtons = new FloorButton();
		activeRequests = new HashMap<>();
	}

	/**
	 * Set the direction of the elevator call and send the request data to the
	 * scheduler
	 * 
	 * @param request the request made by the user/elevator requester
	 */
	public synchronized void readRequest(MessageRequest request) {
		if (request.getDirection() == Direction.UP) {
			if (!(dirButtons.getUpFloorButton())) {
				dirButtons.setUpFloorButton(true);
				// append to data to send
			}
		} else {
			if (!(dirButtons.getDownFloorButton())) {
				dirButtons.setDownFloorButton(true);
				// append to data to send
			}
		}
	}

	/**
	 * Turns of the direction buttons when an elevator arrives
	 * 
	 * @param directionLamp
	 */
	public synchronized void setArrive(Direction directionLamp) {
		if (directionLamp == Direction.UP) {
			dirButtons.setUpFloorButton(false);
			System.out.println("Elevator has arrived going up");
			// activeRequests.get(Direction.UP).clear();
		} else {
			dirButtons.setDownFloorButton(false);
			System.out.println("Elevator has arrived going down");
			// elevatorButtons.get(Direction.DOWN).clear();
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

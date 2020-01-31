package elevatorsim.floor;

import java.util.ArrayList;
import java.util.List;

import elevatorsim.common.MessageRequest;
import elevatorsim.enums.Direction;

/**
 * A floor object to model the individual floors of a building
 *	@author Michael Patsula, Rahul Anilkumar
 */
public class Floor {
	private Integer floorNumber;
	private FloorButton dirButtons;
	private List<MessageRequest> activeUpRequests;
	private List<MessageRequest> activeDownRequests;

	public Floor(int floorNumber) {
		this.floorNumber = floorNumber;
		this.dirButtons = new FloorButton();
		activeUpRequests = new ArrayList<>();
		activeDownRequests = new ArrayList<>();
	}
	
	/**
	 * Adds the elevator request to the list of requests for the floor and
	 * if necessary turns on the direction button lamps
	 * 
	 * @param request the request made by the user/elevator requester
	 */
	public void readRequest(MessageRequest request) {
		if(request.getDirection() == Direction.UP) {
			activeUpRequests.add(request);
			dirButtons.setUpFloorButton(true);
		} else if (request.getDirection() == Direction.DOWN) {
			activeDownRequests.add(request);
			dirButtons.setDownFloorButton(true);
		} else {
			// INVALID Direction - ignore request
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
			activeUpRequests.clear();
		} else if (directionLamp == Direction.DOWN) {
			dirButtons.setDownFloorButton(false);
			System.out.println("Elevator has arrived going down");
			activeDownRequests.clear();
		} else {
			// INVALID Direction - ignore request
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
	
	/**
	 * Get the list of going up messages from this floor
	 * @return the list of messages
	 */
	public List<MessageRequest> getActiveUpRequests() {
		return activeUpRequests;
	}

	/**
	 * Get the list going down messages from this floor
	 * @return the list of messages
	 */
	public List<MessageRequest> getActiveDownRequests() {
		return activeDownRequests;
	}
}

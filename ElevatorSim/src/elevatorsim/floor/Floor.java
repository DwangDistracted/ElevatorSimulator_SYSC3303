package elevatorsim.floor;

import java.util.HashSet;
import java.util.Set;

import elevatorsim.common.requests.ElevatorArrivalRequest;
import elevatorsim.common.requests.ElevatorDestinationRequest;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.constants.Direction;

/**
 * A floor object to model the individual floors of a building
 *	@author Michael Patsula, Rahul Anilkumar, David Wang
 */
public class Floor {
	private Integer floorNumber;
	private DirectionLamp buttonLamps;
	private DirectionLamp arrivalLamps;
	private Set<Integer> activeUpDestinations;
	private Set<Integer> activeDownDestinations;

	public Floor(int floorNumber, int numOfFloors) {
		this.floorNumber = floorNumber;
		activeUpDestinations = new HashSet<>();
		activeDownDestinations = new HashSet<>();
		
		if(floorNumber == 1) {
			this.buttonLamps = new DirectionLamp(true, null);
			this.arrivalLamps = new DirectionLamp(true, null);
		} else if (floorNumber == numOfFloors) {
			this.buttonLamps = new DirectionLamp(null, true);
			this.arrivalLamps = new DirectionLamp(true, null);
		}
	}
	
	/**
	 * Adds the elevator request to the list of requests for the floor and
	 * if necessary turns on the direction button lamps
	 * 
	 * @param request the request made by the user/elevator requester
	 */
	public void readRequest(ElevatorRequest request) {
		if(request.getDirection() == Direction.UP) {
			activeUpDestinations.add(request.getDestFloor());
			try {
				buttonLamps.setUpLamp(true);
			}catch (NullPointerException e) {
				System.out.println("floor tried to set a non existant button");
			}
		} else if (request.getDirection() == Direction.DOWN) {
			activeDownDestinations.add(request.getDestFloor());
			try {
				buttonLamps.setDownLamp(true);
			}catch (NullPointerException e) {
				System.out.println("floor tried to set a non existant button");
			}
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
	public ElevatorDestinationRequest loadPassengers(ElevatorArrivalRequest request ) {
		ElevatorDestinationRequest buttonRequest = null;
		if (request.getElevatorDirection() == Direction.UP) {
			arrivalLamps.setUpLamp(true);
			buttonLamps.setUpLamp(false);
			System.out.println("Elevator has arrived going up");	
			
			buttonRequest = new ElevatorDestinationRequest(request.getArrivalFloor(), request.getElevatorId(), activeUpDestinations);
			activeUpDestinations.clear();
		} else if (request.getElevatorDirection() == Direction.DOWN) {
			arrivalLamps.setDownLamp(true);
			buttonLamps.setDownLamp(false);
			System.out.println("Elevator has arrived going down");
			
			buttonRequest = new ElevatorDestinationRequest(request.getElevatorId(), request.getArrivalFloor(), activeUpDestinations);
			activeDownDestinations.clear();
		} else {
			// INVALID Direction - ignore request
		}
		
		return buttonRequest;
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

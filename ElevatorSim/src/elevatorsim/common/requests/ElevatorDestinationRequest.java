package elevatorsim.common.requests;

import java.util.Set;

import elevatorsim.common.SerializableMessage;

/**
 * A request which contains the destination of passengers for a ceratain elevator 
 */
public class ElevatorDestinationRequest extends SerializableMessage<ElevatorDestinationRequest> implements Request {
	
	private int passengerStartFloor;
	private Set<Integer> destinationFloors;
	private int elevatorId;
	
	public ElevatorDestinationRequest(int elevatorId, int passengerStartFloor, Set<Integer> destinationFloors) {
		this.elevatorId = elevatorId;
		this.destinationFloors = destinationFloors;
		this.passengerStartFloor = passengerStartFloor;
	}
	
	public int getPassengerStartFloor() {
		return passengerStartFloor;
	}
	public void setPassengerStartFloor(int passengerStartFloor) {
		this.passengerStartFloor = passengerStartFloor;
	}
	public Set<Integer> getDestinationfloors() {
		return destinationFloors;
	}
	public void setDestinationfloors(Set<Integer> destinationfloors) {
		this.destinationFloors = destinationfloors;
	}
	public int getElevatorId() {
		return elevatorId;
	}
	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}
	
}

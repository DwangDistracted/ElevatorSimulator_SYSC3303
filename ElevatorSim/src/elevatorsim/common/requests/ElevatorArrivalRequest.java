package elevatorsim.common.requests;

import elevatorsim.common.SerializableMessage;
import elevatorsim.constants.Direction;

public class ElevatorArrivalRequest extends SerializableMessage<ElevatorArrivalRequest> implements Request {
	
	private int arrivalFloor;
	private int elevatorId;
	private Direction elevatorDirection;
	
	public ElevatorArrivalRequest(int elevatorId, int arrivalFloor, Direction elevatorDirection) {
		this.elevatorId = elevatorId;
		this.arrivalFloor = arrivalFloor;
	}
	
	public int getArrivalFloor() {
		return arrivalFloor;
	}
	public void setArrivalFloor(int arrivalFloor) {
		this.arrivalFloor = arrivalFloor;
	}
	public int getElevatorId() {
		return elevatorId;
	}
	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}
	public Direction getElevatorDirection() {
		return elevatorDirection;
	}
	public void setElevatorDirection(Direction elevatorDirection) {
		this.elevatorDirection = elevatorDirection;
	}
	
}

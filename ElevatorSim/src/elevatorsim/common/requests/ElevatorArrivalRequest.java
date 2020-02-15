package elevatorsim.common.requests;

import elevatorsim.constants.Direction;

/**
 * A request sent when the arrival sensor notifies the system that an elevator is
 * arriving to a floor
 */
public class ElevatorArrivalRequest extends Request<ElevatorArrivalRequest> {
	
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

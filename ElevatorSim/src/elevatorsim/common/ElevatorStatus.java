package elevatorsim.common;
import java.util.ArrayList;
import java.util.List;

import elevatorsim.constants.Direction;
import elevatorsim.constants.ElevatorStates;

/**
 * This class contains information that the scheduler can use to determine which elevator to service an elevator request.
 * This should be send to the scheduler from the elevator. (TODO - discuss when/how)
 *  
 * @author David Wang
 */
public class ElevatorStatus extends SerializableMessage<ElevatorStatus> {
	// TODO - what should an elevator send back as its status?
	private ElevatorStates state;
	private Direction direction;
	private int floor;
	private List<Integer> stops;

	public ElevatorStatus(ElevatorStates state) {
		this.state = state;
		this.floor = 1;
		this.direction = null;
		this.stops = new ArrayList<Integer>();
	}

	public static ElevatorStatus empty() {
		return new ElevatorStatus(ElevatorStates.DOOR_OPEN);
	}
	
	public Direction getDirection() {
		return this.direction;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public int getFloor() {
		return this.floor;
	}
	
	public void setFloor(int floor) {
		this.floor = floor;
	}
	
	public ElevatorStates getState() {
		return this.state;
	}
	
	public void setState(ElevatorStates elevatorState) {
		this.state = elevatorState;
	}
	
	public List<Integer> getStops(){
		return this.stops;
	}
	
	public void removeStop(Integer floor) {
		if(this.stops.indexOf(floor) != -1) {
			this.stops.remove(floor);
		}
	}
	
	public void clearStops() {
		this.stops = new ArrayList<Integer>();
	}
	
	public Direction addFloor(Integer floor) {
		if (this.stops.indexOf(floor) != -1 ) {
			return null;
		}
		this.stops.add(floor);
		if (this.stops.size() == 1) {
			if (floor - this.floor > 0) {
				this.direction = Direction.UP;
				return this.direction;
			} else if (floor - this.floor < 0) {
				this.direction = Direction.DOWN;
				return this.direction;
			}
		}
		return null;
	}

	public String toString() {
		return "Status: " + this.state.getStringValue();
	}
}

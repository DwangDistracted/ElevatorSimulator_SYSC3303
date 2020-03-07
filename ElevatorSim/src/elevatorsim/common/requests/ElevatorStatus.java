package elevatorsim.common.requests;
import java.util.ArrayList;
import java.util.List;

import elevatorsim.constants.Direction;
import elevatorsim.constants.ElevatorState;

/**
 * This class contains information that the scheduler can
 * use to determine which elevator to service an elevator request.
 *  
 * @author David Wang, Trevor Bivi
 */
public class ElevatorStatus extends Request<ElevatorStatus> {
	private static final long serialVersionUID = -773085642489212229L;

	private ElevatorState state; // The last known state the elevator sent to the scheduler
	private Direction direction; // The direction the elevator is currently servicing
	private int floor; // If an elevator is not moving in a direction it is at this floor. If it is moving it must be able to stop before the next floor.
	private List<Integer> stops; // The end destinations this specific elevator must stop at

	public ElevatorStatus(ElevatorState state) {
		this.state = state;
		this.floor = 1;
		this.direction = null;
		this.stops = new ArrayList<Integer>();
	}

	public static ElevatorStatus empty() {
		return new ElevatorStatus(ElevatorState.DOOR_OPEN);
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
	
	public ElevatorState getState() {
		return this.state;
	}
	
	public void setState(ElevatorState elevatorState) {
		this.state = elevatorState;
	}
	
	public List<Integer> getStops(){
		return this.stops;
	}
	
	/**
	 * Removes a given floor from the list of stops if it exists
	 * @param floor the floor to remove
	 */
	public void removeStop(Integer floor) {
		if(this.stops.contains(floor)){
			this.stops.remove(floor);
		}
	}
	
	/**
	 * Removes all stops
	 */
	public void clearStops() {
		this.stops = new ArrayList<Integer>();
	}
	
	/**
	 * Adds a floor to the list of destinations this elevator must stop
	 * @param floor the floor that must be stopped at
	 * @return
	 */
	public Direction addFloor(Integer floor) {
		if (this.stops.contains(floor)) {
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

package elevatorsim.constants;

import elevatorsim.common.ElevatorStatus;
import elevatorsim.common.SerializableMessage;

/**
 * Events triggered by the state of an elevator.
 * This should be passed from Elevator to Floor.
 * TODO - can this also include elevator status?
 *  
 * @author David Wang
 */
public class ElevatorEvent extends SerializableMessage<ElevatorEvent> {
	private int floor;
	public ElevatorEvent (int floor) {
		this.floor = floor;
	}
	
	public int getFloor() {
		return this.floor;
	}
	
	public String toString() {
		return "newFloor: " + Integer.toString(floor);
	}
}

package elevatorsim.constants;

import elevatorsim.common.ElevatorStatus;
import elevatorsim.common.SerializableMessage;

/**
 * Floor change events triggered by the an elevator.
 * This should be passed from Elevator to Floor.
 *  
 * @author David Wang, Trevor Bivi
 */
public class ElevatorEvent extends SerializableMessage<ElevatorEvent> {
	private int floor; //The new floor
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

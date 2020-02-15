package elevatorsim.common;

import elevatorsim.constants.ElevatorState;

/**
 * Stores information about a elevator state change
 * This is send from the scheduler to an elevator to tell the elevator how it should change it's state
 * and from the elevator to the scheduler to tell it when the elevator has finished making the change
 * @author Trevor Bivi
 *
 */
public class ElevatorStateChange extends SerializableMessage<ElevatorStateChange> {
	ElevatorState elevatorState;
	public ElevatorStateChange(ElevatorState state) {
		this.elevatorState = state;
	}
	
	public ElevatorState getStateChange() {
		return this.elevatorState;
	}
	
	public String toString() {
		return "state:" + this.elevatorState.toString();
	}
}

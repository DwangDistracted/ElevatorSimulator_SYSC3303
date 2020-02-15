package elevatorsim.common;

import elevatorsim.constants.ElevatorStates;

public class ElevatorStateChange extends SerializableMessage<ElevatorStateChange> {
	ElevatorStates elevatorState;
	public ElevatorStateChange(ElevatorStates state) {
		this.elevatorState = state;
	}
	
	public ElevatorStates getStateChange() {
		return this.elevatorState;
	}
	
	public String toString() {
		return "state:" + this.elevatorState.toString();
	}
}

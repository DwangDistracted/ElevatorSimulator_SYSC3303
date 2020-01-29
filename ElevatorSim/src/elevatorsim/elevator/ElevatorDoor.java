package elevatorsim.elevator;

import elevatorsim.enums.ElevatorDoorState;

public class ElevatorDoor {
	
	private ElevatorDoorState doorState;
	private Elevator elevator;
	
	public ElevatorDoor(Elevator elevator) {
		this.elevator = elevator;
		this.doorState = ElevatorDoorState.OPEN;
	}
	
	public ElevatorDoorState getDoorState() {
		return this.doorState;
	}
	
	public void targetDoorState(ElevatorDoorState doorState) {
		assert (doorState == ElevatorDoorState.OPEN || doorState == ElevatorDoorState.CLOSED);
		
		// future iterations will need to set the state to opening/closing first and take time to reach open/close state??
		if (doorState == ElevatorDoorState.OPEN) {
			this.doorState = ElevatorDoorState.OPEN;
			this.elevator.sendOpenedDoor();
		} else if (doorState == ElevatorDoorState.CLOSED) {
			this.doorState = ElevatorDoorState.CLOSED;
			this.elevator.sendClosedDoor();
		}
	}

	
}


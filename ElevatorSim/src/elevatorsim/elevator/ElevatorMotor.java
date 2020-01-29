package elevatorsim.elevator;

import elevatorsim.enums.ElevatorMotorState;

public class ElevatorMotor {
	private Elevator elevator;
	private ElevatorMotorState direction;
	
	public ElevatorMotor(Elevator elevator) {
		this.elevator = elevator;
		this.direction = ElevatorMotorState.STOP;
	}
	
	public void setMotorDirection (ElevatorMotorState direction) {
		assert (direction == ElevatorMotorState.DOWN ||
				direction == ElevatorMotorState.STOP ||
				direction == ElevatorMotorState.UP );
		
		this.direction = direction;
		
		// in future iterations this should take time??
		if (direction == ElevatorMotorState.STOP) {
			elevator.sendStopped();
		}
	}
	
	public ElevatorMotorState getMotorDirection () {
		return this.direction;
	}
}

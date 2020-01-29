package elevatorsim.enums;

public enum ElevatorMotorState {
	UP,
	DOWN,
	STOP,
	INVALID;
	
	public static boolean contains(String value) {

	    for (ElevatorMotorState s : ElevatorMotorState.values()) {
	        if (s.name().equals(value)) {
	            return true;
	        }
	    }

	    return false;
	}
}
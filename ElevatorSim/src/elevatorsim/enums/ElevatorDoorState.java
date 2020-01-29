package elevatorsim.enums;

public enum ElevatorDoorState {
	OPEN,
	CLOSED,
	OPENING,
	CLOSING,
	INVALID;
	
	public static boolean contains(String value) {

	    for (ElevatorDoorState s : ElevatorDoorState.values()) {
	        if (s.name().equals(value)) {
	            return true;
	        }
	    }

	    return false;
	}
}


package elevatorsim.enums;

public enum Direction {
	UP,
	DOWN,
	INVALID;
	
	public static boolean contains(String value) {

	    for (Direction d : Direction.values()) {
	        if (d.name().equals(value)) {
	            return true;
	        }
	    }

	    return false;
	}
}


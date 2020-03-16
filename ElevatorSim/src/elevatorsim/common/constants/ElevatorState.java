package elevatorsim.common.constants;

/**
 * Represents the different states the elevator can be in
 * Note: The elevator must not be moving if it is in the door closed state (or other states starting in door for that matter)
 * @author Trevor Bivi
 */
public enum ElevatorState {
	DOOR_OPEN ((byte) 0x01, "Door open"),
	DOOR_OPENING ((byte) 0x02, "Door opening"),
	STATIONARY_AND_DOOR_CLOSED ((byte) 0x03, "Stationary and door closed"),
	DOOR_CLOSING ((byte) 0x04, "Door closing"),
	MOTOR_UP ((byte) 0x05, "Motor moving upwards"),
	MOTOR_DOWN ((byte) 0x06, "Motor moving downwards"),
	INVALID ((byte) 0x07, "Invalid");
	
	private byte byteValue;
	private String stringValue;
	
	private ElevatorState(byte byteValue, String stringValue) {
		this.byteValue = byteValue;
		this.stringValue = stringValue;
	}
	
	public byte getByteValue() {
		return byteValue;
	}
	
	public String getStringValue() {
		return this.stringValue;
	}

	
	/**
	 * Identify the elevator state from the corresponding byte value.
	 * @param byteValue The specific byte to identify the elevator's state
	 * @return The elevator's state
	 */
	public ElevatorState getStateFromByteValue(byte byteValue) {
		ElevatorState[] normalStates = new ElevatorState[] {
			DOOR_OPEN,
			DOOR_OPENING,
			STATIONARY_AND_DOOR_CLOSED,
			DOOR_CLOSING,
			MOTOR_UP,
			MOTOR_DOWN
		};
		
		int intValue = (int) byteValue;
		if(intValue < normalStates.length && intValue >= 0) {
			return normalStates[intValue];
		}
		return INVALID;
	}
}

package elevatorsim.constants;

import elevatorsim.common.ElevatorStatus;
import elevatorsim.common.SerializableMessage;

public enum ElevatorStates {
	DOOR_OPEN ((byte) 0x01, "Door open"),
	DOOR_OPENING ((byte) 0x01, "Door opening"),
	DOOR_CLOSED ((byte) 0x02, "Door closed"),
	DOOR_CLOSING ((byte) 0x03, "Door closing"),
	MOTOR_UP ((byte) 0x04, "Motor moving upwards"),
	MOTOR_DOWN ((byte) 0x05, "Motor moving downwards"),
	INVALID ((byte) 0x06, "Invalid");
	
	private byte byteValue;
	private String stringValue;
	
	private ElevatorStates(byte byteValue, String stringValue) {
		this.byteValue = byteValue;
		this.stringValue = stringValue;
	}
	
	public byte getByteValue() {
		return byteValue;
	}
	
	public String getStringValue() {
		return this.stringValue;
	}
	
	public ElevatorStates getStateFromByteValue(byte byteValue) {
		ElevatorStates[] normalStates = new ElevatorStates[] {
			DOOR_OPEN,
			DOOR_OPENING,
			DOOR_CLOSED,
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

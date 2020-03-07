package elevatorsim.constants;

/**
 * Constants used in the Communications System for Elevator Simulator 
 * 
 * @author David Wang
 */
public class NetworkConstants {
	// TODO - Set via configuration files

	/*
	 * The IP of the host that the Scheduler SubSystem runs on 
	 */
	public static final String SCHEDULER_IP = "127.0.0.1";
	/*
	 * The IP of the host that the Scheduler SubSystem runs on 
	 */
	public static final String ELEVATOR_IP = "127.0.0.1";
	/*
	 * The port that the Scheduler listens to 
	 */
	public static final int SCHEDULER_PORT = 2042;
	/*
	 * The port that the Floor Server listens to
	 */
	public static final int FLOOR_RECIEVE_PORT = 1453;

	/*
	 * A Delay used from System Start to when the Server is ready to receive messages.
	 * 
	 * This exists to give time for each elevator and the floor system to register themselves before messages start being sent
	 */
	public static final long DELAY_SERVER_START_MS = 2000;
	/*
	 * The Timeout for a DatagramSocket in the Elevator Simulator Communications System
	 */
	public static final int RESPONSE_TIMEOUT_MS = 500;

	/*
	 * The Length of a Request.
	 */
	public static final int REQUEST_LENGTH = 1024;
	/*
	 * The Length of a Response.
	 */
	public static final int RESPONSE_LENGTH = 4;

	public static final byte NULL_BYTE = (byte)0x00;

	/*
	 * The Type of Message that can be sent
	 */
	public enum MessageTypes {
		REGISTER((byte)0x1F),
		ELEVATOR_REQUEST((byte)0x2F),
		ELEVATOR_EVENT((byte)0x3F),
		STATUS((byte)0x4F),
		EXIT((byte)0x5F),
		RESPONSE((byte)0xFF);
		
		private byte marker;
		private MessageTypes(byte marker) {
			this.marker = marker;
		}
		
		public byte getMarker() {
			return marker;
		}
	}

	/*
	 * The Responses that can be sent
	 */
	public enum ResponseStatus {
		SUCCESS((byte)0x01),
		SERVER_FAILURE((byte)0x02),
		NOT_APPLICABLE((byte)0x03);
		
		private byte marker;
		private ResponseStatus(byte marker) {
			this.marker = marker;
		}
		
		public byte getMarker() {
			return marker;
		}
	}
}

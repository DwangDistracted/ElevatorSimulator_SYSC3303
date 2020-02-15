package elevatorsim.common.requests;


import java.time.LocalTime;
import java.util.Arrays;

import elevatorsim.constants.Direction;
import elevatorsim.constants.NetworkConstants;

/**
 * This is the message object which is passed by the different subsystems in
 * order to communicate the user input to the different components
 * 
 * @author Rahul Anilkumar
 *
 */
public class ElevatorRequest extends Request<ElevatorRequest> implements Comparable<ElevatorRequest> {

	// private static final DateTimeFormatter format1 =
	// DateTimeFormatter.ofPattern("hh:mm:ss:SSS");
	private Integer startFloor;
	private Direction direction;
	private int destFloor;
	private LocalTime timeStamp;
	private int requestId;

	/**
	 * Default constructor to create a MessageRequest object
	 * 
	 * @param timeStamp  the input time for the request
	 * @param startFloor the floor the request was made from
	 * @param direction  the direction up/down that the requester is going
	 * @param destFloor  the floor being traveled to
	 */
	public ElevatorRequest(String timeStamp, String startFloor, String direction, String destFloor) {
		this.timeStamp = LocalTime.parse(timeStamp);
		this.startFloor = Integer.parseInt(startFloor);
		this.direction = convertDirection(direction);
		this.destFloor = Integer.parseInt(destFloor);
	}
	
	/**
	 * Default constructor to create a MessageRequest object
	 * 
	 * @param timeStamp  the input time for the request
	 * @param startFloor the floor the request was made from
	 * @param direction  the direction up/down that the requester is going
	 * @param destFloor  the floor being traveled to
	 */
	public ElevatorRequest(LocalTime timeStamp, int destFloor) {
		this.timeStamp = timeStamp;
		this.destFloor = destFloor;
	}

	public ElevatorRequest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Helper method to convert the String value for direction to an Enum
	 * representation
	 * 
	 * @param direction the direction traveled as a string value
	 * @return The direction traveled as an ENUM object
	 */
	public Direction convertDirection(String direction) {
		if (direction.toLowerCase().equals("up")) {
			return Direction.UP;
		} else if (direction.toLowerCase().equals("down")) {
			return Direction.DOWN;
		} else {
			return Direction.INVALID;
		}
	}

	/**
	 * Getter Method for the starting floor number
	 * 
	 * @return the start floorNumber as an Integer value
	 */
	public Integer getStartFloor() {
		return startFloor;
	}

	/**
	 * Setter Method for the starting floor number
	 * 
	 * @param startFloor The value for the starting floor
	 */
	public void setStartFloor(Integer startFloor) {
		this.startFloor = startFloor;
	}

	/**
	 * Getter Method for the traveling direction
	 * 
	 * @return The Direction traveled in request as a Direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @param direction Setter Method for the traveling direction
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * get the floor number of the destination
	 * 
	 * @return an int representing the destination floor number
	 */
	public int getDestFloor() {
		return destFloor;
	}

	/**
	 * Set the destination Floor value
	 * 
	 * @param destFloor the int value to set the destination floor to
	 */
	public void setDestFloor(int destFloor) {
		this.destFloor = destFloor;
	}

	/**
	 * Get the time stamp of when inputing a request as a LocalDateTime
	 * 
	 * @return the LocalDateTime object identifying the input time
	 */
	public LocalTime getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Set the timeStamp of the input as a LocalDateTime value
	 * 
	 * @param timeStamp for the LocalDateTime to be set
	 */
	public void setTimeStamp(LocalTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * Get the requestId set for this message
	 * 
	 * @return the messages requestId
	 */
	public int getRequestId() {
		return requestId;
	}

	/**
	 * Set the requestId for this message
	 * 
	 * @param requestId the id value to be set for the message;
	 */
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	/**
	 * Override compareTo to compare the different message requests based of
	 * timeStamp
	 * 
	 * @param message the request being made by the user to compare
	 * @return the compared result
	 */
	@Override
	public int compareTo(ElevatorRequest message) {
		return getTimeStamp().compareTo(message.getTimeStamp());
	}

	/**
	 * Overrided the toString method to provide the input message as a sting
	 */
	@Override
	public String toString() {
		String status = "RequestId: " + getRequestId() + " | Timestamp: " + getTimeStamp().toString()
				+ " | StartFloor: " + getStartFloor() + " | Direction: " + ( getDirection() == null ? "null" : getDirection().toString())
				+ " | DestinationFloor: " + getDestFloor();
		return status;
	}

}

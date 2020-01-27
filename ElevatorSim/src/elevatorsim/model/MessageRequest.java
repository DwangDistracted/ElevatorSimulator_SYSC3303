package elevatorsim.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import elevatorsim.enums.Direction;


public class MessageRequest {

    private static final DateTimeFormatter format1 = DateTimeFormatter.ofPattern("hh:mm:ss:SSS");  
	private int startFloor;
	private Direction direction;
	private int destFloor;
	private String timeStamp;
	private double requestId;
	

	public MessageRequest(int startFloor, Direction direction, int destFloor){
	    LocalDateTime currentDateTime = LocalDateTime.now();  
		this.timeStamp = currentDateTime.format(format1);
		this.startFloor = startFloor;
		this.destFloor = destFloor;
		this.direction = direction;
	}
	
	public int getStartFloor() {
		return startFloor;
	}

	public void setStartFloor(int startFloor) {
		this.startFloor = startFloor;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getDestFloor() {
		return destFloor;
	}

	public void setDestFloor(int destFloor) {
		this.destFloor = destFloor;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public double getRequestId() {
		return requestId;
	}

	public void setRequestId(double requestId) {
		this.requestId = requestId;
	}

}

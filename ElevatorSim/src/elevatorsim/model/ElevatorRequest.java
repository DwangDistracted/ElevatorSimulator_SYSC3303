package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import enums.Direction;
import enums.Floor;

public class ElevatorRequest {

    private static final DateTimeFormatter format1 = DateTimeFormatter.ofPattern("hh:mm:ss:SSS");  
	private Floor startFloor;
	private Direction direction;
	private Floor destination;
	private String timeStamp;
	
	public ElevatorRequest(Floor startFloor, Direction direction, Floor destination){
	    LocalDateTime currentDateTime = LocalDateTime.now();  
		this.timeStamp = currentDateTime.format(format1);
		this.startFloor = startFloor;
		this.destination = destination;
		this.direction = direction;
	}
	
	public ElevatorRequest(JSONObject request){
	    LocalDateTime currentDateTime = LocalDateTime.now();  
		this.timeStamp = currentDateTime.format(format1);
		this.startFloor = Floor.valueOf(request.get("Floor").toString().toUpperCase());
		this.destination = Floor.valueOf(request.get("Car Button").toString().toUpperCase());
		this.direction = Direction.valueOf(request.getString("Floor Button").toString().toUpperCase());
	}
	
	public Floor getStartFloor() {
		return startFloor;
	}

	public void setStartFloor(Floor startFloor) {
		this.startFloor = startFloor;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Floor getDestination() {
		return destination;
	}

	public void setDestination(Floor destination) {
		this.destination = destination;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

}

package model;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import enums.Direction;
import enums.Floor;

public class Lobby {
	
	private static HashMap<Floor, FloorButton> floorButtons;
	private static HashMap<Floor, HashMap<Direction, Set<Floor>>> startFloorToDestinFloor;
	
	public Lobby() {
		this.floorButtons = new HashMap<Floor, FloorButton>();
		this.startFloorToDestinFloor = new HashMap<Floor, HashMap<Direction, Set<Floor>>>();
	}
	
	public synchronized static void addToLobby(List<ElevatorRequest> requests) {
		for(ElevatorRequest request : requests) {
			//if necessary update startFloorToDestFloor map
			HashMap<Direction, Set<Floor>> elevatorButtons = startFloorToDestinFloor.get(request.getStartFloor());
			Set<Floor> dirElevButtons = elevatorButtons.get(request.getDirection());
			
			if(dirElevButtons.add(request.getDestination())) {
				elevatorButtons.put(request.getDirection(), dirElevButtons);
				startFloorToDestinFloor.put(request.getDestination(), elevatorButtons);
			}
			
			boolean notifyScheduler = updateFloorButtons(request.getStartFloor(), request.getDirection(), true);
			if(notifyScheduler) {
				//add to some type of data structure
			}
			
			/*
			FloorButton floorDirection = floorButtons.get(request.getStartFloor());
			if(request.getDirection() == Direction.UP) {
				if(floorDirection.getUpFloorButton()) {
					//notify scheduler 
				} else {
					floorDirection.setUpFloorButton(true);
					floorButtons.put(request.getStartFloor(), floorDirection);
				}
			} else {
				if(floorDirection.getUpFloorButton()) {
					//notify scheduler
				} else {
					floorDirection.setDownFloorButton(true);
					floorButtons.put(request.getStartFloor(), floorDirection);
				}
			}*/
		}
	}
	
	public synchronized static void removeFromLobby(Floor arrivalFloor, Direction directionLamp) {
		System.out.println("elevator has arrived");
		//remove people from elevator
		//add people to elevator
		HashMap<Direction, Set<Floor>> elevatorButtons = startFloorToDestinFloor.get(arrivalFloor);
		Set<Floor> floors = elevatorButtons.get(directionLamp); //for scheduler
		elevatorButtons.get(directionLamp).clear();
		startFloorToDestinFloor.put(arrivalFloor, elevatorButtons);
		
		//reset floorLamp
		updateFloorButtons(arrivalFloor, directionLamp ,false);
		
		//send elevator buttons to scheduler
	}
	
	private static boolean updateFloorButtons(Floor floor, Direction direction, boolean activateLamp) {
		//if necessary update floorButtons map
		FloorButton floorDirection = floorButtons.get(floor);
		if(direction == Direction.UP) {
			if(floorDirection.getUpFloorButton() && activateLamp) {
				return true; 
			} else {
				floorDirection.setUpFloorButton(activateLamp);
				floorButtons.put(floor, floorDirection);
			}
		} else {
			if(floorDirection.getDownFloorButton() && activateLamp) {
				return true;
			} else {
				floorDirection.setDownFloorButton(activateLamp);
				floorButtons.put(floor, floorDirection);
			}
		}
		
		return false;
	}

}

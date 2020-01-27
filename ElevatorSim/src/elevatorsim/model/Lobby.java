package elevatorsim.model;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import elevatorsim.enums.Direction;
import elevatorsim.enums.Floor;

public class Lobby {
	
	private Floor floor;
	private FloorButton floorButtons;
	private HashMap<Direction, Set<Floor>> elevatorButtons;
	
	public Lobby(Floor floor) {
		this.floor = floor;
		this.floorButtons = new FloorButton();
		this.elevatorButtons = new HashMap<Direction, Set<Floor>>();
	}

	public synchronized void addToLobby(ElevatorRequest request) {
		if(request.getDirection() == Direction.UP) {
			if(!(floorButtons.getUpFloorButton())) {
				floorButtons.setUpFloorButton(true);
				//append to data to send
			}
			elevatorButtons.get(Direction.UP).add(request.getDestination());
		} else {
			if(!(floorButtons.getDownFloorButton())) {
				floorButtons.setDownFloorButton(true);
				//append to data to send
			}
			elevatorButtons.get(Direction.DOWN).add(request.getDestination());
		}
	}
	
	public synchronized void RemoveFromLobby(Direction directionLamp) {
		if(directionLamp.equals(Direction.UP)) {
			floorButtons.setUpFloorButton(false);
			elevatorButtons.get(Direction.UP).clear();
		} else {
			floorButtons.setDownFloorButton(false);
			elevatorButtons.get(Direction.DOWN).clear();
		}	
	}

	public Floor getFloor() {
		return floor;
	}
}

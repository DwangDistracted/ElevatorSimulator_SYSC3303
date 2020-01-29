
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import elevatorsim.enums.Direction;
import elevatorsim.enums.Floor;
import elevatorsim.model.ElevatorRequest;
import elevatorsim.model.Lobby;
import elevatorsim.util.FileParser;
import elevatorsim.util.Validator;

/**
 * notes:
 * floor can send two different requests to the scheduler
 * 1. floor, direction button, time  (only send one time)
 * 2. floor, elevator buttons        (only send one time)
 *
 * @author micha
 *
 */

public class FloorThread extends Thread {
	
	private static final String path = "C:\\Users\\micha\\Desktop\\";
	private List<ElevatorRequest> elevatorRequests;
	private HashMap<Floor, Lobby> lobbys;
	
	public FloorThread(String name) {
		super(name);
		this. elevatorRequests = new ArrayList<ElevatorRequest>();
		
		//Initialize Lobby
		lobbys = new HashMap<Floor, Lobby>();
		for(Floor floor : Floor.values()) {
			lobbys.put(floor, new Lobby(floor));
		}
	}
	
	public void run()
	{
		while(true) {
			List<JSONObject> inputs = FileParser.parseFiles(path);
		    for(JSONObject input : inputs) {
		    	if(Validator.validateElevInput(input)) {
		    		ElevatorRequest request = new ElevatorRequest(input);
		    		lobbys.get(request.getStartFloor()).addToLobby(request);
		    	} else {
		    		System.out.println("Received Invalid Input");
		    	}
		    }
		    
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
			}
		}
	}
	
	public void notifyArrivalSensor(Floor arrivalfloor, Direction directionLamp) {
		lobbys.get(arrivalfloor).RemoveFromLobby(directionLamp);
	}

	public List<ElevatorRequest> getElevatorRequests() {
		return elevatorRequests;
	}

	public void setElevatorRequests(List<ElevatorRequest> elevatorRequests) {
		this.elevatorRequests = elevatorRequests;
	}

}

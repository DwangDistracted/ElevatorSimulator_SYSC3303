
import java.util.ArrayList;
import java.util.List;
import org.json.*;

import enums.Direction;
import enums.Floor;
import model.ElevatorRequest;
import model.Lobby;
import util.FileParser;
import util.Validator;

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
	private Lobby lobby;
	private boolean arrivalSensor;
	private Direction directionLamp;
	private Floor arrivalFloor;
	
	public FloorThread(String name) {
		super(name);
		this.lobby = new Lobby();
		this.arrivalSensor = false;
		this.directionLamp = null;
	}
	
	public void run()
	{
		while(true) {
		    List<JSONObject> inputs = FileParser.parseFiles(path);
		    List<ElevatorRequest> elevatorRequests = new ArrayList<ElevatorRequest>();
		    for(JSONObject input : inputs) {
		    	if(Validator.validateElevInput(input)) {
		    		elevatorRequests.add(new ElevatorRequest(input));
		    	} else {
		    		System.out.println("Received Invalid Input");
		    	}
		    }
		    
		    //add people to lobby and send data to scheduler
		    if(!elevatorRequests.isEmpty()) {
		    	Lobby.addToLobby(elevatorRequests);
		    	//send to scheduler
		    }
		    //receive data from scheduler and remove people to lobby
		    if(arrivalSensor) {
		    	Lobby.removeFromLobby(arrivalFloor, directionLamp);
		    	this.arrivalSensor = false;
		    }
		    
			/*try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}*/
		}
	}
	
	public void notifyArrivalSensor(Floor arrivalfloor, Direction directionLamp) {
		this.arrivalSensor = true;
		this.directionLamp = directionLamp;
		this.arrivalFloor = arrivalfloor;
	}

}

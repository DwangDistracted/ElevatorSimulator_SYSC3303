package elevatorsim.scheduler;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.requests.ElevatorStatus;
import elevatorsim.constants.Direction;
import elevatorsim.scheduler.ElevatorContactInfo;

/**
 * This is the scheduler for the elevator simulator. 
 * 
 * It is a singleton object responsible for calculating which elevator to send requests to. 
 * It also tracks the state of the scheduler subsystem and some information about elevators it is controlling.
 * 
 * @author David Wang, Thomas Leung, Trevor Bivi
 */
public class Scheduler extends Thread {
	/**
	 * States of the Scheduler Subsystem
	 * @author David Wang
	 */
	public enum SchedulerState {
		LISTENING,
		PROCESSING,
		STOPPED,
		INVALID;
	}
	
	private static Scheduler instance;
	
	/* Maps a Remote Port Number with an Elevator */
	private final ConcurrentMap<ElevatorContactInfo, ElevatorStatus> elevators = new ConcurrentHashMap<>();
	
	
	private final SchedulerServer server;
	private SchedulerState state;
	
	private ArrayList<ElevatorRequest> storedRequests;

	private Scheduler() throws SocketException {
		super("Scheduler");
		server = SchedulerServer.getInstance();
		state = SchedulerState.STOPPED;
		storedRequests = new ArrayList<ElevatorRequest>();
	}
	
	/**
	 * Retrieves the single Scheduler Instance.
	 * @return the Scheduler Instance
	 */
	public static Scheduler getInstance() {
		if (instance == null) { 
			try {
				instance = new Scheduler();
			} catch (SocketException e) {
				System.out.println("Could Not Create Scheduler:");
				e.printStackTrace();
			} 
		}
		return instance;
	}
	
	@Override
	public void run() {
		state = SchedulerState.LISTENING;

		try {
			server.startServer();
			
			while (state != SchedulerState.STOPPED) {
				Thread.sleep(100l);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				System.out.println(this.getName() + " - INFO : Exiting due to " + (state == SchedulerState.STOPPED ? "Request" : "Error"));
				server.stopServer();
			}
		}
	}

	/**
	 * Adds an elevator to the Scheduler System, allowing it to be tracked and considered for elevator requests
	 * @param contact the IP address of the elevator system
	 */
	public void addElevator(ElevatorContactInfo contact) {
		elevators.put(contact, ElevatorStatus.empty());
	}

	/**
	 * Updates the status of a tracked elevator
	 * 
	 * @param elevatorAddress the IP address of the elevator
	 * @param elevatorStatus the new status of the elevator
	 * @return true if the elevator status is updated, false if the elevator is not being tracked
	 */
	public boolean updateElevator(ElevatorContactInfo elevatorAddress, ElevatorStatus elevatorStatus) {
		return elevators.replace(elevatorAddress, elevatorStatus) != null;
	}
	
	/**
	 * Returns an elevator that is not set to move in a direction (note : a elevator is considered to be moving in a direction if it is stopped at a floor but will then continue moving)
	 * @param floor the start floor
	 * @param callDirection the direction to travel
	 * @return the address of the elevator if there is one otherwise null
	 */
	public ElevatorContactInfo findStationaryElevator() {
		for (ElevatorContactInfo key : elevators.keySet()) {
			ElevatorStatus elevatorStatus = elevators.get(key);
			if (elevatorStatus.getDirection() == null) {
				return key;
			}
		}
		return null;
	}


	/**
	 * Returns an elevator that could service a call
	 * @param floor the start floor
	 * @param callDirection the direction to travel
	 * @return the address of the elevator if there is one otherwise null
	 */
	public ElevatorContactInfo findAvailableElevator(int floor, Direction callDirection) {
		ArrayList<ElevatorContactInfo> contacts = new ArrayList<>();
		for (ElevatorContactInfo key : elevators.keySet()) {
			ElevatorStatus elevatorStatus = elevators.get(key);

			//default
			if(elevatorStatus.getDirection() == null && floor == elevatorStatus.getFloor()) {
				contacts.add(key);
			}
			// If we want to go up and elevator is going up  and elevator floor is less than floor
			else if (elevatorStatus.getDirection() == Direction.UP && callDirection == Direction.UP && floor > elevatorStatus.getFloor()){
				contacts.add(key);
			// If we want to go down, elevator is going down and elevator is above the call floor	
			}else if (elevatorStatus.getDirection() == Direction.DOWN && callDirection == Direction.DOWN && floor < elevatorStatus.getFloor()) {
				contacts.add(key);
			}
			// if the elevator is stationary and there is a call up and floor up
			else if(elevatorStatus.getDirection() == null && callDirection == Direction.UP && floor > elevatorStatus.getFloor()) {
				contacts.add(key);
			}
			// If the elevator is stationary and we get a go down call and elevator is at an above floor than this
			else if(elevatorStatus.getDirection() == null && callDirection == Direction.DOWN && floor < elevatorStatus.getFloor()) {
				contacts.add(key);
			}
		}
		
		//Get the requests if multiple elevators can handle this
		int contactSize = contacts.size();
		int closeness = -1;
		ElevatorContactInfo cont=null;
		if(contactSize>0) {
			for(ElevatorContactInfo x: contacts) {
				int val = Math.abs((floor- elevators.get(x).getFloor()));
				if(closeness == -1) {
					closeness = val;
					cont = x;
				}
				else if(val<closeness){
					closeness = val;
					cont = x;
				}
			}
			return cont;
		}
		
		return null;
	}

	public ElevatorStatus getElevatorStatus(ElevatorContactInfo elevator) {
		return elevators.get(elevator);
	}

	/**
	 * Signals that this state machine has started processing a request
	 */
	public void startProcessing() {
		state = (state == SchedulerState.LISTENING || state == SchedulerState.PROCESSING) ? SchedulerState.PROCESSING : SchedulerState.INVALID;
	}

	/**
	 * Signals that this state machine has finished processing a request
	 */
	public void stopProcessing() {
		state = (state == SchedulerState.PROCESSING || state == SchedulerState.LISTENING) ? SchedulerState.LISTENING : SchedulerState.INVALID;
	}

	/**
	 * Signals that this state machine has stopped running
	 */
	public void stopRunning() {
		state = (state == SchedulerState.LISTENING || state == SchedulerState.STOPPED) ? SchedulerState.STOPPED : SchedulerState.INVALID;
	}

	/**
	 * Returns the state of the scheduler state machine
	 * @return the state of the scheduler
	 */
	public SchedulerState getSchedulerState() { 
		return state;
	}
	
	/**
	 * Returns an unmodifiable version of the storedRequests
	 * @return the unmodifiable list of stored requests
	 */
	public List<ElevatorRequest> getStoredRequests(){
		return Collections.unmodifiableList(storedRequests);
	}
	
	/**
	 * Adds an ElevatorRequest to the list of stored requests
	 * @param storedRequests
	 */
	public void addStoredRequest(ElevatorRequest storedRequests) {
		this.storedRequests.add(storedRequests);
	}
	
	/**
	 * Removes a given ElevatorRequest from the list of stored requests
	 * @param object ElevatorRequest to remove
	 */
	public void removeStoredRequest(ElevatorRequest object) {
		this.storedRequests.remove(object);
	}
	
	/**
	 * Removes a Elevator request from the list of stored requests by index
	 * @param index the index of the request to remove
	 */
	public void removeStoredRequest(int index) {
		this.storedRequests.remove(index);
	}
	
	/**
	 * Method for testing the StateMachine
	 * @param state the state to set the value to
	 */
	public void setState(SchedulerState state) {
		this.state = state;
	}

}
package elevatorsim.scheduler;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.requests.ElevatorStatus;
import elevatorsim.constants.Direction;

/**
 * This is the scheduler for the elevator simulator. 
 * 
 * It is a singleton object responsible for calculating which elevator to send requests to. It also tracks the state of the scheduler subsystem and some information about elevators it is controlling
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
	
	private final ConcurrentMap<InetAddress, ElevatorStatus> elevators = new ConcurrentHashMap<>();
	
	
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
			
			while (state != SchedulerState.STOPPED && state != SchedulerState.INVALID) {
				Thread.sleep(100l);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				System.out.println(this.getName() + " - INFO : Exiting");
				server.stopServer();
			}
		}
	}

	/**
	 * Adds an elevator to the Scheduler System, allowing it to be tracked and considered for elevator requests
	 * @param elevatorAddress the IP address of the elevator system
	 */
	public void addElevator(InetAddress elevatorAddress) {
		elevators.put(elevatorAddress, ElevatorStatus.empty());
	}

	/**
	 * Updates the status of a tracked elevator
	 * 
	 * @param elevatorAddress the IP address of the elevator
	 * @param elevatorStatus the new status of the elevator
	 * @return true if the elevator status is updated, false if the elevator is not being tracked
	 */
	public boolean updateElevator(InetAddress elevatorAddress, ElevatorStatus elevatorStatus) {
		return elevators.replace(elevatorAddress, elevatorStatus) != null;
	}

	/**
	 * Finds an available elevator that can service an Elevator Request
	 * @return an Available Elevator
	 */
	public InetAddress findAvailableElevator() {
		// Iteration 2 - only one elevator, just return it
		return elevators.keySet().stream().findFirst().orElseGet(()->null);
	}
	
	/**
	 * Returns an elevator that could service a call
	 * @param floor the start floor
	 * @param direction the direction to travel
	 * @return the address of the elevator if there is one otherwise null
	 */
	public InetAddress findAvailableElevator(int floor, Direction direction) {
		for (InetAddress key : elevators.keySet()) {
			ElevatorStatus elevatorStatus = elevators.get(key);
			if( (elevatorStatus.getDirection() == Direction.UP && direction == Direction.UP && floor > elevatorStatus.getFloor()) ||
				(elevatorStatus.getDirection() == Direction.DOWN && direction == Direction.DOWN && floor < elevatorStatus.getFloor()) ||
				elevatorStatus.getDirection() == null && floor == elevatorStatus.getFloor() ) {
				return key;
			}
		}
		return null;
	}
	
	public ConcurrentMap<InetAddress, ElevatorStatus> getElevators(){
		return elevators;
	}

	/**
	 * Signals that this state machine has started processing a request
	 */
	public void startProcessing() {
		state = (state == SchedulerState.LISTENING) ? SchedulerState.PROCESSING : SchedulerState.INVALID;
	}

	/**
	 * Signals that this state machine has finished processing a request
	 */
	public void stopProcessing() {
		state = (state == SchedulerState.PROCESSING) ? SchedulerState.LISTENING : SchedulerState.INVALID;
	}

	/**
	 * Signals that this state machine has stopped running
	 */
	public void stopRunning() {
		state = (state == SchedulerState.LISTENING) ? SchedulerState.STOPPED : SchedulerState.INVALID;
	}

	/**
	 * Returns the state of the scheduler state machine
	 * @return the state of the scheduler
	 */
	public SchedulerState getSchedulerState() { 
		return state;
	}
	
	/**
	 * returns an unmodifiable version of the storedRequests
	 * @return the unmodifiable list of stored requests
	 */
	public List<ElevatorRequest> getStoredRequests(){
		return Collections.unmodifiableList(storedRequests);
	}
	
	/**
	 * adds an ElevatorRequest to the list of stored requests
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
	 *  Method for testing the StateMachine
	 * @param state the state to set the value to
	 */
	public void setState(SchedulerState state) {
		this.state = state;
	}

}
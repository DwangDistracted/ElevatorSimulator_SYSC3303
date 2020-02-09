package elevatorsim.scheduler;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import elevatorsim.common.ElevatorStatus;

/**
 * This is the scheduler for the elevator simulator. 
 * 
 * It is a singleton object responsible for calculating which elevator to send requests to. It also tracks the state of the scheduler subsystem,
 * 
 * @author David Wang and Thomas Leung
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

	private Scheduler() throws SocketException {
		super("Scheduler");
		server = SchedulerServer.getInstance();
		state = SchedulerState.STOPPED;
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
		// Iteration 1 - only one elevator, just return it
		return elevators.keySet().stream().findFirst().orElseGet(()->null);
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
}

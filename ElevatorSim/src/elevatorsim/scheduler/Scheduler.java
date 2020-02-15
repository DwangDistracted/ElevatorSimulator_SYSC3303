package elevatorsim.scheduler;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import elevatorsim.common.ElevatorRequest;
import elevatorsim.common.ElevatorStatus;
import elevatorsim.constants.Direction;

/**
 * This is the scheduler for the elevator simulator. 
 * 
 * For Iteration 1 - Takes messages from a single floor and elevator and passes the message between the two.
 * 
 * In future, may be broken up into multiple classes.
 * 
 * @author David Wang and Thomas Leung
 */
public class Scheduler extends Thread {
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

	public void addElevator(InetAddress elevatorAddress) {
		elevators.put(elevatorAddress, ElevatorStatus.empty());
	}

	public boolean updateElevator(InetAddress elevatorAddress, ElevatorStatus elevatorStatus) {
		return elevators.replace(elevatorAddress, elevatorStatus) != null;
	}

	public InetAddress findAvailableElevator() {
		// Iteration 1 - only one elevator, just return it
		return elevators.keySet().stream().findFirst().orElseGet(()->null);
	}
	
	public InetAddress findAvailableElevator(int floor, Direction direction) {
		// Iteration 1 - only one elevator, just return it
		
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

	public void startProcessing() {
		state = (state == SchedulerState.LISTENING) ? SchedulerState.PROCESSING : SchedulerState.INVALID;
	}

	public void stopProcessing() {
		state = (state == SchedulerState.PROCESSING) ? SchedulerState.LISTENING : SchedulerState.INVALID;
	}

	public void stopRunning() {
		state = (state == SchedulerState.LISTENING) ? SchedulerState.STOPPED : SchedulerState.INVALID;
	}

	public SchedulerState getSchedulerState() { 
		return state;
	}
	
	public ArrayList<ElevatorRequest> getStoredRequests(){
		return this.storedRequests;
	}
	
	public void setStoredRequests(ArrayList<ElevatorRequest> storedRequests) {
		this.storedRequests = storedRequests;
	}
}

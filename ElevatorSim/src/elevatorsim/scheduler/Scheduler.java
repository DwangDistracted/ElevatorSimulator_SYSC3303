package elevatorsim.scheduler;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import elevatorsim.common.ElevatorStatus;

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
	private static Scheduler instance;
	
	private final ConcurrentMap<InetAddress, ElevatorStatus> elevators = new ConcurrentHashMap<>();
	private final SchedulerServer server;

	private boolean isRunning = false;

	private Scheduler() throws SocketException {
		super("Scheduler");
		server = SchedulerServer.getInstance();
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
		isRunning = true;

		try {
			server.startServer();
			
			while (isRunning) {
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

	public void stopRunning() {
		isRunning = false;
	}
}

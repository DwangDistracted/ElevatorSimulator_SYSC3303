package elevatorsim.elevator;

import elevatorsim.constants.ElevatorState;

import java.net.SocketException;

/**
 * The elevator class
 * stores information about an elevator
 * 
 * @author Trevor Bivi
 */
public class Elevator extends Thread {
	private boolean isRunning = false;
	private int floorAmount;
	private int floor;
	private ElevatorState elevatorState;
	private boolean[] elevatorLampsOn;
	
	/**
	 * Elevator constructor that stores the amount of floors and
	 * a reference to the scheduler singleton
	 * @param floorAmount The amount of floors the elevator can visit
	 */
	public Elevator ( int floorAmount ) {
		super("Elevator");
		this.floorAmount = floorAmount;
		this.elevatorLampsOn = new boolean[floorAmount];
		this.floor = 1;
		this.elevatorState = ElevatorState.DOOR_OPEN;
	}

	/**
	 * The code to run in an elevator thread
	 * Currently the elevator only needs to respond to the scheduler so
	 * The thread just sleeps to allow other threads to run
	 */
	public void run() {
		ElevatorServer server = null;
		isRunning = true;

		try {
			server = new ElevatorServer(this);
			server.startServer();
			
			while (isRunning) {
				Thread.sleep(100l);
			}
		} catch (SocketException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				System.out.println(this.getName() + " - INFO : Exiting");
				server.stopServer();
			}
		}
	}
	
	/**
	 * get state of an elevator lamp
	 * @param lampIndex the index of the floor represented by the lamp (floor# - 1)
	 * @return whether or not the lamp is on
	 */
	public boolean getLampIsOn(int lampIndex) {
		return this.elevatorLampsOn[lampIndex];
	}
	
	/**
	 * Sets the state of a lamp
	 * @param lampIndex the index of the floor represented by the lamp (floor# - 1)
	 * @param isOn
	 */
	public void setLampIsOn(int lampIndex, boolean isOn) {
		this.elevatorLampsOn[lampIndex] = isOn;
	}

	/**
	 * Initiates the stopping of the server
	 */
	public void stopRunning() {
		isRunning = false;
	}
	
	/**
	 * The floor number the elevator is currently on.
	 * @return
	 */
	public int getFloor() {
		return this.floor;
	}
	
	/**
	 * updates the floor the elevator is on
	 * @param newFloor the new floor number
	 */
	public void setFloor(int newFloor) {
		this.floor = newFloor;
	}
	
	/**
	 * Returns the state of the elevator state machine
	 * @return the state
	 */
	public ElevatorState getElevatorState() {
		return this.elevatorState;
	}
	
	/**
	 * Sets the state of the elevator state machine
	 * @param elevatorState the new state
	 */
	public void setElevatorState(ElevatorState elevatorState) {
		this.elevatorState = elevatorState;
	}
}
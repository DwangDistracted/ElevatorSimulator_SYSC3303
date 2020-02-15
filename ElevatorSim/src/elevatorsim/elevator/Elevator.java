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
	
	public boolean getLampIsOn(int lampIndex) {
		return this.elevatorLampsOn[lampIndex];
	}
	
	public void setLampIsOn(int lampIndex, boolean isOn) {
		//System.out.print("SET LAMP" + Integer.toString(lampIndex) + " " + Boolean.toString(isOn));
		this.elevatorLampsOn[lampIndex] = isOn;
	}

	public void stopRunning() {
		isRunning = false;
	}
	
	public int getFloor() {
		return this.floor;
	}
	
	public void setFloor(int newFloor) {
		this.floor = newFloor;
	}
	
	public ElevatorState getElevatorState() {
		return this.elevatorState;
	}
	
	public void setElevatorState(ElevatorState elevatorState) {
		this.elevatorState = elevatorState;
	}
}
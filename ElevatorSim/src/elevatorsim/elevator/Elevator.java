package elevatorsim.elevator;

import elevatorsim.common.MessageReciever;

import java.net.SocketException;

import elevatorsim.common.ElevatorRequest;

/**
 * The elevator class
 * Currently this is just used to receive messages from the scheduler
 * and redirect them to the floors
 * 
 * @author Trevor Bivi (101045460)
 */
public class Elevator extends Thread implements MessageReciever {
	private boolean isRunning = false;
	private int floorAmount;
	
	/**
	 * Elevator constructor that stores the amount of floors and
	 * a reference to the scheduler singleton
	 * @param floorAmount The amount of floors the elevator can visit
	 */
	public Elevator ( int floorAmount ) {
		super("Elevator");
		this.floorAmount = floorAmount;
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
	 * Receives message requests from the schedule and then
	 * sends the messages back to the scheduler but
	 * targeting floors instead of elevators
	 * 
	 * @param message The MessageRequest that should be redirected to floors
	 */
	@Override
	public void receive(ElevatorRequest message) {
		System.out.println("Elevator received message: " + message.toString());
	}

	public void stopRunning() {
		isRunning = false;
	}
}
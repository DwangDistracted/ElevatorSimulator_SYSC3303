package elevatorsim.elevator;

import elevatorsim.common.MessageReciever;
import elevatorsim.common.MessageRequest;
import elevatorsim.enums.MessageDestination;
import elevatorsim.scheduler.Scheduler;

/**
 * The elevator class
 * Currently this is just used to receive messages from the scheduler
 * and redirect them to the floors
 */
public class Elevator extends Thread implements MessageReciever {
	private Scheduler scheduler;
	private int floorAmount;
	
	/**
	 * Elevator constructor that stores the amount of floors and
	 * a reference to the scheduler singleton
	 * @param floorAmount The amount of floors the elevator can visit
	 */
	public Elevator ( int floorAmount ) {
		this.floorAmount = floorAmount;
		this.scheduler = Scheduler.getInstance();
	}

	/**
	 * The code to run in an elevator thread
	 * Currently the elevator only needs to respond to the scheduler so
	 * The thread just sleeps to allow other threads to run
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	public void recieve(MessageRequest message) {
		// TODO Auto-generated method stub
		System.out.println("Elevator received message: " + message.toString() );
		scheduler.sendMessage(MessageDestination.FLOORS, message);
	}
}
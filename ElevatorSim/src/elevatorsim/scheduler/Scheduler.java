package elevatorsim.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elevatorsim.common.MessageReciever;
import elevatorsim.common.MessageRequest;
import elevatorsim.elevator.Elevator;
import elevatorsim.enums.MessageDestination;
import elevatorsim.floor.Floor;

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
	private List<Floor> floors;
	private List<Elevator> elevators;
	
	private BlockingQueue<MessageRequestWrapper> messageRequests;
	
	/**
	 * Creates a Scheduler Object with no floors and elevators configured yet.
	 */
	public Scheduler() {
		floors = new ArrayList<>();
		elevators = new ArrayList<>();
		messageRequests = new LinkedBlockingQueue<>();
	}
	
	/**
	 * Adds a floor to this scheduler to manage
	 * @param floor - the floor to add
	 */
	public void addFloor(Floor floor) {
		floors.add(floor);
	}

	/**
	 * Adds an elevator to this scheduler to manage
	 * @param elevator - the elevator to add
	 */
	public void addElevator(Elevator elevator) {
		elevators.add(elevator);
	}
	
	/**
	 * Tells Scheduler to Send a MessageRequest to the indicated destination.
	 * 
	 * @param destination - the intended destination of the message
	 * @param message - the message
	 */
	public void sendMessage(MessageDestination destination, MessageRequest message) {
		messageRequests.add(new MessageRequestWrapper(destination, message));
	}
	
	/**
	 * Scheduler thread will not run unless there is at least one floor and one elevator it is managing
	 */
	@Override
	public void run() {
		while (!floors.isEmpty() && !elevators.isEmpty()) {
			try {
				MessageRequestWrapper message = messageRequests.take();
				
				// determine the destination of the message
				// WATCH - as this gets more complex, we may need to move this to another method/class
				MessageReciever target;
				if (message.destination == MessageDestination.ELEVATORS) {
					// iteration one - only one elevator
					target = elevators.get(0);
				} else if (message.destination == MessageDestination.FLOORS) {
					// iteration one - only one floor
					target = floors.get(0);
				} else {
					throw new IllegalArgumentException();
				}
				
				// send the message to destination
				target.recieve(message.message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Wraps the MessageRequest Class extending it with destination data.
	 * @author David Wang and Thomas Leung
	 *
	 */
	private class MessageRequestWrapper {
		public final MessageDestination destination;
		public final MessageRequest message;
		
		MessageRequestWrapper(MessageDestination destination, MessageRequest message) {
			this.destination = destination;
			this.message = message;
		}
	}
}

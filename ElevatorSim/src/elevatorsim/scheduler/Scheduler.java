package elevatorsim.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elevatorsim.common.MessageReciever;
import elevatorsim.common.MessageRequest;
import elevatorsim.elevator.Elevator;
import elevatorsim.enums.MessageDestination;
import elevatorsim.floor.FloorController;

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
	private FloorController floorSystem;
	private List<Elevator> elevators;
	
	private BlockingQueue<MessageRequestWrapper> messageRequests;
	private static Scheduler instance;
	
	/**
	 * Creates a Scheduler Object with no floors and elevators configured yet.
	 */
	private Scheduler() {
		elevators = new ArrayList<>();
		messageRequests = new LinkedBlockingQueue<>();
	}
	
	public static Scheduler getInstance() {
		if (instance == null) {
			instance = new Scheduler(); 
		}
		return instance;
	}
	
	/**
	 * Adds a floor to this scheduler to manage
	 * @param floor - the floor to add
	 */
	public void setFloorController(FloorController floorSystem) {
		this.floorSystem = floorSystem;
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
		while (floorSystem != null && !elevators.isEmpty()) {
			try {
				MessageRequestWrapper message = messageRequests.take();
				
				// determine the destination of the message
				// WATCH - as this gets more complex, we may need to move this to another method/class
				MessageReciever target;
				if (message.destination == MessageDestination.ELEVATORS) {
					// iteration one - only one elevator
					target = elevators.get(0);
				} else if (message.destination == MessageDestination.FLOORS) {
					target = floorSystem;
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
	public class MessageRequestWrapper {
		public final MessageDestination destination;
		public final MessageRequest message;
		
		MessageRequestWrapper(MessageDestination destination, MessageRequest message) {
			this.destination = destination;
			this.message = message;
		}
	}
	
	/**
	 * Get the floor controller
	 * @return the floor controller
	 */
	public FloorController getFloorController() {
		return floorSystem;
	}

	/**
	 * Get the list of elevator
	 * @return the list of elevator
	 */
	public List<Elevator> getElevator() {
		return elevators;
	}
	
	/**
	 * Get the list of message requests
	 * 
	 * @return a list of message requests
	 */
	public BlockingQueue<MessageRequestWrapper> getMessageRequests() {
		return messageRequests;
	}
}

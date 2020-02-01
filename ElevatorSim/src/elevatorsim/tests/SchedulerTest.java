package elevatorsim.tests;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorsim.common.MessageRequest;
import elevatorsim.elevator.Elevator;
import elevatorsim.enums.MessageDestination;
import elevatorsim.floor.FloorController;
import elevatorsim.scheduler.Scheduler;
import elevatorsim.scheduler.Scheduler.MessageRequestWrapper;

/**
 * A test class to test the Scheduler functionality
 * 
 * @author Thomas Leung
 *
 */
class SchedulerTest {
	private Scheduler scheduler;
	private FloorController floorController;
	private Elevator elevator;
	private HashMap<Integer, MessageRequest> requestMap;
	private MessageRequest request1, request2;

	/**
	 * Setup objects to be utilized in the test of the Scheduler class
	 * 
	 * @throws Exception Ant exceptions thrown by the tests
	 */
	@BeforeEach
	void setUp() throws Exception {
		scheduler = Scheduler.getInstance();
		requestMap = new HashMap<Integer, MessageRequest>();
		request1 = new MessageRequest("14:05:15.0", "2", "Up", "5");
		request2 = new MessageRequest("05:05:23.0", "1", "Up", "3");
		requestMap.put(1, request1);
		requestMap.put(2, request2);
		requestMap.put(3, new MessageRequest("10:05:12.0", "7", "down", "6"));
		requestMap.put(4, new MessageRequest("12:05:12.0", "4", "down", "1"));
		floorController = new FloorController("floorController", 10, requestMap);
		elevator = new Elevator(10);
	}

	/**
	 * Test if we can get one scheduler
	 */
	@Test
	void getSchedulerTest() {
		assertFalse(scheduler == null);
		scheduler = Scheduler.getInstance();
		assertFalse(scheduler == null);
	}

	/**
	 * Test if the add a floor to scheduler
	 */
	@Test
	void setFloorControllerTest() {
		scheduler.setFloorController(floorController);
		assertTrue(scheduler.getFloorController().equals(floorController));
		assertFalse(scheduler.getFloorController().equals(null));
	}

	/**
	 * Test addElevator method
	 */
	@Test
	void addElevatorTest() {
		// elevator is an ArrayList
		assertTrue(scheduler.getElevator().size() == 0);
		scheduler.addElevator(elevator);
		assertTrue(scheduler.getElevator().size() == 1);
		scheduler.addElevator(elevator);
		assertTrue(scheduler.getElevator().size() == 2);
	}

	/**
	 * Test for sendMessage method
	 */
	@Test
	void setMessageTest() {
		assertTrue(scheduler.getMessageRequests().size() == 0);
		scheduler.sendMessage(MessageDestination.FLOORS, request1);
		assertTrue(scheduler.getMessageRequests().size() == 1);
		scheduler.sendMessage(MessageDestination.ELEVATORS, request2);
		assertTrue(scheduler.getMessageRequests().size() == 2);
		MessageRequestWrapper messageRequest = scheduler.getMessageRequests().remove();
		assertTrue(messageRequest.destination.equals(MessageDestination.FLOORS));
		messageRequest = scheduler.getMessageRequests().remove();
		assertTrue(messageRequest.destination.equals(MessageDestination.ELEVATORS));
	}

}

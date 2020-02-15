package elevatorsim.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorsim.common.requests.ElevatorArrivalRequest;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.constants.Direction;
import elevatorsim.floor.Floor;

/**
 * A test class to test the Floor Class functionality
 * @author Trevor Bivi (101045460)
 *
 */
class FloorTest {
	private Floor flr;
	private ElevatorRequest request, request2, request3, request4;
	private ElevatorArrivalRequest request5;

	/**
	 * Setup objects to be utilized in the testing of the Floor class
	 * @throws Exception Any exceptions thrown by the tests
	 */
	@BeforeEach
	void setUp() throws Exception {
		request = new ElevatorRequest("14:05:15.0", "2", "Up", "5");
		request2 = new ElevatorRequest("05:05:23.0", "1", "Up", "3");
		request3 = new ElevatorRequest("10:05:12.0", "7", "down", "6");
		request4 = new ElevatorRequest("12:05:12.0", "4", "down", "1");
		
		request5 = new ElevatorArrivalRequest(1,2, Direction.UP);
		flr = new Floor(1,10);
	}

	/**
	 * Tests for the readRequest test. It checks that the requests are 
	 * going to the correct arraylist in the Floor class
	 */
	@Test
	void readRequesttest() {
		// check if upRequests are set to the correct arraylist
		assertTrue(flr.getActiveUpRequests().isEmpty());
		flr.readRequest(request);
		assertFalse(flr.getActiveUpRequests().isEmpty());
		assertEquals(flr.getActiveUpRequests().get(0), request);
		assertTrue(flr.getActiveDownRequests().isEmpty());
		// check for the downRequests
		flr.readRequest(request3);
		assertFalse(flr.getActiveDownRequests().isEmpty());
		assertEquals(flr.getActiveDownRequests().get(0), request3);
		assertEquals(flr.getActiveUpRequests().size(), 1);
	}

	/**
	 * It checks the direction of the traveling elevator and tests that
	 * it correctly clears any requests for people going in that direction
	 * 
	 */
	@Test
	void loadPassengerstest() {
		// add some values to the floor, 2 each
		flr.readRequest(request);
		flr.readRequest(request2);
		flr.readRequest(request3);
		flr.readRequest(request4);
		// check if the list of active requests has the 2 entries each
		assertEquals(flr.getActiveUpRequests().size(), 2);
		assertEquals(flr.getActiveDownRequests().size(), 2);

		// check if requests are cleared for a going up elevator arrival
		flr.loadPassengers(request5);
		assertTrue(flr.getActiveUpRequests().isEmpty());

		// check if requests are cleared for a going down elevator arrival
		flr.loadPassengers(request5);
		assertTrue(flr.getActiveDownRequests().isEmpty());
	}

	/**
	 * Tests the getter for floorNumber to ensure that it returns the correct
	 * floor number value
	 */
	@Test
	void getFloorNumbertest() {
		// Check that the correct floor number has been set
		assertTrue(flr.getFloorNumber() == 1);
	}

}

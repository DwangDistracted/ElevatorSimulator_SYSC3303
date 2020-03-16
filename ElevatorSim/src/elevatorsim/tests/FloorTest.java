package elevatorsim.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorsim.common.constants.Direction;
import elevatorsim.common.requests.ElevatorArrivalRequest;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.floor.Floor;

/**
 * A test class to test the Floor Class functionality.
 * @author Trevor Bivi, Thomas Leung
 *
 */
class FloorTest {
	private Floor flr, flr7;
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
		request3 = new ElevatorRequest("10:05:12.0", "7", "Down", "6");
		request4 = new ElevatorRequest("12:05:12.0", "4", "Down", "1");
		
		request5 = new ElevatorArrivalRequest(1,2, Direction.UP);
		flr = new Floor(1,10);
		// assume you are on the seventh floor
		flr7 = new Floor(7, 10);
		
	}

	/**
	 * Tests for the reading UP request from readRequest method. It checks that 
	 * the requests are going to the correct location in the Floor class.
	 */
	@Test
	void readRequestForUptest() {
		// check if upRequests are set to the correct arrayList
		assertTrue(flr.getActiveUpDest().isEmpty());
		flr.readRequest(request);
		assertFalse(flr.getActiveUpDest().isEmpty());
		// you have to convert a set to a list in order to access the content
		List<Integer> listUp = new ArrayList<Integer>(flr.getActiveUpDest());
		assertEquals(request.getDestFloor(), listUp.get(0));
		// check the direction lamp
		assertTrue(flr.getBtnLamp().getUpLamp());
		flr.readRequest(request2);
		listUp = new ArrayList<Integer>(flr.getActiveUpDest());
		assertEquals(request2.getDestFloor(), listUp.get(0));
		assertTrue(flr.getBtnLamp().getUpLamp());
	}
	
	/**
	 * Tests for the reading DOWN request from readRequest method. It checks that 
	 * the requests are going to the correct location in the Floor class.
	 */
	@Test
	void readRequestForDowntest() {
		// check for the downRequests
		assertTrue(flr7.getActiveDownDest().isEmpty());
		flr7.readRequest(request3);
		assertFalse(flr7.getActiveDownDest().isEmpty());
		List<Integer> listDown = new ArrayList<Integer>(flr7.getActiveDownDest());
		assertEquals(request3.getDestFloor(), listDown.get(0));
		assertFalse(flr7.getBtnLamp().getUpLamp());
		assertTrue(flr7.getBtnLamp().getDownLamp());
		assertEquals(flr7.getActiveDownDest().size(), 1);
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
		List<Integer> listUp = new ArrayList<Integer>(flr.getActiveUpDest());
		assertEquals(listUp.size(), 2);
		List<Integer> listDown = new ArrayList<Integer>(flr.getActiveUpDest());
		assertEquals(listDown.size(), 2);

		// check if requests are cleared for a going up elevator arrival
		flr.loadPassengers(request5);
		listUp = new ArrayList<Integer>(flr.getActiveUpDest());
		assertTrue(listUp.isEmpty());

		// check if requests are cleared for a going down elevator arrival
		request5.setElevatorDirection(Direction.DOWN);
		flr.loadPassengers(request5);
		listDown = new ArrayList<Integer>(flr.getActiveUpDest());
		assertTrue(listDown.isEmpty());
	}

	/**
	 * Tests the getter for floorNumber to ensure that it returns the correct
	 * floor number value
	 */
	@Test
	void getFloorNumbertest() {
		// Check that the correct floor number has been set
		assertTrue(flr.getFloorNumber() == 1);
		assertTrue(flr7.getFloorNumber() == 7);
	}

}

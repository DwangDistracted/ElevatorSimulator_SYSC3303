package elevatorsim.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorsim.elevator.Elevator;

class ElevatorTest {
	Elevator elevator;
	
	@BeforeEach
	void setUp() throws Exception {
		elevator = new Elevator(7);
		
		
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}

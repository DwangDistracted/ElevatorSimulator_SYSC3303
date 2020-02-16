package elevatorsim.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorsim.common.requests.ElevatorStateChange;
import elevatorsim.constants.ElevatorState;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.elevator.Elevator;
import elevatorsim.elevator.ElevatorServer;

/**
 * Test case for the elevator subsytem state machine
 * @author Rahul Anilkumar
 *
 */
class ElevatorTest {
	Elevator elevator;
	ElevatorServer es;
	DatagramPacket sendPacket;

	@BeforeEach
	void setUp() throws Exception {
		elevator = new Elevator(7);
		es = new ElevatorServer(elevator);
	}

	/**
	 * Helper method for creating requests
	 * @param stch request to receive
	 * @return the request to be used by the elevator subsystem
	 * @throws IOException
	 */
	public DatagramPacket createPacket(ElevatorStateChange stch) throws IOException {
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		message.write(NetworkConstants.MessageTypes.STATUS.getMarker());
		message.write(NetworkConstants.NULL_BYTE);
		stch.serialize(message);
		message.write(NetworkConstants.NULL_BYTE);
		message.close();
		sendPacket = new DatagramPacket(message.toByteArray(), message.size());
		return sendPacket;
	}

	/**
	 * Check for state transitions with mocked requests that a scheduler would send
	 * for opening, closing and moving up and down
	 * 
	 * @throws IOException
	 */
	@Test
	void testStateMachineTransitionsUp() throws IOException {
		DatagramPacket recPacket;
		// Check the default elevator state
		assertEquals(ElevatorState.DOOR_OPEN, elevator.getElevatorState());
		// Receive a door close request
		recPacket = createPacket(new ElevatorStateChange(ElevatorState.DOOR_CLOSED));
		es.handleElevatorStateChange(recPacket);
		assertEquals(ElevatorState.DOOR_CLOSING, elevator.getElevatorState());
		// mock the door closed response that the scheduler would set
		elevator.setElevatorState(ElevatorState.DOOR_CLOSED);
		assertEquals(ElevatorState.DOOR_CLOSED, elevator.getElevatorState());
		// Receive a motor moving upwards Request
		recPacket = createPacket(new ElevatorStateChange(ElevatorState.MOTOR_UP));
		es.handleElevatorStateChange(recPacket);
		assertEquals(ElevatorState.MOTOR_UP, elevator.getElevatorState());
		// mockElevatorStopped after arrival
		elevator.setElevatorState(ElevatorState.DOOR_CLOSED);
		assertEquals(ElevatorState.DOOR_CLOSED, elevator.getElevatorState());
		// Request of arrival and door opening
		recPacket = createPacket(new ElevatorStateChange(ElevatorState.DOOR_OPEN));
		es.handleElevatorStateChange(recPacket);
		assertEquals(ElevatorState.DOOR_OPENING, elevator.getElevatorState());
	}

	/**
	 * Check for state transitions with mocked requests that a scheduler would send
	 * for opening closing, and moving up and down an elevator
	 * 
	 * @throws IOException
	 */
	@Test
	void testStateMachineTransitionsDown() throws IOException {
		DatagramPacket recPacket;
		// Check the default elevator state
		assertEquals(ElevatorState.DOOR_OPEN, elevator.getElevatorState());
		// Receive a door close request
		recPacket = createPacket(new ElevatorStateChange(ElevatorState.DOOR_CLOSED));
		es.handleElevatorStateChange(recPacket);
		assertEquals(ElevatorState.DOOR_CLOSING, elevator.getElevatorState());
		// mock the door closed response that the scheduler would set
		elevator.setElevatorState(ElevatorState.DOOR_CLOSED);
		assertEquals(ElevatorState.DOOR_CLOSED, elevator.getElevatorState());
		// Receive a motor moving downwards Request
		recPacket = createPacket(new ElevatorStateChange(ElevatorState.MOTOR_DOWN));
		es.handleElevatorStateChange(recPacket);
		assertEquals(ElevatorState.MOTOR_DOWN, elevator.getElevatorState());
		// mockElevatorStopped after arrival
		elevator.setElevatorState(ElevatorState.DOOR_CLOSED);
		assertEquals(ElevatorState.DOOR_CLOSED, elevator.getElevatorState());
		// Request of arrival and door opening
		recPacket = createPacket(new ElevatorStateChange(ElevatorState.DOOR_OPEN));
		es.handleElevatorStateChange(recPacket);
		assertEquals(ElevatorState.DOOR_OPENING, elevator.getElevatorState());
	}

}

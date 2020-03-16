package elevatorsim.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorsim.scheduler.Scheduler;
import elevatorsim.scheduler.Scheduler.SchedulerState;


/**
 * A test class to test the Scheduler functionality
 * 
 * @author Thomas Leung, Rahul Anilkumar
 *
 */
class SchedulerTest {
	private Scheduler scheduler;

	/**
	 * Setup objects to be utilized in the test of the Scheduler class
	 * 
	 * @throws Exception Ant exceptions thrown by the tests
	 */
	@BeforeEach
	void setUp() throws Exception {
		scheduler = Scheduler.getInstance();
	}

	/**
	 * Test if we can get one scheduler instance
	 */
	@Test
	void getSchedulerTest() {
		assertFalse(scheduler == null);
		scheduler = Scheduler.getInstance();
		assertFalse(scheduler == null);
	}

	/**
	 * Test if the valid state transitions occur as intended
	 */
	@Test
	void StateMachineValidTransitionTest() {
		scheduler.setState(SchedulerState.LISTENING);
		//Check that the initial state of the machine is LISTENING
		assertEquals(SchedulerState.LISTENING, scheduler.getSchedulerState());
		//Transition to the Processing state and check the state is in PROCESSING
		scheduler.startProcessing();
		assertEquals(SchedulerState.PROCESSING,scheduler.getSchedulerState());
		//Transition back to the Listening state after processing the requests
		scheduler.stopProcessing();
		assertEquals(SchedulerState.LISTENING, scheduler.getSchedulerState());
		//Transition to the Stopped state of the machine
		scheduler.stopRunning();
		assertEquals(SchedulerState.STOPPED,scheduler.getSchedulerState());
	}
	
	/**
	 * Test if the state machine goes to the invalid state for StartProcessing request
	 */
	@Test
	void InvalidStateTransitionStartProcessingTest() {
		scheduler.setState(SchedulerState.LISTENING);
		//Check that the initial state of the machine is LISTENING
		assertEquals(SchedulerState.LISTENING, scheduler.getSchedulerState());	
		//Transition to the Processing state and check the state is in PROCESSING
		scheduler.startProcessing();
		assertEquals(SchedulerState.PROCESSING,scheduler.getSchedulerState());
		//Transition to an invalid state after processing the requests to check the Invalid State
		scheduler.startProcessing();
		assertEquals(SchedulerState.INVALID, scheduler.getSchedulerState());
	}
	
	/**
	 * Test if the state machine goes to the invalid state for StopProcessing request
	 */
	@Test
	void InvalidStateTransitionStopProcessingTest() {
		scheduler.setState(SchedulerState.LISTENING);
		//Check that the initial state of the machine is LISTENING
		assertEquals(SchedulerState.LISTENING, scheduler.getSchedulerState());	
		//Try to transition to the Listening state without hitting the Processing state and check the state is in INVALID
		scheduler.stopProcessing();
		assertEquals(SchedulerState.INVALID, scheduler.getSchedulerState());
	}
	
	/**
	 * Test if the state machine goes to the invalid state for StopProcessing request
	 */
	@Test
	void InvalidStateTransitionStopRunningTest() {
		scheduler.setState(SchedulerState.LISTENING);
		//Check that the initial state of the machine is LISTENING
		assertEquals(SchedulerState.LISTENING, scheduler.getSchedulerState());	
		//Transition to the Processing state and check the state is in PROCESSING
		scheduler.startProcessing();
		assertEquals(SchedulerState.PROCESSING,scheduler.getSchedulerState());
		//Try to transition to stopped state while still in the processing state and check that the state is INVALID
		scheduler.stopRunning();
		assertEquals(SchedulerState.INVALID, scheduler.getSchedulerState());
	}

	
}

package elevatorsim.floor;

import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.Queue;

import elevatorsim.common.requests.ElevatorArrivalRequest;
import elevatorsim.common.requests.ElevatorDestinationRequest;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.requests.MessageReciever;
import elevatorsim.common.requests.Request;
import elevatorsim.constants.NetworkConstants;

public class FloorEvents extends Thread implements MessageReciever {
	private Queue<Request<?>> eventQueue;
	private FloorController controller;
	private FloorServer server;
	private boolean running;
	
	
	public FloorEvents(FloorController controller){
		this.controller = controller;
		eventQueue = new ArrayDeque<Request<?>>();
	}
	
	/**
	 * Processes floor events
	 */
	public void run() {
		running = true;
		
		try {
			this.server = new FloorServer(this);
			server.startServer();
			Thread.sleep(NetworkConstants.DELAY_SERVER_START_MS);
			while(running) {
				processEvents();
				Thread.sleep(500);
			}
		} catch (InterruptedException | SocketException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				server.sendExitRequest();

				System.out.println(this.getName() + " - INFO : Exiting");
				server.stopServer();
			}
		}
	}
	
	/**
	 * Processes all the floor events and then sends appropriate responses
	 * to other modules when necessary
	 */
	private void processEvents() {
		while(!eventQueue.isEmpty()) {
			Request<?> request = eventQueue.poll();
			
			if(request instanceof ElevatorArrivalRequest) {
				Floor floor = controller.getFloor(((ElevatorArrivalRequest) request).getArrivalFloor());
				floor.loadPassengers((ElevatorArrivalRequest) request);
				
			} else if(request instanceof ElevatorRequest) {
				Floor floor = controller.getFloor(((ElevatorRequest) request).getStartFloor());
				floor.readRequest((ElevatorRequest) request);
				
				server.sendElevatorRequest((ElevatorRequest) request);
			}
		}
	}

	
	/**
	 * This method Receives the arrival signal sent from the scheduler
	 * indicating an elevator has arrived at a particular floor and then 
	 * notifies the correct floor.
	 * @param message - a floor event
	 */
	@Override
	public void receive(Request<?> message) {
		eventQueue.add(message);
	}
	
	
	/**
	 * Stops the floor subsystem from processing events
	 */
	public void stopEventPolling() {
		this.running = false;
	}

}

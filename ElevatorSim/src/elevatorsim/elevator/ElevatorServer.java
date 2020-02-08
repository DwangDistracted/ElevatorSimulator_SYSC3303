package elevatorsim.elevator;

import java.net.DatagramPacket;
import java.net.SocketException;

import elevatorsim.common.ElevatorStatus;
import elevatorsim.constants.ElevatorEvent;
import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.Role;
import elevatorsim.server.UDPServer;
import elevatorsim.util.DatagramPacketUtils;

/**
 * The Server that serves an Elevator instance
 * 
 * @author David Wang
 */
public class ElevatorServer extends UDPServer {
	private final Elevator elevator;
	
	/**
	 * Creates a new Elevator Server
	 * @param elevator the elevator instance this server belongs to
	 * @throws SocketException
	 */
	public ElevatorServer(Elevator elevator) throws SocketException {
		super("ElevatorServer"); // TODO - number the elevators once we have many // Don't bind a fixed port. There will need to many of these.
		this.elevator = elevator;
	}

	@Override
	public Role getRole() {
		return Role.ELEVATORS;
	}

	@Override
	public DatagramPacket handleElevatorRequest(DatagramPacket request) {
		/*
		 * Passes the Elevator Request in the request message to the owning elevator 
		 */
		
		String requestAsString = DatagramPacketUtils.getMessageBodyAsString(request);
		
		System.out.println("ElevatorServer - INFO : Received an Elevator Request " + requestAsString);
		// TODO - deserialize and pass to elevator
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	@Override
	public DatagramPacket handleElevatorStatus(DatagramPacket request) {
		/*
		 * 	Build and Send a Status Update to the Scheduler
		 */
		// TODO - Determine how and what to pass to scheduler - could be that this is superfluous
		sendStatusMessage(new ElevatorStatus("This is an Elevator Status"));
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Stops this Server and Elevator SubSystem
	 */
	@Override
	public DatagramPacket handleExitRequest(DatagramPacket request) {
		elevator.stopRunning();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Sends an Event Message to the Scheduler
	 * @param event the type of event to send
	 */
	public void sendEventMessage(ElevatorEvent event) {
		// TODO
	}

	/**
	 * Sends a Status Update to the Scheduler
	 * @param status the status to send
	 */
	public void sendStatusMessage(ElevatorStatus status) {
		// TODO
	}
}

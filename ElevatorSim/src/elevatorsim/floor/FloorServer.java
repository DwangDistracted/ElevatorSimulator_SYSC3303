package elevatorsim.floor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import elevatorsim.common.requests.ElevatorArrivalRequest;
import elevatorsim.common.requests.ElevatorDestinationRequest;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.constants.TimeConstants;
import elevatorsim.server.UDPServer;

/**
 * The Server that serves a FloorController Instance
 * TODO - Like FloorController - There should only be 1. Maybe enforce Singleton on both FloorController and FloorServer
 * 
 * @author David Wang
 */
public class FloorServer extends UDPServer {
	private final FloorEvents floorEvents;
	
	/**
	 * Creates a new FloorServer. Can only be called once.
	 * @param floorSystem
	 * @throws SocketException
	 */
	public FloorServer(FloorEvents floorSystem) throws SocketException {
		super("FloorSystemServer", NetworkConstants.FLOOR_RECIEVE_PORT);
		this.floorEvents = floorSystem;
	}
	
	@Override
	public Role getRole() {
		return Role.FLOORS;
	}

	@Override
	public DatagramPacket handleElevatorEvent(DatagramPacket request) {
		ByteArrayInputStream baos = new ByteArrayInputStream(request.getData());
		ElevatorArrivalRequest arrivalRequest;
		
		try {
			ObjectInputStream oos = new ObjectInputStream(baos);
			arrivalRequest = (ElevatorArrivalRequest)oos.readObject();
			
			floorEvents.receive(arrivalRequest);
			baos.close();
			oos.close();
		} catch (ClassNotFoundException | IOException e) {
			return MessagePackets.Responses.RESPONSE_FAILURE();
		}
		
		
		
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Sends an Elevator Request to the Scheduler
	 * @param request the elevator request
	 */
	public void sendElevatorRequest(ElevatorRequest request) {
		
		try {
			sender.send(MessagePackets.generateElevatorRequest(request), InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
		} catch (UnknownHostException e) {
			System.out.println("FloorServer - ERROR: Could not find Scheduler Host. Check Network Set Up");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("FloorServer - ERROR: Could not find Request Message");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Sends an Elevator Request to the Scheduler
	 * @param request the elevator request
	 */
	public void sendDestinationRequest(ElevatorDestinationRequest request) {
		try {
			sender.send(MessagePackets.generateElevatorButtonRequest(request), InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
		} catch (UnknownHostException e) {
			System.out.println("FloorServer - ERROR: Could not find Scheduler Host. Check Network Set Up");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("FloorServer - ERROR: Could not find Request Message");
			e.printStackTrace();
		}
	}
	
	/**
	 * Notifies the Scheduler that the Floor System is terminating
	 */
	public void sendExitRequest() {
		try {
			Thread.sleep(TimeConstants.initiateExitDelay);
			sender.send(MessagePackets.REQUEST_SYSTEM_EXIT(), InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
		} catch (UnknownHostException e) {
			System.out.println("FloorServer - ERROR: Could not find Scheduler Host. Check Network Set Up");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("FloorServer - ERROR: Failed to sleep thread");
			e.printStackTrace();
		}
	}
}

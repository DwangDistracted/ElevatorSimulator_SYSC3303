package elevatorsim.floor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import elevatorsim.common.ElevatorRequest;
import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.server.UDPServer;

/**
 * The Server that serves a FloorController Instance
 * TODO - Like FloorController - There should only be 1. Maybe enforce Singleton on both FloorController and FloorServer
 * 
 * @author David Wang
 */
public class FloorServer extends UDPServer {
	private final FloorController controller;
	
	/**
	 * Creates a new FloorServer. Can only be called once.
	 * @param floorSystem
	 * @throws SocketException
	 */
	public FloorServer(FloorController floorSystem) throws SocketException {
		super("FloorSystemServer", NetworkConstants.FLOOR_RECIEVE_PORT);
		this.controller = floorSystem;
	}
	
	@Override
	public Role getRole() {
		return Role.FLOORS;
	}

	@Override
	public DatagramPacket handleElevatorEvent(DatagramPacket request) {
		// TODO
		return MessagePackets.Responses.RESPONSE_NOT_APPLICABLE();
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
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Notifies the Scheduler that the Floor System is terminating
	 */
	public void sendExitRequest() {
		try {
			sender.send(MessagePackets.REQUEST_SYSTEM_EXIT(), InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
		} catch (UnknownHostException e) {
			System.out.println("FloorServer - ERROR: Could not find Scheduler Host. Check Network Set Up");
			e.printStackTrace();
		}
	}
}

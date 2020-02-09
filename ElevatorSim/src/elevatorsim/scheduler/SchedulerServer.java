package elevatorsim.scheduler;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import elevatorsim.common.ElevatorStatus;
import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.server.UDPServer;
import elevatorsim.util.DatagramPacketUtils;	

/**
 * Creates a Server that Services the Scheduler 
 * 
 * @author David Wang
 */
public class SchedulerServer extends UDPServer {
	private static SchedulerServer instance;

	private final ConcurrentMap<InetAddress, Integer> elevators;

	private InetAddress floorSystem = null; 

	private SchedulerServer() throws SocketException {
		super("SchedulerServer", NetworkConstants.SCHEDULER_PORT);
		this.elevators = new ConcurrentHashMap<>();
	}

	public static SchedulerServer getInstance() throws SocketException {
		if (instance == null) { 
			instance = new SchedulerServer(); 
		}
		return instance;
	}
	
	@Override
	public Role getRole() {
		return Role.SCHEDULER;
	}

	/**
	 * Forwards An Elevator Request to a Registered Elevator that can Service it
	 */
	@Override
	public DatagramPacket handleElevatorRequest(DatagramPacket request) {
		Scheduler.getInstance().startProcessing();

		InetAddress availableElevator = Scheduler.getInstance().findAvailableElevator();

		if (availableElevator == null) {
			Scheduler.getInstance().stopProcessing();
			return MessagePackets.Responses.RESPONSE_FAILURE();
		}

		Integer elevatorPort = elevators.get(availableElevator);
		sender.send(DatagramPacketUtils.getCopyOf(request), availableElevator, elevatorPort);

		Scheduler.getInstance().stopProcessing();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Forwards An Elevator Event to the Registered Floor System
	 */
	@Override
	public DatagramPacket handleElevatorEvent(DatagramPacket request) {
		Scheduler.getInstance().startProcessing();

		if (floorSystem == null) {
			Scheduler.getInstance().stopProcessing();
			return MessagePackets.Responses.RESPONSE_FAILURE();
		}

		sender.send(DatagramPacketUtils.getCopyOf(request), floorSystem, NetworkConstants.FLOOR_RECIEVE_PORT);

		Scheduler.getInstance().stopProcessing();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Stores the Updated Elevator Status to the Scheduler SubSystem
	 */
	@Override
	public DatagramPacket handleElevatorStatus(DatagramPacket request) {
		Scheduler.getInstance().startProcessing();

		InetAddress elevator = request.getAddress();
		String statusAsString = DatagramPacketUtils.getMessageBodyAsString(request);

		boolean success = Scheduler.getInstance().updateElevator(elevator, ElevatorStatus.deserialize(statusAsString));

		Scheduler.getInstance().stopProcessing();
		return success ? MessagePackets.Responses.RESPONSE_SUCCESS() : MessagePackets.Responses.RESPONSE_FAILURE();
	}

	/**
	 * Registers the caller as either a Elevator or Floor SubSystem, keeping track of its IPAddress and Port 
	 */
	@Override
	public DatagramPacket handleRegisterRequest(DatagramPacket request) {
		String requestBody = DatagramPacketUtils.getMessageBodyAsString(request);
		Role requesterRole = Role.valueOf(requestBody);

		boolean success = false;
		switch (requesterRole) {
			case FLOORS:
				success = registerFloorSystem(request.getAddress());
				break;
			case ELEVATORS:
				success = registerElevator(request.getAddress(), request.getPort());
				break;
			default:
		}

		return success ? MessagePackets.Responses.RESPONSE_SUCCESS() : MessagePackets.Responses.RESPONSE_FAILURE();
	}

	/**
	 * Forwards this to all Registered Elevators and Terminates the Scheduler System
	 */
	@Override
	public DatagramPacket handleExitRequest(DatagramPacket request) {
		elevators.forEach(
				(addr, port) -> {
					sender.send(MessagePackets.REQUEST_SYSTEM_EXIT(), addr, port);
				});

		Scheduler.getInstance().stopRunning();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	private boolean registerElevator(InetAddress address, int port) {
		System.out.println("Scheduler - INFO : Registered Elevator at " + address.toString() + ":" + port);

		elevators.put(address, port);
		Scheduler.getInstance().addElevator(address);
		return true;
	}

	private boolean registerFloorSystem(InetAddress address) {
		if (floorSystem != null) {
			return false;
		}

		System.out.println("Scheduler - INFO : Registered Floor SubSystem at " + address.toString());
		floorSystem = address;
		return true;
	}
}

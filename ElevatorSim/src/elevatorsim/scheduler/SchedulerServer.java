package elevatorsim.scheduler;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import elevatorsim.common.requests.ElevatorArrivalRequest;
import elevatorsim.common.requests.ElevatorEvent;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.requests.ElevatorStateChange;
import elevatorsim.common.requests.ElevatorStatus;
import elevatorsim.constants.Direction;
import elevatorsim.constants.ElevatorState;
import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.server.UDPServer;
import elevatorsim.util.DatagramPacketUtils;

/**
 * Creates a Server that Services the Scheduler.
 * 
 * @author David Wang, Trevor Bivi
 */
public class SchedulerServer extends UDPServer {
	private static SchedulerServer instance;

	/* Associates a remote port with an Elevator */
	private final ConcurrentMap<Integer, ElevatorContactInfo> elevators;
	private final ConcurrentLinkedQueue<ElevatorRequest> requestQueue;
	private InetAddress floorSystem = null;

	private Timer timer = new Timer();

	private SchedulerServer() throws SocketException {
		super("SchedulerServer", NetworkConstants.SCHEDULER_PORT);
		this.elevators = new ConcurrentHashMap<>();
		this.requestQueue = new ConcurrentLinkedQueue<>();
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
	 * Forwards An Elevator Request to a Registered Elevator that can Service it.
	 * 
	 * @param elevatorRequest The request to the elevator.
	 */
	public void handleElevatorCall(ElevatorRequest elevatorRequest) {
		System.out.println("Handle Elevator Call");
		ElevatorContactInfo availableElevator = 
				Scheduler.getInstance().findAvailableElevator(elevatorRequest.getStartFloor(), elevatorRequest.getDirection());

		// Fix find AvailableElevator method so that it can handle the various cases. Right now it cant seem to handle everything.
		
		// If an elevator isn't available, store the request
		if (availableElevator == null) {
			System.out.println("Im in loop 1");
			Scheduler.getInstance().addStoredRequest(elevatorRequest);
			List<ElevatorRequest> storedRequests = Scheduler.getInstance().getStoredRequests();
			for(ElevatorRequest req: storedRequests) {
				requestQueue.add(req);
			}
			// For ITERATION 2 - Single Elevator just use the first elevator
			// FOR ITERATION 3 - We will need to queue this request to be handled by the first available elevator
			// store requests in a concurrent queue then if the elevator is direction null then get it out of the queue.
			
			
			ElevatorContactInfo anyElevator = Scheduler.getInstance().findStationaryElevator();
			ElevatorStatus elevatorStatus = Scheduler.getInstance().getElevators().get(anyElevator);

			if (storedRequests.size() == 1 && anyElevator != null) {
				System.out.println("Im in loop 2");
				elevatorStatus.addFloor(elevatorRequest.getStartFloor());
				elevatorStatus.setDirection(
						elevatorRequest.getStartFloor() - elevatorStatus.getFloor() > 0 ? 
								Direction.UP : Direction.DOWN);

				sender.send(
						MessagePackets.generateElevatorStateChange(
								new ElevatorStateChange(ElevatorState.STATIONARY_AND_DOOR_CLOSED)),
						anyElevator.address, anyElevator.receiverPort);
			}
			return;
		}

		ElevatorStatus availableElevatorStatus = Scheduler.getInstance().getElevators().get(availableElevator);
		int elevatorStartFloor = availableElevatorStatus.getFloor();
		int boardFloor = elevatorRequest.getStartFloor();

		int floorChange = boardFloor - elevatorStartFloor;
		Direction direction;
		if (floorChange < 0) {
			direction = Direction.DOWN;
		} else if (floorChange > 0) {
			direction = Direction.UP;
		} else {
			direction = null;
		}

		if (direction == null) {
			// send request to elevator so it can send back it's version
			sender.send(MessagePackets.generateElevatorRequest(elevatorRequest), availableElevator.address, availableElevator.receiverPort);
		}
		return;
	}

	/**
	 * Accepting datagram packet request, extract the content and assign to an elevator if possible.
	 * @param An elevator request in DatagraPack form.
	 * @return The corresponding DatagramPacket response after execution.
	 */
	@Override
	public DatagramPacket handleElevatorRequest(DatagramPacket request) {
		System.out.println("Handle Elevator Request");
		Scheduler.getInstance().startProcessing();

		ElevatorRequest elevatorRequest = MessagePackets.deserializeElevatorRequest(request.getData());
		System.out.print("SchedulerServer - Info: Received elevator request " + elevatorRequest.toString() + "\n");

		if (elevatorRequest.getStartFloor() == null) {
			ElevatorStatus elevatorStatus = Scheduler.getInstance().getElevators().get(Scheduler.getInstance().findFirstElevator());

			Direction newDirection = elevatorStatus.addFloor(elevatorRequest.getDestFloor());
			if (newDirection != null) {
				timer.schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						Scheduler.getInstance().startProcessing();

						ElevatorContactInfo elevatorContactInfo = elevators.get(request.getPort());
						sender.send(
								MessagePackets.generateElevatorStateChange(
										new ElevatorStateChange(ElevatorState.STATIONARY_AND_DOOR_CLOSED)),
								elevatorContactInfo.address, elevatorContactInfo.receiverPort);

						Scheduler.getInstance().stopProcessing();
					}
				}, 1000);
			}

		} else {
			handleElevatorCall(elevatorRequest);
		}

		Scheduler.getInstance().stopProcessing();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Forwards An Elevator Event to the Registered Floor System.
	 * @param request A elevator event request.
	 * @return The corresponding DatagramPacket response after execution.
	 */
	@Override
	public DatagramPacket handleElevatorEvent(DatagramPacket request) {
		System.out.println("Handle Elevator Event");
		Scheduler.getInstance().startProcessing();
		if (floorSystem == null) {
			Scheduler.getInstance().stopProcessing();
			return MessagePackets.Responses.RESPONSE_FAILURE();
		}

		ElevatorEvent elevatorEvent = MessagePackets.deserializeElevatorEvent(request.getData());
		System.out.print("SchedulerServer - Info: Received elevator event " + elevatorEvent.toString() + "\n");

		ElevatorContactInfo requesterContactInfo = elevators.get(request.getPort());
		ElevatorStatus requesterStatus = Scheduler.getInstance().getElevators().get(requesterContactInfo);
		
		requesterStatus.setFloor(elevatorEvent.getFloor());
		if (requesterStatus.getStops().indexOf(elevatorEvent.getFloor()) != -1) {
			ElevatorContactInfo elevatorContactInfo = elevators.get(request.getPort());
			sender.send(
					MessagePackets.generateElevatorStateChange(
							new ElevatorStateChange(ElevatorState.STATIONARY_AND_DOOR_CLOSED)),
					elevatorContactInfo.address, elevatorContactInfo.receiverPort);
		}

		Scheduler.getInstance().stopProcessing();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Stores the Updated Elevator Status to the Scheduler SubSystem.
	 * @param request A request for changing state.
	 * @return The corresponding DatagramPacket response after execution.
	 */
	@Override
	public DatagramPacket handleElevatorStateChange(DatagramPacket request) {
		System.out.println("Handle Elevator State Change");
		Scheduler.getInstance().startProcessing();

		ElevatorStateChange elevatorStateChange = MessagePackets.deserializeElevatorStateChange(request.getData());
		System.out.print("SchedulerServer - Info: Received elevator state change update "
				+ elevatorStateChange.toString() + "\n");

		ElevatorContactInfo elevatorContactInfo = elevators.get(request.getPort());
		ElevatorStatus elevatorStatus = Scheduler.getInstance().getElevators().get(elevatorContactInfo);
		ElevatorState elevatorState = elevatorStateChange.getStateChange();
		System.out.println(elevatorStatus.getDirection() + ", " + elevatorStatus.getState() );
		elevatorStatus.setState(elevatorState);
		System.out.println("After change " +elevatorStatus.getDirection() + ", " + elevatorStatus.getStops().size() );
		//TODO elevatorStatus.getDirection() has a null value for the direction. Figure out whats going on here. We should
		// get it so that it isnt null, it should set the value that its going to travel in. Its not adding the floors
		if (elevatorState == ElevatorState.STATIONARY_AND_DOOR_CLOSED) {
			
			if (elevatorStatus.getStops().indexOf(elevatorStatus.getFloor()) != -1) {
				sender.send(
						MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.DOOR_OPEN)),
						elevatorContactInfo.address, elevatorContactInfo.receiverPort);
				
			} else if (elevatorStatus.getDirection() == Direction.UP) {
				sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.MOTOR_UP)),
						elevatorContactInfo.address, elevatorContactInfo.receiverPort);
				
			} else if (elevatorStatus.getDirection() == Direction.DOWN) {
				sender.send(
						MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.MOTOR_DOWN)),
						elevatorContactInfo.address, elevatorContactInfo.receiverPort);
				
			}

		} else if(elevatorState == ElevatorState.DOOR_OPEN) {
			DatagramPacket packet = MessagePackets.generateArrivalRequest(
					new ElevatorArrivalRequest(1, elevatorStatus.getFloor(), elevatorStatus.getDirection()));
			sender.send(packet, floorSystem, NetworkConstants.FLOOR_RECIEVE_PORT);
			elevatorStatus.removeStop(elevatorStatus.getFloor());

			//For stopping the system
			if(elevatorStatus.getStops().size() > 0) {
				timer.schedule( 
				        new java.util.TimerTask() {
				            @Override
				            public void run() {
				            	System.out.print("sending close\n");
				            	Scheduler.getInstance().startProcessing();
				                sender.send(DatagramPacketUtils.getCopyOf(
				                		MessagePackets.generateElevatorStateChange(
				                				new ElevatorStateChange( ElevatorState.STATIONARY_AND_DOOR_CLOSED ))) , 
				                		elevatorContactInfo.address, elevatorContactInfo.receiverPort);
				                Scheduler.getInstance().stopProcessing();				                
				            }
				        }, 
				        1000 
				);
			} else {
				elevatorStatus.setDirection(null);
				List<ElevatorRequest> storedRequests = Scheduler.getInstance().getStoredRequests();
				if (storedRequests.size() > 0) {
					ElevatorRequest firstStoredRequest = storedRequests.get(0);
					Scheduler.getInstance().removeStoredRequest(0);
					handleElevatorCall(firstStoredRequest);
				}
			}

		}

		Scheduler.getInstance().stopProcessing();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Registers the caller as either a Elevator or Floor SubSystem, keeping track
	 * of its IPAddress and Port.
	 * @param request A register request for the Elevator or Floor SubSystem.
	 * @return The corresponding DatagramPacket response after execution.
	 */
	@Override
	public DatagramPacket handleRegisterRequest(DatagramPacket request) {
		String requestBody = DatagramPacketUtils.getMessageBodyAsString(request);
		Role requesterRole = requestBody.contains("FLOORS") ? Role.FLOORS : 
								requestBody.contains("ELEVATORS") ? Role.ELEVATORS : Role.UNKNOWN;

		boolean success = false;
		switch (requesterRole) {
			case FLOORS:
				success = registerFloorSystem(request.getAddress());
				break;
			case ELEVATORS:
				int receiverPort = new BigInteger(Arrays.copyOfRange(request.getData(), 4, request.getLength()-1)).intValue();
				success = registerElevator(request.getAddress(), request.getPort(), receiverPort);
				break;
			default:
		}

		return success ? MessagePackets.Responses.RESPONSE_SUCCESS() : MessagePackets.Responses.RESPONSE_FAILURE();
	}

	/**
	 * Forwards this to all Registered Elevators and Terminates the Scheduler System.
	 * 
	 * @param request The exit request from the floor when there are no more request.
	 * @return The corresponding DatagramPacket response after execution.
	 */
	@Override
	public DatagramPacket handleExitRequest(DatagramPacket request) {
		System.out.print("SchedulerServer - Info: Received exit request\n");
		elevators.forEach((addr, contact) -> {
			sender.send(MessagePackets.REQUEST_SYSTEM_EXIT(), contact.address, contact.receiverPort);
		});
		timer.cancel();
		Scheduler.getInstance().stopRunning();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Check if the elevator has been registered.
	 * @param address IP address of the elevator.
	 * @param port IP address' port of the elevator.
	 * @return True if the elevator is registered, false otherwise.
	 */
	private boolean registerElevator(InetAddress address, int senderPort, int receiverPort) {
		System.out.println("Scheduler - INFO : Registered Elevator at " + address.toString() + "/" + receiverPort);

		ElevatorContactInfo contact = new ElevatorContactInfo(address, receiverPort);
		elevators.put(senderPort, contact);
		Scheduler.getInstance().addElevator(contact);
		return true;
	}

	/**
	 * Check if the floor subsystem has been registered.
	 * @param address The IP address of the floor.
	 * @return True if the floor subsystem has been registered, false otherwise.
	 */
	private boolean registerFloorSystem(InetAddress address) {
		if (floorSystem != null) {
			return false;
		}

		System.out.println("Scheduler - INFO : Registered Floor SubSystem at " + address.toString());
		floorSystem = address;
		return true;
	}
}
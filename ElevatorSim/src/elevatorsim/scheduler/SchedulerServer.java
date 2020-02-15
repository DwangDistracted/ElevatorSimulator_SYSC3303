package elevatorsim.scheduler;

import java.awt.List;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.ElevatorStateChange;
import elevatorsim.common.ElevatorStatus;
import elevatorsim.constants.Direction;
import elevatorsim.constants.ElevatorEvent;
import elevatorsim.constants.ElevatorState;
import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.constants.TimeConstants;
import elevatorsim.server.UDPServer;
import elevatorsim.util.DatagramPacketUtils;	

/**
 * Creates a Server that Services the Scheduler 
 * 
 * @author David Wang, Trevor Bivi
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
	
	public boolean handleElevatorCall(ElevatorRequest elevatorRequest) {
		 // + elevatorRequest. + "an elevator is requested at floor" + elevatorRequest.getStartFloor().toString() + ". (The passenger will travel " + elevatorRequest.getDirection().toString() + "to floor " + Integer.toString(elevatorRequest.getDestFloor()).toString() + ".");
		InetAddress availableElevator = Scheduler.getInstance().findAvailableElevator(elevatorRequest.getStartFloor(), elevatorRequest.getDirection());
		
		//If an elevator isnt available, store the request
		//If there is an available elevator but it's moving then ignore it too for now because it's not worth optimising until there are multiple elevators
		if (availableElevator == null || Scheduler.getInstance().getElevators().get(availableElevator).getDirection() != null ) {
			ArrayList<ElevatorRequest> storedRequests = Scheduler.getInstance().getStoredRequests();
			storedRequests.add(elevatorRequest);
			InetAddress elevatorAddress = Scheduler.getInstance().findAvailableElevator();
			ElevatorStatus elevatorStatus = Scheduler.getInstance().getElevators().get(elevatorAddress);
			if( storedRequests.size() == 1 && elevatorStatus.getDirection() == null ) {
				elevatorStatus.addFloor(elevatorRequest.getStartFloor());
				
				elevatorStatus.setDirection( elevatorRequest.getStartFloor() - elevatorStatus.getFloor() > 0 ? Direction.UP : Direction.DOWN );
				sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.DOOR_CLOSED)), elevatorAddress, elevators.get(elevatorAddress));
			}
			return true;
		}
		System.out.println();
		
		
		int elevatorStartFloor = Scheduler.getInstance().getElevators().get(availableElevator).getFloor();
		int boardFloor = elevatorRequest.getStartFloor();
		int floorChange = boardFloor - elevatorStartFloor;
		Direction direction;
		if( floorChange < 0 ) {
			direction = Direction.DOWN;
		} else if (floorChange > 0) {
			direction = Direction.UP;
		}else {
			direction = null;
		}
		if (direction == null) {
			//send request to elevator so it can send back it's version
			Integer elevatorPort = elevators.get(availableElevator);
			sender.send(MessagePackets.generateElevatorRequest(elevatorRequest), availableElevator, elevatorPort);
		}
	
		return true;
	}
	
	@Override
	public DatagramPacket handleElevatorRequest(DatagramPacket request) {
		Scheduler.getInstance().startProcessing();
		ElevatorRequest elevatorRequest = MessagePackets.deserializeElevatorRequest(request.getData());
		System.out.print("SchedulerServer - Info: Received elevator request " + elevatorRequest.toString() + "\n");
		boolean success = true;
		if (elevatorRequest.getStartFloor() == null) {
			
				//ugly way of adding floor request to only elevator - will improve in future iteration
			ElevatorStatus elevatorStatus = Scheduler.getInstance().getElevators().get( Scheduler.getInstance().findAvailableElevator());
				
			Direction newDirection = elevatorStatus.addFloor( elevatorRequest.getDestFloor() );
			if (newDirection != null) {
				new java.util.Timer().schedule( 
			        new java.util.TimerTask() {
			            @Override
			            public void run() {
			            	Scheduler.getInstance().startProcessing();
			                sender.send(DatagramPacketUtils.getCopyOf( MessagePackets.generateElevatorStateChange(new ElevatorStateChange( ElevatorState.DOOR_CLOSED ))) , request.getAddress(), elevators.get(request.getAddress()));
			                Scheduler.getInstance().stopProcessing();
			           }
			       }, 
			       1000 
				);	
			}
			
		}else {
			success = handleElevatorCall(elevatorRequest);
		}
		Scheduler.getInstance().stopProcessing();
		return success ? MessagePackets.Responses.RESPONSE_SUCCESS() : MessagePackets.Responses.RESPONSE_FAILURE();
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
		
		ElevatorEvent elevatorEvent = MessagePackets.deserializeElevatorEvent(request.getData());
		System.out.print("SchedulerServer - Info: Received elevator event " + elevatorEvent.toString() + "\n");
		
		Scheduler.getInstance().getElevators().get(request.getAddress()).setFloor(elevatorEvent.getFloor());
		if (Scheduler.getInstance().getElevators().get(request.getAddress()).getStops().indexOf(elevatorEvent.getFloor()) != -1) {
			sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.DOOR_CLOSED)), request.getAddress(), this.elevators.get(request.getAddress()));
		}
		//sender.send(DatagramPacketUtils.getCopyOf(request), floorSystem, NetworkConstants.FLOOR_RECIEVE_PORT);

		Scheduler.getInstance().stopProcessing();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}

	/**
	 * Stores the Updated Elevator Status to the Scheduler SubSystem
	 */
	@Override
	public DatagramPacket handleElevatorStateChange(DatagramPacket request) {
		Scheduler.getInstance().startProcessing();
		ElevatorStateChange elevatorStateChange = MessagePackets.deserializeElevatorStateChange(request.getData());
		System.out.print("SchedulerServer - Info: Received elevator state change update " + elevatorStateChange.toString() + "\n");
		InetAddress elevator = request.getAddress();
		ElevatorStatus elevatorStatus = Scheduler.getInstance().getElevators().get(elevator);
		ElevatorState elevatorState = elevatorStateChange.getStateChange();
		// TODO Fix how the elevator status is deserialized. Should it be deserializing a string? or the parsed byte array?
		elevatorStatus.setState(elevatorState); //updateElevator(elevator, ElevatorStatus.deserialize( request.getData() ));

		if(elevatorState == ElevatorState.DOOR_CLOSED) {
			if( elevatorStatus.getStops().indexOf(elevatorStatus.getFloor()) != -1  ) {
				sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.DOOR_OPEN)), elevator, elevators.get(elevator));
			} else if(elevatorStatus.getDirection() == Direction.UP) {
				sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.MOTOR_UP)), elevator, elevators.get(elevator));
			} else if(elevatorStatus.getDirection() == Direction.DOWN) {
				sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.MOTOR_DOWN)), elevator, elevators.get(elevator));
			}
		} else if(elevatorState == ElevatorState.DOOR_OPEN) {
			elevatorStatus.removeStop(elevatorStatus.getFloor());
			if(elevatorStatus.getStops().size() > 0) {
				new java.util.Timer().schedule( 
				        new java.util.TimerTask() {
				            @Override
				            public void run() {
				            	System.out.print("sending close\n");
				            	Scheduler.getInstance().startProcessing();
				                sender.send(DatagramPacketUtils.getCopyOf( MessagePackets.generateElevatorStateChange(new ElevatorStateChange( ElevatorState.DOOR_CLOSED ))) , request.getAddress(), elevators.get(request.getAddress()));
				                Scheduler.getInstance().stopProcessing();
				            }
				        }, 
				        1000 
				);
			} else {
				elevatorStatus.setDirection(null);
				ArrayList<ElevatorRequest> storedRequests = Scheduler.getInstance().getStoredRequests();
				if( storedRequests.size() > 0 ) {
					ElevatorRequest firstStoredRequest = storedRequests.get(0);
					storedRequests.remove(0);
					handleElevatorCall(firstStoredRequest);
				}
			}
			
		}
		
		Scheduler.getInstance().stopProcessing();
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}
	/**
	 * Registers the caller as either a Elevator or Floor SubSystem, keeping track of its IPAddress and Port 
	 */
	@Override
	public DatagramPacket handleRegisterRequest(DatagramPacket request) {
		System.out.print("SchedulerServer - Info: Received registed request\n");
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
		System.out.print("SchedulerServer - Info: Received exit request\n");
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
package elevatorsim.elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import elevatorsim.common.requests.ElevatorEvent;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.requests.ElevatorStateChange;
import elevatorsim.common.requests.ElevatorStatus;
import elevatorsim.constants.ElevatorState;
import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.constants.TimeConstants;
import elevatorsim.server.UDPServer;

/**
 * The Server that serves an Elevator instance
 * 
 * @author David Wang, Trevor Bivi
 */
public class ElevatorServer extends UDPServer {
	private final Elevator elevator;
	private Timer timer;
	private TimerTask timerTask;
	
	/**
	 * Creates a new Elevator Server
	 * @param elevator the elevator instance this server belongs to
	 * @throws SocketException
	 */
	public ElevatorServer(Elevator elevator) throws SocketException {
		super(elevator.getName()); // TODO - number the elevators once we have many // Don't bind a fixed port. There will need to many of these.
		this.elevator = elevator;
	}

	@Override
	public Role getRole() {
		return Role.ELEVATORS;
	}

	/**
	 * Handles an incoming elevator request. Turns on the correct lamp for the destination floor and sends a request for a destination to the scheduler
	 */
	@Override
	public DatagramPacket handleElevatorRequest(DatagramPacket request) {
		/*
		 * Passes the Elevator Request in the request message to the owning elevator 
		 */
		ElevatorRequest floorRequest = MessagePackets.deserializeElevatorRequest(request.getData());
		System.out.print(elevator.getName()+" - INFO : Received an Elevator Request " + floorRequest.toString() + "\n");

		if(elevator.getFloor() == floorRequest.getStartFloor() && elevator.getElevatorState() == ElevatorState.DOOR_OPEN) {
			ElevatorRequest destRequest = new ElevatorRequest( floorRequest.getTimeStamp().plusSeconds(1),  floorRequest.getDestFloor() );
			
			try {
				elevator.setLampIsOn(floorRequest.getDestFloor()-1, true);
				sender.send(MessagePackets.generateElevatorRequest(destRequest) , InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return MessagePackets.Responses.RESPONSE_SUCCESS();
		}

		return MessagePackets.Responses.RESPONSE_FAILURE();
	}
	
	/**
	 * Handles a request to change state. Some states will take time to transition to such as opening and closing doors. Some state changes will be reported back to the scheduler so it can perform the next needed action on the elevator.
	 * @param stateChange a packet containing the desired new status of the elevator
	 */
	@Override
	public DatagramPacket handleElevatorStateChange( DatagramPacket stateChange) {
		ElevatorStateChange elevatorStateChange = MessagePackets.deserializeElevatorStateChange(stateChange.getData());
		System.out.print(elevator.getName() + " " + getReceiverPort()+" - INFO : Received an state change request " + elevatorStateChange.toString() + "\n");

		ElevatorState currentState = elevator.getElevatorState();
		ElevatorState newState = elevatorStateChange.getStateChange();

		boolean error = false;
		if(timer != null) {
			timer.cancel();
		}

		if(timerTask != null) {
			timerTask.cancel();
		}
		
		//If door is currently open or opening
		if ( currentState == ElevatorState.DOOR_OPEN || currentState == ElevatorState.DOOR_OPENING ) {
			//Initiate door close if requests
			if( newState == ElevatorState.STATIONARY_AND_DOOR_CLOSED ) {
				this.elevator.setElevatorState( ElevatorState.DOOR_CLOSING );
				timer = new Timer();
				timerTask = new java.util.TimerTask() {
					@Override
		            public void run() {
						if(elevator.getElevatorState() == ElevatorState.DOOR_CLOSING) {
							elevator.setElevatorState(ElevatorState.STATIONARY_AND_DOOR_CLOSED);
							try {
								sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.STATIONARY_AND_DOOR_CLOSED)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
							} catch (UnknownHostException e) {
								e.printStackTrace();
							}
						}
					}
				};
				timer.schedule( 
				       timerTask, 
				        TimeConstants.changeDoorState 
				);
			} else if( newState != ElevatorState.DOOR_OPEN) {
				error = true;
			}
		//If door is currently closing
		}else if (currentState == ElevatorState.DOOR_CLOSING) {
			//initiate door open if requested
			if(newState == ElevatorState.DOOR_OPEN ) {
				this.elevator.setElevatorState(ElevatorState.DOOR_OPENING);
				timer = new Timer();
				timerTask = new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorState.DOOR_OPENING) {
		            		elevator.setElevatorState(ElevatorState.DOOR_OPEN);
		            		try {
		            			sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.STATIONARY_AND_DOOR_CLOSED)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
		            		} catch (UnknownHostException e) {
		            			e.printStackTrace();
		            		}
		            	}
		            }
				};
				timer.schedule( 
				timerTask, 
				TimeConstants.changeDoorState );
			} else if (newState != ElevatorState.STATIONARY_AND_DOOR_CLOSED) {
				error = true;
			}
		//If door is currently closed
		} else if (currentState == ElevatorState.STATIONARY_AND_DOOR_CLOSED) {
			//Initiate door open if requested
			if(newState == ElevatorState.DOOR_OPEN ) {
				this.elevator.setElevatorState(ElevatorState.DOOR_OPENING);
				this.elevator.setLampIsOn(elevator.getFloor()-1, false);
				timer = new Timer();
				timerTask = new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorState.DOOR_OPENING) {
		            		elevator.setElevatorState(ElevatorState.DOOR_OPEN);
		            		try {
		            			sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.DOOR_OPEN)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
		            		} catch (UnknownHostException e) {
		            			e.printStackTrace();
		            		}
		            	}
		            }
		        };
				timer.schedule( 
				timerTask, 
		        TimeConstants.changeDoorState );
			// move up if requested
			} else if (newState == ElevatorState.MOTOR_UP) {
				elevator.setElevatorState(ElevatorState.MOTOR_UP);
				timer = new Timer();
				timerTask = new TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorState.MOTOR_UP) {
		            		elevator.setFloor(elevator.getFloor()+1);
		            		try {
		            			sender.send( MessagePackets.generateElevatorEvent(new ElevatorEvent(elevator.getFloor())),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
		            		} catch (UnknownHostException e) {
		            			e.printStackTrace();
		            		} catch (IOException e) {
		            			e.printStackTrace();
		            		}
		            	} else {
		            		timer.cancel();
		            	}
		            }
				};
				timer.scheduleAtFixedRate( 
				timerTask, 
				TimeConstants.moveOneFloor,TimeConstants.moveOneFloor );
				
			//Move down if requested
			} else if(newState == ElevatorState.MOTOR_DOWN) {
				
				elevator.setElevatorState(ElevatorState.MOTOR_DOWN);
				timer = new Timer();
				timerTask = new TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorState.MOTOR_DOWN) {
		            		elevator.setFloor(elevator.getFloor()-1);
		            		try {
		            			sender.send( MessagePackets.generateElevatorEvent(new ElevatorEvent(elevator.getFloor())),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
		            		} catch (UnknownHostException e) {
		            			e.printStackTrace();
		            		} catch (IOException e) {
		            			e.printStackTrace();
		            		}
		            	}else {
		            		timer.cancel();
		            	}
		            }
		        };
				timer.scheduleAtFixedRate( 
				timerTask, 
		        TimeConstants.moveOneFloor,TimeConstants.moveOneFloor );
				
			}else if (newState != ElevatorState.STATIONARY_AND_DOOR_CLOSED) {
				error = true;
			}
		//If currently moving
		}else if (currentState == ElevatorState.MOTOR_UP || currentState == ElevatorState.MOTOR_DOWN) {
			//Stop elevator if requested
			if(newState == ElevatorState.STATIONARY_AND_DOOR_CLOSED) {
				this.elevator.setElevatorState(ElevatorState.STATIONARY_AND_DOOR_CLOSED);
				try {
					sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.STATIONARY_AND_DOOR_CLOSED)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			} else if (newState != currentState) {
				error = true;
			}
		}
		
		return error ? MessagePackets.Responses.RESPONSE_FAILURE() : MessagePackets.Responses.RESPONSE_SUCCESS();
		
	}

	/**
	 * Stops this Server and Elevator SubSystem
	 */
	@Override
	public DatagramPacket handleExitRequest(DatagramPacket request) {
		timer.cancel();
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
	 * @throws UnknownHostException 
	 */
	public void sendStatusMessage(ElevatorStatus status) throws UnknownHostException {
		// TODO
		//sender.send(MessagePackets.generateElevatorStatus( status ), InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);

	}
}

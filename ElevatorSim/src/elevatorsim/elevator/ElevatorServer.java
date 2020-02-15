package elevatorsim.elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import elevatorsim.common.ElevatorRequest;
import elevatorsim.common.ElevatorStateChange;
import elevatorsim.common.ElevatorStatus;
import elevatorsim.constants.ElevatorEvent;
import elevatorsim.constants.ElevatorStates;
import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.scheduler.Scheduler;
import elevatorsim.server.UDPServer;
import elevatorsim.util.DatagramPacketUtils;

/**
 * The Server that serves an Elevator instance
 * 
 * @author David Wang
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
		
		//String requestAsString = DatagramPacketUtils.getMessageBodyAsString(request);
		

		ElevatorRequest floorRequest = MessagePackets.deserializeElevatorRequest(request.getData());
		System.out.print("ElevatorServer - INFO : Received an Elevator Request " + floorRequest.toString() + "\n");
		if(this.elevator.getFloor() == floorRequest.getStartFloor() && this.elevator.getElevatorState() == ElevatorStates.DOOR_OPEN) {
			
			ElevatorRequest destRequest = new ElevatorRequest( floorRequest.getTimeStamp().plusSeconds(1),  floorRequest.getDestFloor() );
			
			try {
				sender.send(MessagePackets.generateElevatorRequest(destRequest) , InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO - deserialize and pass to elevator
		return MessagePackets.Responses.RESPONSE_SUCCESS();
	}
	
	public DatagramPacket handleElevatorStateChange( DatagramPacket stateChange) {
		ElevatorStateChange elevatorStateChange = MessagePackets.deserializeElevatorStateChange(stateChange.getData());
		System.out.print("ElevatorServer - INFO : Received an state change request " + elevatorStateChange.toString() + "\n");
		ElevatorStates currentState = this.elevator.getElevatorState();
		ElevatorStates newState = elevatorStateChange.getStateChange();
		boolean error = false;
		if(timer != null) {
			timer.cancel();
		}
		if(timerTask != null) {
			timerTask.cancel();
		}
		
		if ( currentState == ElevatorStates.DOOR_OPEN || currentState == ElevatorStates.DOOR_OPENING ) {
			if( newState == ElevatorStates.DOOR_CLOSED ) {
				this.elevator.setElevatorState( ElevatorStates.DOOR_CLOSING );
				timer = new Timer();
				timerTask = new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorStates.DOOR_CLOSING) {
		            		elevator.setElevatorState(ElevatorStates.DOOR_CLOSED);
		            		try {
								sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorStates.DOOR_CLOSED)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            	}
		            }
		        };
				timer.schedule( 
				       timerTask, 
				        1000 
				);
			} else if( newState != ElevatorStates.DOOR_OPEN) {
				error = true;
			}
		}else if (currentState == ElevatorStates.DOOR_CLOSING) {
			if(newState == ElevatorStates.DOOR_OPEN ) {
				this.elevator.setElevatorState(ElevatorStates.DOOR_OPENING);
				timer = new Timer();
				timerTask = new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorStates.DOOR_OPENING) {
		            		elevator.setElevatorState(ElevatorStates.DOOR_OPEN);
		            		try {
								sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorStates.DOOR_CLOSED)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            	}
		            }
		        };
				timer.schedule( 
				timerTask, 
		        100 );
			} else if (newState != ElevatorStates.DOOR_CLOSED) {
				error = true;
			}
		} else if (currentState == ElevatorStates.DOOR_CLOSED) {
			if(newState == ElevatorStates.DOOR_OPEN ) {
				this.elevator.setElevatorState(ElevatorStates.DOOR_OPENING);
				timer = new Timer();
				timerTask = new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorStates.DOOR_OPENING) {
		            		elevator.setElevatorState(ElevatorStates.DOOR_OPEN);
		            		try {
								sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorStates.DOOR_OPEN)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            	}
		            }
		        };
				timer.schedule( 
				timerTask, 
		        100 );
			} else if (newState == ElevatorStates.MOTOR_UP) {
				elevator.setElevatorState(ElevatorStates.MOTOR_UP);
				timer = new Timer();
				timerTask = new TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorStates.MOTOR_UP) {
		            		elevator.setFloor(elevator.getFloor()+1);
		            		try {
								sender.send( MessagePackets.generateElevatorEvent(new ElevatorEvent(elevator.getFloor())),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            	}else {
		            		timer.cancel();
		            	}
		            }
		        };
				timer.scheduleAtFixedRate( 
				timerTask, 
		        500,500 );
				
				
			} else if(newState == ElevatorStates.MOTOR_DOWN) {
				
				elevator.setElevatorState(ElevatorStates.MOTOR_DOWN);
				timer = new Timer();
				timerTask = new TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorStates.MOTOR_DOWN) {
		            		elevator.setFloor(elevator.getFloor()-1);
		            		try {
								sender.send( MessagePackets.generateElevatorEvent(new ElevatorEvent(elevator.getFloor())),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            	}else {
		            		timer.cancel();
		            	}
		            }
		        };
				timer.scheduleAtFixedRate( 
				timerTask, 
		        500,500 );
				
			}else if (newState != ElevatorStates.DOOR_CLOSED) {
				error = true;
			}
		}else if (currentState == ElevatorStates.MOTOR_UP || currentState == ElevatorStates.MOTOR_DOWN) {
			if(newState == ElevatorStates.DOOR_CLOSED) {
				this.elevator.setElevatorState(ElevatorStates.DOOR_CLOSED);
				try {
					sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorStates.DOOR_CLOSED)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (newState != currentState) {
				error = true;
			}
		}
		
		return error ? MessagePackets.Responses.RESPONSE_FAILURE() : MessagePackets.Responses.RESPONSE_SUCCESS();
		
	}

	//@Override
	//public DatagramPacket handleElevatorStatus(DatagramPacket request) {
	//	/*
	//	 * 	Build and Send a Status Update to the Scheduler
	//	 */
	//	// TODO - Determine how and what to pass to scheduler - could be that this is superfluous
	//	sendStatusMessage( ElevatorStatus);
	//	return MessagePackets.Responses.RESPONSE_SUCCESS();
	//}*/

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
	 * @throws UnknownHostException 
	 */
	public void sendStatusMessage(ElevatorStatus status) throws UnknownHostException {
		// TODO
		//sender.send(MessagePackets.generateElevatorStatus( status ), InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);

	}
}

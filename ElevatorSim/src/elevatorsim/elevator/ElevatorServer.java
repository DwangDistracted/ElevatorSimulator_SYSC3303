package elevatorsim.elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.ElevatorStateChange;
import elevatorsim.common.ElevatorStatus;
import elevatorsim.constants.ElevatorEvent;
import elevatorsim.constants.ElevatorState;
import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.constants.TimeConstants;
import elevatorsim.scheduler.Scheduler;
import elevatorsim.server.UDPServer;
import elevatorsim.util.DatagramPacketUtils;

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

		ElevatorRequest floorRequest = MessagePackets.deserializeElevatorRequest(request.getData());
		System.out.print("ElevatorServer - INFO : Received an Elevator Request " + floorRequest.toString() + "\n");
		if(this.elevator.getFloor() == floorRequest.getStartFloor() && this.elevator.getElevatorState() == ElevatorState.DOOR_OPEN) {
			
			ElevatorRequest destRequest = new ElevatorRequest( floorRequest.getTimeStamp().plusSeconds(1),  floorRequest.getDestFloor() );
			
			try {
				elevator.setLampIsOn(floorRequest.getDestFloor()-1, true);
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
	
	@Override
	public DatagramPacket handleElevatorStateChange( DatagramPacket stateChange) {
		ElevatorStateChange elevatorStateChange = MessagePackets.deserializeElevatorStateChange(stateChange.getData());
		System.out.print("ElevatorServer - INFO : Received an state change request " + elevatorStateChange.toString() + "\n");
		ElevatorState currentState = this.elevator.getElevatorState();
		ElevatorState newState = elevatorStateChange.getStateChange();
		boolean error = false;
		if(timer != null) {
			timer.cancel();
		}
		if(timerTask != null) {
			timerTask.cancel();
		}
		
		if ( currentState == ElevatorState.DOOR_OPEN || currentState == ElevatorState.DOOR_OPENING ) {
			if( newState == ElevatorState.DOOR_CLOSED ) {
				this.elevator.setElevatorState( ElevatorState.DOOR_CLOSING );
				timer = new Timer();
				timerTask = new java.util.TimerTask() {
					@Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorState.DOOR_CLOSING) {
		            		elevator.setElevatorState(ElevatorState.DOOR_CLOSED);
		            		try {
		            			sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.DOOR_CLOSED)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
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
		}else if (currentState == ElevatorState.DOOR_CLOSING) {
			if(newState == ElevatorState.DOOR_OPEN ) {
				this.elevator.setElevatorState(ElevatorState.DOOR_OPENING);
				timer = new Timer();
				timerTask = new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	if(elevator.getElevatorState() == ElevatorState.DOOR_OPENING) {
		            		elevator.setElevatorState(ElevatorState.DOOR_OPEN);
		            		try {
		            			sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.DOOR_CLOSED)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
		            		} catch (UnknownHostException e) {
		            			// TODO Auto-generated catch block
		            			e.printStackTrace();
		            		}
		            	}
		            }
				};
				timer.schedule( 
				timerTask, 
				TimeConstants.changeDoorState );
			} else if (newState != ElevatorState.DOOR_CLOSED) {
				error = true;
			}
		} else if (currentState == ElevatorState.DOOR_CLOSED) {
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
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            	}
		            }
		        };
				timer.schedule( 
				timerTask, 
		        TimeConstants.changeDoorState );
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
		            			// TODO Auto-generated catch block
		            			e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
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
		        TimeConstants.moveOneFloor,TimeConstants.moveOneFloor );
				
			}else if (newState != ElevatorState.DOOR_CLOSED) {
				error = true;
			}
		}else if (currentState == ElevatorState.MOTOR_UP || currentState == ElevatorState.MOTOR_DOWN) {
			if(newState == ElevatorState.DOOR_CLOSED) {
				this.elevator.setElevatorState(ElevatorState.DOOR_CLOSED);
				try {
					sender.send(MessagePackets.generateElevatorStateChange(new ElevatorStateChange(ElevatorState.DOOR_CLOSED)),InetAddress.getByName(NetworkConstants.SCHEDULER_IP), NetworkConstants.SCHEDULER_PORT);
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

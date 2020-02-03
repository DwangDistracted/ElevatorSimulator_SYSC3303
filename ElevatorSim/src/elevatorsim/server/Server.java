package elevatorsim.server;

import java.net.DatagramPacket;
import java.net.SocketException;

import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.Role;

/**
 * This abstract class lays the groundwork for the socket based server used in all three subsystems.
 * It has the ability to send and receive DatagramPackets
 * @author David Wang
 */
public abstract class Server {
	protected final SenderSocket sender; 
	protected final ReceiverSocket receiver;

	private boolean isRunning = false;
	
	/**
	 * Creates a new Server with the Provided Name.
	 * @param serverName the name of this server
	 * @throws SocketException if the server cannot open its sockets
	 */
	public Server(String serverName) throws SocketException {
		this.sender = new SenderSocket(serverName + "Sender", this);
		this.receiver = new ReceiverSocket(serverName + "Reciever", this);
	}

	/**
	 * Creates a new Server with the Provided Name.
	 * @param serverName the name of this server
	 * @param receivePort the port that this server will listen on
	 * @throws SocketException if the server cannot open its sockets
	 */
	public Server(String serverName, int receivePort) throws SocketException {
		this.sender = new SenderSocket(serverName + "Sender", this);
		this.receiver = new ReceiverSocket(serverName + "Reciever", this, receivePort);
	}
	
	/**
	 * Starts the Server
	 */
	public void startServer() {
		isRunning = true;
		sender.start();
		receiver.start();
	}

	/**
	 * Stops the Server
	 */
	public void stopServer() {
		isRunning = false;
	}

	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * @return The Role of this server. Can be Floor or Elevator
	 */
	public abstract Role getRole();

	/**
	 * Handles an Elevator Request
	 * @param request the elevator request
	 * @return a response packet depending on the success of the handler
	 */
	public DatagramPacket handleElevatorRequest(DatagramPacket request) {
		return MessagePackets.Responses.RESPONSE_NOT_APPLICABLE();
	}
	/**
	 * Handles an Elevator Event
	 * @param request the elevator event
	 * @return a response packet depending on the success of the handler
	 */
	public DatagramPacket handleElevatorEvent(DatagramPacket request) {
		return MessagePackets.Responses.RESPONSE_NOT_APPLICABLE();
	}
	/**
	 * Handles an Elevator Status Request
	 * @param request the elevator status request
	 * @return a response packet depending on the success of the handler
	 */
	public DatagramPacket handleElevatorStatus(DatagramPacket request) {
		return MessagePackets.Responses.RESPONSE_NOT_APPLICABLE();
	}
	/**
	 * Handles an Register Request
	 * @param request the register request
	 * @return a response packet depending on the success of the handler
	 */
	public DatagramPacket handleRegisterRequest(DatagramPacket request) {
		return MessagePackets.Responses.RESPONSE_NOT_APPLICABLE();
	}
	/**
	 * Handles an Exit Request
	 * @param request the exit request
	 * @return a response packet depending on the success of the handler
	 */
	public DatagramPacket handleExitRequest(DatagramPacket request) {
		return MessagePackets.Responses.RESPONSE_NOT_APPLICABLE();
	}
}

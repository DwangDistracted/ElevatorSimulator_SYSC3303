package elevatorsim.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.util.DatagramPacketUtils;

/**
 * This class contains the basic logic for the Receiver Portion of the Server present in the Elevator and Floor Subsystems
 * @author David Wang
 */
public class ReceiverSocket extends Thread {
	private final Server parentServer;
	
	private final DatagramSocket receivePort;
	private final DatagramSocket responsePort;

	/**
	 * Sets up the Socket Server to listen for messages on a random port number
	 * 
	 * @param threadName the name of the receiver thread
	 * @param parent the server this receiver thread belongs to
	 * @param receivePortNum the post this server should listen for messages on
	 * @throws SocketException if the sockets cannot be created
	 */
	protected ReceiverSocket(String threadName, Server parent) throws SocketException {
		super(threadName);
		parentServer = parent;
		
		receivePort = new DatagramSocket();
		receivePort.setSoTimeout(NetworkConstants.RESPONSE_TIMEOUT_MS);
		responsePort = new DatagramSocket();
		responsePort.setSoTimeout(NetworkConstants.RESPONSE_TIMEOUT_MS);
	}
	/**
	 * Sets up the Socket Server to listen for messages on the indicated port number
	 * 
	 * @param threadName the name of the receiver thread
	 * @param parent the server this receiver thread belongs to
	 * @param receivePortNum the post this server should listen for messages on
	 * @throws SocketException if the sockets cannot be created
	 */
	protected ReceiverSocket(String threadName, Server parent, int receivePortNum) throws SocketException {
		super(threadName);
		this.parentServer = parent;

		receivePort = new DatagramSocket(receivePortNum);
		receivePort.setSoTimeout(NetworkConstants.RESPONSE_TIMEOUT_MS);
		responsePort = new DatagramSocket();
		responsePort.setSoTimeout(NetworkConstants.RESPONSE_TIMEOUT_MS);
	}
	
	/**
	 * Handle a request from the scheduler in a new Thread so that the receiver isn't blocked while handling
	 * @param request the request to handle
	 */
	private void service(DatagramPacket request) {
		new Thread(() -> {
			DatagramPacket response = null;
			
			if (DatagramPacketUtils.isElevatorEvent(request)) {
				response = parentServer.handleElevatorEvent(request);
				
			} else if (DatagramPacketUtils.isElevatorRequest(request)) {
				response = parentServer.handleElevatorRequest(request);
				
			} else if (DatagramPacketUtils.isStatusRequest(request)) {
				response = parentServer.handleElevatorStatus(request);
				
			} else if (DatagramPacketUtils.isExitRequest(request)) {
				response = parentServer.handleExitRequest(request);
				
			} else if (DatagramPacketUtils.isRegisterRequest(request)) {
				response = parentServer.handleRegisterRequest(request);
			}

			if (response == null) {
				response = MessagePackets.Responses.RESPONSE_NOT_APPLICABLE();
			}
			
			synchronized (responsePort) {
				response.setSocketAddress(request.getSocketAddress());
				try {
					responsePort.send(response);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Registers this component's receive port with the Scheduler Server (and subsystem) so that the scheduler can contact this client.
	 * @return if the registration was successful
	 * @throws IOException
	 */
	private boolean registerWithScheduler() {
		try {			
			ByteArrayOutputStream message = new ByteArrayOutputStream();
			message.write(NetworkConstants.MessageTypes.REGISTER.getMarker());
			message.write(NetworkConstants.NULL_BYTE);
			message.write(parentServer.getRole().name().getBytes());
			message.write(NetworkConstants.NULL_BYTE);
	
			DatagramPacket request = new DatagramPacket(message.toByteArray(), message.size());
			request.setAddress(InetAddress.getByName(NetworkConstants.SCHEDULER_IP));
			request.setPort(NetworkConstants.SCHEDULER_PORT);
			receivePort.send(request);

			DatagramPacket response = MessagePackets.Responses.RESPONSE_PENDING(); 
			try {
				receivePort.receive(response);
			} catch (IOException timeout) {
				System.out.println(this.getName() + " - ERROR: Did Not Receive a Response");
			}

			return DatagramPacketUtils.isSuccessResponse(response);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void run() {
		if (parentServer.getRole() == Role.FLOORS || parentServer.getRole() == Role.ELEVATORS) {
			while (!registerWithScheduler());
			System.out.println(this.getName() + " - Info: Registered With Scheduler");
		}

		try {
			while(parentServer.isRunning()) {
				DatagramPacket request = new DatagramPacket(new byte[NetworkConstants.REQUEST_LENGTH],
						NetworkConstants.REQUEST_LENGTH);
				try {
					receivePort.receive(request);
					service(request);
				} catch (IOException timeout) {
					// doesn't matter, keep waiting
				}
			}
		} finally {
			receivePort.close();
		}
	}
}

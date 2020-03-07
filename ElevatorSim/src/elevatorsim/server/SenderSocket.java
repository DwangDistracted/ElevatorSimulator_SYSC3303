package elevatorsim.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
import elevatorsim.constants.Role;
import elevatorsim.util.DatagramPacketUtils;

/**
 * This class contains the logic for the Sender Portion of the Server present in the Elevator and Floor Subsystems.
 * @author David Wang
 */
public class SenderSocket extends Thread {
	private final UDPServer parentServer;
	private final DatagramSocket senderPort;
	private BlockingQueue<DatagramPacket> pendingMessages;
	
	/**
	 * Sets up the Socket Server to send messages to the scheduler
	 * 
	 * @param threadName the name of the sender thread
	 * @throws SocketException if a socket cannot be created
	 */
	protected SenderSocket(String threadName, UDPServer parentServer) throws SocketException {
		super(threadName);
		
		this.parentServer = parentServer;
		this.senderPort = new DatagramSocket();
		senderPort.setSoTimeout(NetworkConstants.RESPONSE_TIMEOUT_MS);
		pendingMessages = new LinkedBlockingQueue<>();
	}

	/**
	 * Add a message to send to the scheduler
	 * @param message the message to send
	 * @param destination the message's destination as Socket Address
	 */
	public void send(DatagramPacket message, InetAddress destination, Integer destinationPort) {
		message.setAddress(destination);
		message.setPort(destinationPort);
		pendingMessages.add(message);
	}

	/**
	 * Registers this server's ports with the Scheduler Server (and subsystem) so that the scheduler can contact this client.
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
			message.write(BigInteger.valueOf(parentServer.getReceiverPort()).toByteArray()); // scheduler can find senderPort and InetAddress from the request itself
			message.write(NetworkConstants.NULL_BYTE);
	
			DatagramPacket request = new DatagramPacket(message.toByteArray(), message.size());
			request.setAddress(InetAddress.getByName(NetworkConstants.SCHEDULER_IP));
			request.setPort(NetworkConstants.SCHEDULER_PORT);
			senderPort.send(request);

			DatagramPacket response = MessagePackets.Responses.RESPONSE_PENDING(); 
			try {
				senderPort.receive(response);
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
		try {
			if (parentServer.getRole() == Role.FLOORS || parentServer.getRole() == Role.ELEVATORS) {
				while (!registerWithScheduler());
				System.out.println(this.getName() + " - Info: Registered With Scheduler");
			}

			while(parentServer.isRunning()) {
				DatagramPacket messageToSend = null;

				try {
					messageToSend = pendingMessages.poll(NetworkConstants.RESPONSE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 

				if (messageToSend != null) {
					senderPort.send(messageToSend);
					
					DatagramPacket response = MessagePackets.Responses.RESPONSE_PENDING();
					try {
						senderPort.receive(response);
					} catch (IOException timeout) {
						System.out.println(this.getName() + " - ERROR: Did Not Receive a Response");
					}
					
					if (!DatagramPacketUtils.isSuccessResponse(response)) {
						System.out.println(this.getName() + " - WARNING: Did Not Receive an OK Response\n\t" + DatagramPacketUtils.print(response));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			senderPort.close();
		}
	}
}

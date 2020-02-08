package elevatorsim.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import elevatorsim.constants.MessagePackets;
import elevatorsim.constants.NetworkConstants;
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
	 * @param message
	 */
	public void send(DatagramPacket message, InetAddress destination, int destinationPort) {
		message.setAddress(destination);
		message.setPort(destinationPort);
		pendingMessages.add(message);
	}

	@Override
	public void run() {
		try {
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

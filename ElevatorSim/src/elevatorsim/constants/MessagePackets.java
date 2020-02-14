package elevatorsim.constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import elevatorsim.common.ElevatorRequest;

/**
 * This class contains the DatagramPackets that are commonly used by our communications system.
 * These packets would be constants except for the limitation that a DatagramPacket can only be received or sent once before a new one is needed to be able to send/receive again.
 * 
 * The Structure of the Packet is as follows:
 * 	[TYPE MARKER - 1 BYTE] /0 [BODY - 1-n bytes] /0
 * 
 * @author David Wang
 */
public class MessagePackets {
	/*
	 * This Offset is the index in a message where the body of the message (the data) starts
	 */
	public static final int MESSAGE_DATA_OFFSET = 2;
	
	/**
	 * A Request for the Status of an Elevator.
	 * Length 2
	 */
	public static DatagramPacket REQUEST_STATUS() {
		return new DatagramPacket(new byte[] {NetworkConstants.MessageTypes.STATUS.getMarker(),
												NetworkConstants.NULL_BYTE},
								   2);
	}
	
	/**
	 * A Request Terminate the System
	 * Length 2
	 */
	public static DatagramPacket REQUEST_SYSTEM_EXIT() {
		return new DatagramPacket(new byte[] {NetworkConstants.MessageTypes.EXIT.getMarker(),
												NetworkConstants.NULL_BYTE},
								   2);
	}

	/**
	 * Creates an Elevator Request with the provided ElevatorRequest as its body
	 * @param body the ElevatorRequest to use as the request's body
	 * @return a DatagramPacket that can be sent with a Socket Server
	 * @throws IOException if the ElevatorRequest fails to be serialized
	 */
	public static DatagramPacket generateElevatorRequest(ElevatorRequest body) throws IOException {
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		message.write(NetworkConstants.MessageTypes.ELEVATOR_REQUEST.getMarker());
		message.write(NetworkConstants.NULL_BYTE);
		body.serialize(message);
		message.write(NetworkConstants.NULL_BYTE);
		
		return new DatagramPacket(message.toByteArray(), message.size());
	}

	/**
	 * Creates an Elevator Event with the provided ElevatorEvent as its body
	 * @param body the ElevatorEvent to use as the request's body
	 * @return a DatagramPacket that can be sent with a Socket Server
	 * @throws IOException if the ElevatorEvent fails to be serialized
	 */
	public static DatagramPacket generateElevatorEvent(ElevatorEvent body) throws IOException {
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		message.write(NetworkConstants.MessageTypes.ELEVATOR_EVENT.getMarker());
		message.write(NetworkConstants.NULL_BYTE);
		message.write(body.name().getBytes());
		message.write(NetworkConstants.NULL_BYTE);
		
		return new DatagramPacket(message.toByteArray(), message.size());
	}
	
	/**
	 * All the Response Messages that a Socket Server can Return
	 */
	public static class Responses {
		/**
		 * A Response indicating that the Server has no way to handle the Request
		 */
		public static DatagramPacket RESPONSE_NOT_APPLICABLE() {
			return new DatagramPacket(new byte[] {NetworkConstants.MessageTypes.RESPONSE.getMarker(),
													NetworkConstants.NULL_BYTE,
													NetworkConstants.ResponseStatus.NOT_APPLICABLE.getMarker(),
													NetworkConstants.NULL_BYTE},
										NetworkConstants.RESPONSE_LENGTH);
		}

		/**
		 * A Response indicating that the Server has successfully handled the request
		 */
		public static DatagramPacket RESPONSE_SUCCESS() {
			return new DatagramPacket(new byte[] {NetworkConstants.MessageTypes.RESPONSE.getMarker(),
													NetworkConstants.NULL_BYTE,
													NetworkConstants.ResponseStatus.SUCCESS.getMarker(),
													NetworkConstants.NULL_BYTE},
									   NetworkConstants.RESPONSE_LENGTH);
		}

		/**
		 * A Response indicating that the Server has failed to handle the request
		 */
		public static DatagramPacket RESPONSE_FAILURE() {
			return new DatagramPacket(new byte[] {NetworkConstants.MessageTypes.RESPONSE.getMarker(),
													NetworkConstants.NULL_BYTE,
													NetworkConstants.ResponseStatus.SERVER_FAILURE.getMarker(),
													NetworkConstants.NULL_BYTE},
								   		NetworkConstants.RESPONSE_LENGTH);
		}

		/**
		 * A Response object that has yet to be written. Use to wait for a response in DatagramSocket::receive
		 */
		public static DatagramPacket RESPONSE_PENDING() {
			return new DatagramPacket(new byte[NetworkConstants.RESPONSE_LENGTH], NetworkConstants.RESPONSE_LENGTH); 
		}
	}
}

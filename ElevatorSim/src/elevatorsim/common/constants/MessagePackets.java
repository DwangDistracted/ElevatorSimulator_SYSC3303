package elevatorsim.common.constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

import elevatorsim.common.requests.ElevatorArrivalRequest;
import elevatorsim.common.requests.ElevatorDestinationRequest;
import elevatorsim.common.requests.ElevatorEvent;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.requests.ElevatorStateChange;

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
	public static DatagramPacket generateElevatorRequest(ElevatorRequest body) {
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		message.write(NetworkConstants.MessageTypes.ELEVATOR_REQUEST.getMarker());
		message.write(NetworkConstants.NULL_BYTE);
		body.serialize(message);
		message.write(NetworkConstants.NULL_BYTE);
		
		return new DatagramPacket(message.toByteArray(), message.size());
	}
	
	/**
	 * Deserializes an elevator request packet's data
	 * @param data the data of the packet
	 * @return the elevator request
	 * @throws IllegalArgument if the ElevatorRequest fails to be deserialized
	 */
	public static ElevatorRequest deserializeElevatorRequest(byte[] data) {
		if(data[0] != NetworkConstants.MessageTypes.ELEVATOR_REQUEST.getMarker() ||
				data[1] != NetworkConstants.NULL_BYTE ||
				data[data.length-1] != NetworkConstants.NULL_BYTE) {
			throw new IllegalArgumentException("Tried to deserialize an invalid elevator request message");
		}
		return ElevatorRequest.deserialize(Arrays.copyOfRange(data, 2, data.length-1));
	}
	
	/**
	 * Creates an elevator arrival request with the provided ElevatorArrivalRequest as its body
	 * @param body the ElevatorArrivalRequest to use as the request's body
	 * @return a DatagramPacket that can be sent with a Socket Server
	 * @throws IOException if the ElevatorRequest fails to be serialized
	 */
	public static DatagramPacket generateArrivalRequest(ElevatorArrivalRequest body) {
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		message.write(NetworkConstants.MessageTypes.ELEVATOR_EVENT.getMarker());
		message.write(NetworkConstants.NULL_BYTE);
		body.serialize(message);
		message.write(NetworkConstants.NULL_BYTE);
		
		return new DatagramPacket(message.toByteArray(), message.size());
	}
	
	/**
	 * Deserialize an elevator arrival request packet's data
	 * @param data the data of the packet
	 * @return the elevator arrival request
	 * @throws IllegalArgument if the ElevatorRequest fails to be deserialized
	 */
	public static ElevatorArrivalRequest deserializeArrivalRequest(byte[] data) {
		if(data[0] != NetworkConstants.MessageTypes.ELEVATOR_EVENT.getMarker() ||
				data[1] != NetworkConstants.NULL_BYTE ||
				data[data.length-1] != NetworkConstants.NULL_BYTE) {
			throw new IllegalArgumentException("Tried to deserialize an invalid elevator request message");
		}
		return ElevatorArrivalRequest.deserialize(Arrays.copyOfRange(data, 2, data.length-1));
	}
	
	/**
	 * Creates an elevator destination request with the provided ElevatorDestinationRequest as its body
	 * @param body the ElevatorDestinationRequest to use as the request's body
	 * @return a DatagramPacket that can be sent with a Socket Server
	 */
	public static DatagramPacket generateDestinationRequest(ElevatorDestinationRequest body) {
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		message.write(NetworkConstants.MessageTypes.ELEVATOR_EVENT.getMarker());
		message.write(NetworkConstants.NULL_BYTE);
		body.serialize(message);
		message.write(NetworkConstants.NULL_BYTE);
		
		return new DatagramPacket(message.toByteArray(), message.size());
	}
	
	/**
	 * Deserialize an destination request packet's data
	 * @param data the data of the packet
	 * @return the elevator destination request
	 * @throws IllegalArgument if the ElevatorDestinationRequest fails to be deserialized
	 */
	public static ElevatorDestinationRequest deserializeDestinationRequest(byte[] data) {
		if(data[0] != NetworkConstants.MessageTypes.ELEVATOR_EVENT.getMarker() ||
				data[1] != NetworkConstants.NULL_BYTE ||
				data[data.length-1] != NetworkConstants.NULL_BYTE) {
			throw new IllegalArgumentException("Tried to deserialize an invalid elevator request message");
		}
		return ElevatorDestinationRequest.deserialize(Arrays.copyOfRange(data, 2, data.length-1));
	}
	
	/**
	 * Creates an Elevator State Change with the provided ElevatorStateChange as its body
	 * @param body the ELevatorStateChange to use as the request's body
	 * @return the datagram packet
	 */
	public static DatagramPacket generateElevatorStateChange(ElevatorStateChange body) {
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		message.write( NetworkConstants.MessageTypes.STATUS.getMarker());
		message.write(NetworkConstants.NULL_BYTE);
		body.serialize(message);
		message.write(NetworkConstants.NULL_BYTE);
		return new DatagramPacket(message.toByteArray(), message.size());
	}
	
	/**
	 * Deserializes an elevator state change packet's data
	 * @param data the data of the packet
	 * @return the elevator state change
	 * @throws IllegalArgumentException if the ElevatorStateChange fails to be deserialized
	 */
	public static ElevatorStateChange deserializeElevatorStateChange(byte[] data) {
		if(data[0] != NetworkConstants.MessageTypes.STATUS.getMarker() ||
				data[1] != NetworkConstants.NULL_BYTE ||
				data[data.length-1] != NetworkConstants.NULL_BYTE) {
			throw new IllegalArgumentException("Tried to deserialize an invalid state cange message");
		}
		return ElevatorStateChange.deserialize(Arrays.copyOfRange(data, 2, data.length-1));
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
		body.serialize(message);
		message.write(NetworkConstants.NULL_BYTE);
		
		return new DatagramPacket(message.toByteArray(), message.size());
	}
	
	/**
	 * Deserializes an elevator event packet's data
	 * @param data the data of the packet
	 * @return the elevator state change
	 * @throws IllegalArgumentException if the ElevatorEvent fails to be deserialized
	 */
	public static ElevatorEvent deserializeElevatorEvent(byte[] data) {
		if(data[0] != NetworkConstants.MessageTypes.ELEVATOR_EVENT.getMarker() ||
				data[1] != NetworkConstants.NULL_BYTE ||
				data[data.length-1] != NetworkConstants.NULL_BYTE) {
			throw new IllegalArgumentException("Tried to deserialize an invalid elevator event message");
		}
		return ElevatorEvent.deserialize(Arrays.copyOfRange(data,2,data.length-1));
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

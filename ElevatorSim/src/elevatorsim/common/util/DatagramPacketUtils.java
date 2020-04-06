package elevatorsim.common.util;

import java.net.DatagramPacket;

import elevatorsim.common.constants.MessagePackets;
import elevatorsim.common.constants.NetworkConstants;

/**
 * This Class contains some methods to facilitate the manipulation of DatagramPackets
 * @author David Wang
 */
public class DatagramPacketUtils {
	public static boolean isRegisterRequest(DatagramPacket request) {
		return NetworkConstants.MessageTypes.REGISTER.getMarker() == request.getData()[0];
	}
	public static boolean isStateChangeRequest(DatagramPacket request) {
		return NetworkConstants.MessageTypes.STATUS.getMarker() == request.getData()[0];
	}
	public static boolean isElevatorRequest(DatagramPacket request) {
		return NetworkConstants.MessageTypes.ELEVATOR_REQUEST.getMarker() == request.getData()[0];
	}
	public static boolean isElevatorEvent(DatagramPacket request) {
		return NetworkConstants.MessageTypes.ELEVATOR_EVENT.getMarker() == request.getData()[0];
	}
	public static boolean isExitRequest(DatagramPacket request) {
		return NetworkConstants.MessageTypes.EXIT.getMarker() == request.getData()[0];
	}
	public static boolean isResponse(DatagramPacket response) {
		return NetworkConstants.MessageTypes.RESPONSE.getMarker() == response.getData()[0];
	}
	public static boolean isSuccessResponse(DatagramPacket response) {
		return isResponse(response) && response.getData()[MessagePackets.MESSAGE_DATA_OFFSET] == NetworkConstants.ResponseStatus.SUCCESS.getMarker();
	}
	public static boolean isFailureResponse(DatagramPacket response) {
		return isResponse(response) && response.getData()[MessagePackets.MESSAGE_DATA_OFFSET] == NetworkConstants.ResponseStatus.SERVER_FAILURE.getMarker();
	}
	public static boolean isNotApplicableResponse(DatagramPacket response) {
		return isResponse(response) && response.getData()[MessagePackets.MESSAGE_DATA_OFFSET] == NetworkConstants.ResponseStatus.NOT_APPLICABLE.getMarker();
	}

	/**
	 * Turns the body of a DatagramPacket message into a String
	 * @param packet the DatagramPacket
	 * @return the string representation of the DatagramPacket's Data
	 */
	public static String getMessageBodyAsString(DatagramPacket packet) {
		byte[] message = packet.getData();
		String body = new String(message, MessagePackets.MESSAGE_DATA_OFFSET, message.length-MessagePackets.MESSAGE_DATA_OFFSET);
		return body.trim();
	}

	/**
	 * Transforms a DatagramPacket into a String containing the DatagramPacket's data as hexadecimal
	 * @param packet the DatagramPacket
	 * @return String representation of the DatagramPacket's data as hexadecimal
	 */
	public static String print(DatagramPacket packet) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("[ ");
	    for (byte b : packet.getData()) {
	        sb.append(String.format("0x%02X ", b));
	    }
	    sb.append("]");
	    return sb.toString();
	}

	/**
	 * Copies a given DatagramPacket into a new DatagramPacket Object
	 * @param original the given DatagramPacket
	 * @return a new DatagramPacket with the same data as the given DatagramPacket
	 */
	public static DatagramPacket getCopyOf(DatagramPacket original) {
		return new DatagramPacket(original.getData(), original.getLength());
	}
}

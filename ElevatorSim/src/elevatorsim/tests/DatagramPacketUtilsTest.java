package elevatorsim.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorsim.constants.NetworkConstants;
import elevatorsim.util.DatagramPacketUtils;

/**
 * Test class for DatagramPacketUtils class
 * @author Rahul Anilkumar
 *
 */
class DatagramPacketUtilsTest {
	DatagramPacket sendPacket;
	String testVal;
	
	/**
	 * Setup method to initialize required values
	 * @throws Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		testVal="Tests conducted";
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		message.write(NetworkConstants.MessageTypes.ELEVATOR_REQUEST.getMarker());
		message.write(NetworkConstants.NULL_BYTE);
		message.write(testVal.getBytes());
		message.write(NetworkConstants.NULL_BYTE);
		message.close();
		sendPacket = new DatagramPacket(message.toByteArray(), message.size());
	}

	/**
	 * Test the static method to get the body of a datagram packet back as a string
	 */
	@Test
	void getMessageBodyAsStringTest() {
		String test = DatagramPacketUtils.getMessageBodyAsString(sendPacket);
		assertTrue(test!=null);
		assertEquals(test,testVal);
	}

	/**
	 * Test the static method to print the body of a datagram packet  data as hex values 
	 */
	@Test
	void printTest() {
		String testValHex = "[ 0x2F 0x00 0x54 0x65 0x73 0x74 0x73 0x20 0x63 0x6F 0x6E 0x64 0x75 0x63 0x74 0x65 0x64 0x00 ]";
		String test = DatagramPacketUtils.print(sendPacket);
		assertTrue(test!=null);
		assertEquals(test,testValHex);
	}
	
	/**
	 * Test the static method to get a copy of a datagram packet
	 */
	@Test
	void getCopyOfTest() {
		DatagramPacket test = DatagramPacketUtils.getCopyOf(sendPacket);
		assertTrue(test!=null);
		assertEquals(test.getData(),sendPacket.getData());
	}
}

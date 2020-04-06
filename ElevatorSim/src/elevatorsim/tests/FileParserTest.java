package elevatorsim.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorsim.common.constants.Direction;
import elevatorsim.common.requests.ElevatorRequest;
import elevatorsim.common.util.FileParser;
/**
 * Test cases for the file parser
 * @author Rahul Anilkumar
 *
 */
class FileParserTest {
	String path;
	private static final String inputFilePath = "resources/test.txt";
	
	/**
	 * Sets up the test by instantiating the path string with its absolute path
	 * to the test file
	 * @throws Exception any exception that may occur during testing
	 */
	@BeforeEach
	void setUp() throws Exception {
		path = new File(inputFilePath).getAbsolutePath();
	}

	/**
	 * Test to check the functionality of the parser and to ensure that all components
	 * associated with the parser are working as intended.
	 */
	@Test
	void parseInputFiletest() {
		HashMap<Integer, ElevatorRequest> requestMap = new HashMap<Integer, ElevatorRequest> ();
		//Check that the map is initailly empty
		assertTrue(requestMap.isEmpty());
		requestMap = FileParser.parseInputFile(path);
		//chack that the map is not empty and filled with the expected values from test.txt 
		//found in the resources directory
		assertFalse(requestMap.isEmpty());
		assertTrue(requestMap.size() == 5);
		
		// check values for a message with the following input from the test input file
		// 14:05:15.0 2 Up 5
		assertEquals(requestMap.get(2).getTimeStamp().toString(),"14:05:15");
		assertEquals(requestMap.get(2).getStartFloor(),2);
		assertEquals(requestMap.get(2).getDirection(),Direction.UP);
		assertEquals(requestMap.get(2).getDestFloor(),5);
		
		//Check if the earliest timestamp is the first element in the map with the
		//first requestId of 0 input message would be -- 05:05:15.0 1 Up 3
		assertEquals(requestMap.get(0).getTimeStamp().toString(),"05:05:15");
		assertEquals(requestMap.get(0).getRequestId(),0);
		
		//check latest timestamp input -- 15:05:15.0 5 Down 4
		assertEquals(requestMap.get(4).getTimeStamp().toString(),"15:05:15");
		assertEquals(requestMap.get(4).getRequestId(),4);
	}
}
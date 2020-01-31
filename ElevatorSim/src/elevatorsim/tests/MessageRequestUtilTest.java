package elevatorsim.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorsim.common.MessageRequest;
import elevatorsim.util.MessageRequestUtil;

/**
 * Test class for the MessageRequestUtil class
 * @author Rahul Anilkumar
 *
 */
class MessageRequestUtilTest {

	private ArrayList<MessageRequest> requestL;
	private HashMap<Integer, MessageRequest> mess;
	private MessageRequest request, request2, request3, request4;
	
	/**
	 * Setup for each of the tests
	 * @throws Exception any excpetions that may occur
	 */
	@BeforeEach
	void setUp() throws Exception {
		request = new MessageRequest("14:05:15.0", "2", "Up", "5");
		request2 = new MessageRequest("05:05:23.0", "1", "Up", "3");
		request3 = new MessageRequest("10:05:12.0", "7", "down", "6");
		request4 = new MessageRequest("12:05:12.0", "2", "Up", "7");
		requestL = new ArrayList<MessageRequest>();
		requestL.add(request);
		requestL.add(request2);
		requestL.add(request3);
		requestL.add(request4);
		System.out.println(requestL.size());
		mess = new HashMap<Integer, MessageRequest>();
		mess.put(0, request);
		mess.put(1, request2);
		mess.put(2, request3);
		mess.put(3, request4);
	}

	/**
	 * Test case for the SortByTimeStamp utility method
	 */
	@Test
	void SortByTimestamptest() {
		HashMap<Integer, MessageRequest> requestMap = new HashMap<Integer, MessageRequest>();
		//check that the map is empty
		assertTrue(requestMap.isEmpty());
		requestMap = MessageRequestUtil.sortByTimestamp(requestL);
		//check that the requestMap is not null;
		assertFalse(requestMap.isEmpty());
		//check that the values are sorted in the correct order by time 
		assertTrue(requestMap.get(0).equals(request2));
		assertTrue(requestMap.get(1).equals(request3));
		assertTrue(requestMap.get(2).equals(request4));
		assertTrue(requestMap.get(3).equals(request));
	}

	/**
	 * Test case for the getSingleMessage utility method
	 */
	@Test
	void getSingleMessagetest() {
		//check that the map is not empty
		assertFalse(mess.isEmpty());
		// Check if the expected value is returned;
		assertTrue(MessageRequestUtil.getSingleMessage(mess,2).equals(request3));
		//Check that the value is null if a vlaue outside of the map is requested
		assertTrue(MessageRequestUtil.getSingleMessage(mess,10) == null);		
	}
	
	/**
	 * Test case for the removeMessage utility method with a single requestId as a parameter 
	 */
	@Test
	void removeOneMessagetest() {
		//check that the map is not empty
		assertFalse(mess.isEmpty());
		// Check if the expected value is returned;
		assertTrue(MessageRequestUtil.removeMessage(mess,1));
		//Check that the value is false if a value outside of the map is requested
		assertFalse(MessageRequestUtil.removeMessage(mess,10));		
	}
	
	/**
	 * Test case for the removeMessage utility method with an array of requestIds to remove 
	 */
	@Test
	void removeMultipleMessagetest() {
		//check that the map is not empty
		assertFalse(mess.isEmpty());
		// Check if the expected value is returned;
		int[] myNums = {1, 3};
		assertTrue(MessageRequestUtil.removeMessage(mess,myNums));
		//Check that the value is false if a value outside of the map is requested
		myNums[1] = 2;
		assertFalse(MessageRequestUtil.removeMessage(mess,myNums));		
	}
	
	/**
	 * Test case for the getAllRequestIds utility method
	 */
	@Test
	void getAllRequestIdstest() {
		//check that the map is not empty
		assertFalse(mess.isEmpty());
		// Check if the number of request ids is equal to number of entries in the map;
		assertTrue(MessageRequestUtil.getAllRequestIds(mess).size() == mess.size());
	}
	
	/**
	 * Test case for the getRequestMapByFloor utility method
	 */
	@Test
	void getRequestMapByFloortest() {
		//check that the map is not empty
		assertFalse(mess.isEmpty());
		// Check if the number of requests in the returned map == the expected number of requests
		assertTrue(MessageRequestUtil.getRequestMapByFloor(mess,2).size() == 2);
		//Check that the floors values are correct for the returned map elements
		HashMap<Integer, MessageRequest> requestMap = MessageRequestUtil.getRequestMapByFloor(mess,2);
		for(Integer key: requestMap.keySet()) {
			assertTrue(requestMap.get(key).getStartFloor() == 2);
		}
		
	}
	
	
	
}

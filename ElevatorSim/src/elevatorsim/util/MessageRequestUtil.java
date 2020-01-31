package elevatorsim.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import elevatorsim.common.MessageRequest;

/**
 * A utility class to help with manipulating the MessageRequest DataStructures
 * @author Rahul Anilkumar
 *
 */
public class MessageRequestUtil {

	/**
	 * Returns a HashMap with the key value of a unique request id and messages
	 * sorted by the time of input
	 * 
	 * @return a Hashmap containing all the messages read from a list
	 */
	public static HashMap<Integer, MessageRequest> sortByTimestamp(ArrayList<MessageRequest> requestList) {
		HashMap<Integer, MessageRequest> requestMap = new HashMap<Integer, MessageRequest>();
		ArrayList<MessageRequest> list = requestList;
		Collections.sort(list);
		int requestId = 0;
		for (MessageRequest request : list) {
			request.setRequestId(requestId);
			requestMap.put(requestId, request);
			requestId++;
		}
		return requestMap;
	}

	/**
	 * Returns a message based of the requestId passed
	 * 
	 * @param requestMap the map that contains requests
	 * @param requestId  the identifier for the request;
	 * @return the message for the specified request
	 */
	public static MessageRequest getSingleMessage(HashMap<Integer, MessageRequest> requestMap, int requestId) {
		MessageRequest request = null;
		try {
			if (requestMap.containsKey(requestId)) {
				request = requestMap.get(requestId);
			} else {
				System.out.println("There is no active request for " + requestId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * Removes a single message from the map corresponding to the specified
	 * requestId
	 * 
	 * @param requestMap the map being modified
	 * @param requestId  the requestId used to remove a specified message
	 */
	public static void removeMessage(HashMap<Integer, MessageRequest> requestMap, int requestId) {
		if (requestMap.containsKey(requestId)) {
			requestMap.remove(requestId);
		} else {
			System.out.println("There is no active request for " + requestId);
		}
	}

	/**
	 * Removes all Messages based of a list of requestIds that have been serviced.
	 * Use to remove a batch of messages from the map at the same time
	 * 
	 * @param requestMap the map being modified
	 * @param requestIds the array of message requestIds being removed
	 */
	public static void removeMessage(HashMap<Integer, MessageRequest> requestMap, int[] requestIds) {
		for (int id : requestIds) {
			requestMap.remove(id);
		}
	}

	/**
	 * Retrieves a set containing all the requestIds still in the map
	 * 
	 * @param requestMap the map that the requestIds are retireved from
	 * @return A set containing all requestIds in the map
	 */
	public static Set<Integer> getAllRequestIds(HashMap<Integer, MessageRequest> requestMap) {
		return requestMap.keySet();
	}
	
	/**
	 * Gets a map of all the requests for a specified starting floor number
	 * @param requestMap the map that the requests are gathered from
	 * @param floorNumber the floor number that the requests are being aggregated for
	 * @return the aggregated map of requests
	 */
	public static HashMap<Integer, MessageRequest> getRequestMapByFloor(HashMap<Integer, MessageRequest> requestMap, int floorNumber){
		HashMap<Integer, MessageRequest> newMap = new HashMap<Integer, MessageRequest>();
		for(Integer key: requestMap.keySet()) {
			if(requestMap.get(key).getStartFloor() == floorNumber) {
				newMap.put(key,requestMap.get(key));
			}
		}
		return newMap;
	}

}

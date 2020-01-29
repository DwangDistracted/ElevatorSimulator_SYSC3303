package elevatorsim.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import elevatorsim.model.MessageRequest;

public class MessageRequestUtil {

	/**
	 * Returns a Hashmap with the key value of a unique request id and messages
	 * sorted by the time of input
	 * 
	 * @return
	 */
	public static HashMap<Integer, MessageRequest> sortByTimestamp(ArrayList<MessageRequest> requestList) {
		HashMap<Integer, MessageRequest> requestMap = new HashMap<Integer, MessageRequest>();
		ArrayList<MessageRequest> list = requestList;
		Collections.sort(list);
		int requestId = 0;
		for (MessageRequest request : list) {
			request.setRequestId(requestId);
			System.out.println("Item#" + requestId);
			requestMap.put(requestId, request);
			System.out.println(requestMap.get(requestId).toString()); // TODO remove later
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
	
	
	public static HashMap<Integer, MessageRequest> getRequestMapByFloor(HashMap<Integer, MessageRequest> requestMap, int floorNumber){
		HashMap<Integer, MessageRequest> newMap = new HashMap<Integer, MessageRequest>();
		for(Integer key: requestMap.keySet()) {
			if(requestMap.get(key).getStartFloor() == floorNumber) {
				newMap.put(key,requestMap.get(key));
			}
		}
		return newMap;
		
	}

	// TODO remove later for testing only.
	public static void main(String[] args) {
		HashMap<Integer, MessageRequest> mess = new HashMap<Integer, MessageRequest>();
		MessageRequest request = new MessageRequest("14:05:15.0", "2", "Up", "5");
		MessageRequest request2 = new MessageRequest("05:05:23.0", "1", "Up", "3");
		ArrayList<MessageRequest> requestL = new ArrayList<MessageRequest>();
		requestL.add(request);
		requestL.add(request2);
		System.out.println(requestL.size());
		mess = sortByTimestamp(requestL);
		System.out.println(getSingleMessage(mess, 0).toString() + " " + getSingleMessage(mess, 1).toString());
	}

}

package elevatorsim.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import elevatorsim.common.MessageRequest;

/**
 * File parser to parse the inputs for the system
 * @author Rahul, Michael, Thomas, David, Trevor
 *
 */
public class FileParser {
	
	/**
	 * Parse the file and return a Hashmap of the messages sorted by timestamp and a requestId set as the key value
	 * Request Id is sequential with the order of the sorted messages. Ex. Earliest request will have starting Request Id = 0
	 * @param filePath the file path of the input file
	 * @return the Hashmap containing all the messages parsed from the input file
	 */
	public static HashMap<Integer, MessageRequest> parseInputFile(String filePath){
		ArrayList<MessageRequest> requestList= getMessageRequestList(getInputFromFile(filePath));
		HashMap<Integer, MessageRequest> requestMap = MessageRequestUtil.sortByTimestamp(requestList);
		return requestMap;
	}
	
	/**
	 * Parses the file and saves each line of input into an arraylist as an array of Strings
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	private static ArrayList<String[]> getInputFromFile(String filePath){
		ArrayList<String[]> inputMessages = new ArrayList<String[]>();
		File file = new File(filePath);
		if(file.isFile()) {
			Scanner scanner;
			try {
				scanner = new Scanner(new File(filePath));
				while (scanner.hasNextLine()) {
					String str[] = scanner.nextLine().split(" ");
					inputMessages.add(str);
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return inputMessages;
	}
	
	/**
	 * Fetch all inputs passed in the input file and return a list of MessageRequest objects
	 * modeling the input requests
	 * @param filePath String representation of the location of the file
	 * @return A list of MessageRequest objects
	 */
	private static ArrayList<MessageRequest> getMessageRequestList(ArrayList<String[]> inputMessages){
		ArrayList<MessageRequest> inputRequests = new ArrayList<MessageRequest>();
		for(String[] input:inputMessages) {
			MessageRequest request = new MessageRequest(input[0],input[1],input[2],input[3]);
			inputRequests.add(request);
		}
		return inputRequests;
	}
}

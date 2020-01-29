package elevatorsim;

import java.util.HashMap;

import elevatorsim.model.MessageRequest;
import elevatorsim.util.FileParser;
import elevatorsim.util.MessageRequestUtil;

public class Main {

	public static void main(String[] args) {
		//Just test stuff to try out some manipulations with the Messaging structure
		/*
		HashMap<Integer, MessageRequest> requestMap = FileParser.parseInputFile("C:\\Users\\Micro\\Documents\\school\\SYSC3303\\Project\\ElevatorSimulator_SYSC3303\\ElevatorSim\\Resources\\test.txt");
		//MessageRequestUtil.removeMessage(requestMap, 3);
		//System.out.println(MessageRequestUtil.getSingleMessage(requestMap, 3).toString());
		System.out.println(MessageRequestUtil.getSingleMessage(requestMap, 4).toString() +"\n----------\n");
		
		HashMap<Integer, MessageRequest> newMap = MessageRequestUtil.getRequestMapByFloor(requestMap, 7);
		for(Integer key: newMap.keySet()) {
			System.out.println(newMap.get(key).toString());
		}*/
	}

}

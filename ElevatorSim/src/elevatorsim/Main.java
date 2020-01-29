package elevatorsim;

import java.util.HashMap;

import elevatorsim.model.MessageRequest;
import elevatorsim.util.FileParser;

public class Main {

	public static void main(String[] args) {
		HashMap<Integer, MessageRequest> requestMap = FileParser.parseInputFile("You can use the test file in Resources folder to test It has some dummy input");
	}

}

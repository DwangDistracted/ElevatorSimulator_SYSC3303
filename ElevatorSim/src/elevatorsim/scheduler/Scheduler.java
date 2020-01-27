package elevatorsim.scheduler;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler extends Thread {
	private List<Floor> floors;
	private List<Elevator> elevators;
	
	private Queue<MessageRequest> messageRequests;
	
	public Scheduler() {
		floors = new ArrayList<Floor>();
		elevators = new ArrayList<Elevator>();
		messageRequests = new LinkedBlockingQueue<MessageRequest>();
	}
	
	public synchronized void addFloor(Floor floor) {
		floors.add(floor);
	}
	public synchronized void addElevator(Elevator elevator) {
		elevators.add(elevator);
	}
	
	public 
	
	@Override
	public void run() {
		while (true) {
			MessageRequest request = messageRequests.pop();
			// find the destination
			// send to destination
		}
	}
}

package elevatorsim.elevator;

import elevatorsim.common.MessageReciever;
import elevatorsim.common.MessageRequest;
import elevatorsim.enums.MessageDestination;
import elevatorsim.scheduler.Scheduler;

public class Elevator extends Thread implements MessageReciever {
	private Scheduler scheduler;
	private int floorAmount;
	
	public Elevator ( int floorAmount ) {
		this.floorAmount = floorAmount;
		scheduler = Scheduler.getInstance();
	}

	public void run() {
		while (true) {
			try {
				//get message
				wait();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void recieve(MessageRequest message) {
		// TODO Auto-generated method stub
		System.out.println("Elevator received message: " + message.toString() );
		scheduler.sendMessage(MessageDestination.FLOORS, message);
	}
}
package elevatorsim.common;

/**
 * This class contains information that the scheduler can use to determine which elevator to service an elevator request.
 * This should be send to the scheduler from the elevator. (TODO - discuss when/how)
 *  
 * @author David Wang
 */
public class ElevatorStatus extends ElevatorMessage<ElevatorStatus> {
	// TODO - what should an elevator send back as its status?
	private String placeholder;

	public ElevatorStatus(String text) {
		this.placeholder = text;
	}

	public static ElevatorStatus empty() {
		return new ElevatorStatus("");
	}

	public String toString() {
		return placeholder;
	}
}

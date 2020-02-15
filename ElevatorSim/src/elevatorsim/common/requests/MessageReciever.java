package elevatorsim.common.requests;

/**
 * An interface for the Messages being sent between the different components
 * @author David Wang
 */
public interface MessageReciever {
	/**
	 * Receives a message from a Message Sender
	 * @param message the message being received
	 */
	public void receive(Request message);
}

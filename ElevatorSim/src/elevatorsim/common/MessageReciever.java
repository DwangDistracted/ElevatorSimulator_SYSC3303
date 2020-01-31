package elevatorsim.common;

/**
 * An interface for the Messages being sent between the different components
 *
 */
public interface MessageReciever {
	/**
	 * Receives a message from a Message Sender
	 * @param message the message being recieved
	 */
	public void recieve(MessageRequest message);
}

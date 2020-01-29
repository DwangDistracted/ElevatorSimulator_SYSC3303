package elevatorsim.common;

public interface MessageReciever {
	/**
	 * Receives a message from a Message Sender
	 * @param message
	 */
	public void recieve(MessageRequest message);
}

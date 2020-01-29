package elevatorsim.scheduler;

public interface MessageRequester {
	public void send(Floor destination);
	public void recieve(MessageRequest message);
}

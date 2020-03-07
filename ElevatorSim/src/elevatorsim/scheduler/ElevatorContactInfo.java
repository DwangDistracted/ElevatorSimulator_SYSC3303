package elevatorsim.scheduler;

import java.net.InetAddress;

public class ElevatorContactInfo {
	final InetAddress address;
	final int receiverPort;
	
	ElevatorContactInfo(InetAddress address, int receiverPort) {
		this.address = address;
		this.receiverPort = receiverPort;
	}
}
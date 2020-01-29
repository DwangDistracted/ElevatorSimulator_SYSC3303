package elevatorsim.elevator;


public class ElevatorButton {
	private int floorIndex;
	private Elevator elevator;

	public ElevatorButton (Elevator elevator, int floorIndex) {
		this.elevator = elevator;
		this.floorIndex = floorIndex;
	}
	
}

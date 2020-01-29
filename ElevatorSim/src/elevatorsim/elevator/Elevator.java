package elevatorsim.elevator;

import elevatorsim.common.MessageReciever;
import elevatorsim.common.MessageRequest;
import elevatorsim.enums.ElevatorDoorState;
import elevatorsim.enums.ElevatorMotorState;

public class Elevator extends Thread implements MessageReciever {
	private int floorIndex;
	private ElevatorButton[] elevatorButtons;
	private ElevatorLamp[] elevatorLamps;
	private ElevatorDoor elevatorDoor;
	private ElevatorMotor elevatorMotor;
	
	public Elevator ( int floorAmount ) {
		elevatorButtons = new ElevatorButton[floorAmount];
		elevatorLamps = new ElevatorLamp[floorAmount];
		for (int i = 0; i < floorAmount; i++) {
			elevatorButtons[i] = new ElevatorButton(this,i);
			elevatorLamps[i] = new ElevatorLamp(this,i);
		}
		elevatorDoor = new ElevatorDoor(this);
		elevatorMotor = new ElevatorMotor(this);
	}
	
	public void sendButtonPress(int floorIndex){
		System.out.println("ELEVATOR SEND BUTTON PRESS EVENT W/ PARAM" + String.valueOf(floorIndex) );
		
	}
	
	public void sendStopped() {
		System.out.println("ELEVATOR SEND STOP EVENT");
	}
	
	public void sendOpenedDoor() {
		System.out.println("ELEVATOR SEND OPENED DOOR EVENT");
	}
	
	public void sendClosedDoor() {
		System.out.println("ELEVATOR SEND ClOSED DOOR EVENT");
	}
	
	private void closeDoor() {
		elevatorDoor.targetDoorState(ElevatorDoorState.CLOSED);
	}
	
	private void openDoor() {
		System.out.print("opening door");
		elevatorDoor.targetDoorState(ElevatorDoorState.OPEN);
	}
	
	private void motorUp() {
		elevatorMotor.setMotorDirection(ElevatorMotorState.UP);
	}
	
	private void motorDown() {
		elevatorMotor.setMotorDirection(ElevatorMotorState.DOWN);
	}
	
	private void motorStop() {
		elevatorMotor.setMotorDirection(ElevatorMotorState.STOP);
	}
	
	public static void main(String[] args) {
		System.out.print("running my elevator entry");
		Elevator elevator = new Elevator(5);
		elevator.openDoor();
	}
	
	public void run() {
		while (true) {
			try {
				//get message
				System.out.print("why god");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void recieve(MessageRequest message) {
		// TODO Auto-generated method stub
		
	}
}
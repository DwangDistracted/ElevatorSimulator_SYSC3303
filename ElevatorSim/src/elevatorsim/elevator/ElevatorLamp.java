package elevatorsim.elevator;


public class ElevatorLamp {

		private Elevator elevator;
		private int floorIndex;
		private boolean isOn;
		
		public ElevatorLamp (Elevator elevator, int floorIndex) {
			this.elevator = elevator;
			this.floorIndex = floorIndex;
			this.isOn = false;
		}
		
		public void setIsOn(boolean isOn) {
			assert (isOn == true || isOn == false);
			this.isOn = isOn;
		}
		
		public boolean getIsOn() {
			return this.isOn;
		}
		
		public int getFloorIndex() {
			return this.floorIndex;
		}
}
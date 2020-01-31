package elevatorsim.floor;

/**
 * An object to model the buttons on the floor
 * @author Michael
 *
 */
public class FloorButton {
	private boolean upFloorButton;
	private boolean downFloorButton;
	
	/**
	 * A constructor to set the floor buttons to unclicked state
	 */
	public FloorButton() {
		upFloorButton = false;
		downFloorButton = false;
	}
	
	/**
	 * A constructor to set the buttons to a specified value
	 * @param upFloorButton
	 * @param downFloorButton
	 */
	public FloorButton(boolean upFloorButton, boolean downFloorButton) {
		this.upFloorButton = upFloorButton;
		this.downFloorButton = downFloorButton;
	}

	/**
	 * Getter method to get the up button
	 * @return true if the up button is clicked/active
	 */
	public boolean getUpFloorButton() {
		return upFloorButton;
	}

	/**
	 * Setter method to set the up button
	 * @param upFloorButton the status of the up button to set to
	 */
	public void setUpFloorButton(boolean upFloorButton) {
		this.upFloorButton = upFloorButton;
	}

	/**
	 * Getter method to get the down button
	 * @return True if the button is pressed/active
	 */
	public boolean getDownFloorButton() {
		return downFloorButton;
	}

	/**
	 * Setter method to set the down button as active or inactive
	 * @param downFloorButton the status the button is set to
	 */
	public void setDownFloorButton(boolean downFloorButton) {
		this.downFloorButton = downFloorButton;
	}

}

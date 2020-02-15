package elevatorsim.floor;

/**
 * An object to model the buttons on the floor
 * 
 * true -> lamp on
 * false -> lamp off
 * null -> no lamp
 * @author Michael Patsula
 *
 */
public class DirectionLamp {
	private Boolean upLamp;
	private Boolean downLamp;
	
	/**
	 * A constructor to set the buttons to a specified value.
	 * True indicate lamp is activated, and false indicates lamp is deactivate
	 * Setting lamp to null indicates the lamp diesn't exist
	 * @param upFloorButton
	 * @param downFloorButton
	 */
	public DirectionLamp(Boolean upLamp, Boolean downLamp) {
		this.upLamp = upLamp;
		this.downLamp = downLamp;
	}

	/**
	 * Getter method to get the up button
	 * @return true if the up button is clicked/active
	 */
	public boolean getUpLamp() {
		return upLamp;
	}

	/**
	 * Setter method to set the up button
	 * @param upFloorButton the status of the up button to set to
	 */
	public boolean setUpLamp(boolean upLamp) {
		if(this.upLamp != null) {
			this.upLamp = upLamp;
			return true;
		}
		
		return false;
	}

	/**
	 * Getter method to get the down button
	 * @return True if the button is pressed/active
	 */
	public boolean getDownLamp() {
		return downLamp;
	}

	/**
	 * Setter method to set the down button as active or inactive
	 * @param downFloorButton the status the button is set to
	 */
	public boolean setDownLamp(boolean downLamp) {
		if(this.downLamp != null) {
			this.downLamp = downLamp;
			return true;
		}
		
		return false;
	}

}
package model;

public class FloorButton {
	private boolean upFloorButton;
	private boolean downFloorButton;
	
	public FloorButton(boolean upFloorButton, boolean downFloorButton) {
		this.upFloorButton = upFloorButton;
		this.downFloorButton = downFloorButton;
	}

	public boolean getUpFloorButton() {
		return upFloorButton;
	}

	public void setUpFloorButton(boolean upFloorButton) {
		this.upFloorButton = upFloorButton;
	}

	public boolean getDownFloorButton() {
		return downFloorButton;
	}

	public void setDownFloorButton(boolean downFloorButton) {
		this.downFloorButton = downFloorButton;
	}

}

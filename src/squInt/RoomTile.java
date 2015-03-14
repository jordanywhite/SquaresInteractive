package squInt;

/**
 * A Tile within a Room object.
 * 
 * This is a separate class so we can add functionality later (direction, etc)
 *
 */
public class RoomTile {
	private int occupantID; // userID, or -1 if empty
	
	public RoomTile() {
		this.occupantID = -1;
	}
	
	public int getOccupantID() { return occupantID; }
	
	public void setOccupantID(int userID) { occupantID = userID; }
}

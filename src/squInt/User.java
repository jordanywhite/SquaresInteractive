package squInt;

/**
 * One User per host. Contains the local host's data.
 *
 */
public abstract class User {
	private int userID;
	private int[] otherUsers;
	private Room room;
	private GUI gui;
	
	public User() {
		this.room = new Room();
		this.gui = new GUI();
	}
	
	public int getUserID() { return userID; }
	public Room getRoom() { return room; }
	public GUI getGui() { return gui; }
	
	public void setUserID(int userID) { this.userID = userID; }
	public void setRoom(Room room) { this.room = room; }
	public void setGui(GUI gui) { this.gui = gui; }

}

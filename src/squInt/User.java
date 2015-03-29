package squInt;

import java.net.Inet4Address;
import java.net.InetAddress;

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

	public int getUserID() {
		return userID;
	}

	public Room getRoom() {
		return room;
	}

	public GUI getGui() {
		return gui;
	}

	public InetAddress getIp() {
		// TODO make this method not terrible
		try{
			return Inet4Address.getLocalHost();
		} catch(Exception e) {
			return null;
		}
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}

}

package squInt;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * One User per host. Contains the local host's data.
 *
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 *
 */
public abstract class User extends Player{
	private int userID;
	private int[] otherUsers;
	private Room room;

	public User(int x, int y, int direction, boolean ableToMove, int id) {		
		super(x, y, direction, ableToMove, id);
		this.room = new Room();
	}

	public int getUserID() {
		return userID;
	}

	public Room getRoom() {
		return room;
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

}

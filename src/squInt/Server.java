package squInt;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Queue;

/**
 * The User object for the server.
 *
 */
public class Server extends User {
	
	public static final int MAX_CLIENTS = 30; //64
	public static final int SERVER_ID = 0;
	/*
	 * Hash table with User ids connecected to ip addresses for interesting
	 * information
	 */
	Hashtable<Integer, InetAddress> myIpLookup;

	/*
	 * The unique ids assigned to every client by the server
	 */
	int[] userIds;

	/*
	 * The id to be assigned to the next client
	 */
	int nextId = 1;

	public Server() {
		// Make a user with all the bells and whistles
		super(0,0,Player.DOWN,true,SERVER_ID);

		// Make this server a proper user with an id and ip
		setUserID(SERVER_ID);
		userIds = new int[MAX_CLIENTS];
		userIds[0] = getUserID();
		myIpLookup = new Hashtable<Integer, InetAddress>();
		myIpLookup.put(SERVER_ID, this.getIp());
	}

	/**
	 * getMasterRoom The one room to rule them all
	 * 
	 */
	public Room getMasterRoom() {
		return this.getRoom();
	}

	/**
	 * getClientIpAddress
	 */
	public InetAddress getClientIpAddress(int id) {
		return myIpLookup.get(id);
	}

	/**
	 * registerNewUser Register a new user with the server and assign a user id
	 * to this new user
	 * 
	 * @param addr
	 *            The ip address of the new client
	 * @return the id of client assigned by the server
	 */
	public int registerUser(InetAddress addr) {
		if (addr == null) {
			System.out.println("ERROR: Null IP address");
			return -1;
		}
		myIpLookup.put(nextId, addr);
		nextId++;
		return nextId - 1;
	}

	/**
	 * removeUser Remove a disconnected client from our server
	 * 
	 * @param id
	 *            the id of the removed client
	 */
	public void removeUser(int id) {
		myIpLookup.remove(id);
	}

//	/**
//	 * requestAction Judge the worth of an action
//	 * 
//	 * @param action
//	 *            on which we shall act on
//	 * @return true if the action is worthy, false if the action is found
//	 *         wanting
//	 */
//	public boolean requestAction(ClientAction action) {
//		// TODO: Reaffirm mechanics
//		return true;
//	}
}

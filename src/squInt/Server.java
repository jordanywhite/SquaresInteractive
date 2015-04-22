package squInt;

import java.awt.Point;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * The User object for the server.
 *
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 */
public class Server extends User {

	public static final int MAX_CLIENTS = 32;
	public static final int SERVER_ID = 0;
	/*
	 * Hash table with User ids connected to ip addresses for interesting
	 * information
	 */
	private Hashtable<Integer, InetAddress> myIpLookup;

	/*
	 * The unique ids assigned to every client by the server
	 */
	private ArrayList<Integer> userIds;

	/*
	 * Convenient way to know where everyone is for the server
	 */
	private int[][] idPos;

	/*
	 * The id to be assigned to the next client
	 */
	private int nextId;

	public Server() {
		// Make a user with all the bells and whistles
		super(0, 0, Player.DOWN, true, SERVER_ID);
		nextId = 1;

		// Make this server a proper user with an id and ip
		userIds.add(SERVER_ID);

		// Init ease of access id array
		idPos = new int[Room.WIDTH][Room.HEIGHT];
		for (int j = 0; j < Room.HEIGHT; j++) {
			for (int i = 0; i < Room.WIDTH; i++) {
				idPos[i][j] = -1;
			}
		}
		idPos[0][0] = SERVER_ID;
		room.getTileAt(0, 0).setOccupantID(SERVER_ID);

		myIpLookup = new Hashtable<Integer, InetAddress>();
		myIpLookup.put(SERVER_ID, this.getIp());
	}

	/**
	 * getPlayerPos retreive x and y position for a player in a point
	 * 
	 * @param id
	 *            the client
	 * @return a point with the x and y pos of a player, or null if id is unused
	 */
	public Point getPlayerPos(int id) {
		for (int j = 0; j < Room.HEIGHT; j++) {
			for (int i = 0; i < Room.WIDTH; i++) {
				if (idPos[i][j] == id) {
					return new Point(i, j);
				}
			}
		}
		return null;
	}

	/**
	 * changePlayerPos move the players effective location to designated spot
	 * 
	 * @param id
	 *            client to be moved
	 * @param toX
	 *            x pos to move to
	 * @param toY
	 *            y pos to move to
	 * @return true if successfully moved, false otherwise
	 */
	public boolean changePlayerPos(int id, int toX, int toY) {
		Point p = getPlayerPos(id);
		if (p == null) {
			return false;
		}

		int x = (int) p.getX();
		int y = (int) p.getY();
		if (!inRoom(x, y) || !inRoom(toX, toY)) {
			return false;
		}
		if (idPos[toX][toY] == -1) {
			idPos[x][y] = -1;
			idPos[toX][toY] = id;
			room.getTileAt(x, y).setOccupantID(-1);
			room.getTileAt(toX, toY).setOccupantID(id);
			return true;
		}
		return false;
	}

	/**
	 * inRoom is this point in the room
	 * 
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @return true if in the room, false otherwise
	 */
	public boolean inRoom(int x, int y) {
		return (x >= 0 && x < Room.WIDTH) && (y >= 0 && y < Room.HEIGHT);
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

		int x = 0;
		int y = 0;

		// Give our new user a tile to start at
		while (true) {
			x = (int) (Math.random() * (Room.WIDTH + 1));
			y = (int) (Math.random() * (Room.HEIGHT + 1));
			if (idPos[x][y] == -1) {
				idPos[x][y] = nextId;
				room.getTileAt(x, y).setOccupantID(nextId);
				userIds.add(nextId);
				break;
			}
		}

		// Increment the user id for the next user and return the id we used for
		// this user
		nextId++;
		return nextId - 1;
	}

	/**
	 * removeUser Remove a disconnected client from our server
	 * 
	 * @param id
	 *            the id of the removed client
	 * @retun true if the removal was successful, false otherwise
	 */
	public boolean removeUser(int id) {
		Point hereIAm = getPlayerPos(id);
		if (hereIAm != null) {
			room.getTileAt(hereIAm.x, hereIAm.y).setOccupantID(-1);
			myIpLookup.remove(id);
			idPos[hereIAm.y][hereIAm.y] = -1;
			userIds.remove(id);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * kick A more satisfying form of removing a user
	 * 
	 * @param id
	 *            client to be kicked
	 * @return true if successfully kicked, false otherwise
	 */
	public boolean kick(int id) {
		return removeUser(id);
	}

	/**
	 * requestAction Judge the worth of an action
	 *
	 * @param action
	 *            on which we shall act on
	 * @return true if the action is worthy, false if the action is found
	 *         wanting
	 */
	public boolean requestAction(PlayerAction action) {

		if (action == null) {
			return false;
		}

		int playerId = action.playerId;
		int x = (int) getPlayerPos(playerId).getX();
		int y = (int) getPlayerPos(playerId).getY();
		if (!inRoom(x, y)) {
			return false;
		}

		//TODO: The action also needs the current direction of the client
		switch (action.action) {
		case MOVE_UP: {
			return moveTo(playerId, x, y, 0, -1);
		}
		case MOVE_DOWN: {
			return moveTo(playerId, x, y, 0, 1);
		}
		case MOVE_LEFT: {
			return moveTo(playerId, x, y, -1, 0);
		}
		case MOVE_RIGHT: {
			return moveTo(playerId, x, y, 1, 0);
		}
		case INTERACT:
			// TODO
			break;
		default:
			System.out.println("Invalid action: should be impossible but apparently not.");
			return false;
		}

		return false;
	}
	
	/**
	 * moveTo pointless method that moves things in a more understandable way
	 * 
	 * @param id client id to be moved
	 * @param x client x position
	 * @param y client y position
	 * @param xOffset move this far horizontally
	 * @param yOffset move this far vertically
	 * @return true is successfully moved, false otherwise
	 */
	private boolean moveTo(int id, int x, int y, int xOffset, int yOffset) {
		return changePlayerPos(id, x + xOffset, y + yOffset);
	}
}

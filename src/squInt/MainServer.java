package squint;

import guiMap.Map;
import guiMap.MapEditor;
import guiMap.MapSquare;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import actions.Action;
import actions.PlayerAction;
import player.Player;
import resourceManagement.AvatarGroup;
import resourceManagement.ResourceLoader;
import serverManagement.DataPort;
import serverManagement.ServerConnectionTable;
import serverManagement.ServerQueuedMessage;

/**
 * The server that hosts the interactive room.
 *
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 */
public class MainServer {

	public static final int MAX_CLIENTS = 32;
	public static final int SERVER_ID = 0;

	/*
	 * The id to be assigned to the next client
	 */
	private int nextId = 0;
	
	public static int totalConnections = 2;
	
	// Keep track of how many clients are currently connected
	private static int currentNumConnections = 0;
	
	// These are references to data in the 'level' variable
	private MapSquare[][] mapSquares = null;
	
	// The players connected to the server
	private HashMap<Integer, Player> players = null;
	
	// The avatar names
	private String[] avatarNames = null;
	
	private ArrayList<String> initMsgs = new ArrayList<String>();
	
	// Holds the server connection table
	private ServerConnectionTable serverTable = null;
	
	// holds the incoming message queue
	private LinkedList<ServerQueuedMessage> serverQueue = null;
	
	// The thread that listens for incoming connections to the server
	private Thread serverTableThread;
	
	public static void main(String[] args) {
		/** TO RUN: run MainServer and then run MainClient numConnections times **/
		MainServer mainServer = new MainServer();
		
		// now have server just wait for inc messages and print them (until program is terminated)
		while(true) {
			if (mainServer.serverTable.getNumConnections() != currentNumConnections) {
				currentNumConnections = mainServer.serverTable.getNumConnections();
				// A player is registered with the same ID as the connection
				String playerInitMsg = mainServer.registerUser();
				mainServer.initMsgs.add(playerInitMsg);
				// Since the player / connection ID is stored in nextId which is incremented,
				// the id for this connection is nextId - 1;
				mainServer.serverTable.sendToAll(playerInitMsg);
				
				// Every time somone connects, resend all player locations to all clients
				for (String initMsg : mainServer.initMsgs) {
					mainServer.serverTable.sendToAll(initMsg);
				}
			}
			
			if(mainServer.serverQueue.peek() != null) {
				// ServerQueuedMessage object just contains the source DataPort and the received String
				ServerQueuedMessage sqm = mainServer.serverQueue.poll();
				System.out.println("SERV RCVD from " + sqm.source.getUniqueId() + ": " + sqm.message);
				
				if (mainServer.requestAction(PlayerAction.parseFromMsg(sqm.message))) {
					// send diff
					mainServer.serverTable.sendToAll(sqm.message);
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * constructor
	 */
	public MainServer() {		
		// Generate (unfortunately) a lot of data but only save the meta data
		generateMetaData();
		
		
		// create the server connection and get the incoming-message queue object
		serverTable = new ServerConnectionTable(9999); // the server's collection of DataPorts
		serverQueue = serverTable.getIncMessageQueue(); // the server's master incoming message queue (from all sources).
		// (ServerQueuedMessage is just a tuple of the source DataPort
		//  and message String so we can know the source)

		// start the connection server
		serverTableThread = new Thread(serverTable);
		serverTableThread.start();
		
		// server is now ready and listening for connections!!!
	}
	

	/**
	 *  creates a server message that broadcasts a new user connection
	 *  
	 * @param playerId the connecting player
	 * @return the new server message
	 */
	public String generatePlayerInitMessage(int playerId) {				
		return "SI#" + DataPort.INIT_MSG + "#" + playerId 
				+ "@" + players.get(playerId).avatarName 
				+ "@" + players.get(playerId).x 
				+ "@" + players.get(playerId).y;
	}
	/**
	 * generateMetaData - initialize server meta data
	 */
	private void generateMetaData() {
		// Create a resource loader so we can get textures
		ResourceLoader resLoad = new ResourceLoader();		
		// Create the level's map editor
		MapEditor me = new MapEditor(resLoad, SquintGUI.MAP_LEVEL, SquintGUI.CANVAS_WIDTH, SquintGUI.CANVAS_HEIGHT, SquintGUI.MAP_LAYERS, SquintGUI.MAP_DIM);
		// Edit the level using the map editor
		me.makeRoom(6,3,14,16,"wood_floor","walls", "shadows");
		// Save the level's map editor as a map
		Map level = (Map)me;
		// Allow for easy access to the map squares
		mapSquares = level.map.squares;
		// Create an avatar group for the players
		AvatarGroup avatars = new AvatarGroup(resLoad, "re");
		avatarNames = new String[avatars.avatars.keySet().size()];
		int idx = 0;
		for (String avatarName : avatars.avatars.keySet()) {
			avatarNames[idx++] = avatarName;
		}
		players = new HashMap<Integer, Player>();
	}

	/**
	 * registerNewUser Register a new user with the server and assign a user id
	 * to this new user
	 * 
	 * @param addr
	 *            The ip address of the new client
	 * @return the id of client assigned by the server
	 */
	public String registerUser() {
		// Increment the user id for the next user and return the id we used for
		// this user
		Point putThemHere = findLocationForPlayer();
		Player newPlayer = new Player(avatarNames[(int)(Math.random() * avatarNames.length)], Player.MoveDirection.DOWN, true, nextId++, putThemHere.x, putThemHere.y);
		if (newPlayer.avatarName == null) {
			// failed to create user?
			return null;
		}
		addUser(newPlayer);
		changeMapOccupation(newPlayer.x, newPlayer.y, newPlayer.id, true);
				
		return generatePlayerInitMessage(newPlayer.id);
	}
	
	/**
	 * Helper method for finding a random position to place the user
	 * 
	 * @return the new location
	 */
	private Point findLocationForPlayer() {
		Point spawnHere = new Point();
		// Pick a pseudorandom location to place the player based on the given map
		Integer[] numRows = new Integer[mapSquares.length];
		for (int i = 0; i < numRows.length; i++) {
			numRows[i] = i;
		}
		Collections.shuffle(Arrays.asList(numRows));	// Get a random ordering of valid rows
		boolean foundSpot = false;
		// Go through each row until we find a row with an open spot for a player
		findSpotLoop:
		for (int row : numRows) {
			Integer[] numCols = new Integer[mapSquares[row].length];
			for (int i = 0; i < numCols.length; i++) {
				numCols[i] = i;
			}
			Collections.shuffle(Arrays.asList(numCols));	// Get a random ordering of valid rows
			for (int col : numCols) {
				// Make sure the square isn't solid
				if (mapSquares[row][col].sqType != MapSquare.SquareType.SOLID && mapSquares[row][col].isOccupied == false) {
					foundSpot = true;
					spawnHere.x = col;
					spawnHere.y = row;
					break findSpotLoop;
				}
			}
		}
		if (!foundSpot) {
			System.out.println("No room for player number: " + (nextId - 1));
		}
		return spawnHere;
	}

	/**
	 * removeUser Remove a disconnected client from our server
	 * 
	 * @param id
	 *            the id of the removed client
	 * @retun true if the removal was successful, false otherwise
	 */
	public boolean removeUser(int id) {
		if (!players.containsKey(id)) {
			return false;
		}
		Player removeThisGuy = players.get(id);
		changeMapOccupation(removeThisGuy.x, removeThisGuy.y, -1, false);
		players.remove(id);
		
		// TODO: At this point we would broadcast removing the player?
		
		return true;
	}
	
	/**
	 * addUser - adds a user to the player list
	 * 
	 * @param player the player to add
	 */
	public void addUser(Player player) {
		players.put(player.id, player);
	}
	
	/**
	 * Update a map square to indicate whether it contains a player and if so
	 * what is the player's ID
	 * 
	 * @param playerX
	 * @param playerY
	 * @param playerID
	 * @param occupied
	 */
	public void changeMapOccupation(int playerX, int playerY, int playerID, Boolean occupied) {
		mapSquares[playerY][playerX].isOccupied = occupied;
		mapSquares[playerY][playerX].playerId = occupied ? playerID : -1;
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
	 * THE CALLER MUST BROADCAST A VALID MOVE TO CLIENTS TODO
	 *
	 * @param playerAction
	 *            on which we shall act on
	 * @return true if the action is worthy, false if the action is found
	 *         wanting
	 */
	public boolean requestAction(PlayerAction playerAction) {

		if (playerAction == null) {
			return false;
		}

		int playerId = playerAction.playerId;
		if (!players.containsKey(playerId)) {
			return false;
		}
		
		int moveDirection = PlayerAction.getActionNum(playerAction.action);		

		// TODO BROADCAST THE MOVE (DIFF) TO ALL CONNECTED CLIENTS
		return isValidMove(moveDirection, playerId);
	}

	/**
	 * Determines if a move is valid based on the player's location and
	 * the direction that the player is trying to move
	 * 
	 * Updates the map if the move was valid
	 * 
	 * @param moveDirection	The direction the player is trying to move
	 * @param playerId		The player that is trying to move
	 * @return	Whether or not the proposed move is valid
	 */
	public boolean isValidMove(int moveDirection, int playerId) {
		// Check if the player is simply rotating in place
		if (moveDirection != players.get(playerId).direction) {
			// Player is rotating in place, they can do that all they want
			players.get(playerId).direction = moveDirection;
			return true;
		}
		// Get the coordinates of the destination square based on the move direction
		Point destSquarePoint = getNewPlayerPosition(players.get(playerId), moveDirection);
		// Get the destination square based on the move direction
		MapSquare destSquare = mapSquares[destSquarePoint.y][destSquarePoint.x];
		// Check if the destination square is occupied or SOLID
		if (!destSquare.isOccupied && !destSquare.sqType.equals(MapSquare.SquareType.SOLID)) {
			// Update the map to indicate the new location
			updateMap(destSquare, playerId);	
			// Update the player to be in a new position
			updatePlayerPosition(destSquarePoint, playerId);
			// Square is available to be moved into, let the player know they can move
			return true;
		}
		return false;
	}
	
	/**
	 * updatePlayerPosition - updates a players position to a given point
	 * 
	 * @param newPoint the new position
	 * @param playerId the player to move to this new position
	 */
	private void updatePlayerPosition(Point newPoint, int playerId) {
		Player player = players.get(playerId);
		player.x = newPoint.x;
		player.y = newPoint.y;		
	}
	
	/**
	 * Update the map to indicate that a player has moved
	 * 	Reset their previous square
	 * 	Set their new square
	 * 
	 * @param destSquare	The square that the player moved to
	 * @param playerId		The ID of the player that moved
	 */
	private void updateMap(MapSquare destSquare, int playerId) {
		// First get the old map square and clear it
		Player player = players.get(playerId);
		MapSquare oldSquare = mapSquares[player.y][player.x];
		// Reset the old square
		oldSquare.isOccupied = false;
		oldSquare.playerId = -1;
		// Set the new square
		destSquare.playerId = playerId;
		destSquare.isOccupied = true;		
	}
	
	/**
	 * Figure out where the player would end up if they moved in
	 * a direction
	 * 
	 * @param player
	 * @param direction
	 * @return
	 */
	private Point getNewPlayerPosition(Player player, int direction){
		Point newPoint = new Point(player.x, player.y);
		Action action = PlayerAction.getActionFromInt(direction);
		switch(action) {
			case MOVE_RIGHT: newPoint.x++;	break;
			case MOVE_UP:	newPoint.y--;	break;
			case MOVE_LEFT:	newPoint.x--;	break;
			case MOVE_DOWN:	newPoint.y++;	break;
			case INTERACT:	break;
		}
		return newPoint;
	}
}

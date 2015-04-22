package gui_client;
import java.awt.Point;
import java.util.HashMap;


public class PretendServer {
	
	// The server has a copy of the map
	private MapSquare[][] map = null;
	// The server knows about all the connected players
	private HashMap<Integer, Player> players = null;
	
	public PretendServer(MapSquare[][] map, Player[] players) {
		this.map = map;
		// init the mapping of player id's to players
		this.players = new HashMap<Integer, Player>();
		for (Player player : players) {	
			this.players.put(player.id, player);
		}
	}
	
	/**
	 * Adds a new player to the hashmap of connected players
	 * 
	 * @param player	The new player that connected
	 */
	public void newPlayer(Player player) {
		players.put(player.id, player);
	}
	
	/**
	 * Removes the player that disconnected
	 * 
	 * @param playerId	The ID of the player that disconnected
	 */
	public void removePlayer(int playerId) {
		players.remove(playerId);
	}
	
	/**
	 * A client pressed a movement key and wants to know if it can move
	 * 
	 * @param moveDirection	The direction the player is trying to move
	 * @param playerId		The ID of the player that wants to move
	 * @return	Whether the player moved or not
	 */
	public boolean lookIPressedSomethingCanIMove(int moveDirection, int playerId) {
		return isValidMove(moveDirection, playerId);
	}

	/**
	 * Determines if a move is valid based on the player's location and
	 * the direction that the player is trying to move
	 * 
	 * @param moveDirection	The direction the player is trying to move
	 * @param playerId		The player that is trying to move
	 * @return	Whether or not the proposed move is valid
	 */
	public boolean isValidMove(int moveDirection, int playerId) {
		// Check if the player is simply rotating in place
		if (moveDirection != players.get(playerId).direction) {
			// Player is rotating in place, they can do that all they want
			return true;
		}
		// Get the coordinates of the destination square based on the move direction
		Point destSquarePoint = getNewPlayerPosition(players.get(playerId), moveDirection);
		// Get the destination square based on the move direction
		MapSquare destSquare = map[destSquarePoint.y][destSquarePoint.x];
		// Check if the destination square is occupied or SOLID
		if (!destSquare.isOccupied && !destSquare.sqType.equals(MapSquare.SquareType.SOLID)) {
			// Update the map to indicate the new location
			updateMap(destSquare, playerId);
			// Square is available to be moved into, let the player know they can move
			return true;
		}
		return false;
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
		MapSquare oldSquare = map[player.y][player.x];
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
		switch(direction) {
			case Player.Move.RIGHT: newPoint.x++;	break;
			case Player.Move.UP:	newPoint.y--;	break;
			case Player.Move.LEFT:	newPoint.x--;	break;
			case Player.Move.DOWN:	newPoint.y++;	break;
		}
		return newPoint;
	}
}

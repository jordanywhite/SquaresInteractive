package guiMap;

/**
 * 
 * 
 * @author Caleb Piekstra
 *
 */

public class MapSquare {
	
	
	public static enum SquareType {
		SOLID, 	// The square cannot be occupied by a player as it is a solid object
		EMPTY,	// The square can be occupied by a player, it is not solid
		UNDEF,	// The square does not have a type yet
	};
	
	public SquareType sqType;	// What type of square it is - determines whether it can be occupied by a player
	public boolean isOccupied;	// Whether the square is occupied or not ( if it is occupied, no player can move into it )	
	public boolean isAnimated;	// Whether or not the square has some form of texture animation	
	public int playerId;		// The index of the player in this square
	public int row;				// What row of the map the square is on
	public int col;				// What col of the map the square is on
	
	/**
	 * The constructor
	 * 
	 * This simply stores information about a logical map square
	 * This is used by the server to determine where players are located and 
	 * where they can move.
	 *  
	 * @param squareType	The type of square
	 * @param occupied		Whether the suqare is occupied by a player
	 * @param playerId		The player's ID or -1 if not occupied
	 * @param row			The logical row of the square
	 * @param col			The logical column of the square
	 */
	public MapSquare(SquareType squareType, boolean occupied, int playerId, int row, int col) {
		sqType = squareType;
		isOccupied = occupied;
		this.playerId = playerId;
		isAnimated = false;
		this.row = row;
		this.col = col;
	}
}

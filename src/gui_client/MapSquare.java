package gui_client;

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
	public int row;			// What row of the map the square is on
	public int col;			// What col of the map the square is on
	
	public MapSquare(SquareType squareType, boolean occupied, int playerId, int row, int col) {
		sqType = squareType;
		isOccupied = occupied;
		this.playerId = playerId;
		isAnimated = false;
		this.row = row;
		this.col = col;
	}
}

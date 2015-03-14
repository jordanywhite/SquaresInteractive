package squInt;

/**
 * A room that can have players in it.
 * 
 * Contains a grid of RoomTiles that represents what's inside the room.
 *
 */
public class Room {
	public static final int WIDTH = 5;
	public static final int HEIGHT = 5;
	
	private RoomTile[][] tiles;
	
	public Room() {
		tiles = new RoomTile[WIDTH][HEIGHT];
		for(int j = 0; j < HEIGHT; j++) {
			for(int i = 0; i < WIDTH; i++) {
				tiles[i][j] = new RoomTile();
			}
		}
	}
	
	public RoomTile getTileAt(int xPos, int yPos) {
		return tiles[xPos][yPos];
	}
}

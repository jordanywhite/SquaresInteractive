package squInt;

import gui_client.SquintGUI;

/**
 * A room that can have players in it.
 * 
 * Contains a grid of RoomTiles that represents what's inside the room.
 *
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 *
 */
public class Room {
	
	// Room dimensions
	public static final int WIDTH = SquintGUI.CANVAS_WIDTH / SquintGUI.MAP_DIM;
	public static final int HEIGHT = SquintGUI.CANVAS_HEIGHT / SquintGUI.MAP_DIM;

	private RoomTile[][] tiles; // room tiles represented as 2D array
	
	/**
	 * constructor
	 */
	public Room() {
		tiles = new RoomTile[WIDTH][HEIGHT];
		for(int j = 0; j < HEIGHT; j++) {
			for(int i = 0; i < WIDTH; i++) {
				tiles[i][j] = new RoomTile(i, j);
			}
		}
	}
	
	/**
	 * returns RoomTile at given x, y pos
	 * 
	 * @param xPos x-coordinate
	 * @param yPos y-coordinate
	 * @return RoomTile at given coordinates
	 */
	public RoomTile getTileAt(int xPos, int yPos) {
		if(xPos < 0 || xPos >= WIDTH || yPos < 0 || yPos >= HEIGHT) {
			return null;
		}
		return tiles[xPos][yPos];
	}

	// not valid
	public String generateMessage(Room room) {
		String data = "SI#" + DataPort.ROOM_MSG + "#";
		for (int j = 0; j < Room.WIDTH; j++) {
			for (int i = 0; i < Room.HEIGHT; i++) {
				data += "@" + room.getTileAt(i, j).getOccupantID();
			}
		}
		return data;
	}

	// not valid
	public static Room parseFromMessage(String str) {
		// parse the room out of the string ([occupantID]@[occupantID]@[occupantID]...)
		String[] splitStr = str.split("@");
		if(splitStr.length != Room.WIDTH*Room.HEIGHT + 1) {
			System.out.println("INVALID ROOM STR: " + str);
			return null;
		}

		Room newRoom = new Room();
		for(int j=0; j<Room.HEIGHT; j++) {
			for(int i=0; i<Room.WIDTH; i++) {
				newRoom.getTileAt(i, j).setOccupantID(Integer.parseInt(splitStr[j*Room.WIDTH + i + 1]));
			}
		}
		
		return newRoom;
	}
}

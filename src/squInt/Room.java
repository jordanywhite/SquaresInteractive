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

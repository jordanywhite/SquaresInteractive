package squInt;

import java.io.*;
import java.net.*;

/**
 * The client's DataPort representing the connection from the client to the server.
 *
 */
public class DataPortClient extends DataPort {
	

	public DataPortClient(String hostname, int port) throws IOException {
		super(hostname, port);
	}

	@Override
	public void processMessage(String str) {
//		if(!isValidMessage(str)) {
//			// in the future, we will return if this happens
//		}
		
		// for now, just print the message:
		System.out.println("CLIENT RECEIVED: " + str);
		// for now, reply with canned message:
		send("Trails indeed!");
		System.out.println("CLIENT REPLYING: \"Trails indeed!\"");
	}

	public static Room parseRoomString(String str) {
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

	public void sendAction(ClientAction clientAction) {
		String data = "SI#" + clientAction.getUserID() + "#" + clientAction.getAction();
		send(data);
	}
	
}
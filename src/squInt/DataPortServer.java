package squInt;

import java.io.*;
import java.net.*;

/**
 * A server's DataPort representing a connection from the server to a client.
 *
 */
public class DataPortServer extends DataPort {

	public DataPortServer(String hostname, int port) throws IOException {
		super(hostname, port);
	}
	
	public DataPortServer(Socket s) throws IOException {
		super(s);
	}

	@Override
	public void processMessage(String str) {
//		if(!isValidMessage(str)) {
//			// in the future, we will return if this happens
//		}
		
		// for now, just print the message:
		System.out.println("SERVER RECEIVED: " + str);
	}

	public ClientAction parseAction(String action) {
		// parse the action out of the string (SI#[userID]#[Action])
		String[] split = action.split("#");
		
		return new ClientAction(0,Action.INTERACT);
	}

	public void sendRoom(Room room) {
		String data = "SI";
		for (int j = 0; j < Room.WIDTH; j++) {
			for (int i = 0; i < Room.HEIGHT; i++) {
				data += "#" + room.getTileAt(i, j).getOccupantID();
			}
		}
		send(data);
	}
}
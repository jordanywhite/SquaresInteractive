package actions;

/**
 * ServerMessage
 * 
 * Attributes of a server message that should be used for most if not
 * all messages sent from the server to a client
 * 
 * @author Caleb Piekstra 
 *
 */
public class ServerMessage {
	
	// message types
	public static final int INIT_MSG = 0;
	public static final int ACTION_MSG = 1;
	public static final int ROOM_MSG = 2;
	public static final int UPDATE_MSG = 3;
	public static final int INVALID_MSG = 999;
	
	public int playerId; 

	/**
	 * Checks a string for valid message format.
	 * @param msg
	 * @return
	 */
	public static boolean isValidMessage(String msg) {
		// message format: SI#[MessageType]#[payload...]
		if(!msg.startsWith("SI#")) {
			System.out.println("IGNORED: " + msg);
			return false;
		}

		String[] splitMsg = msg.split("#");
		if(splitMsg.length != 3) {
			System.out.println("INVALID MSG FORMAT: " + msg);
			return false;
		}

		return true;
	}
	
	public static int getMessageType(String message) { 
		if (isValidMessage(message)) {
			String[] splitMsg = message.split("#");
			int msgType = Integer.parseInt(splitMsg[1]);
			return msgType;
		}
		return INVALID_MSG;
	}
}

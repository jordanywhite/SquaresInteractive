package squInt;

/**
 * pairs an action with a playerId. This will get converted into a
 * string (helper methods are here to encode/decode) and sent over the
 * network
 *
 */
public class PlayerAction {
	public int playerId;
	public Action action;
	
//	public int direction;
	public PlayerAction(int playerId, Action action) {
		this.playerId = playerId;
		this.action = action;
	}
	
	public static String generateActionMessage(int playerId, int actionInt) {
//		int actionNum = getActionNum(action);
//		
//		if(actionNum == -1) {
//			return null;
//		}
//		
		return "SI#" + DataPort.ACTION_MSG + "#" + playerId + "@" + actionInt;
	}
	
	public static PlayerAction parseFromMsg(String msg) {
		if(!isValidMessage(msg)) {
			return null;
		}
		
		PlayerAction parsedPacket = null;
		
		try {
			String[] splitMsg = msg.split("#");
			int msgType = Integer.parseInt(splitMsg[1]);
			String[] payload = splitMsg[2].split("@");
			
			if(msgType == DataPort.ACTION_MSG) {
				int playerId = Integer.parseInt(payload[0]);
				Action action = getActionFromInt(Integer.parseInt(payload[1]));
				
				if(action != null) {
					parsedPacket = new PlayerAction(playerId, action);
				}
			}
			
		} catch (NumberFormatException e) {
			System.out.println("ERROR PARSING MESSAGE: " + msg);
		}
		
		return parsedPacket;
	}

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
	
	public static int getActionNum(Action a) {
		switch(a) {
		case MOVE_DOWN:
			return 0;
		case MOVE_LEFT:
			return 1;
		case MOVE_UP:
			return 2;
		case MOVE_RIGHT:
			return 3;
		case INTERACT:
			return 4;
		default:
			return -1;
		}
	}
	
	public static Action getActionFromInt(int i) {
		switch(i) {
		case 0:
			return Action.MOVE_DOWN;
		case 1:
			return Action.MOVE_LEFT;
		case 2:
			return Action.MOVE_UP;
		case 3:
			return Action.MOVE_RIGHT;
		case 4:
			return Action.INTERACT;
		default:
			return null;
		}
	}
}

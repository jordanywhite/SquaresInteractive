package actions;

/**
 * 
 * PlayerInit
 * 
 * Used to instruct a client to create a player.
 * 
 * @author Caleb Piekstra 
 *
 */
public class PlayerInit extends PlayerUpdate {
	public String avatarName;

	public PlayerInit(int playerId, String avatarName, int x, int y, int direction) {
		super(playerId, x, y, direction);
		this.avatarName = avatarName;
	}
	
	/**
	 * generateUpdateMessage - creates server message
	 *  
	 * @param playerId
	 * @param actionInt
	 * @return message
	 */
	public static String generateInitMessage(int playerId, String avatarName, int x, int y, int direction) {
		return "SI#" + ServerMessage.INIT_MSG + "#" + playerId + "@" + avatarName + "@" + x + "@" + y + "@" + direction;
	}
	
	/**
	 * converts string message to valid player update.
	 * 
	 * @param msg message to parse
	 * @return parsed packet
	 */
	public static PlayerInit parseFromMsg(String msg) {
		if(!isValidMessage(msg)) {
			return null;
		}
		
		PlayerInit parsedPacket = null;
		
		try {
			String[] splitMsg = msg.split("#");
			int msgType = Integer.parseInt(splitMsg[1]);
			String[] payload = splitMsg[2].split("@");
			
			if(msgType == ServerMessage.INIT_MSG) {
				int playerId = Integer.parseInt(payload[0]);
				String avatarName = payload[1];
				int x = Integer.parseInt(payload[2]);
				int y = Integer.parseInt(payload[3]);
				int direction = Integer.parseInt(payload[4]);
				
				parsedPacket = new PlayerInit(playerId, avatarName, x, y, direction);
			}
			
		} catch (NumberFormatException e) {
			System.out.println("ERROR PARSING MESSAGE: " + msg);
		}
		
		return parsedPacket;
	}

}

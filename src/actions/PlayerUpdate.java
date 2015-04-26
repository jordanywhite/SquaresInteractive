package actions;


/**
 * PlayerUpdate
 * 
 * NOT YET IMPLEMENTED
 * 
 * This should be used by the server in intervals of say, 10 seconds.
 * The server will broadcast these updates for all players to make sure
 * that each client is synchronized with the server
 * 
 * pairs an action with a playerId. This gets converted into a
 * string (helper methods are here to encode/decode) and sent over the
 * network
 * 
 * @author Caleb Piekstra
 *
 */
public class PlayerUpdate extends ServerMessage{
	public int x; 
	public int y; 
	public int direction; 
	
	/** 
	 * constructor
	 * 
	 * @param playerId 	player to update
	 * @param x			player's logical column location
	 * @param y			player's logical row location
	 * @param direction	direction the player should be facing
	 */
	public PlayerUpdate(int playerId, int x, int y, int direction) {
		this.playerId = playerId;
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	
	/**
	 * generateUpdateMessage - creates server message
	 *  
	 * @param playerId
	 * @param actionInt
	 * @return message
	 */
	public static String generateUpdateMessage(int playerId, int x, int y, int direction) {
		return "SI#" + ServerMessage.UPDATE_MSG + "#" + playerId + "@" + x + "@" + y + "@" + direction;
	}
	
	/**
	 * converts string message to valid player update.
	 * 
	 * @param msg message to parse
	 * @return parsed packet
	 */
	public static PlayerUpdate parseFromMsg(String msg) {
		if(!isValidMessage(msg)) {
			return null;
		}
		
		PlayerUpdate parsedPacket = null;
		
		try {
			String[] splitMsg = msg.split("#");
			int msgType = Integer.parseInt(splitMsg[1]);
			String[] payload = splitMsg[2].split("@");
			
			if(msgType == ServerMessage.UPDATE_MSG) {
				int playerId = Integer.parseInt(payload[0]);
				int x = Integer.parseInt(payload[1]);
				int y = Integer.parseInt(payload[2]);
				int direction = Integer.parseInt(payload[3]);
				
				parsedPacket = new PlayerUpdate(playerId, x, y, direction);
			}
			
		} catch (NumberFormatException e) {
			System.out.println("ERROR PARSING MESSAGE: " + msg);
		}
		
		return parsedPacket;
	}
}

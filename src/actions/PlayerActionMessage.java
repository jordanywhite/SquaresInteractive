package actions;

import actions.Action.PlayerAction;


/**
 * PlayerAction
 * 
 * pairs an action with a playerId. This gets converted into a
 * string (helper methods are here to encode/decode) and sent over the
 * network
 * 
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 *
 */
public class PlayerActionMessage extends ServerMessage {
	public PlayerAction action;
	public String message;
	
	/** 
	 * constructor
	 * 
	 * @param playerId player producing action
	 * @param action direction player is moving
	 */
	public PlayerActionMessage(int playerId, PlayerAction action, String message) {
		this.playerId = playerId;
		this.action = action;
		this.message = message;
	}
	
	/**
	 * generateMoveMessage - creates server message
	 *  
	 * @param playerId	The ID of the player requesting the action
	 * @param actionInt	The player action's int value
	 * @return message	The move message
	 */
	public static String generateActionMessage(int playerId, int actionInt, String msg) {
		return "SI#" + ServerMessage.ACTION_MSG + "#" + playerId + "@" + actionInt + "@" + msg;
	}
	
	/**
	 * generateMoveMessage - creates server message
	 *  
	 * @param playerId	The ID of the player requesting the action
	 * @param action	The player action
	 * @return message	The move message
	 */
	public static String generateActionMessage(int playerId, PlayerAction action, String msg) {
		return generateActionMessage(playerId, Action.getActionNum(action), msg);
	}
	
	/**
	 * converts string message to valid player action.
	 * 
	 * @param msg message to parse
	 * @return parsed packet
	 */
	public static PlayerActionMessage parseFromMessage(String msg) {
		if(!isValidMessage(msg)) {
			return null;
		}
		
		PlayerActionMessage parsedPacket = null;
		
		try {
			String[] splitMsg = msg.split("#");
			int msgType = Integer.parseInt(splitMsg[1]);
			String[] payload = splitMsg[2].split("@");
			
			if(msgType == ServerMessage.ACTION_MSG) {
				int playerId = Integer.parseInt(payload[0]);
				PlayerAction action = Action.getActionFromInt(Integer.parseInt(payload[1]));
				String message = payload[2];
				
				if(action != null) {
					parsedPacket = new PlayerActionMessage(playerId, action, message);
				}
			}
			
		} catch (NumberFormatException e) {
			System.out.println("ERROR PARSING MESSAGE: " + msg);
		}
		
		return parsedPacket;
	}
}

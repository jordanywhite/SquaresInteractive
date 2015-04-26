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
public class PlayerMove extends ServerMessage {
	public PlayerAction action;
	
	/** 
	 * constructor
	 * 
	 * @param playerId player producing action
	 * @param action direction player is moving
	 */
	public PlayerMove(int playerId, PlayerAction action) {
		this.playerId = playerId;
		this.action = action;
	}
	
	/**
	 * generateActionMessage - creates server message
	 *  
	 * @param playerId	The id of the player requesting the action
	 * @param actionInt	The player action's int value
	 * @return message	The move message
	 */
	public static String generateMoveMessage(int playerId, int actionInt) {
		return "SI#" + ServerMessage.MOVE_MSG + "#" + playerId + "@" + actionInt;
	}
	
	/**
	 * generateActionMessage - creates server message
	 *  
	 * @param playerId	The id of the player requesting the action
	 * @param action	The player action
	 * @return message	The move message
	 */
	public static String generateMoveMessage(int playerId, PlayerAction action) {
		return "SI#" + ServerMessage.MOVE_MSG + "#" + playerId + "@" + Action.getActionNum(action);
	}
	
	/**
	 * converts string message to valid player action.
	 * 
	 * @param msg message to parse
	 * @return parsed packet
	 */
	public static PlayerMove parseFromMsg(String msg) {
		if(!isValidMessage(msg)) {
			return null;
		}
		
		PlayerMove parsedPacket = null;
		
		try {
			String[] splitMsg = msg.split("#");
			int msgType = Integer.parseInt(splitMsg[1]);
			String[] payload = splitMsg[2].split("@");
			
			if(msgType == ServerMessage.MOVE_MSG) {
				int playerId = Integer.parseInt(payload[0]);
				PlayerAction action = Action.getActionFromInt(Integer.parseInt(payload[1]));
				
				if(action != null) {
					parsedPacket = new PlayerMove(playerId, action);
				}
			}
			
		} catch (NumberFormatException e) {
			System.out.println("ERROR PARSING MESSAGE: " + msg);
		}
		
		return parsedPacket;
	}
}

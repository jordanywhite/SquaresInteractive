package player;
import java.awt.Point;

import actions.Action;
import actions.Action.PlayerAction;
import resourceManagement.ResourceLoader;

/**
 * 
 * The back end user to be used in the interactive room
 * 
 * @author Caleb Piekstra
 *
 */

public class Player {
	
	public static class MoveDirection { 
		public static final int DOWN = Action.getActionNum(PlayerAction.MOVE_DOWN);
		public static final int LEFT = Action.getActionNum(PlayerAction.MOVE_LEFT);
		public static final int UP = Action.getActionNum(PlayerAction.MOVE_UP);
		public static final int RIGHT = Action.getActionNum(PlayerAction.MOVE_RIGHT);
	}	
	
	public static final int MOVE_DELAY = 250; // in milliseconds
	public static final int MOVE_PHASES = 5;
	public static final int JUMP_PHASES = 10;
	
	public static final int WALKING = 2;
	public static final int RUNNING = 1;
	
	public static final String DEFAULT_AVATAR = "green";

	// Player attributes
	public int x; 						// the x location of the player using tile coordinates
	public int y;						// the y location of the player using tile coordinates
	public int direction;				// direction the player want to move
	public boolean allowedToMove;		// is player allowed to move?
	public int animatePhase;			// which animation phase player is in
	public int id;						// player id
	public int speed;					// player movement speed
	public boolean inAnimationPhase;	// is player being animated?
	public boolean isJumping;			// is player jumping?
	
	// Holds the textures for the player 
	public String avatarName = null;
		
	// Resources
	public ResourceLoader resources = null;
	
	/**
	 * constructor
	 * 
	 * initialize everything!
	 */
	public Player(String avatarName, int direction, boolean canIMove, int playerIdx, int x, int y) {
		// If the direction was incorrect, set it to be a default DOWN direction
		if (direction > MoveDirection.RIGHT || direction < MoveDirection.DOWN) {
			this.direction = MoveDirection.DOWN;
		} else {
			this.direction = direction;			
		}
		animatePhase = 0;
		speed = WALKING;
		id = playerIdx;		
		allowedToMove = canIMove;
		inAnimationPhase = false;
		isJumping = false;
		this.x = x;
		this.y = y;
		// Set the avatar last, if we couldn't create a player then it will have a null avatar
		this.avatarName = avatarName;
	}	
	
	/**
	 * Figure out where the player would end up if they moved in
	 * a direction
	 * 
	 * @param player
	 * @param direction
	 * @return
	 */
	public static Point getNewPlayerPosition(Player player, int direction){
		Point newPoint = new Point(player.x, player.y);
		PlayerAction action = Action.getActionFromInt(direction);
		switch(action) {
			case MOVE_RIGHT: newPoint.x++;	break;
			case MOVE_UP:	newPoint.y--;	break;
			case MOVE_LEFT:	newPoint.x--;	break;
			case MOVE_DOWN:	newPoint.y++;	break;
			case INTERACT:	break;
			case INVALID:	break;
		}
		return newPoint;
	}
}

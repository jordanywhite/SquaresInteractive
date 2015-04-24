package gui_client;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * 
 * @author Caleb Piekstra
 *
 */

public class Player {
	
	public static class Move { 
		public static final int DOWN = 0;
		public static final int LEFT = 1;
		public static final int UP = 2;
		public static final int RIGHT = 3;
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
	public int direction;
	public boolean allowedToMove;
	public int animatePhase;
	private Timer moveTimer = null;
	public int id;
	public int speed;
	public boolean inAnimationPhase;
	public boolean isJumping;
	
	// Holds the textures for the player 
	public String avatarName = null;
		
	// Resources
	public ResourceLoader resources = null;
	
	public Player(String avatarName, int direction, boolean canIMove, int playerIdx, int x, int y) {
		// If the direction was incorrect, set it to be a default DOWN direction
		if (direction > Move.RIGHT || direction < Move.DOWN) {
			this.direction = Move.DOWN;
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
	
	public void startMoveTimer() {		
		allowedToMove = false;
		moveTimer = new Timer();
		moveTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				allowedToMove = true;
				moveTimer.cancel();
			}		
		}, MOVE_DELAY);
	}
	
}

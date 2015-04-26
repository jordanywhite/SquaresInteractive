package actions;

/**
 * Enumeration of actions that can be taken by players in the room
 * 
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 *
 */

public class Action {

	public enum PlayerAction {
		MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, INTERACT, INVALID
	}

	// getter methods //

	public static int getActionNum(PlayerAction a) {
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
		case INVALID:
			return 999;
		default:
			return 999;
		}
	}

	public static PlayerAction getActionFromInt(int i) {
		switch(i) {
		case 0:
			return PlayerAction.MOVE_DOWN;
		case 1:
			return PlayerAction.MOVE_LEFT;
		case 2:
			return PlayerAction.MOVE_UP;
		case 3:
			return PlayerAction.MOVE_RIGHT;
		case 4:
			return PlayerAction.INTERACT;
		case 999:
			return PlayerAction.INVALID;
		default:
			return null;
		}
	}
}

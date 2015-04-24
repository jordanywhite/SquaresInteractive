package playerMovement;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import player.Player;

/**
 * 
 * Animates a player's movement across the room
 * 
 * @author Caleb Piekstra
 *
 */

public class MovePlayer extends AnimatePlayer {
	
	public static void movePlayer(int direction, Player player, Callable<?> updateDisplayCallable) {
		if (player == null || !player.allowedToMove) {
			return;		// Make sure we have a player to move and that they are allowed to move
		}		
		boolean animate = false;
		// If the player is not currently facing this direction,
		// turn them to face that direction
		if (player.direction != direction) {
			player.direction = direction;
		} else {
			// Otherwise, trigger the movement animation
			animate = true;	
		}
		if (animate) {
			player.allowedToMove = false;	// The player is not allowed to move while they are animating
			player.animatePhase = 1;		// Start the animation
			ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
			// Determine a delay based on the animation
			int delay = player.isJumping ? ANIMATION_DELAY_JUMP : ANIMATION_DELAY_STEP;
			// Used to keep track of the animation timer
			ScheduledFuture<?> taskHandler = scheduledExecutor.scheduleAtFixedRate(
					new Animators.MoveAnimation(player, updateDisplayCallable),
					0, delay, TimeUnit.MILLISECONDS);
			// Add our handler to the list of handlers to be handled
		    animationHandlers.put(player.id, taskHandler);
		} else {
			// If not animating, the player is allowed to move
			player.allowedToMove = true;
		}
	}
}

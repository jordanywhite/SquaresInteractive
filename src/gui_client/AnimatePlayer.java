package gui_client;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;

/**
 * 
 * 
 * @author Caleb Piekstra
 *
 */

public class AnimatePlayer {
	
	
	/**___________________________________________________________________________________________**\
   /  / 
  /  |  
 < | |	Animation
  \  |
   \  \___________________________________________________________________________________________
	\**                                                                                           **/	
	
	// in milliseconds, represents the total theoretical time of the moving animation between two squares
	public static final int ANIMATION_DELAY = 250;										
	// in milliseconds, how much time each phase of the moving animation should take
	public static final int ANIMATION_DELAY_STEP = ANIMATION_DELAY / Player.MOVE_PHASES; 
	// in milliseconds, how much time each phase of the moving animation should take
	public static final int ANIMATION_DELAY_JUMP = ANIMATION_DELAY / Player.JUMP_PHASES; 
	
	// Pair each animation executor with a playerId to make sure that each player gets their own executor
	public static Hashtable<Integer, ScheduledFuture<?>> animationHandlers = new Hashtable<Integer, ScheduledFuture<?>>();
	
	// This sequence will have to be modified so that both the source and destination tiles
	// are considered to be occupied during the animation in order to reserve the destination
	// so that no other player can try to occupy it during the animation sequence
	public static void animatePlayer(Player player, Callable<?> updateDisplayCallable) {	
		if (player == null) {
			return;
		}
		boolean endPhases = false;
		if ((player.isJumping && player.animatePhase >= Player.JUMP_PHASES)
			|| (!player.isJumping && player.animatePhase >= Player.MOVE_PHASES)) {
			endPhases = true;
		}		
		if (!player.inAnimationPhase) {
			player.inAnimationPhase = true;
			
			// Update the display
			try {
				updateDisplayCallable.call();
			} catch (Exception e) {}
		}	
		if (endPhases) {		
			// Reset to default of no animation		
			player.animatePhase = -1;			
			
			// Let the player move again now that the animation is complete
			player.allowedToMove = true; 
			
			// Kill the animation timer by canceling the handler
			animationHandlers.get(player.id).cancel(true);
			animationHandlers.remove(player.id);
			
			// Update the display			
			try {
				updateDisplayCallable.call();
			} catch (Exception e) {}
		}
	}
}

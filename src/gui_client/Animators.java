package gui_client;
import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 * 
 * 
 * @author Caleb Piekstra
 *
 */
public class Animators {

	/**
	 * Used to keep track of what player to move, 
	 * when to move, and when to trigger a GUI update
	 *
	 */
	public static class MoveAnimation extends TimerTask {
		private Player player = null;
		private int speedToggle;
		private Callable<?> callable;
		
		public MoveAnimation(Player player, Callable<?> callme) {
			this.player = player;
			speedToggle = player.speed;
			callable = callme;
		}
		public void run() {
			// If the player is walking, the animation will occur every other run()
			if (--speedToggle <= 0) { 
				speedToggle = player.speed;	// Update the speed of the player in case they started sprinting during the animation
				AnimatePlayer.animatePlayer(player, callable);
			}
		}
	}

	/**
	 * Used to loop through an animation sequence and
	 * notify the GUI of when to update to the next
	 * phase (texture) of the animation
	 *
	 */
	public static class TerrainAnimation extends TimerTask {
		private final int numPhases;		// The number of phases in the animation
		private int currentPhase;			// The current phase of the animation
		private final Callable<?> callable;	// The function to run every once in a while
		private final TextureGroup textures;// The textures in the animation
		
		public TerrainAnimation (TextureGroup textures, Callable<?> callme) {
			callable = callme;
			currentPhase = 0;
			this.textures = textures;
			this.numPhases = textures.textures.size();
		}
		public void run() {
			// Trigger the next phase of the animation
			++currentPhase;
			currentPhase %= numPhases;
			try {
				callable.call();
			} catch (Exception e) {}
		}
		public Texture getCurrentPhaseTexture() {
			return GlobalHelper.textureGroupToArrayList(textures).get(currentPhase);
		}
	}
}

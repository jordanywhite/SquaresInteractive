import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

public class Player {

	public static final int DOWN = 0;
	public static final int LEFT = 1;
	public static final int UP = 2;
	public static final int RIGHT = 3;
	
	public static final int MOVE_DELAY = 250; // in milliseconds

	// Player attributes
	public static Color PLAYER_COLOR = Color.YELLOW;   
	public int x; 						// the x location of the player using tile coordinates
	public int y;						// the y location of the player using tile coordinates
	public int direction;
	public boolean allowedToMove;
	private Timer moveTimer = null;
	
	public Player(int tile_x, int tile_y, int direction, boolean canIMove) {
		if (tile_x < 0 || tile_x > SquintMainWindow.TILES_DIM) {
			x = 0;
		} else {
			x = tile_x;
		}
		if (tile_y < 0 || tile_y > SquintMainWindow.TILES_DIM) {
			y = 0;
		} else {
			y = tile_y;
		}
		if (direction < RIGHT || direction > DOWN) {
			this.direction = DOWN;
		} else {
			this.direction = direction;			
		}
		allowedToMove = canIMove;
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

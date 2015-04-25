package squInt;
import guiMap.Map;
import guiMap.MapEditor;
import guiMap.MapSquare;
import imageProcessing.ImageEditor;
import imageProcessing.Transparency;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import actions.Action;
import actions.PlayerAction;
import player.Player;
import playerMovement.MovePlayer;
import resourceManagement.Avatar;
import resourceManagement.AvatarGroup;
import resourceManagement.ResourceLoader;
import resourceManagement.Texture;

/**
 * This code is the GUI portion of a larger project being built for a networking class
 * at the University of Portland. The project is meant to demonstrate certain aspects
 * and concepts of how networks communicate data.
 * 
 * Team members: Jordan White, Kai Jorgensen, Bryce Matsuda, Caleb Piekstra
 * 
 * @author Caleb Piekstra
 *
 */
public class SquintGUI extends JPanel implements KeyListener {

	// Cereal Identification (not frosted flakes)
	private static final long serialVersionUID = -8781116976800446850L;

	// Window title
	public static final String TITLE = "Squares Interactive - SQUINT";

	// Size of the desktop client window
	public static final int CANVAS_WIDTH = 800;		// the pixel width of the application window
	public static final int CANVAS_HEIGHT = 800;	// the pixel height of the application window

	// Constants that define the level's characteristics
	public static final int MAP_LAYERS = 4;	// The number of texture layers in the map
	public static final int MAP_LEVEL = 0;	// Currently unimplemented

	public static final int TERRAIN_ANIMATION_DELAY = 200;	

	// The player attached to the client/GUI
	public Player player;
	// The number of clients connected to the host
	public int num_players = 0;		
	// Used to keep track of keys that have not been released
	public List<Integer> heldKeys = new ArrayList<Integer>();	

	// the number of pixels per grid square
	public static final int MAP_DIM = 40;				

	// The "background" image for the level - used so we only redraw moving parts
	private BufferedImage roomBackgroundImage = null;

	// The entire level
	Map level = null;

	// These are pointers to data in the 'level' variable
	private MapSquare[][] mapSquares = null;
	private MapSquare[] animatedSquares = null;

	// Resource loader
	private ResourceLoader resLoad = null;

	// Texture Groups for the avatars
	AvatarGroup avatars = null;

	// The "players connected to the server" (not really)
	public HashMap<Integer, Player> players = null;

	// The client attached to this GUI
	MainClient mainClient;
	
	/** Constructor to setup the GUI components */
	public SquintGUI(MainClient mainClient) 
	{				
		// Create a resource loader so we can get textures
		resLoad = new ResourceLoader();		
		// Create the level's map editor
		MapEditor me = new MapEditor(resLoad, MAP_LEVEL, CANVAS_WIDTH, CANVAS_HEIGHT, MAP_LAYERS, MAP_DIM);
		// Edit the level using the map editor
		editLevel(me);
		// Save the level's map editor as a map
		level = (Map)me;
		// Allow for easy access to the map squares
		mapSquares = level.map.squares;
		// Allow for easy access to the map's animated squares
		animatedSquares = level.map.animatedSquares;
		// Create a static background image for the level
		roomBackgroundImage = makeImage(level.map.textures, level.map.coords);
		// Create an avatar group for the players
		avatars = new AvatarGroup(resLoad, "re");

		// Set up the client window
		setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));		
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);

		// Set up the hashtable of players
		players = new HashMap<Integer, Player>();
		this.mainClient = mainClient;
	}

	/**
	 * This will be called when we receive an INIT_MSG from the server telling us
	 * what our player ID is and what avatar to use
	 * 
	 * @param avatarName
	 * @return
	 */
	public void createPlayer(int playerId, String avatarName, int x, int y, int direction) {
		if (playerId > num_players) {
			num_players = playerId;
		}
		Player player = new Player(avatarName, direction, true, playerId, x, y);
		if (this.player == null) {
			this.player = player;
		}
		//		// Add the player to the list of players
		players.put(player.id, player);	
		// Update the map to indicate the player "spawning"
		changeMapOccupation(player.x, player.y, player.id, true);
		// Return a reference to the player if needed
		//		return player;
	}

	/**
	 * This sets up the room so we can draw it
	 * 
	 * @param level
	 */
	private void editLevel(MapEditor level) {
		// This is our room
		level.makeRoom(6,3,14,16,"wood_floor","walls", "shadows");
	}

	/**
	 * Set up the client window
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public void initGUI(final SquintGUI gui) {
		// Run GUI codes in the Event-Dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame(TITLE);
				frame.setContentPane(gui);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.pack();             // "this" JFrame packs its components
				frame.setLocationRelativeTo(null); // center the application window
				frame.setVisible(true);            // show it
			}
		});
	}

	/**___________________________________________________________________________________________**\
   /  / 
  /  |  
 < | |	Player Movement / Location Updates
  \  |
   \  \___________________________________________________________________________________________
	\**                                                                                           **/	

	/**
	 * Handles player movement upon keyboard input
	 * 
	 * @param direction
	 * @param player
	 */
	public void movePlayer(int direction, int playerId) {
		// The player in our GUI that needs to move
		Player player = players.get(playerId);
		// A callable method so we can repaint during animation
		PlayerAnimator aniUp = null;

		// Check if we are simply changing direction or animating
		if (player.direction == direction) {
			// See if we are moving instead of jumping
			if (!player.isJumping) {
				// Holds the destination location of the player
				Point newSquareLoc = getNewPlayerPosition(player, direction);		
				// Holds the map square at the destination point
				MapSquare destinationSquare = level.getMapSquare(newSquareLoc);
				// Make sure there is a square at the destination
				if (destinationSquare == null) {
					return;
				}
				// Get the location where the player will be located but don't actually 
				// update the player's location - the old one needs to be maintained for
				// the duration of the movement animation
				// Update the map to show that the destination square has been claimed
				changeMapOccupation(newSquareLoc.x, newSquareLoc.y, player.id, true);
			}			
			// MoveDirection the player
			aniUp = new PlayerAnimator();
			aniUp.setPlayer(player);
		}
		MovePlayer.movePlayer(direction, player, aniUp);
		repaint();
	}

	/**
	 * Update a map square to indicate whether it contains a player and if so
	 * what is the player's ID
	 * 
	 * @param playerX
	 * @param playerY
	 * @param playerID
	 * @param occupied
	 */
	public void changeMapOccupation(int playerX, int playerY, int playerID, Boolean occupied) {
		mapSquares[playerY][playerX].isOccupied = occupied;
		mapSquares[playerY][playerX].playerId = occupied ? playerID : -1;
		repaint();	// Update the GUI
	}

	/**
	 * Update the location of the player - called after completion
	 * of the movement animation
	 *  
	 * @param player
	 */
	private void updatePlayerLocation(Player player) {
		if (player.isJumping) {
			// If we were jumping, we are done now
			player.isJumping = false;
			return;
		}
		// Update the map to indicate that the player is no longer at it's old location
		changeMapOccupation(player.x, player.y, player.id, false);
		// Animation has been completed at this point, update the player's location
		Point newLocation = getNewPlayerPosition(player, player.direction);
		player.x = newLocation.x;
		player.y = newLocation.y;
	}

	/**
	 * Figure out where the player would end up if they moved in
	 * a direction
	 * 
	 * @param player
	 * @param direction
	 * @return
	 */
	private Point getNewPlayerPosition(Player player, int direction){
		Point newPoint = new Point(player.x, player.y);
		Action action = PlayerAction.getActionFromInt(direction);
		switch(action) {
		case MOVE_RIGHT: newPoint.x++;	break;
		case MOVE_UP:	newPoint.y--;	break;
		case MOVE_LEFT:	newPoint.x--;	break;
		case MOVE_DOWN:	newPoint.y++;	break;
		case INTERACT:	break;
		}
		return newPoint;
	}

	/**___________________________________________________________________________________________**\
   /  / 
  /  |  
 < | |	GUI Drawing
  \  |
   \  \___________________________________________________________________________________________
	\**                                                                                           **/	

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);  
		// paint background
		drawMap(g);
		// Update the animated textures
		drawAnimatedTerrain(g);
		// Update the avatar textures
		drawAvatars((Graphics2D) g, false);
	}

	/**
	 * This creates a static "background" image so we don't have to redraw the
	 * entire map every time a GUI update is triggered
	 * 
	 * @param textures
	 * @param coords
	 * @return
	 */
	public BufferedImage makeImage(Texture[][][] textures, Point[][] coords) {
		BufferedImage bImg = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, 
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bImg.createGraphics();

		// background drawing here, the display that doesn't change
		drawGrid(g2, textures, coords);

		g2.dispose();
		return bImg;
	}

	/**
	 * Updates the application window to display the active map static background
	 * 
	 * @param g		the graphics object
	 * @param level	the map to be drawn
	 */
	private void drawMap(Graphics g) {
		// Ensure that a static background image has been generated for the map
		if (roomBackgroundImage != null) {
			g.drawImage(roomBackgroundImage, 0, 0, this);
		}
	}

	private void drawAnimatedTerrain(Graphics g) {
		if (animatedSquares == null) return;
		for (MapSquare animatedSquare : animatedSquares) {
			drawImageToGrid(level.map.animator.getCurrentPhaseTexture().textureFile, animatedSquare.col * MAP_DIM, animatedSquare.row * MAP_DIM, g, false, false);
		}		
	}

	/**
	 * Draws the avatar(s) onto the map
	 * 
	 * @param g2d		the 2d graphics object
	 * @param modeIsAI	whether or not the program is running in ai mode or user-controlled mode
	 */
	private void drawAvatars(Graphics2D g2d, boolean modeIsAI) {
		// Draw each of the players in the list of players
		Iterator<Player> playerIterator = players.values().iterator();
		while (playerIterator.hasNext()) {
			drawPlayer(playerIterator.next(), g2d);	// Draw the player
		}
	}

	/**
	 * Draws the map like a grid
	 * 
	 * @param numTilesPerRowAndCol		the number of tiles per row and per column
	 * @param g							the graphics object
	 */
	private void drawGrid(Graphics g, Texture[][][] textures, Point[][] coords) {	    				
		for (int layer = 0, layerCap = textures.length; layer < layerCap; layer++) {
			for (int row = 0, rowCap = textures[layer].length; row < rowCap; row++) {
				for (int col = 0, colCap = textures[layer][row].length; col < colCap; col++) {
					// Allow for empty grid spots in case of larger images
					if ( textures[layer][row][col] != null && textures[layer][row][col].textureFile != null ) {
						// If the image to be drawn to the grid is for shading we want to handle it differently
						if (textures[layer][row][col].textureDir.contains("shad")) {
							drawImageToGrid(textures[layer][row][col].textureFile, coords[row][col].x, coords[row][col].y, g, true, false);							
						} else {
							drawImageToGrid(textures[layer][row][col].textureFile, coords[row][col].x, coords[row][col].y, g, false, false);
						}
					}
				}
			}	
		}
	}

	/**
	 * Draw an image onto the room
	 * 
	 * @param imageFile		The path to the image file
	 * @param x_coord		the x coordinate of the top left corner where the image should be drawn (it is drawn from there to the bottom right)
	 * @param y_coord		the y coordinate of the top left corner where the image should be drawn
	 * @param g				the graphics object that we are drawing the image onto
	 * @param isShading		whether or not the image is being used to add shading
	 * @param scaleImage	whether or not to scale the image to fit inside a tile dimension (used to make scale player avatars)
	 */
	private void drawImageToGrid(File imageFile, int x_coord, int y_coord, Graphics g, boolean isShading, boolean scaleImage) {
		Image img = null;
		BufferedImage bimg = null;	// Used if the image needs to be scaled
		try {
			img = ImageIO.read(imageFile);
			bimg = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to read image file");
		}
		if (img == null) {
			return;
		}
		if (isShading) {			
			// Go through each shade level, represented by a unique color, and
			// replace the color with a specified opacity level
			Color shadeLevelOneColor = new Color(0x000000);
			Color shadeLevelTwoColor = new Color(0x7F007F);
			Color shadeLevelThreeColor = new Color(0xff00ff);
			img = Transparency.makeColorTransparent(img, shadeLevelOneColor, 0x65);
			img = Transparency.makeColorTransparent(img, shadeLevelTwoColor, 0x40);
			img = Transparency.makeColorTransparent(img, shadeLevelThreeColor, 0x20);
			// Once all colors have had their opacity modified, convert the image to a black grayscale
			ImageFilter filter = new GrayFilter(true, 0);  
			ImageProducer producer = new FilteredImageSource(img.getSource(), filter);  
			img = Toolkit.getDefaultToolkit().createImage(producer);  
		}
		if (scaleImage) {
			Dimension scaledSize = ImageEditor.getScaledDimension(new Dimension(bimg.getWidth(), bimg.getHeight()), new Dimension(MAP_DIM, MAP_DIM), true);
			img = resizeToBig(img, scaledSize.width, scaledSize.height);
		}
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(img, x_coord, y_coord, null);
	}	

	/**
	 * Make a larger copy of the original image
	 * 
	 * @param originalImage
	 * @param biggerWidth
	 * @param biggerHeight
	 * @return
	 */
	private Image resizeToBig(Image originalImage, int biggerWidth, int biggerHeight) {
		int type = BufferedImage.TYPE_INT_ARGB;


		BufferedImage resizedImage = new BufferedImage(biggerWidth, biggerHeight, type);
		Graphics2D g = resizedImage.createGraphics();

		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(originalImage, 0, 0, biggerWidth, biggerHeight, this);
		g.dispose();


		return resizedImage;
	}

	/**
	 * Draw the player onto the map
	 * 
	 * @param player
	 * @param g
	 */
	private void drawPlayer(Player player, Graphics g) {	
		if (player == null) {
			return;
		}
		int player_x = player.x * MAP_DIM;// + LEFT_WALL_OFFSET;
		int player_y = player.y * MAP_DIM;// + TOP_WALL_OFFSET;	
		String animationSuffix = "";

		if (player.animatePhase > 0) {
			if (player.isJumping) {
				Texture t = level.textures.get("misc").getTextureExact("jump_circle.png");
				// Draw a circle "beneath" the player so it is obvious they are jumping and not floating upwards
				drawImageToGrid(t.textureFile, player_x, player_y, g, false, true);				
				// Calculate the halfway point in pixels
				int halfwayDisplacement = (MAP_DIM / Player.JUMP_PHASES) * Player.JUMP_PHASES/2;
				// Calculate the amount of pixels away from standing we are at this phase of the animation
				int displacement = (MAP_DIM / Player.JUMP_PHASES) * player.animatePhase;
				if (displacement > halfwayDisplacement) {
					displacement = halfwayDisplacement-(displacement-halfwayDisplacement);
					// we are jumping, not falling
					if (displacement < 0) {
						displacement = 0;
					}
				}
				// Jumping up
				player_y -= displacement;	
			} else {
				int animationStep = (MAP_DIM / Player.MOVE_PHASES) * player.animatePhase;
				if (player.direction == Player.MoveDirection.RIGHT) {
					player_x += animationStep;		
				} else if (player.direction == Player.MoveDirection.UP) {
					player_y -= animationStep;								
				} else if (player.direction == Player.MoveDirection.LEFT) {
					player_x -= animationStep;	
				} else if (player.direction == Player.MoveDirection.DOWN) {	
					player_y += animationStep;	
				}				
				animationSuffix = "-" + ((player.animatePhase % 2) + 1);
			}
			// We have drawn the latest phase of the animation, update the player
			player.inAnimationPhase = false;
			player.animatePhase++;
		}
		String textureName = player.direction + animationSuffix + ".png";
		// Get the avatar (texture group) based on the avatar assigned to us by the server
		Avatar avatar = avatars.getAvatar(player.avatarName);
		Texture t = avatar.getTextureWithName(textureName);
		if (t != null) {
			drawImageToGrid(t.textureFile, player_x, player_y - MAP_DIM/2, g, false, true);
		}
	}

	/**___________________________________________________________________________________________**\
   /  / 
  /  |  
 < | |	Animation Handlers
  \  |
   \  \___________________________________________________________________________________________
	\**                                                                                           **/	

	/**
	 * A callable that is used to update and redraw the level
	 * during player animation
	 *
	 */
	public class PlayerAnimator implements Callable<Void> {
		private Player player = null;

		// Call this before using .call()
		public void setPlayer(Player player) {
			this.player = player;			
		}

		@Override
		public Void call() throws Exception {
			if (player != null) {
				// If the player has finished it's animation, it is allowed to move again
				if (player.allowedToMove) {					
					// update the player's location
					updatePlayerLocation(player);
				}
			}
			// Update the display
			repaint();
			return null;
		}		
	}

	/**
	 * A callable used to redraw animated textures during animation
	 *
	 */
	public class TerrainAnimator implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			// REPAINT! and stand still no more.
			repaint();
			return null;
		}		
	}

	/**___________________________________________________________________________________________**\
   /  / 
  /  |  
 < | |	Keyboard Events
  \  |
   \  \___________________________________________________________________________________________
	\**                                                                                           **/	

	/**
	 * Key presses allow the primary GUI player (relative to this client's 
	 * window) to move around the room
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// Check if the press key is in the collection of held keys
		if (!heldKeys.contains(e.getKeyCode())) {
			// Keep track of the pressed key's held-state
			heldKeys.add(new Integer(e.getKeyCode()));		
		}
		// Change the player's speed to RUNNING if SHIFT is held
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			player.speed = Player.RUNNING;	
		}
		// Do not process player movements unless the player is allowed to move
		if (player.allowedToMove) {
			int moveDirection = -1;
			if(heldKeys.contains(KeyEvent.VK_D) || heldKeys.contains(KeyEvent.VK_RIGHT)) {
				// The 'D' key corresponds to RIGHT
				moveDirection = Player.MoveDirection.RIGHT;
			} else if(heldKeys.contains(KeyEvent.VK_W) || heldKeys.contains(KeyEvent.VK_UP)) {
				// The 'W' key corresponds to UP
				moveDirection = Player.MoveDirection.UP;
			} else if(heldKeys.contains(KeyEvent.VK_A) || heldKeys.contains(KeyEvent.VK_LEFT)) {
				// The 'A' key corresonds to LEFT
				moveDirection = Player.MoveDirection.LEFT;
			} else if(heldKeys.contains(KeyEvent.VK_S) || heldKeys.contains(KeyEvent.VK_DOWN)) {
				// The 'S' key corresponds to DOWN
				moveDirection = Player.MoveDirection.DOWN;
			} else if(heldKeys.contains(KeyEvent.VK_SPACE)) {
				// The 'SPACE' key makes the player jump
				player.isJumping = true;
				moveDirection = player.direction;
			} else if(heldKeys.contains(KeyEvent.VK_Q)) {
				// The 'Q' key makes the player spin counter-clockwise
				int modVal = Player.MoveDirection.RIGHT + 1;
				moveDirection = ((((player.direction-1) % modVal) + modVal) % modVal);
			} else if(heldKeys.contains(KeyEvent.VK_E)) {
				// The 'E' key makes the player spin clockwise
				moveDirection = (player.direction + 1) % (Player.MoveDirection.RIGHT + 1);
			}

			// Check to see if a "movement key" was pressed
			if (moveDirection != -1) { 
				// If the player is jumping, handle the movement locally
				if (player.isJumping) {
					movePlayer(moveDirection, player.id);
				} else {
					// If the player is not jumping then send a move request to the server
					String sendMe = PlayerAction.generateActionMessage(player.id, moveDirection);
					mainClient.connection.send(sendMe);	
				}	        	
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Once a key has been released, stop keeping track of it's held-state
		heldKeys.remove(new Integer(e.getKeyCode()));
		if (e.getKeyCode() == KeyEvent.VK_SHIFT ) {
			player.speed = Player.WALKING;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Not used
	}
}

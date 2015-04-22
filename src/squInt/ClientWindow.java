package squInt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 * Displays room as a Java Swing GUI
 * 
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 */
// Swing Program Template
@SuppressWarnings("serial")
public class ClientWindow extends JPanel implements KeyListener {

	// Window title
	public static final String TITLE = "...Squares Interactive GUI...";

	// Name-constants (DIM stands for Dimension)
	public static final int CANVAS_DIM = 400;					// The pixel width and height of the room
	public static final int CANVAS_WIDTH = CANVAS_DIM;			// the pixel width of the room
	public static final int CANVAS_HEIGHT = CANVAS_WIDTH;		// the pixel height of the room
	public static final int TILES_DIM = 10;						// the number of tiles (squares) in a row or column
	public static final int TILE_DIM = CANVAS_DIM / TILES_DIM;	// the number of pixels per tile
	
	public Player player = null; // player that will be attached to this individual GUI
	public ArrayList<User> players = null; // list of other connected players 
	
	/** Constructor to setup the GUI components */
	public ClientWindow() {
		player = new Client(0,0,Player.DOWN, true, (int)(Math.random()*50000));	
		players = new ArrayList<User>();
		players.add((User)player);
		
		setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		
		addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
	}

	/** Custom painting codes on this JPanel */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);  // paint background
		setBackground(Color.WHITE);
		Graphics2D g2d = (Graphics2D) g;
		
		drawGrid(TILES_DIM, g);	// Draw the room grid
//		drawPlayer(player.x, player.y, g2d);	// Draw the player
		
		// Draw everyone connected to the server
		for(Player p : players) {
			drawPlayer(p.x, p.y, g2d);	// Draw the player
		}
	}

	/**
	 * Draws the gridlines to define tiles in the room
	 * 
	 * @param numTilesPerRowAndCol		the number of tiles per row and per column
	 * @param g							the graphics object
	 */
	private void drawGrid(int numTilesPerRowAndCol, Graphics g) {	      
		int stepWidth = CANVAS_WIDTH / numTilesPerRowAndCol;
		int stepHeight = CANVAS_HEIGHT / numTilesPerRowAndCol;
		
		for (int i = 0; i <= TILES_DIM; i++) {
			g.drawLine(stepWidth * i, 0, stepWidth*i, CANVAS_HEIGHT);
			g.drawLine(0, stepHeight * i, CANVAS_WIDTH, stepHeight * i);
		}		
	}

	/**
	 *  Draws player on a screen
	 * @param tile_x the x-coordinate position
	 * @param tile_y the y-coordinate position
	 * @param g canvas to draw on
	 */
	private void drawPlayer(int tile_x, int tile_y, Graphics g) {	
		double player_height = TILE_DIM * 0.8;
		double player_width = player_height;
		double eye_height = player_height / 4;
		double eye_width = eye_height;

		int player_x = tile_x * TILE_DIM;
		int player_y = tile_y * TILE_DIM;	

		double player_x_offset = (TILE_DIM - player_width) / 2;
		double player_y_offset = (TILE_DIM - player_height) / 2;
		double eye_left_x_offset = player_x_offset + eye_width * 0.8;  
		double eye_y_offset = player_y_offset + eye_height * 1.0;
		double eye_right_x_offset = player_width - (eye_left_x_offset - player_x_offset) - eye_width/2; //player_x + (TILE_DIM - (eye_left_x_offset - player_x) - player_eye_width);  

		// Translate so we are drawing only within the relevant tile
		g.translate(player_x, player_y);
	    Graphics2D gg = (Graphics2D) g.create();
	    gg.rotate(Math.toRadians(90 * player.direction), TILE_DIM/2, TILE_DIM/2);

	    
		gg.setColor(Player.PLAYER_COLOR);
		gg.fillArc((int)player_x_offset, (int)player_y_offset, (int)player_height, (int)player_width, 0, 360);
		gg.setColor(Color.BLACK);

		// Outline the player
		gg.drawArc((int)player_x_offset, (int)player_y_offset, (int)player_height, (int)player_width, 0, 360);   

		// Draw the smile
		gg.drawArc((int)player_x_offset, (int)(TILE_DIM - eye_y_offset - player_height), (int)player_height, (int)player_width, 235, 70);

		// Draw the eyes
		gg.fillArc((int)eye_left_x_offset, (int)eye_y_offset, (int)eye_height, (int)eye_width, 0, 360);
		gg.fillArc((int)eye_right_x_offset, (int)eye_y_offset, (int)eye_height, (int)eye_width, 0, 360);
	    gg.dispose();
	    
	    gg = (Graphics2D) g.create();
	}
	
	// Directional methods //
	
	private void moveRight() {
		player.moveRight();	
	}
	
	private void moveUp() {
		player.moveUp();		
	}
	
	private void moveLeft() {
		player.moveLeft();		
	}
	
	private void moveDown() {
		player.moveDown();				
	}


	/** The entry main() method */
	public static void main(String[] args) {
		// Run GUI codes in the Event-Dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame(TITLE);
				frame.setContentPane(new ClientWindow());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.pack();             // "this" JFrame packs its components
				frame.setLocationRelativeTo(null); // center the application window
				frame.setVisible(true);            // show it
			}
		});
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (player.allowedToMove) {
	        if(e.getKeyCode()== KeyEvent.VK_D || e.getKeyCode()== KeyEvent.VK_RIGHT) {
	        	moveRight();
	        } else if(e.getKeyCode()== KeyEvent.VK_W || e.getKeyCode()== KeyEvent.VK_UP) {
	        	moveUp();
	        } else if(e.getKeyCode()== KeyEvent.VK_A || e.getKeyCode()== KeyEvent.VK_LEFT) {
	        	moveLeft();
	        } else if(e.getKeyCode()== KeyEvent.VK_S || e.getKeyCode()== KeyEvent.VK_DOWN) {
	        	moveDown();
	        }
	        repaint();
		}
//        System.out.println("keyPressed");
	}

	@Override
	public void keyReleased(KeyEvent e) {
//        System.out.println("keyReleased");
	}

	@Override
	public void keyTyped(KeyEvent e) {
//        System.out.println("keyTyped");
	}
}

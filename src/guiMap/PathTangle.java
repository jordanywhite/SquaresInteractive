package guiMap;
import java.awt.Point;
import java.util.ArrayList;

/**
 * PARTIAL IMPLEMENTATION
 * 	Needs to support diagonal paths
 * 	Needs to support a sequence of points (curves)
 * 
 * This class is used to determine the logical locations of
 * a path's textures. It is given a list of points along the path
 * such as the starting point, and a corner in the path, and the
 * end of the path.
 * 
 * 	For example, the following path would have 3 points:
 * 	 (start)
 * 		|
 * 		|____ (end)
 *  (corner) 
 *  
 *  
 * @author Caleb Piekstra
 *
 */
public class PathTangle {
	
	// The direction of a segment of the path
	private static enum Direction { LEFT, RIGHT, DOWN, UP };
	
	// Holds the each square in the path
	private ArrayList<PathSquare> pathSquares = null;
	
	// Each logical texture square of the path
	public final class PathSquare {
		
		// The name or substring of the name of the texture		
		public final String name;
		
		// The logical location of the square		
		public final Point coord;
		
		public PathSquare (String name, Point location) {
			this.name = name;
			coord = location;
		}
	}	
	
	/**
	 * There must be at least 2 points in the path
	 * Note that the width is the size of the center of the pathway, 
	 * excluding the borders. So, a width of 1 would actually be 3 wide 
	 * after the outer borders are added
	 * 
	 * @param points	The points along the path (see PathTangle description)
	 * @param width		The logical width of the center of the path
	 */
	public PathTangle(Point[] points, int width) {
		if (points.length >= 2) {
			// Initialize the list of pathSquares
			pathSquares = new ArrayList<PathSquare>();
			
			for (int pIdx = 0, numPoints = points.length - 1; pIdx < numPoints; pIdx++) {
				// Look at two points along the path at a time
				Point p1 = points[pIdx];
				Point p2 = points[pIdx+1];
				
				// Determine the direction to get to p2 from p1
				Direction direction = determineDirection(p1, p2);
				
				// Set the side walls, corners, and middle section
				createPath(direction, width, p1, p2);
				
				// Handle specific direction cases
			}
		}
	}
	
	/**
	 * Used to get the squares that make up the path
	 * 
	 * @return	Every square in the path
	 */
	public ArrayList<PathSquare> getSquares() {
		return new ArrayList<PathSquare>(pathSquares);
	}
	
	/**
	 * Given a direction, width, and two points (start and end)
	 * generate the path squares for the entire path
	 * 
	 * 
	 * @param d		The direction of the path (between two points)
	 * @param width	The width of the center of the path
	 * @param p1	The logical starting point of the path
	 * @param p2	The logical ending point of the path
	 */
	private void createPath(Direction d, int width, Point p1, Point p2) {
		// Determine how far away from the center the corners are
		int sideOneOffset = 0;	// Top or Left depending on path direction
		int sideTwoOffset = 0;	// Bot or Right depending on path direction
		// If the width is odd, balance the offsets
		if (width % 2 != 0) {	
			sideOneOffset = sideTwoOffset = ((width / 2) + 1);
		} else {
			// If the width is even, the offsets will be off by 1
			sideOneOffset = width;
			sideTwoOffset = width - 1;
		}
		if (d == Direction.LEFT || d == Direction.RIGHT) {
			// If the direction is left then we want to swap the points
			// to pretend like the direction was right (so the following code works)
			if (d == Direction.LEFT) {
				Point tempP = p2;
				p2 = p1;
				p1 = tempP;
			}
			// Set the four corners
			pathSquares.add(new PathSquare("top-left", new Point(p1.x, p1.y - sideOneOffset)));
			pathSquares.add(new PathSquare("bot-left", new Point(p1.x, p1.y + sideTwoOffset)));
			pathSquares.add(new PathSquare("top-right", new Point(p2.x, p1.y - sideOneOffset)));
			pathSquares.add(new PathSquare("bot-right", new Point(p2.x, p1.y + sideTwoOffset)));
			// Set the left and right walls
			for (int i = 0; i < width; i++) {
				// (i - width/2) ensures that p1.y is the center of the path (top to bottom)
				pathSquares.add(new PathSquare("left", new Point(p1.x, p1.y + (i - width/2))));
				pathSquares.add(new PathSquare("right", new Point(p2.x, p2.y + (i - width/2))));
			}
			// Set middle path and top/bottom padding (horizontal Oreo!)
			for (int col = p1.x + 1, endCol = p2.x; col < endCol; col++) {
				// Add the top wafer
				pathSquares.add(new PathSquare("top", new Point(col, p1.y - sideOneOffset)));				
				// Add the delicious cream filling
				for (int i = 0; i < width; i++) {
					pathSquares.add(new PathSquare("middle", new Point(col, p1.y + (i - width /2))));
				}				
				// Add the bottom wafer
				pathSquares.add(new PathSquare("bot", new Point(col, p1.y + sideTwoOffset)));
			}
		} else {
			// If the direction is up then we want to swap the points
			// to pretend like the direction was down (so the following code works)
			if (d == Direction.UP) {
				Point tempP = p2;
				p2 = p1;
				p1 = tempP;
			}
			// Set the four corners
			pathSquares.add(new PathSquare("top-left", new Point(p1.x - sideOneOffset, p1.y)));
			pathSquares.add(new PathSquare("bot-left", new Point(p2.x - sideOneOffset, p2.y)));
			pathSquares.add(new PathSquare("top-right", new Point(p1.x + sideTwoOffset, p1.y)));
			pathSquares.add(new PathSquare("bot-right", new Point(p2.x + sideTwoOffset, p2.y)));
			// Set the top and bottom walls
			for (int i = 0; i < width; i++) {
				// (i - width/2) ensures that p1.x is the center of the path (left to right)
				pathSquares.add(new PathSquare("top", new Point(p1.x + (i - width /2), p1.y)));
				pathSquares.add(new PathSquare("bot", new Point(p2.x + (i - width /2), p2.y)));
			}
			// Set middle path and left/right padding (vertical Oreo!)
			for (int row = p1.y + 1, endRow = p2.y; row < endRow; row++) {
				// Add the left wafer
				pathSquares.add(new PathSquare("left", new Point(p1.x - sideOneOffset, row)));				
				// Add the delicious cream filling
				for (int i = 0; i < width; i++) {
					pathSquares.add(new PathSquare("middle", new Point(p1.x + (i - width /2), row)));
				}				
				// Add the right wafer
				pathSquares.add(new PathSquare("right", new Point(p1.x + sideTwoOffset, row)));				
			}
		}
	}
	
	/**
	 * This does not handle diagonals, it assumes that two points are
	 * exactly in the same row or column
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	private Direction determineDirection(Point p1, Point p2) {
		if (p1.y == p2.y) {
			if (p1.x < p2.x) {
				return Direction.RIGHT;
			} else {
				return Direction.LEFT;
			}
		} else {
			if (p1.y < p2.y) {
				return Direction.DOWN;
			} else {
				return Direction.UP;
			}
		}
	}
}

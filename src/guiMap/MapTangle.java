package guiMap;

/**
 * The purpose of this is to be able to easily create a rectangular room.
 * 
 * This will define where the walls, corners, shadows and flooring should
 * be located in the map based on an initial rectangular area.
 * 
 * This essentially automates the generation of a rectangular room by 
 * defining all locations of all features of a plain room after
 * being given only a top left corner and bottom right corner of the area.
 * 
 * @author Caleb Piekstra
 *
 */
public class MapTangle {
	
		// Dimensions of the rectangle floor
		public final int startRow;
		public final int startCol;
		public final int endRow;
		public final int endCol;
		
		// Stores different wall type dimensions
		public final Wall walls;		
		
		// Stores different wall shadow type dimensions
		public final WallShadow wallShadows;
		
		// Stores different corner type dimensions
		public final Corner corners;		
		
		// Stores different corner shadow type dimensions
		public final CornerShadow cornerShadows;
				
		// The logical location attributes of a wall
		public class WallStruct {
			public final int row;
			public final int col;
			public final int end;
			
			public WallStruct(int row, int col, int length) {
				this.row = row;
				this.col = col;
				this.end = length;
			}
		}
		
		// The logical location attributes of a wall shadow
		public final class ShadowWallStruct extends WallStruct {
			public ShadowWallStruct(int row, int col, int length) {
				super(row, col, length);				
			}			
		}
		
		// The logical location attributes of a corner
		public class CornerStruct {					
			public final int row;
			public final int col;
			public CornerStruct(int row, int col) {
				this.row = row;
				this.col = col;
			}
		}
		
		// The logical location attributes of a corner shadow
		public final class ShadowCornerStruct extends CornerStruct {
			public ShadowCornerStruct(int row, int col) {
				super(row, col);
			}			
		}
		
		// The locations of corners in a logical grid
		public class Corner {
			public final CornerStruct topLeft;
			public final CornerStruct topRight;
			public final CornerStruct botLeft;
			public final CornerStruct botRight;
			
			public Corner() {
				// Set corner dimensions
				topLeft = new CornerStruct(startRow-3, startCol-1);
				topRight = new CornerStruct(startRow-3, endCol);
				botLeft = new CornerStruct(endRow, startCol-1);
				botRight = new CornerStruct(endRow, endCol);
			}
		}

		// The locations of walls in a logical grid
		public class Wall {	
			public final WallStruct left;
			public final WallStruct right;
			public final WallStruct top;
			public final WallStruct bottom;
			
			public Wall() {
				// Set wall dimensions			
				top = new WallStruct(startRow-3, startCol+1, endCol-1);
				bottom = new WallStruct(endRow+1, startCol+1, endCol-1);
				right = new WallStruct(startRow, endCol+1, endRow-1);
				left = new WallStruct(startRow, startCol-1, endRow-1);
			}
		}

		// The locations of wall shadows in a logical grid
		public final class WallShadow {
			
			public final ShadowWallStruct left;
			public final ShadowWallStruct top;
			
			public WallShadow() {
				// Set wall shadow dimensions		
				top = new ShadowWallStruct(startRow, startCol+1, endCol);
				left = new ShadowWallStruct(startRow+1, startCol, endRow);		
			}			
		}

		// The locations of corner shadows in a logical grid
		public final class CornerShadow {
			public final ShadowCornerStruct topLeft;
			
			public CornerShadow() {
				// Set corner shadow dimensions		
				topLeft = new ShadowCornerStruct(startRow, startCol);
			}			
		}
				
		/**
		 * The constructor
		 * 
		 * Generates the logical locations of all basic features of a room
		 * based on the provided rectangular area parameters.
		 * 
		 * @param startRow	The logical top left corner row of the area
		 * @param startCol	The logical top left corner column of the area
		 * @param endRow	The logical bottom right corner row of the area
		 * @param endCol	The logical bottom right corner column of the area
		 */
		public MapTangle(int startRow, int startCol, int endRow, int endCol) {
			
			// Set rectangular dimensions (for the floor)
			this.startRow = startRow;
			this.startCol = startCol;
			this.endRow = endRow;
			this.endCol = endCol;			
			
			// Set corner dimensions
			corners = new Corner();
			
			// Set wall dimensions			
			walls = new Wall();
			
			// Set wall shadows		
			wallShadows = new WallShadow();
			
			// Set corner shadows
			cornerShadows = new CornerShadow();
		}
	}
package gui_client;

/**
 * 
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
				
		/**
		 * Structs
		 *
		 */
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
		
		public final class ShadowWallStruct extends WallStruct {
			public ShadowWallStruct(int row, int col, int length) {
				super(row, col, length);				
			}			
		}
		
		public class CornerStruct {					
			public final int row;
			public final int col;
			public CornerStruct(int row, int col) {
				this.row = row;
				this.col = col;
			}
		}
		
		public final class ShadowCornerStruct extends CornerStruct {
			public ShadowCornerStruct(int row, int col) {
				super(row, col);
			}			
		}
		
		/**
		 * Map-Feature Group Classes
		 *
		 */
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
		
		public final class WallShadow {
			
			public final ShadowWallStruct left;
			public final ShadowWallStruct top;
			
			public WallShadow() {
				// Set wall shadow dimensions		
				top = new ShadowWallStruct(startRow, startCol+1, endCol);
				left = new ShadowWallStruct(startRow+1, startCol, endRow);		
			}			
		}
		
		public final class CornerShadow {
			public final ShadowCornerStruct topLeft;
			
			public CornerShadow() {
				// Set corner shadow dimensions		
				topLeft = new ShadowCornerStruct(startRow, startCol);
			}			
		}
		
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
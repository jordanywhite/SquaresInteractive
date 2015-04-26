package guiMap;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import resourceManagement.ResourceLoader;

/**
 * The purpose of this class is to provide a higher level view of map generation.
 * At some point this class file will gather map parameters from resource files,
 * but for now everything is done from here for testing purposes. 
 * 
 * @author Caleb Piekstra
 *
 */

public class MapEditor extends Map {	
	
	/**
	 * Creates the map
	 * 
	 * @param resLoader		Where to look for texture files
	 * @param map_id		The level ID
	 * @param map_width		The pixel width of the map
	 * @param map_height	The pixel height of the map
	 * @param num_layers	The number of texture layers (i.e. terrain, shading)
	 * @param squareDim		The pixel dimension of a map square
	 */
	public MapEditor(ResourceLoader resLoader, int map_id, int map_width, int map_height, int num_layers, int squareDim) {
		// Create the super map!
		super(resLoader, map_id, map_width, map_height, num_layers, squareDim);			
	} 
	
	
	/**___________________________________________________________________________________________**\
   /  / 
  /  |  
 < | |	MAP EDITING
  \  |
   \  \___________________________________________________________________________________________
	\**                                                                                           **/	
	
	/**
	 * Creates the "Room" level that was used for the project's prototype and demo
	 * 
	 * @param sr			The logical top left row of the room
	 * @param sc			The logical top left column of the room
	 * @param er			The logical bottom right row of the room
	 * @param ec			The logical bototm right column of the room
	 * @param floorGroup	The name of the texture group to find floor textures
	 * @param wallGroup		The name of the texture group to find wall textures
	 * @param shadowGroup	The name of the texture group to find shadow textures
	 */
	public void makeRoom(int sr, int sc, int er, int ec, String floorGroup, String wallGroup, String shadowGroup) {
		
		// Create an object that determines a grid mapping of the room's features
		MapTangle mt = new MapTangle(sr, sc, er, ec);
		
		// Add the floor
		addTerrain(mt.startRow, mt.startCol, mt.endRow, mt.endCol, floorGroup, new Seed(15,4));
		
		// Add the walls around the room
		addWall(mt.walls.top.row, mt.walls.top.col, mt.walls.top.end, new Wall(Wall.TOP), wallGroup, new Ratio(5, 2));
		addWall(mt.walls.bottom.row, mt.walls.bottom.col, mt.walls.bottom.end, new Wall(Wall.BOTTOM), wallGroup, new Ratio(5, 2));
		addWall(mt.walls.right.row, mt.walls.right.col, mt.walls.right.end, new Wall(Wall.RIGHT), wallGroup, new Ratio(5, 2));
		addWall(mt.walls.left.row, mt.walls.left.col, mt.walls.left.end, new Wall(Wall.LEFT), wallGroup, new Ratio(5, 2));
		
		// Add wall corners to the room
		addCorner(mt.corners.topRight.row, mt.corners.topRight.col, new Corner(Corner.TOP_RIGHT), new CornerSize(CornerSize.LARGE), wallGroup);
		addCorner(mt.corners.topLeft.row, mt.corners.topLeft.col, new Corner(Corner.TOP_LEFT), new CornerSize(CornerSize.LARGE), wallGroup);
		addCorner(mt.corners.botLeft.row, mt.corners.botLeft.col, new Corner(Corner.BOT_LEFT), new CornerSize(CornerSize.SMALL), wallGroup);
		addCorner(mt.corners.botRight.row, mt.corners.botRight.col, new Corner(Corner.BOT_RIGHT), new CornerSize(CornerSize.SMALL), wallGroup);
		
		// Add wall shadows to the room
		addWallShadow(mt.wallShadows.top.row, mt.wallShadows.top.col, mt.wallShadows.top.end, new WallShadow(WallShadow.TOP), shadowGroup);
		addWallShadow(mt.wallShadows.left.row, mt.wallShadows.left.col, mt.wallShadows.left.end, new WallShadow(WallShadow.LEFT), shadowGroup);
		
		// Add wall corner shadows to the room
		addCornerShadow(mt.cornerShadows.topLeft.row, mt.cornerShadows.topLeft.col, new CornerShadow(CornerShadow.TOP_LEFT), shadowGroup);
		
		// Set which textures are considered to be SOLID map squares
		setSolids(new String[]{"walls"}, new String[]{"corner-q2-small.png"});		
	}
	
	/**
	 * Creates the "Outside House" level that was intended to be used for the demo
	 * but could not be completed in time.
	 * 
	 * @param sr					The logical top left row of the outside area
	 * @param sc					The logical top left column of the outside area
	 * @param er					The logical bottom right row of the outside area
	 * @param ec					The logical bototm right column of the outside area
	 * @param terrainGroup			The name of the texture group to find terrain textures
	 * @param animatedTerrainGroup	The name of the  texture group to find animated terrain textures
	 * @param terrainAnimationDelay	The delay between animations for animated terrain
	 * @param houseGroup			The name of the  texture group to find house textures
	 * @param fenceGroup			The name of the  texture group to find fence textures
	 * @param callableAnimator		An animator for the animated textures
	 */
	public void makeOutside(int sr, int sc, int er, int ec, String terrainGroup, String animatedTerrainGroup, int terrainAnimationDelay, String houseGroup, String fenceGroup, Callable<?> callableAnimator) {
		// Add the floor
		addTerrain(sr, sc, er, ec, terrainGroup, new Seed(1,0));	
		addAnimatedTerrain(12, 1, 18, 7, animatedTerrainGroup, terrainAnimationDelay, callableAnimator);
		addPathway(new Point[]{new Point(18, 0), new Point(18, 10)}, 2, "grass", new Seed(10,0));
		
		// TODO - not ready yet
		addObject(5,5,"table");
		
		// This must be done AFTER adding ALL animated textures
		setAnimatedSquares();
		// Set which textures are considered to be SOLID map squares
		setSolids(new String[]{"water"}, new String[]{""});	
	}
	
	/**
	 * This method goes through and looks for animated texture squares in
	 * a map and creates an array that contains all of them.
	 * 
	 * The puspose is to do the pre-processing now and create a simple 
	 * array of textures which the GUI has to animate and doesn't have
	 * to look for them every phase of their animation.
	 */
	public void setAnimatedSquares() {
		// This will hold the list of animated textures in the map
		ArrayList<MapSquare> animatedSquares = new ArrayList<MapSquare>();
		
		// Go through every map square and check to see if the square contains an animated texture
		for (int row = 0, numRows = map.squares.length; row < numRows; ++row) {
			for (int col = 0, numCols = map.squares[0].length; col < numCols; ++col) {				
				if (map.squares[row][col].isAnimated) {
					// Add the map square to the list of aniamted squares
					animatedSquares.add(map.squares[row][col]);
				}
			}
		}
		
		// Initialize the array of animated squares
		map.animatedSquares = new MapSquare[animatedSquares.size()];
		// Store the map squares that were in the arraylist in the array
		map.animatedSquares = animatedSquares.toArray(map.animatedSquares);				
	}
	
	/**
	 * Returns an arraylist of all texture group names available to the map editor
	 * 
	 * @return	The arraylist of directories in which to find textures for the map
	 */
	public ArrayList<String> getAvailableTextureGroups() {
		return new ArrayList<String>(super.textures.keySet());	
	}
	
	/**
	 * A helper method used to disguise some of the parameters needed by the map editor
	 * 
	 * Makes a call to generate a pathway given the provided parameters
	 * 
	 * @param points			A list of points along the pathway 
	 * @param pathWidth			The width of the center of the pathway
	 * @param terrainGroup		The name of the texture group to find path textures
	 * @param middleTerrainSeed	A seed for randomness in the textures in the center area of a path
	 */
	public void addPathway(Point[] points, int pathWidth, String terrainGroup, Seed middleTerrainSeed) {
		generatePathway(this.map, new MapLayer(MapLayer.TERRAIN), points, pathWidth, terrainGroup, middleTerrainSeed);
	}
	
	/**
	 * A helper method used to disguise some of the parameters needed by the map editor
	 * 
	 * Makes a call to generate terrain given the provided parameters
	 * 
	 * @param startRow		The logical top left row of the area
	 * @param startCol		The logical top left column of the area
	 * @param endRow		The logical bottom right row of the area
	 * @param endCol		The logical bottom right column of the area
	 * @param terrainGroup	The name of the texture group to find terrain textures
	 * @param seed			The seed to use for randomness in terrain textures
	 */
	public void addTerrain(int startRow, int startCol, int endRow, int endCol, String terrainGroup, Seed seed) {
		generateTerrain(this.map, new MapLayer(MapLayer.TERRAIN), startRow, startCol, endRow, endCol, terrainGroup, seed);
	}
	
	/**
	 * 
	 * @param startRow			The logical top left row of the animated area
	 * @param startCol			The logical top left column of the animated area
	 * @param endRow			The logical bottom right row of the animated area
	 * @param endCol			The logical bottom right column of the animated area
	 * @param terrainGroup		The name of the texture group to find animated terrain textures
	 * @param animationDelay	The delay between animation phases
	 * @param callableAnimator	The animator used to animate the textures
	 */
	public void addAnimatedTerrain(int startRow, int startCol, int endRow, int endCol, String terrainGroup, int animationDelay, Callable<?> callableAnimator) {
		generateAnimatedTerrain(this.map, new MapLayer(MapLayer.TERRAIN), startRow, startCol, endRow, endCol, terrainGroup, animationDelay, callableAnimator);
	}
	
	/**
	 * A helper method used to disguise some of the parameters needed by the map editor
	 * 
	 * Makes a call to generate a wall given the provided parameters
	 * 
	 * @param row		The logical row to start drawing the wall
	 * @param col		The logical column to start drawing the wall
	 * @param end		The length of wall
	 * @param wallType	The type of wall
	 * @param wallGroup	The name of the texture group to find wall textures
	 * @param wallRatio	The ratio of wall texture subgroups
	 */
	public void addWall(int row, int col, int end, Wall wallType, String wallGroup, Ratio wallRatio) {
		generateWall(this.map, new MapLayer(MapLayer.WALL), wallType, wallGroup, wallRatio, row, col, end);
	}
	
	/**
	 * A helper method used to disguise some of the parameters needed by the map editor
	 * 
	 * Makes a call to generate a corner given the provided parameters
	 * 
	 * @param row			The logical row to draw the corner
	 * @param col			The logical column to draw the corner
	 * @param cornerType	The type of corner
	 * @param cornerSize	The texture "size" (see CornerSize)
	 * @param cornerGroup	The name of the texture group to find the corner textures
	 */
	public void addCorner(int row, int col, Corner cornerType, CornerSize cornerSize, String cornerGroup) {
		generateCorner(this.map, new MapLayer(MapLayer.WALL), cornerType, cornerSize, cornerGroup, row, col);		
	}
	
	/**
	 * A helper method used to disguise some of the parameters needed by the map editor
	 * 
	 * Makes a call to generate an object given the provided parameters
	 * 
	 * @param topLeftRow	The logical top left row to start drawing the object
	 * @param topLeftCol	The logical top left column to start drawing the object
	 * @param objectType	The name of the texture group to find the object's textures
	 */
	public void addObject(int topLeftRow, int topLeftCol, String objectType) {
		generateObject(this.map, new MapLayer(MapLayer.OBJECT), topLeftRow, topLeftCol, objectType);
	}
	
	/**
	 * A helper method used to disguise some of the parameters needed by the map editor
	 * 
	 * Makes a call to generate shading for a wall given the provided parameters
	 * 
	 * @param row				The logical row to start drawing the shadows
	 * @param col				The logical column to start drawing the shadows
	 * @param end				The length of the line of shadows
	 * @param wallShadowType	The type of wall shadow
	 * @param shadowGroup		The name of the texture group to find shadow textures
	 */
	public void addWallShadow(int row, int col, int end, WallShadow wallShadowType, String shadowGroup) {
		generateWallShading(this.map, new MapLayer(MapLayer.SHADOW), wallShadowType, shadowGroup, row, col, end);
	}
	
	/**
	 * A helper method used to disguise some of the parameters needed by the map editor
	 * 
	 * Makes a call to generate shading for a corner given the provided parameters
	 * 
	 * @param row				The logical row to draw the corner
	 * @param col				The logical column to draw the corner
	 * @param cornerShadowType	The type of corner shadow
	 * @param shadowGroup		The name of the texture group to find shadow textures
	 */
	public void addCornerShadow(int row, int col, CornerShadow cornerShadowType, String shadowGroup) {
		generateCornerShading(this.map, new MapLayer(MapLayer.SHADOW), cornerShadowType, shadowGroup, row, col);
	}
	
	/**
	 * A helper method used to disguise some of the parameters needed by the map editor
	 * 
	 * Makes a call to set which map squares are "solid" - cannot be occupied by a player
	 * 
	 * @param solids		The names of the texture groups that are SOLIDs
	 * @param exceptions	Names of textures within the texture groups that should not be SOLID
	 */
	public void setSolids(String[] solids, String[] exceptions) {
		setMapSquareTypes(this.map, solids, exceptions);		
	}	
}

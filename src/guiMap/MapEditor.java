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
	
	public void setAnimatedSquares() {
		ArrayList<MapSquare> animatedSquares = new ArrayList<MapSquare>();
		for (int row = 0, numRows = map.squares.length; row < numRows; ++row) {
			for (int col = 0, numCols = map.squares[0].length; col < numCols; ++col) {					
				if (map.squares[row][col].isAnimated) {
					animatedSquares.add(map.squares[row][col]);
				}
			}
		}
		map.animatedSquares = new MapSquare[animatedSquares.size()];
		map.animatedSquares = animatedSquares.toArray(map.animatedSquares);				
	}
	
	public ArrayList<String> getAvailableTextureGroups() {
		return new ArrayList<String>(super.textures.keySet());	
	}
	
	public void addPathway(Point[] points, int pathWidth, String terrainType, Seed middleTerrainSeed) {
		generatePathway(this.map, new MapLayer(MapLayer.TERRAIN), points, pathWidth, terrainType, middleTerrainSeed);
	}
	
	public void addTerrain(int startRow, int startCol, int endRow, int endCol, String terrainType, Seed seed) {
		generateTerrain(this.map, new MapLayer(MapLayer.TERRAIN), startRow, startCol, endRow, endCol, terrainType, seed);
	}
	
	public void addAnimatedTerrain(int startRow, int startCol, int endRow, int endCol, String terrainType, int animationDelay, Callable<?> callableAnimator) {
		generateAnimatedTerrain(this.map, new MapLayer(MapLayer.TERRAIN), startRow, startCol, endRow, endCol, terrainType, animationDelay, callableAnimator);
	}
	
	public void addWall(int row, int col, int end, Wall wallType, String wallGroup, Ratio wallRatio) {
		generateWall(this.map, new MapLayer(MapLayer.WALL), wallType, wallGroup, wallRatio, row, col, end);
	}
	
	public void addCorner(int row, int col, Corner cornerType, CornerSize cornerSize, String cornerGroup) {
		generateCorner(this.map, new MapLayer(MapLayer.WALL), cornerType, cornerSize, cornerGroup, row, col);		
	}
	
	public void addObject(int topLeftRow, int topLeftCol, String objectType) {
		generateObject(this.map, new MapLayer(MapLayer.OBJECT), topLeftRow, topLeftCol, objectType);
	}
	
	public void addWallShadow(int row, int col, int end, WallShadow wallShadowType, String shadowGroup) {
		generateWallShading(this.map, new MapLayer(MapLayer.SHADOW), wallShadowType, shadowGroup, row, col, end);
	}
	
	public void addCornerShadow(int row, int col, CornerShadow cornerShadowType, String shadowGroup) {
		generateCornerShading(this.map, new MapLayer(MapLayer.SHADOW), cornerShadowType, shadowGroup, row, col);
	}
	
	public void setSolids(String[] solids, String[] exceptions) {
		setMapSquareTypes(this.map, solids, exceptions);		
	}	
}

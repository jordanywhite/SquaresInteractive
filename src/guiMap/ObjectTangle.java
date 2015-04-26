package guiMap;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import resourceManagement.Texture;

/**
 * IN PROGRESS
 * 
 * NOT YET IMPLEMENTED
 * 
 * STOP READING
 * 
 * SERIOUSLY
 * 
 * THE AUTHOR DOESN'T EVEN UNDERSTAND IT
 * 
 * @author Caleb Piekstra
 *
 */
	
public class ObjectTangle {
	
	// These hold the logical values specified by the component names
	private int numLogicalRows = -1;
	private int numLogicalCols = -1;
	// These hold the actual values based on the square dim
	private int numActualRows = 0;
	private int numActualCols = 0;
	// Holds the offsets of the object
	private final int rowOffset;
	private final int colOffset;
	// Holds the number of pixels across/down a map square
	private final int squareDim;
	// This holds the names of the components
	private ArrayList<String> componentNames = null;
	
	public ObjectTangle(ArrayList<Texture> components, int squareDim, int row, int col) {
		componentNames = new ArrayList<String>();
		rowOffset = row;
		colOffset = col;
		this.squareDim = squareDim;
		
		int currentRow = 0;
		int currentCol = 0;		
		int currentLogRow = 0;
		int currentLogCol = 0;
		
		PhysicalGrid grid = new PhysicalGrid();
		for (Texture t: components) {			
			ImageDim img = new ImageDim(t);
			if (img.logicalRow > currentLogRow) {
				currentRow += img.actualRows; 
			}
			if (img.logicalCol > currentLogCol) {
				currentCol += img.actualCols;
			}
		}
	}
	
	private int roundUp(int value) {
		if (value % squareDim != 0) {
			// Make up the difference
			value += (squareDim - value % squareDim);
		}
		return value;
	}
	
	public class PhysicalGrid {
		// This is essentially a dynamic [row][col] scheme with a boolean to indicate whether
		// the coordinate is filled by part/all of an image, or is 'empty'
		public ArrayList<ArrayList<Boolean>> grid = null;
		
		public PhysicalGrid() {
			grid = new ArrayList<ArrayList<Boolean>>();
		}
		
		public void addImage(int firstRow, int firstCol, int numRows, int numCols) {
			// Check if we have the row already
			if (grid.size() >= firstRow + numRows) {
				// Check if we have to add more columns, or just set values
				if (firstRow == 0) {
					extendRows(numCols, firstRow, firstRow + numRows);
				} else {
					setValues(firstRow, firstRow + numRows, firstCol, firstCol + numCols);
				}
			} else {
				// Add more rows
				extendCols(numRows, firstCol, firstCol + numCols);				
			}
		}
		
		private void setValues(int sRow, int eRow, int sCol, int eCol) {
			for (int row = sRow; row < eRow; row++) {
				for (int col = sCol; col < eCol; col++) {
					grid.get(row).set(col, true);
				}
			}
		}
		
		private void extendRows(int numCols, int sRow, int eRow) {
			// Go through all rows
			for (int row = 0, numRows = grid.size(); row < numRows; row++) {
				// Add the specified number of columns
				for (int i = 0; i < numCols; i++) {
					// If the row is between sRow and eRow, set it true, otherwise false
					grid.get(row).add(row >= sRow && row < eRow);
				}
			}
		}
		
		private void extendCols(int numRows, int sCol, int eCol) {
			// Save the old numRows
			int firstRow = grid.size();
			// First add the new rows
			for (int i = 0; i < numRows; i++) {
				grid.add(new ArrayList<Boolean>(grid.get(0).size()));
			}
			// Go through the new rows
			for (int row = firstRow, lastRow = grid.size(); row < lastRow; row++) {
				// Set all the columns
				for (int col = 0; col < grid.get(row).size(); col++) {
					// If the row is between sRow and eRow, set it true, otherwise false
					grid.get(row).set(col, col >= sCol && col < eCol);
				}
			}
		}
	}
	
	public class ImageDim {
		
		public int imageHeight;
		public int actualRows;
		public int imageWidth;
		public int actualCols;
		public int logicalRow;
		public int logicalCol;
		
		public ImageDim(Texture t) {
			// Figure out the dimensions of the image
			BufferedImage bimg = null;
			try {
				bimg = ImageIO.read(t.textureFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bimg == null) {
				return;
			}
			imageHeight = bimg.getHeight();
			actualRows = roundUp(imageHeight) / squareDim;
			imageWidth = bimg.getWidth();
			actualCols = roundUp(imageWidth) / squareDim;
		}
	}
}

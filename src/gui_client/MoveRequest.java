package gui_client;
import java.awt.Point;

/**
 * 
 * 
 * @author Caleb Piekstra
 *
 */

public class MoveRequest {
	
	public static boolean canIMoveHere(MapSquare mapSquare, Point newPoint, int rowWidth, int colHeight) {
		// check array bounds and check the destination square is not a SOLID and is not occupied	
		if (	(newPoint.y >= 0 && newPoint.y <= colHeight) 
			&& 	(newPoint.x >= 0 && newPoint.x <= rowWidth)
			&& 	(mapSquare.sqType != MapSquare.SquareType.SOLID && !mapSquare.isOccupied)) 
		{
			return true;
		}
		return false;
	}
}

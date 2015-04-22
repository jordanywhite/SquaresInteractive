package gui_client;
import java.io.File;

/**
 * 
 * 
 * @author Caleb Piekstra
 *
 */

public class Texture {
	public File textureFile = null;
	public String textureName = null;
	public String textureDir = null; 
	
	public Texture(File f, String name, String dir) {
		textureFile = f;
		textureName = name;
		textureDir = dir;
	}//end constructor

}

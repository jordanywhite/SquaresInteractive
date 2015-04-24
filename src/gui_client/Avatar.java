package gui_client;

/**
 * 
 * The character to be used in the interactive room
 * 
 * @author Caleb Piekstra
 *
 */

public class Avatar {
	
	// The name of the avatar
	public String name = null;
	// The textures for the avatar
	public TextureGroup textureGroup = null;
	
	/**
	 * constructor
	 * 
	 * @param avatarName avatar name
	 * @param avatarTextures avatar textures
	 */
	public Avatar(String avatarName, TextureGroup avatarTextures) {
		// Set the name of the avatar
		name = avatarName;
		// Set the textures for the avatar
		textureGroup = avatarTextures;			
	}// end constructor
	
	/**
	 * getTextureWithName
	 *  
	 * @param name
	 * @return texture
	 */
	public Texture getTextureWithName(String name) {
		// Look through all textures in the group
		for (Texture t : textureGroup.textures.values()) {
			// If one of the textures has the same name
			if (t.textureName.equals(name)) {
				// Return the texture
				return t;
			}
		}
		// Texture not found, return null
		return null;
	}
}

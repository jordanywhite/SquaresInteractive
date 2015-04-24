package resourceManagement;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * AvatarGroup - group of avatars
 * 
 * @author Caleb Piekstra
 *
 */

public class AvatarGroup {
	// All of the avatars in the group
	public HashMap<String, Avatar> avatars = null;
	// The name of the group of avatars
	public String groupName = null;
	// All avatar groups - used to check if the requested group is valid
	private ArrayList<String> avatarGroups = null;
		
	// Resources
	private ResourceLoader resources = null;
	
	public AvatarGroup(ResourceLoader resLoader, String avatarGroup) {
		// Save the group name
		groupName = avatarGroup;
		// Save the resource loader
		resources = resLoader;		
		// Load all groups in the 'avatars' directory
		loadGroups("avatars");
		// If the requested group does not exist, load the first group 
		if (!avatarGroups.contains(avatarGroup) && !avatarGroups.isEmpty()) {	
			groupName = avatarGroups.get(0);
		}
		// load the avatars for the avatar group			
		loadFiles(".png", groupName);		
	}
	
	public Avatar getRandomAvatar() {
		return (Avatar)avatars.values().toArray()[(int)(Math.random()*avatars.size())];
	}
	
	public Avatar getAvatar(String avatarName) {		
		return avatars.get(avatarName);
	}
	
	private void loadGroups(String dir) {		
		// Get all the directories that are in the requested group
		ArrayList<File> dirFiles = resources.getAllDirsInDir(dir);
		// Create a list to hold the paths for the directories
		avatarGroups = new ArrayList<String>();
		// Populate the directory paths using the directory files we got from the resource loader
		for (File file : dirFiles) {
			avatarGroups.add(GlobalHelper.getLastBitFromUrl(file.getAbsolutePath()));
		}
	}
	
	private void loadFiles(String fileType, String requestedGroup) {
		// Initialize the hashmap of avatars
		avatars = new HashMap<String, Avatar>();
		// Get all the directories that are in the requested group
		ArrayList<File> dirFiles = resources.getAllDirsInDir(requestedGroup);
		// Create a list to hold the paths for the directories
		ArrayList<String> dirPaths = new ArrayList<String>();
		// Populate the directory paths using the directory files we got from the resource loader
		for (File file : dirFiles) {
			dirPaths.add(file.getAbsolutePath());
		}
		// Used to count how many avatar texture groups were loaded
		int avatarCount = 0;	
		// Go through every directory and create a texture group for it
		for(String dir : dirPaths) {
			// Get the group label
			String avatarName = GlobalHelper.getLastBitFromUrl(dir);
			// Get the group of textures corresponding to the label
			TextureGroup avatarTextures = new TextureGroup(resources.getAllFilesInDirWithExten(avatarName, fileType), avatarName);
			// Make sure we actually have textures in the texture group
			if (avatarTextures.textures != null) {
				// Put the texture group with the label, group, in our map of groups
				avatars.put(avatarName, new Avatar(avatarName,avatarTextures));
				avatarCount++;
			}			
		}
		System.out.println(avatarCount + " avatars loaded.");
	}		
}

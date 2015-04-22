package gui_client;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * 
 * 
 * @author Caleb Piekstra
 *
 */

public class GlobalHelper {
	
	/**
	 * source: http://stackoverflow.com/questions/4050087/how-to-obtain-the-last-path-segment-of-an-uri
	 * 
	 * @param url
	 * @return
	 */
	public static String getLastBitFromUrl(final String url){
	    return url.replaceFirst(".*\\\\([^\\\\?]+).*", "$1");
	}
	
	public static String getNthSegmentFromURLEnd(String url, final int n) {
		String segment = "";
		for (int i = 0; i <= n; i++) {
			segment = getLastBitFromUrl(url);
			int lastIdx = url.lastIndexOf(segment);
			url = new StringBuilder(url).replace(lastIdx, lastIdx+segment.length(),"").toString();
		}
		return segment;
	}	
	public static class TextureComparator implements Comparator<Texture> {
	    @Override
	    public int compare(Texture t1, Texture t2) {
	        return t1.textureName.compareTo(t2.textureName);
	    }
	}
	
	public static ArrayList<Texture> textureGroupToArrayList(TextureGroup tg) {
		// Return an empty list if not texture group provided
		if (tg == null) return new ArrayList<Texture>();	
		// Else return the textures in the texture group in an array list 
		return new ArrayList<Texture>(tg.textures.values());
	}
	
}

package imageProcessing;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * Take a color and convert it to a grayscale with a specified 
 * level of transparency within an image.
 * 
 * 
 * @source http://stackoverflow.com/questions/665406/how-to-make-a-color-transparent-in-a-bufferedimage-and-save-as-png
 * 
 * @author Caleb Piekstra
 *
 */
public class Transparency {
	public static Image makeColorTransparent(Image im, final Color color, final int alpha) {

		final ImageFilter filter = new RGBImageFilter() {

			// the color we are looking for... Alpha bits are set to opaque
			public int markerRGB = color.getRGB() | 0xFF000000;

			public final int filterRGB(int x, int y, int rgb) {
				if ( ( rgb | 0xFF000000 ) == markerRGB ) {
					// Mark the alpha bits as zero - transparent
					return (0x00FFFFFF | (alpha << 24)) & rgb;
				}
				else {
					// nothing to do
					return rgb;
				}
			}
		}; 

		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}
}
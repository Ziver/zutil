package zutil.image;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * This is a static class containing image utilities
 * 
 * @author Ziver
 */
public class ImageUtil {

	/**
	 * Resizes an BufferedImage
	 * 
	 * @param source is the image to resize
	 * @param width is the wanted width
	 * @param height is the wanted height
	 * @param keep_aspect is if the aspect ratio of the image should be kept
	 * @return the resized image
	 */
	public static BufferedImage scale(BufferedImage source, int width, int height, boolean keep_aspect){
		double scale_width = (double)width / source.getWidth();
		double scale_height = (double)height / source.getHeight();

		// aspect calculation
		if(keep_aspect){
			if(scale_width * source.getHeight() > height){
				scale_width = scale_height;
			}else{
				scale_height = scale_width;
			}
		}


		BufferedImage tmp = new BufferedImage(
				(int)(scale_width * source.getWidth()), 
				(int)(scale_height * source.getHeight()), 
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = tmp.createGraphics();

		AffineTransform at = AffineTransform.getScaleInstance(scale_width, scale_height);
		g2d.drawRenderedImage(source, at);
		g2d.dispose();
		return tmp;
	}
}

/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package zutil.image;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * This is a static class containing image utility methods
 * 
 * @author Ziver
 */
public class ImageUtil {

	/**
	 * Resizes a BufferedImage
	 * 
	 * @param 		source 		is the image to resize
	 * @param 		width 		is the wanted width
	 * @param 		height 		is the wanted height
	 * @param 		keep_aspect is if the aspect ratio of the image should be kept
	 * @return 		the 		resized image
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

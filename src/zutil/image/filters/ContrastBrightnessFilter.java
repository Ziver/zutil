/*******************************************************************************
 * Copyright (c) 2013 Ziver
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

package zutil.image.filters;

import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.image.RAWImageUtil;

public class ContrastBrightnessFilter extends ImageFilterProcessor{
	private double contrast;
	private double brightness;

	/**
	 * Creates a ContrastBrightnessEffect object with the given values
	 * @param img The image to apply the effect to
	 */
	public ContrastBrightnessFilter(BufferedImage img){
		this(img, 3, 1.2);
	}

	/**
	 * Creates a ContrastBrightnessEffect object with the given values
	 * @param img The image to apply the effect to
	 * @param con The contrast to apply
	 * @param brig The brightness to apply
	 */
	public ContrastBrightnessFilter(BufferedImage img, double con, double brig){
		super(img);
		contrast = con;
		brightness = brig;
	}

	@Override
	public int[][][] process(int[][][] data, int startX, int startY, int stopX, int stopY) {
		int mean = RAWImageUtil.getMeanValue(data);
		
		int[][][] output = RAWImageUtil.copyArray(data);
		
		RAWImageUtil.addMeanValue(output, startX, startY, stopX, stopY, mean*(-1));
		RAWImageUtil.scale(output, startX, startY, stopX, stopY, contrast);
		RAWImageUtil.addMeanValue(output, startX, startY, stopX, stopY, (int)(brightness*mean));

		RAWImageUtil.clip(output, startX, startY, stopX, stopY);
		
		return output;
	}

}

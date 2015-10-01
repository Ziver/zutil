/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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
 */

package zutil.image.filters;

import zutil.image.ImageFilterProcessor;
import zutil.image.RAWImageUtil;
import zutil.math.ZMath;

import java.awt.image.BufferedImage;

/**
 * Applies an Convolution kernel to the specified image
 * 
 * @author Ziver
 */
public class ConvolutionFilter extends ImageFilterProcessor{
	private double[][] kernel;

	protected ConvolutionFilter(BufferedImage img) {
		super(img);
	}
	
	public ConvolutionFilter(double[][] kernel) {
		this(null, kernel);
	}
	
	/**
	 * Applies an Convolution kernel to the specified image
	 * 
	 * @param img is the image
	 * @param kernel is the kernel to apply to the image
	 */
	public ConvolutionFilter(BufferedImage img, double[][] kernel) {
		super(img);
		this.kernel = kernel;
	}

	@Override
	public int[][][] process(int[][][] data, int startX, int startY, int stopX,	int stopY) {
		if(kernel == null) this.kernel = generateKernel();
		
		int[][][] tmpData = new int[data.length][data[0].length][4];
		int xk_length = kernel[0].length;
		int yk_length = kernel.length;

		for(int y=startY; y<stopY ;y++){
			setProgress(ZMath.percent(0, (stopY-startY), y+1));
			for(int x=startX; x<stopX ;x++){
				tmpData[y][x][0] = data[y][x][0]; // alpha
				
				for(int yk=0; yk<yk_length ;yk++){
					for(int xk=0; xk<xk_length ;xk++){
						if(0 <= y-yk_length/2+yk && y-yk_length/2+yk < data.length && 
								0 <= x-xk_length/2+xk && x-xk_length/2+xk < data[0].length){ // check that its not out of index
							tmpData[y][x][1] += data[y-yk_length/2+yk][x-xk_length/2+xk][1] * kernel[yk][xk];
							tmpData[y][x][2] += data[y-yk_length/2+yk][x-xk_length/2+xk][2] * kernel[yk][xk];
							tmpData[y][x][3] += data[y-yk_length/2+yk][x-xk_length/2+xk][3] * kernel[yk][xk];
						}
					}
				}
			}
		}
		
		RAWImageUtil.clip(tmpData, startX, startY, stopX, stopY);
		
		return tmpData;
	}
	
	/**
	 * Returns the kernel or null if it has not been generated yet.
	 */
	public double[][] getKernel(){
		return kernel;
	}
	
	/**
	 * Should be overridden by a subclass
	 * 
	 * @return an special generated kernel
	 */
	protected double[][] generateKernel(){
		return null;
	}

}

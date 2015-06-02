/*
 * Copyright (c) 2015 ezivkoc
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
import zutil.math.ZMath;

import java.awt.image.BufferedImage;

/**
 * Generates an image that contains the edges of the source image
 * 
 * @author Ziver
 * INFO: http://en.wikipedia.org/wiki/Sobel_operator
 */
public class SobelEdgeDetectionFilter extends ImageFilterProcessor{
	private static final double[][] xG_kernel = new double[][]{
		{+1, 0, -1},
		{+2, 0, -2},
		{+1, 0, -1}
	};
	private static final double[][] yG_kernel = new double[][]{
		{+1, +2, +1},
		{ 0,  0,  0},
		{-1, -2, -1}
	};
	
	
	public SobelEdgeDetectionFilter(BufferedImage img) {
		super(img);
	}

	@Override
	public int[][][] process(int[][][] data, int startX, int startY, int stopX,	int stopY) {
		ConvolutionFilter conv = new ConvolutionFilter(xG_kernel);
		int[][][] xG = conv.process(data, startX, startY, stopX, stopY);
		setProgress(33);
		
		conv = new ConvolutionFilter(yG_kernel);
		int[][][] yG = conv.process(data, startX, startY, stopX, stopY);
		setProgress(66);
		
		int[][][] output = new int[data.length][data[0].length][4];
		for(int y=startY; y<stopY ;y++){
			setProgress(66+ZMath.percent(0, (stopY-startY), y+1)/100*34);
			for(int x=startX; x<stopX ;x++){
				output[y][x][0] = data[y][x][0];
				output[y][x][1] = (int)Math.sqrt( xG[y][x][1]*xG[y][x][1] + yG[y][x][1]*yG[y][x][1] );
				output[y][x][2] = (int)Math.sqrt( xG[y][x][2]*xG[y][x][2] + yG[y][x][2]*yG[y][x][2] );
				output[y][x][3] = (int)Math.sqrt( xG[y][x][3]*xG[y][x][3] + yG[y][x][3]*yG[y][x][3] );
				/*
				output[y][x][1] = Math.abs( xG[y][x][1] ) + Math.abs(yG[y][x][1] );
				output[y][x][2] = Math.abs( xG[y][x][2] ) + Math.abs(yG[y][x][2] );
				output[y][x][3] = Math.abs( xG[y][x][3] ) + Math.abs(yG[y][x][3] );
				*/
			}
		}
		
		// gradient's direction:
		// 0 = arctan( yG/xG )
		
		return output;
	}
	
	
}

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
package zutil.image.filters;

import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.image.RAWImageUtil;
import zutil.math.ZMath;

public class SpotLightFilter extends ImageFilterProcessor{
	private int radius;
	private int xPos;
	private int yPos;
	
	/**
	 * Sets up a default spotlight effect in
	 * the middle of the image
	 */
	public SpotLightFilter(BufferedImage img){
		this(img, 100, -1, -1);
	}

	/**
	 * Sets up a custom spotlight 
	 * @param r The radius of the spotlight in pixels
	 */
	public SpotLightFilter(BufferedImage img, int r){
		this(img, r, -1, -1);
	}
	
	/**
	 * Sets up a custom spotlight 
	 * @param r The radius of the spotlight in pixels
	 * @param x The x position of the spotlight, if -1 then it will be centered
	 * @param y The y position of the spotlight, if -1 then it will be centered
	 */
	public SpotLightFilter(BufferedImage img, int r, int x, int y){
		super(img);
		radius = r;
		xPos = x;
		yPos = y;
	}

	@Override
	public int[][][] process(int[][][] data, int startX, int startY, int stopX, int stopY) {
		if(xPos < 0) xPos = data[0].length/2;
		if(yPos < 0) yPos = data.length/2;
		
		int[][][] output = new int[data.length][data[0].length][4];
		
		double scale, dx, dy, distance;
		for(int y=startY; y<stopY ;y++){
			setProgress(ZMath.percent(0, (stopY-startY)-1, y));
			for(int x=startX; x<stopX ;x++){
				dx = x-xPos;
				dy = y-yPos;
				
				distance = Math.sqrt(dx*dx+dy*dy);
				
				if(distance > radius){
					scale = 0;
				}
				else{
					scale = 1-(distance/radius);
				}
				
				output[y][x][0] = data[y][x][0];
				output[y][x][1] = RAWImageUtil.clip((int)(scale * data[y][x][1]));
				output[y][x][2] = RAWImageUtil.clip((int)(scale * data[y][x][2]));
				output[y][x][3] = RAWImageUtil.clip((int)(scale * data[y][x][3]));
			}
		}
		return output;
	}

}

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

import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.image.RAWImageUtil;
import zutil.math.ZMath;


public class DitheringFilter extends ImageFilterProcessor{
	// default palette is black and white
	private int[][] palette = {
			{255,0,0,0},
			{255,255,255,255}
	};


	/**
	 * Sets up a default DitheringEffect
	 */
	public DitheringFilter(BufferedImage img){
		super(img);
	}
	
	/**
	 * Creates a Dithering Effect object
	 * @param img The image to apply the effect on
	 * @param palette The palette to use on the image 
	 * int[colorCount][4]
	 * 0 -> Alpha data
	 * 		Red data
	 * 		Green data
	 * 4 ->	Blue data
	 */
	public DitheringFilter(BufferedImage img, int[][] palette){
		super(img);
		this.palette = palette;
	}

	@Override
	public int[][][] process(int[][][] data, int startX, int startY, int stopX, int stopY) {
		int error, index;
		int[] currentPixel;
		int[][][] output = RAWImageUtil.copyArray(data);
		
		for(int y=startY; y<stopY ;y++){
			setProgress(ZMath.percent(0, stopY-startY-1, y));
			for(int x=startX; x<stopX ;x++){
				currentPixel = output[y][x];
				index = findNearestColor(currentPixel, palette);
				output[y][x] = palette[index];

				for (int i = 1; i < 4; i++)	{
					error = currentPixel[i] - palette[index][i];
					if (x + 1 < output[0].length) {
						output[y+0][x+1][i] = RAWImageUtil.clip( output[y+0][x+1][i] + (error*7)/16 );
					}
					if (y + 1 < data.length) {
						if (x - 1 > 0) 
							output[y+1][x-1][i] = RAWImageUtil.clip( output[y+1][x-1][i] + (error*3)/16 );
						output[y+1][x+0][i] = RAWImageUtil.clip( output[y+1][x+0][i] + (error*5)/16 );
						if (x + 1 < data[0].length) 
							output[y+1][x+1][i] = RAWImageUtil.clip( output[y+1][x+1][i] + (error*1)/16 );
					}
				}
			}
		}
		
		return output;
	}
	
    private static int findNearestColor(int[] color, int[][] palette) {
        int minDistanceSquared = 255*255 + 255*255 + 255*255 + 1;
        int bestIndex = 0;
        for (byte i = 0; i < palette.length; i++) {
            int Rdiff = color[1] - palette[i][0];
            int Gdiff = color[2] - palette[i][1];
            int Bdiff = color[3] - palette[i][2];
            int distanceSquared = Rdiff*Rdiff + Gdiff*Gdiff + Bdiff*Bdiff;
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                bestIndex = i;
            }
        }
        return bestIndex;
    }
}

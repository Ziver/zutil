/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.image.filter;

import zutil.image.ImageFilterProcessor;
import zutil.image.RAWImageUtil;
import zutil.math.ZMath;

import java.awt.image.BufferedImage;

public class BlurFilter extends ImageFilterProcessor{
    private int blurValue;

    /**
     * Creates a blur effect on the image
     * @param img The image to blur
     */
    public BlurFilter(BufferedImage img){
        this(img, 10);
    }

    /**
     * Creates a blur effect on the image
     * @param img The image to blur
     * @param blur The amount to blur
     */
    public BlurFilter(BufferedImage img, int blur){
        super(img);
        blurValue = blur;
    }

    @Override
    public int[][][] process(int[][][] data, int startX, int startY, int stopX, int stopY) {
        int inputPeak = RAWImageUtil.getPeakValue(data);

        int[][][] tmpData = new int[data.length][data[0].length][4];
        int[][][] output = RAWImageUtil.copyArray(data);
        //Perform the convolution one or more times in succession
        int redSum, greenSum, blueSum, outputPeak;
        for(int i=0; i<blurValue ;i++){
            //Iterate on each pixel as a registration point.
            for(int y=startY; y<stopY ;y++){
                setProgress(ZMath.percent(0, (blurValue-1)*(stopY-startY-2), i*(stopY-startY-2)+y));
                for(int x=startX; x<stopX ;x++){
                    if(x == 0 || x == output[0].length-1 || y == 0 || y == output.length-1){
                        redSum = output[y][x][1] * 9;
                        greenSum = output[y][x][2] * 9;
                        blueSum = output[y][x][3] * 9;
                    }
                    else{
                        redSum =
                            output[y - 1][x - 1][1] +
                            output[y - 1][x - 0][1] +
                            output[y - 1][x + 1][1] +
                            output[y - 0][x - 1][1] +
                            output[y - 0][x - 0][1] +
                            output[y - 0][x + 1][1] +
                            output[y + 1][x - 1][1] +
                            output[y + 1][x - 0][1] +
                            output[y + 1][x + 1][1];
                        greenSum =
                            output[y - 1][x - 1][2] +
                            output[y - 1][x - 0][2] +
                            output[y - 1][x + 1][2] +
                            output[y - 0][x - 1][2] +
                            output[y - 0][x - 0][2] +
                            output[y - 0][x + 1][2] +
                            output[y + 1][x - 1][2] +
                            output[y + 1][x - 0][2] +
                            output[y + 1][x + 1][2];
                        blueSum =
                            output[y - 1][x - 1][3] +
                            output[y - 1][x - 0][3] +
                            output[y - 1][x + 1][3] +
                            output[y - 0][x - 1][3] +
                            output[y - 0][x - 0][3] +
                            output[y - 0][x + 1][3] +
                            output[y + 1][x - 1][3] +
                            output[y + 1][x - 0][3] +
                            output[y + 1][x + 1][3];
                    }
                    tmpData[y][x][0] = output[y][x][0];
                    tmpData[y][x][1] = redSum;
                    tmpData[y][x][2] = greenSum;
                    tmpData[y][x][3] = blueSum;
                }
            }

            // getting the new peak value and normalizing the image
            outputPeak = RAWImageUtil.getPeakValue(tmpData);
            RAWImageUtil.normalize(output, tmpData, startX, startY, stopX, stopY, ((double)inputPeak)/outputPeak );
        }
        return output;
    }
}

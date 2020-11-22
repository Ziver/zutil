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
import zutil.math.ZMath;

import java.awt.image.BufferedImage;

public class ResizeImage extends ImageFilterProcessor{
    private int width;
    private int height;

    /**
     * Will create a ResizeImage object and fix the height with the aspect
     * of the width
     *
     * @param img The image to resize
     * @param w The new width
     */
    public ResizeImage(BufferedImage img, int w){
        this(img, w, -1);
    }

    /**
     * Will create a ResizeImage object
     *
     * @param img The image to resize
     * @param w The new width if -1 then it will be scaled whit aspect of the hight
     * @param h The new height if -1 then it will be scaled whit aspect of the width
     */
    public ResizeImage(BufferedImage img, int w, int h){
        super(img);
        width = w;
        height = h;
    }

    @Override
    public int[][][] process(final int[][][] data, int startX, int startY, int stopX, int stopY) {
        if(width < 1){
            height = (int)(((double)width/(stopX-startX))*(stopY-startY));
        }
        else if(height < 1){
            width = (int)(((double)height/(stopY-startY))*(stopX-startY));
        }

        int[][][] newData = new int[height][width][4];
        double xScale = ((double)(stopX-startX)/width);
        double yScale = ((double)(stopY-startY)/height);

        for(int y=0; y<width ;y++){
            setProgress(ZMath.percent(0, width-1, y));
            for(int x=0; x<height ;x++){
                newData[y][x][0] = data[(int)(y*yScale)][(int)(x*xScale)][0];
                newData[y][x][1] = data[(int)(y*yScale)][(int)(x*xScale)][1];
                newData[y][x][2] = data[(int)(y*yScale)][(int)(x*xScale)][2];
                newData[y][x][3] = data[(int)(y*yScale)][(int)(x*xScale)][3];
            }
        }

        return newData;
    }
}

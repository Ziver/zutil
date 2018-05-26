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

package zutil.image.filter;

import java.awt.image.BufferedImage;

/**
 * The MedianFilter is used for noise reduction and things
 * 
 * @author Ziver
 */
public class MeanBlurFilter extends ConvolutionFilter{
    private int windowSize;

    /**
     * Setup a default MedianFilter
     *
     * @param img is the image to process
     */
    public MeanBlurFilter(BufferedImage img) {
        this(img, 10);
    }

    /**
     * Setup a default MedianFilter
     *
     * @param img is the image to process
     * @param pixels is the size of the window
     */
    public MeanBlurFilter(BufferedImage img, int pixels) {
        super(img);
        this.windowSize = pixels;
    }

    protected double[][] generateKernel(){
        double[][] kernel = new double[windowSize][windowSize];

        double mean = 1.0/(windowSize*windowSize);
        for(int y=0; y<windowSize ;y++){
            for(int x=0; x<windowSize ;x++){
                kernel[y][x] = mean;
            }
        }

        return kernel;
    }
}

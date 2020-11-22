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

import java.awt.image.BufferedImage;

/**
 * Blurs an image whit the Gaussian blur algorithm
 * 
 * @author Ziver
 */
public class GaussianBlurFilter extends ConvolutionFilter{
    private int size;
    private double sigma;

    public GaussianBlurFilter(BufferedImage img) {
        this(img, 5, 1.4);
    }

    public GaussianBlurFilter(BufferedImage img, int size, double sigma) {
        super(img);
        this.size = size;
        this.sigma = sigma;
    }

    protected double[][] generateKernel(){
        return gaussianFunction(size, size, sigma);
    }

    /**
     * Generates the kernel from the specified values
     */
    public static double[][] gaussianFunction(int size_x, int size_y, double sigma){
        double[][] kernel;
        int center_x = size_x/2;
        int center_y = size_y/2;

        kernel = new double[size_y][size_x];
        for(int y=0; y<size_y ;y++){
            for(int x=0; x<size_x ;x++){
                double tmp_x = (double)( (x-center_x)*(x-center_x) )/(2*sigma*sigma);
                double tmp_y = (double)( (y-center_y)*(y-center_y) )/(2*sigma*sigma);
                kernel[y][x] =  1.0/(2*Math.PI*sigma*sigma) * Math.exp( -(tmp_x + tmp_y) );
            }
        }

        return kernel;
    }
}

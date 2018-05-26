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

import zutil.algo.sort.sortable.SortableDataList;
import zutil.image.ImageFilterProcessor;
import zutil.image.RAWImageUtil;
import zutil.math.ZMath;

import java.awt.image.BufferedImage;

/**
 * The MedianFilter is used for noise reduction and things
 *
 * @author Ziver
 */
public class MedianFilter extends ImageFilterProcessor{
    private int windowSize;
    private boolean[] channels;

    /**
     * Setup a default MedianFilter
     *
     * @param img The image to process
     */
    public MedianFilter(BufferedImage img) {
        this(img, 10);
    }

    /**
     * Setup a default MedianFilter
     *
     * @param img The image to process
     * @param pixels The size of the window
     */
    public MedianFilter(BufferedImage img, int pixels) {
        this(img, pixels, new boolean[]{true,true,true,true});
    }

    /**
     * Setup a default MedianFilter
     *
     * @param img The image to process
     * @param pixels The size of the window
     * @param channels Is a 4 element array for witch channels to use the filter on
     */
    public MedianFilter(BufferedImage img, int pixels, boolean[] channels) {
        super(img);
        this.windowSize = pixels;
        this.channels = channels;
    }

    /*
           edgex := (window width / 2) rounded down
           edgey := (window height / 2) rounded down
           for x from edgex to image width - edgex:
               for y from edgey to  image height - edgey:
                   colorArray[window width][window height];
                   for fx from 0 to window width:
                       for fy from 0 to window height:
                           colorArray[fx][fy] := pixelvalue[x + fx - edgex][y + fy - edgey]
                   Sort colorArray[][];
                   pixelValue[x][y] := colorArray[window width / 2][window height / 2];
     */
    @Override
    public int[][][] process(int[][][] data, int startX, int startY, int stopX, int stopY) {
        int[][][] tmpData = RAWImageUtil.copyArray(data);

        int edgeX = windowSize / 2;
        int edgeY = windowSize / 2;

        int[][] tmpArray = new int[4][256*2];
        int pixelCount;
        for(int y=startY; y<stopY ;y++){
            setProgress(ZMath.percent(0, stopY-startY-1, y));
            for(int x=startX; x<stopX ;x++){

                pixelCount = 0;
                for(int fy=0; fy<windowSize ;fy++){
                    for(int fx=0; fx<windowSize ;fx++){
                        if(y+fy-edgeY >= 0 && y+fy-edgeY < data.length && x+fx-edgeX >= 0 && x+fx-edgeX < data[0].length){
                            //colorArray[fx][fy] := pixelvalue[x + fx - edgex][y + fy - edgey]
                                                                               if(channels[0]) tmpArray[0][ getMedianIndex( tmpData[y + fy - edgeY][x + fx - edgeX][0] ) ]++;
                            if(channels[1]) tmpArray[1][ getMedianIndex( tmpData[y + fy - edgeY][x + fx - edgeX][1] ) ]++;
                            if(channels[2]) tmpArray[2][ getMedianIndex( tmpData[y + fy - edgeY][x + fx - edgeX][2] ) ]++;
                            if(channels[3]) tmpArray[3][ getMedianIndex( tmpData[y + fy - edgeY][x + fx - edgeX][3] ) ]++;
                            pixelCount++;
                        }
                    }
                }

                if(channels[0]) tmpData[y][x][0] = findMedian(tmpArray[0], pixelCount/2);
                if(channels[1]) tmpData[y][x][1] = findMedian(tmpArray[1], pixelCount/2);
                if(channels[2]) tmpData[y][x][2] = findMedian(tmpArray[2], pixelCount/2);
                if(channels[3]) tmpData[y][x][3] = findMedian(tmpArray[3], pixelCount/2);
            }
        }

        return tmpData;
    }

    private int getMedianIndex(int i){
        if(i < 0) return Math.abs(i);
        else return i+256;
    }

    private int findMedian(int[] median, int medianCount){
        int sum = 0;
        int ret = 0;
        for(int i=0; i<median.length ;i++){
            sum += median[i];
            median[i] = 0;
            if(sum >= medianCount && ret == 0){
                ret = i-256;
            }
        }

        return ret;
    }


    class SortableARGB implements SortableDataList<Integer>{
        private int[][][] data;
        private int cols;
        private int rows;
        private int channel;

        public SortableARGB(int[][][] data, int cols, int rows, int channel){
            this.data = data;
            this.cols = cols;
            this.rows = rows;
            this.channel = channel;
        }

        public int compare(int a, int b) {
            return compare(a, data[ getY(b) ][ getX(b) ][ channel ]);
        }

        public int compare(int a, Integer b) {
            return ((Integer)data[ getY(a) ][ getX(a) ][ channel ]).compareTo(b);
        }

        public Integer get(int i) {
            return data[ getY(i) ][ getX(i) ][ channel ];
        }

        public void set(int i, Integer o){
            data[ getY(i) ][ getX(i) ][ channel ] = o;
        }

        public int size() {
            return cols * rows;
        }

        public void swap(int a, int b) {
            int tmp = data[ getY(a) ][ getX(a) ][ channel ];
            data[ getY(a) ][ getX(a) ][ channel ] = data[ getY(b) ][ getX(b) ][ channel ];
            data[ getY(b) ][ getX(b) ][ channel ] = tmp;
        }


        private int getX(int a){
            return a % cols;
        }

        private int getY(int a){
            return a / cols;
        }

    }

}

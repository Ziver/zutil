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

package zutil.image;

import zutil.ProgressListener;

import java.awt.image.BufferedImage;

/**
 * This is a abstract class for all the effects
 *
 * Inspiration:
 * http://www.dickbaldwin.com/tocadv.htm
 *
 * @author Ziver
 */
public abstract class ImageFilterProcessor {
    private BufferedImage img;
    private ProgressListener<ImageFilterProcessor,?> progress;

    public ImageFilterProcessor(BufferedImage img) {
        this.img = img;
    }

    /**
     * Sets the listener
     * @param 		listener 		is the listener, null to disable the progress
     */
    public void setProgressListener(ProgressListener<ImageFilterProcessor,?> listener) {
        this.progress = listener;
    }

    /**
     * Returns the listener
     */
    public ProgressListener<?,?> getProgressListener() {
        return this.progress;
    }

    /**
     * Sets the progress in percent
     */
    protected void setProgress(double percent) {
        if (progress != null)
            progress.progressUpdate(this, null, percent);
    }

    /**
     * Applies a effect to a given image
     *
     * @param 		effect 		The effect to use
     * @param 		img			The image to process
     * @return 					The processed image
     */
    public static ImageFilterProcessor getProcessor(String effect, BufferedImage img) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ImageFilterProcessor processor = (ImageFilterProcessor)Class.forName(effect).newInstance();
        processor.img = img;
        return processor;
    }

    /**
     * Adds the chosen effect to the image
     *
     * @return 					The Image with the effect
     */
    public BufferedImage process() throws InterruptedException{
        int cols = img.getWidth();
        int rows = img.getHeight();

        if (cols < 0 || rows < 0) {
            throw new InterruptedException("Image not Loaded!!!");
        }

        // converts the img to raw data
        int[][][] data = convertToArray(img, cols, rows);
        //processes the image
        data = process(data);
        //converts back the image
        return convertToImage(data, data[0].length, data.length);
    }

    /**
     * Creates a Integer array with the pixel data of the image <pre>
     * int[row][col][4]
     * 0 -&gt; Alpha data
     *      Red data
     *      Green data
     * 4 -&gt; Blue data </pre>
     *
     * @param 		img 		is the image to convert
     * @param 		cols 		is the columns of the image
     * @param 		rows 		is the rows of the image
     * @return 					A is the integer array
     */
    public static int[][][] convertToArray(BufferedImage img, int cols, int rows) throws InterruptedException{
        int[][][] data = new int[rows][cols][4];
        // Reads in the image to a one dim array
        int[] pixels = img.getRGB(0, 0, cols, rows, null, 0, cols);

        // Read the pixel data and put it in the data array
        for (int y=0; y<rows; y++) {
            // reading a row
            int[] aRow = new int[cols];
            for (int x=0; x<cols; x++) {
                int element = y * cols + x;
                aRow[x] = pixels[element];
            }

            // Reading in the color data
            for (int x=0; x<cols; x++) {
                //Alpha data
                data[y][x][0] = ((aRow[x] >> 24) & 0xFF);
                //Red data
                data[y][x][1] = ((aRow[x] >> 16) & 0xFF);
                //Green data
                data[y][x][2] = ((aRow[x] >> 8)	& 0xFF);
                //Blue data
                data[y][x][3] = ((aRow[x])& 0xFF);
            }
        }
        return data;
    }


    /**
     * Converts a pixel data array to a java Image object
     *
     * @param 		pixels 		is the pixel data array
     * @param 		cols 		is the columns of the image
     * @param 		rows 		is the rows of the image
     * @return 					A Image
     */
    public static BufferedImage convertToImage(int[][][] pixels, int cols, int rows) {
        int[] data = new int[cols * rows * 4];

        //Move the data into the 1D array.  Note the
        // use of the bitwise OR operator and the
        // bitwise left-shift operators to put the
        // four 8-bit bytes into each int.
        int index = 0;
        for (int y=0; y<rows; y++) {
            for (int x=0; x< cols; x++) {
                data[index] = ((pixels[y][x][0] << 24) & 0xFF000000)
                | ((pixels[y][x][1] << 16) & 0x00FF0000)
                | ((pixels[y][x][2] << 8) & 0x0000FF00)
                | ((pixels[y][x][3]) & 0x000000FF);
                index++;
            }

        }

        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_4BYTE_ABGR);
        img.setRGB(0, 0, cols, rows, data, 0, cols);

        return img;
    }

    /**
     * Runs the image thru the processor
     *
     * @param 	data 		is the raw image to apply the effect to. This will NOT be altered
     */
    public int[][][] process(int[][][] data) {
        return process(data, 0, 0, data[0].length, data.length);
    }

    /**
     * The underlying effect is run here
     *
     * @param 		data is 	the raw image to apply the effect to. This will NOT be altered
     * @param 		startX 		is the x pixel of the image to start from
     * @param 		startY 		is the y pixel of the image to start from
     * @param 		stopX 		is the x pixel of the image to stop
     * @param 		stopY 		is the y pixel of the image to stop
     * @return 					either the modified data parameter or an new array
     */
    public abstract int[][][] process(int[][][] data, int startX, int startY, int stopX, int stopY);
}

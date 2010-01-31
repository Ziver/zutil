package zutil.image;

/**
 * Some util methods for image processing
 * @author Ziver
 *
 */
public class RAWImageUtil {
	
	/**
	 * Returns the peek value in the image
	 * 
	 * @param data The image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @return The peak value of the image
	 */
	public static int getPeakValue(int[][][] data) {
		return getPeakValue(data, 0, 0, data[0].length, data.length);
	}
	
	/**
	 * Returns the peek value in the image
	 * 
	 * @param data The image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @return The peak value of the image
	 */
	public static int getPeakValue(int[][][] data, int startX, int startY, int stopX, int stopY) {
		int peak = 0;
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				if(data[y][x][1] > peak) peak = data[y][x][1];
				if(data[y][x][2] > peak) peak = data[y][x][2];
				if(data[y][x][3] > peak) peak = data[y][x][3];
			}
		}
		return peak;
	}

	/**
	 * Normalizes the image data by the given scale
	 * 
	 * @param data The image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @param scale The scale to normalize the image by
	 */
	public static void normalize(int[][][] data, int startX, int startY, int stopX, int stopY, double scale) {
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				data[y][x][1] = (int)(data[y][x][1] * scale);
				data[y][x][2] = (int)(data[y][x][2] * scale);
				data[y][x][3] = (int)(data[y][x][3] * scale);
			}
		}
	}
	
	/**
	 * Normalizes the image data by the given scale
	 * 
	 * @param output The output data array
	 * @param data The image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @param scale The scale to normalize the image by
	 */
	public static void normalize(int[][][] output, int[][][] data, int startX, int startY, int stopX, int stopY, double scale) {
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				output[y][x][1] = (int)(data[y][x][1] * scale);
				output[y][x][2] = (int)(data[y][x][2] * scale);
				output[y][x][3] = (int)(data[y][x][3] * scale);
			}
		}
	}

	/**
	 * Returns the RMS value of the image
	 * (The RMS value is a measure of the width of the color distribution.)
	 * 
	 * @param data is the image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @return The RMS value for the image
	 */
	public static int getRMS(int[][][] data, int startX, int startY, int stopX, int stopY){
		int pixelCount = 0;
		long accum = 0;
		for(int y=startY; y <stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				accum += data[y][x][1] * data[y][x][1];
				accum += data[y][x][2] * data[y][x][2];
				accum += data[y][x][3] * data[y][x][3];
				pixelCount += 3;
			}
		}
		int meanSquare = (int)(accum/pixelCount);
		int rms = (int)(Math.sqrt(meanSquare));
		return rms;
	}

	/**
	 * Multiplies the given image data by the given value
	 * 
	 * @param data is the image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @param scale is the number to scale the image color by
	 */
	public static void scale(int[][][] data, int startX, int startY, int stopX, int stopY, double scale){
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				data[y][x][1] *= scale;
				data[y][x][2] *= scale;
				data[y][x][3] *= scale;
			}
		}
	}

	/**
	 * Returns the mean value of the given image data
	 * 
	 * @param data is the image data
	 * @return the mean value of the image
	 */
	public static int getMeanValue(int[][][] data){
		return getMeanValue(data, 0, 0, data[0].length, data.length);
	}
	
	/**
	 * Returns the mean value of the given image data
	 * 
	 * @param data is the image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @return the mean value of the image
	 */
	public static int getMeanValue(int[][][] data, int startX, int startY, int stopX, int stopY){
		int[] tmp = getMeanArray(data, startX, startY, stopX, stopY);
		return (tmp[0] + tmp[1] + tmp[2])/3;
	}
	
	/**
	 * Returns an mean array containing a mean value for each color
	 * 
	 * @param data is the image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @return the mean value of the image
	 */
	public static int[] getMeanArray(int[][][] data, int startX, int startY, int stopX, int stopY){
		int mean[] = new int[3];
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				mean[0] += data[y][x][1];
				mean[1] += data[y][x][2];
				mean[2] += data[y][x][3];
			}
		}
		// calculate the mean value
		int pixelCount = (stopY-startY)*(stopX-startX);
		mean[0] /= pixelCount;
		mean[1] /= pixelCount;
		mean[2] /= pixelCount;
		
		return mean;
	}

	/**
	 * removes the mean value from the image data
	 * 
	 * @param data is the image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @param mean is the mean value
	 */
	public static void remMeanValue(int[][][] data, int startX, int startY, int stopX, int stopY, int mean){
		addMeanValue(data, startX, startY, stopX, stopY, -mean);
	}
	
	/**
	 * Adds the mean value to the image data
	 * 
	 * @param data is the image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @param mean is the mean value
	 */
	public static void addMeanValue(int[][][] data, int startX, int startY, int stopX, int stopY, int mean){
		addMeanArray(data, startX, startY, stopX, stopY, new int[]{mean, mean, mean});
	}
	
	/**
	 * removes an mean array containing a mean value for each color
	 * 
	 * @param data is the image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @param mean is an array of length 3 containing a mean value for each color RGB
	 */
	public static void remMeanArray(int[][][] data, int startX, int startY, int stopX, int stopY, int[] mean){
		addMeanArray(data, startX, startY, stopX, stopY, new int[]{-mean[0], -mean[1], -mean[2]});
	}
	
	/**
	 * Adds an mean array containing a mean value for each color
	 * 
	 * @param data is the image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @param mean is an array of length 3 containing a mean value for each color RGB
	 */
	public static void addMeanArray(int[][][] data, int startX, int startY, int stopX, int stopY, int[] mean){
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				data[y][x][1] += mean[0];
				data[y][x][2] += mean[1];
				data[y][x][3] += mean[2];
			}
		}
	}
	
	/**
	 * Copies all the pixel data from the image to the new array
	 * 
	 * @param data The data to copy
	 * @param xStart X start position on the source
	 * @param yStart Y start position on the source
	 * @param width The amount of pixels to copy
	 * @param hight The amount of pixels to copy
	 * @return A copy of the data array
	 */
	public static int[][][] crop(int[][][] data, int xStart, int yStart, int width, int hight){
		return crop(data, xStart, yStart, null, 0, 0, width, hight);
	}
	
	/**
	 * Copies all the pixel data from the image to the new array
	 * 
	 * @param data The data to copy
	 * @param xData X start position in the source
	 * @param yData Y start position in the source
	 * @param crop The destination
	 * @param xCrop X start position in the destination
	 * @param yCrop Y start position in the destination
	 * @param width The amount of pixels to copy
	 * @param hight The amount of pixels to copy
	 * @return A copy of the data array
	 */
	public static int[][][] crop(int[][][] data, int xData, int yData, int[][][] crop, int xCrop, int yCrop, int width, int hight){
		if(crop==null) crop = new int[width][hight][4];
		for(int y=0; y<width ;y++){
			for(int x=0; x<hight ;x++){
				crop[y+yData][x+xData][0] = data[y+yCrop][x+xCrop][0];
				crop[y+yData][x+xData][1] = data[y+yCrop][x+xCrop][1];
				crop[y+yData][x+xData][2] = data[y+yCrop][x+xCrop][2];
				crop[y+yData][x+xData][3] = data[y+yCrop][x+xCrop][3];
			}
		}
		return crop;
	}
	
	/**
	 * Copies the given array to a new one that it returns
	 * 
	 * @param data The data to duplicate
	 * @return an copy of the array
	 */
	public static int[][][] copyArray(int[][][] data){
		return copyArray(data, 0, 0, data[0].length, data.length);
	}
	
	/**
	 * Copies the given array to a new one that it returns
	 * 
	 * @param data The data to duplicate
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @return The array copy
	 */
	public static int[][][] copyArray(int[][][] data, int startX, int startY, int stopX, int stopY){
		int[][][] copy = new int[data.length][data[0].length][4];
		return copyArray(data, copy, startX, startY, stopX, stopY);
	}
	
	/**
	 * Copies the given array to a new one that it returns
	 * 
	 * @param data The data to duplicate
	 * @param dest is the array to copy the data to
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @return the dest array
	 */
	public static int[][][] copyArray(int[][][] data, int[][][] dest, int startX, int startY, int stopX, int stopY){
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				dest[y][x][0] = data[y][x][0];
				dest[y][x][1] = data[y][x][1];
				dest[y][x][2] = data[y][x][2];
				dest[y][x][3] = data[y][x][3];
			}
		}
		return dest;
	}

	/**
	 * This method clips the values of the pixel so that they
	 * are in the range 0-255
	 * 
	 * @param data The image data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 */
	public static void clip(int[][][] data, int startX, int startY, int stopX, int stopY){
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				data[y][x][1] = clip(data[y][x][1]);
				data[y][x][2] = clip(data[y][x][2]);
				data[y][x][3] = clip(data[y][x][3]);
			}
		}
	}

	/**
	 * This method clips the values of a color so that it
	 * is in the range 0-255
	 * 
	 * @param color
	 * @return
	 */
	public static int clip(int color){
		if(color < 0) 
			return 0;
		else if(color > 255) 
			return 255;
		else 
			return color;
	}
}

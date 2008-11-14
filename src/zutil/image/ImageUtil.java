package zutil.image;

/**
 * Some util methods for image processing
 * @author Ziver
 *
 */
public class ImageUtil {
	/**
	 * Returns the peek value in the image
	 * 
	 * @param data The image data
	 * @param cols The number of columns
	 * @param rows The number of rows
	 * @return The peak value of the image
	 */
	public static int peakValue(int[][][] data, int cols, int rows) {
		int peak = 0;
		for(int y=0; y<rows ;y++){
			for(int x=0; x<cols ;x++){
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
	 * @param cols The number of columns
	 * @param rows The number of rows
	 * @param scale The scale to normalize the image by
	 */
	public static void normalize(int[][][] data, int cols, int rows, double scale) {
		for(int y=0; y<rows ;y++){
			for(int x=0; x<cols ;x++){
				data[y][x][1] = (int)(data[y][x][1] * scale);
				data[y][x][2] = (int)(data[y][x][2] * scale);
				data[y][x][3] = (int)(data[y][x][3] * scale);
			}
		}
	}

	/**
	 * Returns the rms value of the image
	 * (The RMS value is a measure of the width of the color distribution.)
	 * 
	 * @param data The image data
	 * @param cols The number of columns
	 * @param rows The number of rows
	 * @return The rms value for the image
	 */
	public static int rms(int[][][] data, int cols, int rows){
		int pixelCount = 0;
		long accum = 0;
		for(int y=0; y <rows ;y++){
			for(int x=0; x<cols ;x++){
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
	 * @param data The image data
	 * @param cols The number of columns
	 * @param rows The number of rows
	 * @param scale The number to scale the image by
	 */
	public static void scale(int[][][] data, int cols, int rows, double scale){
		for(int y=0; y<rows ;y++){
			for(int x=0; x<cols ;x++){
				data[y][x][1] *= scale;
				data[y][x][2] *= scale;
				data[y][x][3] *= scale;
			}
		}
	}

	/**
	 * Returns the mean value of the given image data
	 * 
	 * @param data The image data
	 * @param cols The column count
	 * @param rows The row count
	 * @return The mean value of the image
	 */
	public static int meanValue(int[][][] data,int cols, int rows){
		int pixelCount = 0;
		long accum = 0;
		for(int y=0; y<rows ;y++){
			for(int x=0; x<cols ;x++){
				accum += data[y][x][1];
				accum += data[y][x][2];
				accum += data[y][x][3];
				pixelCount += 3;
			}
		}
		// calculate the mean value
		return (int)(accum/pixelCount);
	}

	/**
	 * Adds the mean value to the image data
	 * 
	 * @param data The image data
	 * @param cols The number of columns
	 * @param rows The number of rows
	 * @param mean The mean value
	 */
	public static void addMeanValue(int[][][] data,int cols, int rows, int mean){
		for(int y=0; y<rows ;y++){
			for(int x=0; x<cols ;x++){
				data[y][x][1] += mean;
				data[y][x][2] += mean;
				data[y][x][3] += mean;
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
}

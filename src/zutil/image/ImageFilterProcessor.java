package zutil.image;

import java.awt.image.BufferedImage;

import zutil.ProgressListener;

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
	private ProgressListener progress;

	public ImageFilterProcessor(BufferedImage img){
		this.img = img;
	}
	
	/**
	 * Sets the listener
	 * @param listener The listener, null to disable the progress
	 */
	public void setProgressListener(ProgressListener listener){
		this.progress = listener;
	}
	
	/**
	 * Sets the progress in percent
	 */
	protected void setProgress(double percent){
		if(progress != null) progress.progressUpdate(this, null, percent);
	}
	
	/**
	 * Applies a effect to a given image
	 * 
	 * @param effect The effect to use
	 * @param img The image to process
	 * @return The processed image
	 */
	public static ImageFilterProcessor getProcessor(String effect, BufferedImage img) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException{
		ImageFilterProcessor processor = (ImageFilterProcessor)Class.forName(effect).newInstance();
		processor.img = img;
		return processor;
	}

	/**
	 * Adds the chosen effect to the image
	 * 
	 * @return The Image with the effect
	 * @throws InterruptedException
	 */
	public BufferedImage process() throws InterruptedException{
		int cols = img.getWidth();
		int rows = img.getHeight();

		if(cols < 0 || rows < 0){
			throw new InterruptedException("Image not Loaded!!!");
		}

		// converts the img to raw data
		int[][][] data = convertToArray(img, cols, rows);
		//processes the image
		process(data);
		//converts back the image
		return convertToImage(data, data[0].length, data.length);
	}

	/**
	 * Creates a Integer array whit the pixel data of the image
	 * int[row][col][4]
	 * 0 -> Alpha data
	 * 		Red data
	 * 		Green data
	 * 4 ->	Blue data
	 * 
	 * @param img The image to convert
	 * @param cols Columns of the image
	 * @param rows Rows of the image
	 * @return A Integer array
	 * @throws InterruptedException
	 */
	public static int[][][] convertToArray(BufferedImage img, int cols, int rows) throws InterruptedException{
		int[][][] data = new int[rows][cols][4];
		// Reads in the image to a one dim array
		int[] pixels = img.getRGB(0, 0, cols, rows, null, 0, cols);

		// Read the pixel data and put it in the data array
		for(int y=0; y<rows ;y++){
			// reading a row
			int[] aRow = new int[cols];
			for(int x=0; x<cols ;x++){
				int element = y * cols + x;
				aRow[x] = pixels[element];
			}

			// Reading in the color data
			for(int x=0; x<cols ;x++){
				//Alpha data
				data[y][x][0] = (aRow[x] >> 24) & 0xFF;
				//Red data
				data[y][x][1] = (aRow[x] >> 16) & 0xFF;
				//Green data
				data[y][x][2] = (aRow[x] >> 8)	& 0xFF;
				//Blue data
				data[y][x][3] = (aRow[x])& 0xFF;
			}
		}
		return data;
	}


	/**
	 * Converts a pixel data array to a java Image object
	 * 
	 * @param pixels The pixel data array
	 * @param cols Columns of the image
	 * @param rows Rows of the image
	 * @return A Image
	 */
	public static BufferedImage convertToImage(int[][][] pixels, int cols, int rows){
		int[] data = new int[cols * rows * 4];

		//Move the data into the 1D array.  Note the
		// use of the bitwise OR operator and the
		// bitwise left-shift operators to put the
		// four 8-bit bytes into each int.
		int index = 0;
		for(int y=0; y<rows ;y++){
			for(int x=0; x< cols ;x++){
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
	 * Runs the image thrue the processor
	 * @param data The raw image to apply the effect to
	 */
	public void process(final int[][][] data){
		process(data, 0, 0, data[0].length, data.length);
	}
	
	/**
	 * The underlying effect is run here
	 * @param data The raw image to apply the effect to
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 */
	public abstract void process(final int[][][] data, int startX, int startY, int stopX, int stopY);
}

package zutil.image.filters;

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

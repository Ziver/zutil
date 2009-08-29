package zutil.image.filters;

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

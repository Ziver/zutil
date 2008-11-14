package zutil.image.filters;

import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.image.ImageUtil;
import zutil.math.ZMath;

public class BlurFilter extends ImageFilterProcessor{
	private int blurValue;

	/**
	 * Creates a blur effect on the image
	 * @param img The image to blur
	 */
	public BlurFilter(BufferedImage img){
		this(img, 10);
	}

	/**
	 * Creates a blur effect on the image
	 * @param img The image to blur
	 * @param blur The amount to blur
	 */
	public BlurFilter(BufferedImage img, int blur){
		super(img);
		blurValue = blur;
	}
	
	@Override
	public int[][][] process(final int[][][] data, int cols, int rows) {
		int inputPeak = ImageUtil.peakValue(data, cols, rows);

		int[][][] output = new int[rows][cols][4];
		//Perform the convolution one or more times
		// in succession
		for(int i=0; i<blurValue ;i++){
			//Iterate on each pixel as a registration
			// point.
			for(int y=1; y<rows-2 ;y++){
				setProgress(ZMath.percent(0, (blurValue-1)*(rows-3), i*(rows-3)+y));
				for(int x=0+1; x<cols-2 ;x++){
					int redSum =
						data[y - 1][x - 1][1] +
						data[y - 1][x - 0][1] +
						data[y - 1][x + 1][1] +
						data[y - 0][x - 1][1] +
						data[y - 0][x - 0][1] +
						data[y - 0][x + 1][1] +
						data[y + 1][x - 1][1] +
						data[y + 1][x - 0][1] +
						data[y + 1][x + 1][1];
					int greenSum =
						data[y - 1][x - 1][2] +
						data[y - 1][x - 0][2] +
						data[y - 1][x + 1][2] +
						data[y - 0][x - 1][2] +
						data[y - 0][x - 0][2] +
						data[y - 0][x + 1][2] +
						data[y + 1][x - 1][2] +
						data[y + 1][x - 0][2] +
						data[y + 1][x + 1][2];
					int blueSum =
						data[y - 1][x - 1][3] +
						data[y - 1][x - 0][3] +
						data[y - 1][x + 1][3] +
						data[y - 0][x - 1][3] +
						data[y - 0][x - 0][3] +
						data[y - 0][x + 1][3] +
						data[y + 1][x - 1][3] +
						data[y + 1][x - 0][3] +
						data[y + 1][x + 1][3];

					output[y][x][0] = data[y][x][0];
					output[y][x][1] = redSum;
					output[y][x][2] = greenSum;
					output[y][x][3] = blueSum;
				}
			}

			// getting the new peak value and normalizing the image
			int outputPeak = ImageUtil.peakValue(output, cols, rows);
			ImageUtil.normalize(output, cols, rows, ((double)inputPeak)/outputPeak );
		}
		
		return output;
	}
}

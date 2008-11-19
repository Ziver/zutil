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
		int[][][] tmpData = ImageUtil.copyArray(data, cols, rows);
		//Perform the convolution one or more times in succession
		int redSum, greenSum, blueSum, outputPeak;
		for(int i=0; i<blurValue ;i++){
			//Iterate on each pixel as a registration point.
			for(int y=0; y<rows ;y++){
				setProgress(ZMath.percent(0, (blurValue-1)*(rows-2), i*(rows-2)+y));
				for(int x=0; x<cols ;x++){
					if(x == 0 || x == cols-1 || y == 0 || y == rows-1){
						redSum = tmpData[y][x][1] * 9;
						greenSum = tmpData[y][x][2] * 9;
						blueSum = tmpData[y][x][3] * 9;
					}
					else{
						redSum =
							tmpData[y - 1][x - 1][1] +
							tmpData[y - 1][x - 0][1] +
							tmpData[y - 1][x + 1][1] +
							tmpData[y - 0][x - 1][1] +
							tmpData[y - 0][x - 0][1] +
							tmpData[y - 0][x + 1][1] +
							tmpData[y + 1][x - 1][1] +
							tmpData[y + 1][x - 0][1] +
							tmpData[y + 1][x + 1][1];
						greenSum =
							tmpData[y - 1][x - 1][2] +
							tmpData[y - 1][x - 0][2] +
							tmpData[y - 1][x + 1][2] +
							tmpData[y - 0][x - 1][2] +
							tmpData[y - 0][x - 0][2] +
							tmpData[y - 0][x + 1][2] +
							tmpData[y + 1][x - 1][2] +
							tmpData[y + 1][x - 0][2] +
							tmpData[y + 1][x + 1][2];
						blueSum =
							tmpData[y - 1][x - 1][3] +
							tmpData[y - 1][x - 0][3] +
							tmpData[y - 1][x + 1][3] +
							tmpData[y - 0][x - 1][3] +
							tmpData[y - 0][x - 0][3] +
							tmpData[y - 0][x + 1][3] +
							tmpData[y + 1][x - 1][3] +
							tmpData[y + 1][x - 0][3] +
							tmpData[y + 1][x + 1][3];
					}
					output[y][x][0] = tmpData[y][x][0];
					output[y][x][1] = redSum;
					output[y][x][2] = greenSum;
					output[y][x][3] = blueSum;
				}
			}

			// getting the new peak value and normalizing the image
			outputPeak = ImageUtil.peakValue(output, cols, rows);
			ImageUtil.normalize(tmpData, output, cols, rows, ((double)inputPeak)/outputPeak );
		}

		return tmpData;
	}
}

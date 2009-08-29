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
	public int[][][] process(int[][][] data, int startX, int startY, int stopX, int stopY) {
		int inputPeak = ImageUtil.getPeakValue(data);

		int[][][] tmpData = new int[data.length][data[0].length][4];
		int[][][] output = ImageUtil.copyArray(data);
		//Perform the convolution one or more times in succession
		int redSum, greenSum, blueSum, outputPeak;
		for(int i=0; i<blurValue ;i++){
			//Iterate on each pixel as a registration point.
			for(int y=startY; y<stopY ;y++){
				setProgress(ZMath.percent(0, (blurValue-1)*(stopY-startY-2), i*(stopY-startY-2)+y));
				for(int x=startX; x<stopX ;x++){
					if(x == 0 || x == output[0].length-1 || y == 0 || y == output.length-1){
						redSum = output[y][x][1] * 9;
						greenSum = output[y][x][2] * 9;
						blueSum = output[y][x][3] * 9;
					}
					else{
						redSum =
							output[y - 1][x - 1][1] +
							output[y - 1][x - 0][1] +
							output[y - 1][x + 1][1] +
							output[y - 0][x - 1][1] +
							output[y - 0][x - 0][1] +
							output[y - 0][x + 1][1] +
							output[y + 1][x - 1][1] +
							output[y + 1][x - 0][1] +
							output[y + 1][x + 1][1];
						greenSum =
							output[y - 1][x - 1][2] +
							output[y - 1][x - 0][2] +
							output[y - 1][x + 1][2] +
							output[y - 0][x - 1][2] +
							output[y - 0][x - 0][2] +
							output[y - 0][x + 1][2] +
							output[y + 1][x - 1][2] +
							output[y + 1][x - 0][2] +
							output[y + 1][x + 1][2];
						blueSum =
							output[y - 1][x - 1][3] +
							output[y - 1][x - 0][3] +
							output[y - 1][x + 1][3] +
							output[y - 0][x - 1][3] +
							output[y - 0][x - 0][3] +
							output[y - 0][x + 1][3] +
							output[y + 1][x - 1][3] +
							output[y + 1][x - 0][3] +
							output[y + 1][x + 1][3];
					}
					tmpData[y][x][0] = output[y][x][0];
					tmpData[y][x][1] = redSum;
					tmpData[y][x][2] = greenSum;
					tmpData[y][x][3] = blueSum;
				}
			}

			// getting the new peak value and normalizing the image
			outputPeak = ImageUtil.getPeakValue(tmpData);
			ImageUtil.normalize(output, tmpData, startX, startY, stopX, stopY, ((double)inputPeak)/outputPeak );
		}	
		return output;	
	}
}

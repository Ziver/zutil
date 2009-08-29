package zutil.image.filters;

import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.math.ZMath;

/**
 * Generates an image that contains the edges of the source image
 * 
 * @author Ziver
 * INFO: http://en.wikipedia.org/wiki/Sobel_operator
 */
public class SobelEdgeDetectionFilter extends ImageFilterProcessor{
	private static final double[][] xG_kernel = new double[][]{
		{+1, 0, -1},
		{+2, 0, -2},
		{+1, 0, -1}
	};
	private static final double[][] yG_kernel = new double[][]{
		{+1, +2, +1},
		{ 0,  0,  0},
		{-1, -2, -1}
	};
	
	
	public SobelEdgeDetectionFilter(BufferedImage img) {
		super(img);
	}

	@Override
	public int[][][] process(int[][][] data, int startX, int startY, int stopX,	int stopY) {
		ConvolutionFilter conv = new ConvolutionFilter(xG_kernel);
		int[][][] xG = conv.process(data, startX, startY, stopX, stopY);
		setProgress(33);
		
		conv = new ConvolutionFilter(yG_kernel);
		int[][][] yG = conv.process(data, startX, startY, stopX, stopY);
		setProgress(66);
		
		int[][][] output = new int[data.length][data[0].length][4];
		for(int y=startY; y<stopY ;y++){
			setProgress(66+ZMath.percent(0, (stopY-startY), y+1)/100*34);
			for(int x=startX; x<stopX ;x++){
				output[y][x][0] = data[y][x][0];
				output[y][x][1] = (int)Math.sqrt( xG[y][x][1]*xG[y][x][1] + yG[y][x][1]*yG[y][x][1] );
				output[y][x][2] = (int)Math.sqrt( xG[y][x][2]*xG[y][x][2] + yG[y][x][2]*yG[y][x][2] );
				output[y][x][3] = (int)Math.sqrt( xG[y][x][3]*xG[y][x][3] + yG[y][x][3]*yG[y][x][3] );
				/*
				output[y][x][1] = Math.abs( xG[y][x][1] ) + Math.abs(yG[y][x][1] );
				output[y][x][2] = Math.abs( xG[y][x][2] ) + Math.abs(yG[y][x][2] );
				output[y][x][3] = Math.abs( xG[y][x][3] ) + Math.abs(yG[y][x][3] );
				*/
			}
		}
		
		// gradient's direction:
		// 0 = arctan( yG/xG )
		
		return output;
	}
	
	
}

package zutil.image.filters;

import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.math.ZMath;

public class ColorIntensityFilter extends ImageFilterProcessor{
	private boolean invert;
	private double redScale;
	private double greenScale;
	private double blueScale;

	public ColorIntensityFilter(BufferedImage img){
		this(img, 0.2, 0.2, 0.2, false);
	}

	/**
	 * Creates a ColorIntensityEffect object with the given values
	 * @param img The image data
	 * @param inv If the image color should be inverted
	 */
	public ColorIntensityFilter(BufferedImage img, boolean inv){
		this(img, 0.5, 0.5, 0.5, inv);
	}

	/**
	 * Creates a ColorIntensityEffect object with the given values
	 * @param img The image data
	 * @param red The scale of red (0-1)
	 * @param green The scale of green (0-1)
	 * @param blue The scale of blue (0-1)
	 */
	public ColorIntensityFilter(BufferedImage img, double red, double green, double blue){
		this(img, red, green, blue, false);
	}

	/**
	 * Creates a ColorIntensityEffect object with the given values
	 * @param img The image data
	 * @param red The scale of red (0-1)
	 * @param green The scale of green (0-1)
	 * @param blue The scale of blue (0-1)
	 * @param inv If the image color should be inverted
	 */
	public ColorIntensityFilter(BufferedImage img, double red, double green, double blue, boolean inv){
		super(img);
		invert = false;
		redScale = red;
		greenScale = green;
		blueScale = blue;
	}

	@Override
	public int[][][] process(int[][][] data, int startX, int startY, int stopX, int stopY) {
		int[][][] output = new int[data.length][data[0].length][4];
		// making sure the scales are right
		if(redScale > 1) redScale = 1;
		else if(redScale < 0) redScale = 0;

		if(greenScale > 1) greenScale = 1;
		else if(greenScale < 0) greenScale = 0;

		if(blueScale > 1) blueScale = 1;
		else if(blueScale < 0) blueScale = 0;
		
		// Applying the color intensity to the image
		for(int y=startY; y<stopY ;y++){
			setProgress(ZMath.percent(0, stopY-startY-1, y));
			for(int x=startX; x<stopX ;x++){
				if(!invert){
					// inversion
					output[y][x][0] = data[y][x][0];
					output[y][x][1] = (int)( 255 - data[y][x][1] * redScale );
					output[y][x][2] = (int)( 255 - data[y][x][2] * greenScale );
					output[y][x][3] = (int)( 255 - data[y][x][3] * blueScale );
				}
				else{
					output[y][x][0] = data[y][x][0];
					output[y][x][1] = (int)( data[y][x][1] * redScale );
					output[y][x][2] = (int)( data[y][x][2] * greenScale );
					output[y][x][3] = (int)( data[y][x][3] * blueScale );
				}
			}
		}
		return output;
	}

}

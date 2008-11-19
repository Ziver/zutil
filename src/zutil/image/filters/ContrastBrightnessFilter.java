package zutil.image.filters;

import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.image.ImageUtil;

public class ContrastBrightnessFilter extends ImageFilterProcessor{
	private double contrast;
	private double brightness;

	/**
	 * Creates a ContrastBrightnessEffect object with the given values
	 * @param img The image to apply the effect to
	 */
	public ContrastBrightnessFilter(BufferedImage img){
		this(img, 3, 1.2);
	}

	/**
	 * Creates a ContrastBrightnessEffect object with the given values
	 * @param img The image to apply the effect to
	 * @param con The contrast to apply
	 * @param brig The brightness to apply
	 */
	public ContrastBrightnessFilter(BufferedImage img, double con, double brig){
		super(img);
		contrast = con;
		brightness = brig;
	}

	@Override
	public int[][][] process(final int[][][] data, int cols, int rows) {
		int mean = ImageUtil.meanValue(data, cols, rows);
		
		int[][][] output = ImageUtil.copyArray(data, cols, rows);
		
		ImageUtil.addMeanValue(output, cols, rows, mean*(-1));
		ImageUtil.scale(output, cols, rows, contrast);
		ImageUtil.addMeanValue(output, cols, rows, (int)(brightness*mean));

		ImageUtil.clip(output ,cols, rows);
		
		return output;
	}

}

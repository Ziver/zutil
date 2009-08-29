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
	public int[][][] process(int[][][] data, int startX, int startY, int stopX, int stopY) {
		int mean = ImageUtil.getMeanValue(data);
		
		int[][][] output = ImageUtil.copyArray(data);
		
		ImageUtil.addMeanValue(output, startX, startY, stopX, stopY, mean*(-1));
		ImageUtil.scale(output, startX, startY, stopX, stopY, contrast);
		ImageUtil.addMeanValue(output, startX, startY, stopX, stopY, (int)(brightness*mean));

		ImageUtil.clip(output, startX, startY, stopX, stopY);
		
		return output;
	}

}

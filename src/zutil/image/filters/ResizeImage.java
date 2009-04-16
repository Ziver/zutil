package zutil.image.filters;

import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.image.ImageUtil;
import zutil.math.ZMath;

public class ResizeImage extends ImageFilterProcessor{
	private int width;
	private int height;
	
	private int[][][] newData;
	
	/**
	 * Will create a ResizeImage object and fix the height with the aspect
	 * of the width
	 * 
	 * @param img The image to resize
	 * @param w The new width
	 */
	public ResizeImage(BufferedImage img, int w){
		this(img, w, -1);
	}
	
	/**
	 * Will create a ResizeImage object
	 * 
	 * @param img The image to resize
	 * @param w The new width if -1 then it will be scaled whit aspect of the hight
	 * @param h The new height if -1 then it will be scaled whit aspect of the width
	 */
	public ResizeImage(BufferedImage img, int w, int h){
		super(img);
		width = w;
		height = h;
	}
	
	@Override
	public void process(final int[][][] data, int startX, int startY, int stopX, int stopY) {
		if(width < 1){
			height = (int)(((double)width/(stopX-startX))*(stopY-startY));
		}
		else if(height < 1){
			width = (int)(((double)height/(stopY-startY))*(stopX-startY));
		}
		
		newData = new int[height][width][4];
		double xScale = ((double)(stopX-startX)/width);
		double yScale = ((double)(stopY-startY)/height);
		
		for(int y=0; y<width ;y++){
			setProgress(ZMath.percent(0, width-1, y));
			for(int x=0; x<height ;x++){
				newData[y][x][0] = data[(int)(y*yScale)][(int)(x*xScale)][0];
				newData[y][x][1] = data[(int)(y*yScale)][(int)(x*xScale)][1];
				newData[y][x][2] = data[(int)(y*yScale)][(int)(x*xScale)][2];
				newData[y][x][3] = data[(int)(y*yScale)][(int)(x*xScale)][3];
			}
		}
		
		ImageUtil.copyArray(newData, data, 0, 0, width, height);
	}

	/**
	 * Returns the resulting image after processing
	 * @return an image or null if not processed
	 */
	public int[][][] getResult(){
		return newData;
	}
}

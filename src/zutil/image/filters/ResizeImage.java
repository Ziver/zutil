package zutil.image.filters;

import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.math.ZMath;

public class ResizeImage extends ImageFilterProcessor{
	private int width;
	private int hight;
	
	
	/**
	 * Will create a ResizeImage object and fix the hight with the aspect
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
	 * @param h The new hight if -1 then it will be scaled whit aspect of the width
	 */
	public ResizeImage(BufferedImage img, int w, int h){
		super(img);
		width = w;
		hight = h;
	}
	
	@Override
	public int[][][] process(final int[][][] data, int cols, int rows) {
		if(width < 1){
			hight = (int)(((double)width/cols)*rows);
		}
		else if(hight < 1){
			width = (int)(((double)hight/rows)*cols);
		}
		
		int[][][] tmp = new int[hight][width][4];
		double xScale = ((double)cols/width);
		double yScale = ((double)rows/hight);
		
		for(int y=0; y<width ;y++){
			setProgress(ZMath.percent(0, width-1, y));
			for(int x=0; x<hight ;x++){
				tmp[y][x][0] = data[(int)(y*yScale)][(int)(x*xScale)][0];
				tmp[y][x][1] = data[(int)(y*yScale)][(int)(x*xScale)][1];
				tmp[y][x][2] = data[(int)(y*yScale)][(int)(x*xScale)][2];
				tmp[y][x][3] = data[(int)(y*yScale)][(int)(x*xScale)][3];
			}
		}
		
		return tmp;
	}

}

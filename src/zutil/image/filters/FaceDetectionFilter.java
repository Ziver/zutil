package zutil.image.filters;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import zutil.image.ImageFilterProcessor;
import zutil.image.ImageUtil;
import zutil.math.ZMath;

public class FaceDetectionFilter extends ImageFilterProcessor{

	public FaceDetectionFilter(BufferedImage img) {
		super(img);
	}

	@Override
	public void process(int[][][] data, int startX, int startY, int stopX, int stopY) {
		int[][][] IRgBy = convertARGBToIRgBy(data, startX, startY, stopX, stopY);
		
		MedianFilter median = new MedianFilter(null, 4*getSCALE(stopX-startX, stopY-startY), new boolean[]{false,false,true,true});
		median.process(IRgBy);
		setProgress(ZMath.percent(0, 4, 1));
		
		//********* Texture Map ********
		median = new MedianFilter(null, 8*getSCALE(stopX-startX, stopY-startY), new boolean[]{false,true,false,false});
		int[][][] textureMap = ImageUtil.copyArray(IRgBy);
		median.process(IRgBy);
		
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				textureMap[y][x][1] = Math.abs(IRgBy[y][x][1]-textureMap[y][x][1]);
			}
		}
		
		median = new MedianFilter(null, 12*getSCALE(stopX-startX, stopY-startY), new boolean[]{false,true,false,false});
		textureMap = ImageUtil.copyArray(IRgBy);
		median.process(textureMap);
		setProgress(ZMath.percent(0, 4, 2));
		
		//*********** Hue & Saturation *********
		int[][] skinMap = new int[data.length][data[0].length];
		int[][] hueMap = new int[data.length][data[0].length];
		int[][] saturationMap = new int[data.length][data[0].length];
		
		int hue, saturation;
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				// hue = (atan^2(Rg,By))
				hue = (int)( Math.atan2(IRgBy[y][x][2], IRgBy[y][x][3]) * 360/2*Math.PI);
				// saturation = sqrt(Rg^2+By^2)
				saturation = (int) Math.sqrt(IRgBy[y][x][2]*IRgBy[y][x][2] + IRgBy[y][x][3]*IRgBy[y][x][3]);
				
				hueMap[y][x] = hue;
				saturationMap[y][x] = saturation;
				
				// (1) texture<4.5, 120<160, 10<60
				// (2) texture<4.5, 150<180, 20<80
				if((textureMap[y][x][1] < 4.5 && (hue >= 120 && hue <= 160) && (saturation >= 10 && saturation <= 60)) ||
						(textureMap[y][x][1] < 4.5 && (hue >= 150 && hue <= 180) && (saturation >= 20 && saturation <= 80)) ){
					skinMap[y][x] = 1;
				}
			}
		}
		setProgress(ZMath.percent(0, 4, 3));
		
		//************** SkinMap dilation ********************
		skinMap = dilation(skinMap , startX, startY, stopX, stopY);
		
		
		//*****************************************************
		setProgress(100);
		//return convertArrayToARGBchannel(hueMap, startX, startY, stopX, stopY, -150, 150, 1);
		//return convertArrayToARGBchannel(saturationMap, startX, startY, stopX, stopY, 0, 70, 1);
		ImageUtil.copyArray(
				convertArrayToARGBchannel(skinMap, startX, startY, stopX, stopY, 0, 1, 2), 
				data, startX, startY, stopX, stopY);
	}
	
	private int[][] dilation(int[][] data, int startX, int startY, int stopX, int stopY){
		int[][] output = new int[data.length][data[0].length];
		int radX = 8;
		int radY = 8;
		
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				if(data[y][x] == 1){
					
					for(int dy=y-radY; dy<y+radY ;dy++){
						for(int dx=x-radX; dx<x+radX ;dx++){
							if(dy >= 0 && dy < data.length && dx >= 0 && dx < data[0].length) 
								output[dy][dx] = 1;
						}
					}
					
				}
			}
		}
		
		return output;
	}
	
	
	/**
	 * Converts the given data array to a color image
	 * 
	 * @param data The 2d data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @param min The minimum value in the data
	 * @param max The maximum value in the data
	 * @param channel The color channel to apply the data to
	 * @return A ARGB array
	 */
	public int[][][] convertArrayToARGBchannel(int[][] data, int startX, int startY, int stopX, int stopY,int min, int max, int channel){
		int[][][] output = new int[data.length][data[0].length][4];
		
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				output[y][x][0] = 255;
				output[y][x][channel] = (int) ZMath.percent(min, max, data[y][x]);
			}
		}
		
		return output;
	}
	
	/**
	 * Converts RGB color to log-opponent (IRgBy) with the formula:
	 *  I= [L(R)+L(B)+L(G)]/3
	 *  Rg = L(R)-L(G)
	 *  By = L(B)-[L(G)+L(R)]/2
	 *
	 * @param data The RGB data
	 * @param startX is the x pixel of the image to start from
	 * @param startY is the y pixel of the image to start from
	 * @param stopX is the x pixel of the image to stop
	 * @param stopY is the y pixel of the image to stop
	 * @return IRgBy data
	 */
	public int[][][] convertARGBToIRgBy(int[][][] data, int startX, int startY, int stopX, int stopY){
		int[][][] output = new int[data.length][data[0].length][4];
		
		for(int y=startY; y<stopY ;y++){
			for(int x=startX; x<stopX ;x++){
				output[y][x][0] = data[y][x][0];
				// I= [L(R)+L(B)+L(G)]/3
				output[y][x][1] = (
						IRgByFunction(data[y][x][1]) + 
						IRgByFunction(data[y][x][2]) + 
						IRgByFunction(data[y][x][3])
						) / 3;
				// Rg = L(R)-L(G)
				output[y][x][2] = IRgByFunction(output[y][x][1]) - IRgByFunction(data[y][x][2]);
				// By = L(B)-[L(G)+L(R)]/2
				output[y][x][3] = IRgByFunction(output[y][x][3]) - 
						(IRgByFunction(output[y][x][2]) - IRgByFunction(output[y][x][1])) / 2;
			}
		}
		
		return output;
	}
	// Helper function to convertToIRgBy()
	private int IRgByFunction(int value){
		return (int)(105*Math.log10(value+1));
	}
	
	
	private int getSCALE(int width, int height){
		return (width+height)/320;
	}
	
	
	
	public Rectangle getFaceRectangle(){
		return null;
	}

}

/*
 * Copyright (c) 2015 ezivkoc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import zutil.image.ImageUtil;
import zutil.io.file.FileUtil;

/**
 * This class is a panel with a background image
 * @author Ziver
 *
 */
public class JImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/** The original image */
	private BufferedImage org_img;
	/** An resized copy of the image */
	private BufferedImage resized_img;
	/** If the image should be scaled to the size of the component */
	private boolean scale = true;
	/** If the aspect ratio is to be kept */
	private boolean keep_aspect = true;

	public JImagePanel(){}
	
	/**
	 * Creates a new instance of this class
	 * 
	 * @param img is the path to the image
	 */
	public JImagePanel(String img) throws IOException {
		this(ImageIO.read( FileUtil.find( img ) ));
	}

	/**
	 * Creates a new instance of this class
	 * 
	 * @param img is the image to use
	 */
	public JImagePanel(BufferedImage img) {
		this.org_img = img;		
	}

	/**
	 * Sets if the image should be scaled to the size of the panel
	 * @param b true of false
	 */
	public void scale(boolean b){
		scale = b;
	}

	/**
	 * Sets the background image
	 * 
	 * @param img is the image that will be used
	 */
	public void setImage(BufferedImage img){
		this.org_img = img;
		this.resized_img = null;
	}

	/**
	 * If the panel should keep the aspect ratio in the image when resizing
	 */
	public void keepAspect(boolean b){
		keep_aspect = b;
	}

	public void paintComponent(Graphics g) {
		if(org_img == null)
			super.paintComponent(g);
		else if(scale){
			if(resized_img == null ||
					this.getWidth() != resized_img.getWidth() || 
					this.getHeight() != resized_img.getHeight()){
				resized_img = ImageUtil.scale(org_img, this.getWidth(), this.getHeight(), keep_aspect);
			}
			g.drawImage(resized_img, 0, 0, null);
		}
		else{
			g.drawImage(org_img, 0, 0, null);			
		}

	}

}

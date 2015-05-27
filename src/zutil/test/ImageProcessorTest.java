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

package zutil.test;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JLabel;

import zutil.ProgressListener;
import zutil.image.ImageFilterProcessor;
import zutil.image.filters.GaussianBlurFilter;
import zutil.image.filters.BlurFilter;
import zutil.image.filters.ColorIntensityFilter;
import zutil.image.filters.ContrastBrightnessFilter;
import zutil.image.filters.DitheringFilter;
import zutil.image.filters.MeanBlurFilter;
import zutil.image.filters.MedianFilter;
import zutil.image.filters.ResizeImage;
import zutil.image.filters.SobelEdgeDetectionFilter;
import zutil.image.filters.SpotLightFilter;

@SuppressWarnings({ "unused", "rawtypes" })
public class ImageProcessorTest implements ProgressListener{
	private static String imgPath = "test.gif";
	//private static String imgPath = "test2.jpg";

	private JLabel processedLabel;
	private JLabel orginalLabel;
	private JProgressBar progress;

	public static void main(String[] args){
		new ImageProcessorTest();
	}

	@SuppressWarnings("unchecked")
	public ImageProcessorTest(){
		JFrame frame = getJFrame();
		BufferedImage img = null;

		try {
			// Read from an input stream
			InputStream is = new BufferedInputStream(new FileInputStream(imgPath));
			img = ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		ImageIcon orginalIcon = new ImageIcon(img);
		orginalLabel.setIcon(orginalIcon);
		frame.setVisible(true);
		frame.pack();
		
		BufferedImage procImg = null;
		try {
			//ImageFilterProcessor processor = new SobelEdgeDetectionFilter(img);
			ImageFilterProcessor processor = new GaussianBlurFilter(img);
			//ImageFilterProcessor processor = new BlurFilter(img, 100);
			//ImageFilterProcessor processor = new ColorIntensityFilter(img, true);
			//ImageFilterProcessor processor = new ContrastBrightnessFilter(img);
			//ImageFilterProcessor processor = new DitheringFilter(img);
			//ImageFilterProcessor processor = new MeanBlurFilter(img);
			//ImageFilterProcessor processor = new MedianFilter(img);
			//ImageFilterProcessor processor = new ResizeImage(img,100,100);
			//ImageFilterProcessor processor = new SpotLightFilter(img,100,100,100);
			
			processor.setProgressListener(this);
			procImg = processor.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ImageIcon processedIcon = new ImageIcon(procImg);
		processedLabel.setIcon(processedIcon);

		
		frame.pack();
	}

	private JFrame getJFrame() {
		processedLabel = new JLabel("Processed");
		orginalLabel = new JLabel("Orginal");
		
		progress = new JProgressBar();
		progress.setMaximum(100);
		progress.setValue(0);
		progress.setIndeterminate(false);
		progress.setStringPainted(true);
		
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		jPanel.add(orginalLabel, BorderLayout.NORTH);
		jPanel.add(processedLabel, BorderLayout.CENTER);
		jPanel.add(progress, BorderLayout.SOUTH);

		JFrame jFrame = new JFrame("ImageProcessorTest");
		jFrame.setSize(new Dimension(715, 361));
		jFrame.setContentPane(jPanel);
		jFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		return jFrame;
	}

	public void progressUpdate(Object source, Object info, double percent) {
		progress.setValue((int)percent);
	}
}

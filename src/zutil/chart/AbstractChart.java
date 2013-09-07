/*******************************************************************************
 * Copyright (c) 2013 Ziver
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
 ******************************************************************************/
package zutil.chart;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.logging.Logger;

import javax.swing.JPanel;

import zutil.log.LogUtil;

public abstract class AbstractChart extends JPanel{
	private static final Logger logger = LogUtil.getLogger();
	private static final long serialVersionUID = 1L;
	
	/** The offset from the borders of the panel in pixels */
	public static final int PADDING = 20; 
	
	protected ChartData data;
	protected int width;
	protected int height;
	protected Rectangle chartBound;

	
	
	protected void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		
		Rectangle bound = drawScale( g2 );
		drawChart( g2, bound );
	}
	
	protected Rectangle drawScale(Graphics2D g2){
		if( data == null )
			return null;
		
		// update values
		width = this.getWidth();
		height = this.getHeight();
		Rectangle bound = new Rectangle();
		
		// Values
		int stepLength = 7;
		
		/////// Temp values
		// Calculate Font sizes 
		FontMetrics metric = g2.getFontMetrics();
		int fontHeight = metric.getHeight();
		int fontXWidth = 0;
		int fontYWidth = 0;
		for( Point p : data.getPoints() ){
			int length = 0;
			String tmp = data.getXString( p.x );			
			if( tmp != null ) 	length = metric.stringWidth( tmp );
			else				length = metric.stringWidth( ""+p.x );
			fontXWidth = Math.max(length, fontXWidth);
			
			tmp = data.getXString( p.y );
			if( tmp != null ) 	length = metric.stringWidth( tmp );
			else				length = metric.stringWidth( ""+p.y );
			fontYWidth = Math.max(length, fontYWidth);
		}
		// Calculate origo
		Point origo = new Point( PADDING+fontYWidth+stepLength, height-PADDING-fontHeight-stepLength );
		bound.x = (int) (origo.getX()+1);
		bound.y = PADDING;
		bound.width = width-bound.x-PADDING;
		bound.height = (int) (origo.getY()-PADDING-1);
		// Calculate Axis scales
		double xScale = (double)(Math.abs(data.getMaxX())+Math.abs(data.getMinX()))/bound.width;
		double yScale = (double)(Math.abs(data.getMaxY())+Math.abs(data.getMinY()))/bound.height;
		
		
		/////// Draw	
		// Y Axis
		g2.draw( new Line2D.Double( origo.getX(), PADDING, origo.getX(), origo.getY() )); 
		// X Axis
		g2.draw( new Line2D.Double( origo.getX(), origo.getY(), width-PADDING, origo.getY() ));
		// Y Axis steps and labels
		g2.draw( new Line2D.Double( origo.getX(), origo.getY(), origo.getX()-stepLength, origo.getY() ));
		g2.draw( new Line2D.Double( origo.getX(), PADDING, origo.getX()-stepLength, PADDING ));
		
		// X Axis steps and labels
		g2.draw( new Line2D.Double( width-PADDING, origo.getY(), width-PADDING, origo.getY()+stepLength ));
		
		// DEBUG
		/*
		g2.setColor(Color.red);
		g2.drawRect(bound.x, bound.y, bound.width, bound.height);
		*/
		return bound;
	}
	
	/**
	 * This method is called after the chart scale has been drawn. 
	 * This method will draw the actual chart
	 * 
	 * @param 		g		is the Graphics object that will paint the chart
	 * @param 		bound	is the bounds of the chart, the drawing should not exceed this bound
	 */
	protected abstract void drawChart(Graphics2D g2, Rectangle bound);
	
	
	/**
	 * Sets the data that will be drawn.
	 * 
	 * @param 		data		is the data to draw
	 */
	public void setChartData(ChartData data){
		this.data = data;
	}
	
	/**
	 * Converts a x value to ax pixel coordinate
	 * 
	 * @param 		x		is the x data value
	 * @return				pixel coordinate, or 0 if the chart have not been drawn yet.
	 */
	protected int getXCoordinate(int x){
		return 0;
	}
	
	/**
	 * Converts a y value to a y pixel coordinate
	 * 
	 * @param 		y		is the y data value
	 * @return				pixel coordinate, or 0 if the chart have not been drawn yet.
	 */
	protected int getYCoordinate(int y){
		return 0;
	}
}

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

package zutil.chart;

import zutil.log.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public abstract class AbstractChart extends JPanel{
    private static final Logger logger = LogUtil.getLogger();

    /** The offset from the borders of the panel in pixels */
    public static final int PADDING = 20;

    protected ChartData data;
    protected int width;
    protected int height;
    protected Rectangle chartBound;



    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Rectangle bound = drawAxis( g2, new Rectangle(0, 0, getWidth(), getHeight()));
        drawChart( g2, bound );
    }


    /**
     * This method will draw the axis of the chart
     *
     * @param 		g2		is the Graphics object that will paint the chart
     * @param 		bound	is the bounds of the axis, the drawing should not exceed this bound
     * @return a Rectangle object specifying the drawn are of the axis
     */
    protected abstract Rectangle drawAxis(Graphics2D g2, Rectangle bound);

    /**
     * This method is called after the chart scale has been drawn.
     * This method will draw the actual chart
     *
     * @param 		g2		is the Graphics object that will paint the chart
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
     * Converts a x value to a x pixel coordinate
     *
     * @param   x       is the x data value
     * @param   scale   is the data scale
     * @param   bound   is the drawing bounds
     * @return a x pixel coordinate
     */
    static protected double getXCoordinate(double x, double scale, Rectangle bound){
        return bound.x + x * scale;
    }

    /**
     * Converts a y value to a y pixel coordinate
     *
     * @param 	y       is the y data value
     * @param   scale   is the data scale
     * @param   bound   is the drawing bounds
     * @return a y pixel coordinate
     */
    static protected double getYCoordinate(double y, double scale, Rectangle bound){
        return bound.y + bound.height - ( y * scale );
    }

    static protected double getXScale(ChartData data, Rectangle bound){
        return (double) bound.width / (Math.abs(data.getMaxX()) + Math.abs(data.getMinX()));
    }

    static protected double getYScale(ChartData data, Rectangle bound){
        return (double) bound.height / (Math.abs(data.getMaxY()) + Math.abs(data.getMinY()));
    }
}

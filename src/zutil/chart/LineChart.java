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

import java.awt.*;
import java.awt.geom.Line2D;

public class LineChart extends LineAxis{
    private static final long serialVersionUID = 1L;

    @Override
    protected void drawChart(Graphics2D g2, Rectangle bound) {
        g2.setPaint(new Color(237,28,36));
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Calculate position

        double xScale = getXScale(data, bound);
        double yScale = getYScale(data, bound);

        // Draw lines

        Point prevP = null;
        for(Point p : data.getPoints()){
            if (prevP != null)
                drawLine(g2, bound, xScale, yScale, prevP.x, prevP.y, p.x, p.y);
            prevP = p;
        }
    }

    private void drawLine(Graphics2D g2, Rectangle bound, double xScale, double yScale,
                          double x1, double y1, double x2, double y2){
        // Line
        g2.draw(new Line2D.Double(
                getXCoordinate(x1, xScale, bound),
                getYCoordinate(y1, yScale, bound),
                getXCoordinate(x2, xScale, bound),
                getYCoordinate(y2, yScale, bound)));
    }
}

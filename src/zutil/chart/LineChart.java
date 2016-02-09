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

public class LineChart extends AbstractChart{
	private static final long serialVersionUID = 1L;

	@Override
	protected void drawChart(Graphics2D g2, Rectangle bound) {
		// TODO Auto-generated method stub
		drawLine(g2,  50, 50,100,150);
		drawLine(g2, 100,150,150,100);
		drawLine(g2, 150,100,200,300);
		drawLine(g2, 200,300,250,40);
	}

	private void drawLine(Graphics2D g2, float x1, float y1, float x2, float y2){
		// Shadow
		g2.setPaint(new Color(220,220,220));
		g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
		g2.draw( new Line2D.Float(x1, y1+2, x2, y2+2));
		// Smoth shadow
		g2.setPaint(new Color(230,230,230));
		g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
		g2.draw( new Line2D.Float(x1+1, y1+3, x2-1, y2+3));
		
		// Line border
		g2.setPaint(new Color(255,187,187));
		g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
		g2.draw( new Line2D.Float(x1, y1, x2, y2));
		
		// Line
		g2.setPaint(new Color(237,28,36));
		g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
		g2.draw( new Line2D.Float(x1, y1, x2, y2));
	}
}

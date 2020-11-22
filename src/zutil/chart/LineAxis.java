/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

/**
 *
 */
public abstract class LineAxis extends AbstractChart{

    @Override
    protected Rectangle drawAxis(Graphics2D g2, Rectangle bound){
        if( data == null )
            return null;

        width = bound.width;
        height = bound.height;
        chartBound = new Rectangle();
        int stepLength = 7;

        // **********************************
        // Calculate Font sizes
        // **********************************

        FontMetrics metric = g2.getFontMetrics();
        int fontHeight = metric.getHeight();
        int fontXWidth = 0;
        int fontYWidth = 0;
        for( Point p : data.getData() ){
            int length;
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

        Point origo = new Point(
                PADDING + fontYWidth + stepLength,
                height - PADDING - fontHeight - stepLength );
        chartBound.x = (int) (origo.getX() + 1);
        chartBound.y = PADDING;
        chartBound.width = width - chartBound.x - PADDING;
        chartBound.height = (int) (origo.getY() - PADDING - 1);

        // **********************************
        // Draw
        // **********************************

        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

        // Y Axis
        g2.draw( new Line2D.Double( origo.getX(), PADDING, origo.getX(), origo.getY()+PADDING/2 ));
        // X Axis
        g2.draw( new Line2D.Double( origo.getX()-PADDING/2, origo.getY(), width-PADDING, origo.getY() ));
        // Y Axis steps and labels

        // X Axis steps and labels

        // DEBUG
        /*
        g2.setColor(Color.red);
        g2.drawRect(bound.x, bound.y, bound.width, bound.height);
        */
        return chartBound;
    }

}

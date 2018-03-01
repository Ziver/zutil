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
        for( Point p : data.getPoints() ){
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
        g2.draw( new Line2D.Double( origo.getX(), PADDING, origo.getX(), origo.getY() ));
        // X Axis
        g2.draw( new Line2D.Double( origo.getX(), origo.getY(), width-PADDING, origo.getY() ));
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

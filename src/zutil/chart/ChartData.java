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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChartData {
	
	private HashMap<Integer,String> xStrings;
	private HashMap<Integer,String> yStrings;
	private int maxx;
	private int minx;
	private int maxy;
	private int miny;

	private ArrayList<Point> points;
	
	
	public ChartData(){
		xStrings = new HashMap<Integer,String>();
		yStrings = new HashMap<Integer,String>();
		
		points = new ArrayList<Point>();
	}
	
	public void setXValueString(int x, String name){
		xStrings.put(x, name);
	}
	public void setYValueString(int y, String name){
		yStrings.put(y, name);
	}

	
	public void addPoint(int x, int y){
		points.add( new Point( x, y));
		setMaxMin(x, y);
	}	
	public void addPoint(int y){
		points.add( new Point( maxx, y));
		maxx++;
		setMaxMin(maxx, y);
	}
	public void addPoint(String x, int y){
		xStrings.put(maxx, x);		
		points.add( new Point( maxx, y));
		maxx++;
		setMaxMin(maxx, y);
	}
	
	
	private void setMaxMin(int x, int y){
		if( x > maxx )	maxx = x;
		else if( x < minx ) minx = x;
		
		if( y > maxy )	maxx = y;
		else if( y < miny ) minx = y;
	}
	
	public int getMaxX(){
		return maxx;
	}
	public int getMinX(){
		return minx;
	}
	public int getMaxY(){
		return maxy;
	}
	public int getMinY(){
		return miny;
	}
	public String getXString(int x){
		return xStrings.get(x);
	}
	public String getYString(int y){
		return yStrings.get(y);
	}
	
	protected List<Point> getPoints(){
		return points;
	}
}

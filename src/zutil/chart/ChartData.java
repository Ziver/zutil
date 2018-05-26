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
    private int maxX;
    private int minX;
    private int maxY;
    private int minY;

    private ArrayList<Point> points;


    public ChartData(){
        xStrings = new HashMap<>();
        yStrings = new HashMap<>();

        points = new ArrayList<>();
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


    private void setMaxMin(int x, int y){
        if( x > maxX) maxX = x;
        if( x < minX) minX = x;

        if( y > maxY) maxY = y;
        if( y < minY) minY = y;
    }

    public int getMaxX(){
        return maxX;
    }
    public int getMinX(){
        return minX;
    }
    public int getMaxY(){
        return maxY;
    }
    public int getMinY(){
        return minY;
    }
    public String getXString(int x){
        return xStrings.get(x);
    }
    public String getYString(int y){
        return yStrings.get(y);
    }

    protected List<Point> getData(){
        return points;
    }
}

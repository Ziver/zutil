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
import java.util.logging.Level;


public class ChartTest extends JFrame{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		LogUtil.setLevel("zutil", Level.FINEST);
		ChartTest frame = new ChartTest();
		frame.setVisible(true);
	}
	
	public ChartTest(){
		ChartData data = new ChartData();
		data.addPoint(0,0);
		data.addPoint(1,1);
		data.addPoint(2,2);
		data.addPoint(3,3);
		data.addPoint(4,4);
		data.addPoint(5,5);
		data.addPoint(6,6);
		data.addPoint(7,7);
		data.addPoint(8,8);
		
		LineChart chart = new LineChart();
		chart.setChartData( data );
		add( chart );

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize(600, 400);
	}

}

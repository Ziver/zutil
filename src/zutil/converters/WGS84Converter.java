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

package zutil.converters;

public class WGS84Converter {
	public static void main(String[] args){
		System.out.println(toWGS84Decimal("N 59� 47' 43\"")+" "+toWGS84Decimal(" E 17� 42' 55\""));
		System.out.println(toWGS84Decimal("55� 0' 0\"")+" "+toWGS84Decimal("68� 59' 59,999\""));
		System.out.println(toWGS84Decimal("55� 0.001'")+" "+toWGS84Decimal("68� 59.999'"));
		System.out.println(toWGS84Decimal("3444.0000S")+" "+toWGS84Decimal("13521.0000E"));
		System.out.println(toWGS84Decimal("-44.0001")+" "+toWGS84Decimal("521.0001"));
	}

	/**
	 * Converts an WGS84 coordinate to an WGS84 decimal coordinate
	 * 
	 * @param coordinate is the coordinate to convert
	 * @return the new coordinate in decimal degrees, returns 0 if conversions fails
	 */
	public static float toWGS84Decimal(String coordinate){
		float deg=0, min=0, sec=0, neg=1;
		coordinate = coordinate.trim().replaceAll(",", ".").toUpperCase();
		if(coordinate.contains("S") || coordinate.contains("W"))
			neg = -1;
		
		// 55� 0' 68� 59,999 or 55� 0' 0" 68� 59' 59,999"
		if(coordinate.matches("[NSWE ]? ?[0-9]{1,3}� [0-9]{1,2}.?[0-9]*'[ 0-9.\\\"]*")){
			coordinate = coordinate.replaceAll("[NSEW�'\\\"]", "").trim();
			String[] tmp = coordinate.split(" ");
			deg = Float.parseFloat(tmp[0]);
			min = Float.parseFloat(tmp[1]);
			if(tmp.length > 2){
				sec = Float.parseFloat(tmp[2]);
			}
		}
		// 3444.0000S 13521.0000E
		else if(coordinate.matches("[0-9]{4,5}.[0-9]*[NSEW]{1}")){
			coordinate = coordinate.replaceAll("[NS EW]", "");
			float tmpf = Float.parseFloat(coordinate);
			deg = (int)(tmpf/100);
			min = tmpf-(deg*100);
		}
		// 55.0 68.99999
		else if(coordinate.matches("\\-?[0-9]{2,3}.[0-9]*")){
			return Float.parseFloat(coordinate);
		}
		
		return neg*(deg + min/60 + sec/3600);
	}
}

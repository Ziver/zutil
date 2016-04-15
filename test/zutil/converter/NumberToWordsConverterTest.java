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

package zutil.converter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import zutil.converter.NumberToWordsConverter;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(Parameterized.class)
public class NumberToWordsConverterTest {
		
	@Parameters
	public static Collection<Object[]> parameters() {		
		Object[][] data = new Object[][] {
			{-77,  "minus seventy seven"}
			, {-2, "minus two"}
			, {1,  "one"}
			, {0,  "zero"}
			, {7,  "seven"}
			, {11, "eleven"}
			, {12, "twelve"}
			, {17, "seventeen"}
			, {20, "twenty"}
			, {21, "twenty one"}
			, {23, "twenty three"}
			, {25, "twenty five"}
			, {30, "thirty"}
			, {34, "thirty four"}
			, {50, "fifty"}
			, {70, "seventy"}
			, {100, "one hundred"}
			, {110, "one hundred ten"}
			, {131, "one hundred thirty one"}
			, {222, "two hundred twenty two"}
			, {1000, "one thousand"}
			, {10_000, "ten thousand"}
			, {100_000, "one hundred thousand"}
			, {1000_000, "one million"}
			, {10_000_000, "ten million"}
			, {Integer.MAX_VALUE, "two billion one hundred forty seven million four hundred eighty three thousand six hundred forty seven"}
			, {Integer.MIN_VALUE, "minus two billion one hundred forty seven million four hundred eighty three thousand six hundred forty eight"}
			};
		return Arrays.asList(data);
	}
	
	
	private int input;
	private String expected;
	

	public NumberToWordsConverterTest(int input, String expected) {
		this.input = input;
		this.expected = expected;
	}

	@Test
	public void testConvert() {
		assertThat(new NumberToWordsConverter().convert(input)
													, is(equalTo(expected)));
	}

}
	
	
